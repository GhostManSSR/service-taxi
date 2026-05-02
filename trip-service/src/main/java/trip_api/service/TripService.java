package trip_api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import trip_api.client.UserClient;
import trip_api.dto.CreateTripRequest;
import trip_api.entity.Trip;
import trip_api.entity.TripStatus;
import trip_api.repository.TripRepository;
import user_api.dto.DriverResponse;
import user_api.dto.DriverStatusEvent;

import java.time.LocalDateTime;
import java.util.List;

import static user_api.entity.DriverStatus.AVAILABLE;
import static user_api.entity.DriverStatus.BUSY;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository repository;
    private final UserClient userClient;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String DRIVER_CACHE_KEY = "available_driver";
    private static final String TRIP_EXCHANGE = "trip.exchange";

    private static final String CACHE_DRIVERS = "available_drivers";

    @Transactional
    public Trip create(CreateTripRequest request) {

        userClient.checkPassenger(request.getPassengerId());

        DriverResponse driver = getDriver();

        Trip trip = new Trip();
        trip.setPassengerId(request.getPassengerId());
        trip.setDriverId(driver.getId());
        trip.setStatus(TripStatus.CREATED);
        trip.setOrigin(request.getOrigin());
        trip.setDestination(request.getDestination());
        trip.setPrice(calculatePrice());
        trip.setCreatedAt(LocalDateTime.now());
        trip.setUpdatedAt(LocalDateTime.now());

        repository.save(trip);

        rabbitTemplate.convertAndSend(
                TRIP_EXCHANGE,
                "trip.created",
                trip.getId()
        );

        rabbitTemplate.convertAndSend(
                "user.exchange",
                "driver.status.update",
                new DriverStatusEvent(driver.getId(), BUSY)
        );

        redisTemplate.opsForValue().set(DRIVER_CACHE_KEY, driver);

        return trip;
    }

    public Trip get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
    }

    public List<Trip> getByPassenger(Long passengerId) {
        return repository.findByPassengerId(passengerId);
    }

    @Transactional
    public Trip updateStatus(Long id, TripStatus status) {

        Trip trip = get(id);

        trip.setStatus(status);
        trip.setUpdatedAt(LocalDateTime.now());

        repository.save(trip);

        rabbitTemplate.convertAndSend(
                TRIP_EXCHANGE,
                "trip.status.changed",
                trip.getId()
        );

        if (status == TripStatus.COMPLETED) {

            rabbitTemplate.convertAndSend(
                    "user.exchange",
                    "driver.status.update",
                    new DriverStatusEvent(trip.getDriverId(), AVAILABLE)
            );
        }

        return trip;
    }

    private double calculatePrice() {
        double distance = 10.0;
        double rate = 2.5;
        return distance * rate;
    }

    private DriverResponse getDriver() {

        DriverResponse cached =
                (DriverResponse) redisTemplate.opsForValue().get(DRIVER_CACHE_KEY);

        if (cached != null) {
            return cached;
        }

        DriverResponse driver = userClient.assignDriver();

        redisTemplate.opsForValue().set(DRIVER_CACHE_KEY, driver);

        return driver;
    }

    private DriverResponse getDriverFromCache() {
        return (DriverResponse) redisTemplate.opsForValue().get(CACHE_DRIVERS);
    }
}
