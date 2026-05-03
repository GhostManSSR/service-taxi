package trip_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import trip_api.client.UserClient;
import trip_api.dto.*;
import trip_api.entity.DistanceResult;
import trip_api.entity.Rates;
import trip_api.entity.Trip;
import trip_api.service.FreeDistanceService;
import trip_api.entity.TripStatus;
import trip_api.repository.TripRepository;
import user_api.dto.DriverResponse;
import user_api.dto.DriverStatusEvent;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static reactor.netty.http.HttpConnectionLiveness.log;
import static user_api.entity.DriverStatus.AVAILABLE;
import static user_api.entity.DriverStatus.BUSY;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository repository;
    private final UserClient userClient;
    private final RabbitTemplate rabbitTemplate;
    private final FreeDistanceService freeDistanceService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String DRIVER_CACHE_KEY = "available_driver";
    private static final String TRIP_EXCHANGE = "trip.exchange";
    private static final String DRIVER_STATUS_KEY = "driver:status:";
    private static final String CACHE_DRIVERS = "available_drivers";
    private final ObjectMapper mapper = new ObjectMapper();

    @Transactional()
    public Trip create(CreateTripRequest request) {
        log.info("Creating trip for passenger: {}", request.getPassengerId());

        userClient.checkPassenger(request.getPassengerId());

        DriverResponse driver = getDriver();
        log.info("Assigned driver: {}", driver.getId());

        PriceResult result = calculatePrice(
                request.getRate(),
                request.getOrigin(),
                request.getDestination()
        );

        Trip trip = Trip.builder()
                .passengerId(request.getPassengerId())
                .driverId(driver.getId())
                .status(TripStatus.CREATED)
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .rate(request.getRate())
                .price(result.price())
                .distanceKm(result.distanceKm())
                .durationMin(result.durationMin())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Trip savedTrip = repository.save(trip);
        log.info("Trip saved with ID: {}", savedTrip.getId());

        sendTripEvent(savedTrip, "trip.created");

        CorrelationData correlationData = new CorrelationData(
                "driver-" + driver.getId()
        );

        rabbitTemplate.convertAndSend(
                "user.exchange",
                "driver.status.update",
                new DriverStatusEvent(driver.getId(), BUSY),
                correlationData
        );

        redisTemplate.opsForValue().set(DRIVER_CACHE_KEY, driver, Duration.ofMinutes(30));
        return savedTrip;
    }

    public List<Trip> getAll(){
        return repository.findAll();
    }

    private void sendTripEvent(Trip trip, String routingKey) {

        TripEvent event = new TripEvent(
                trip.getId(),
                trip.getPassengerId(),
                trip.getDriverId(),
                trip.getOrigin(),
                trip.getDestination(),
                trip.getPrice(),
                trip.getRating(),
                trip.getDistanceKm(),
                trip.getDurationMin(),
                trip.getStatus().name()
        );

        rabbitTemplate.convertAndSend(
                TRIP_EXCHANGE,
                routingKey,
                event
        );
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

        Trip saved = repository.save(trip);

        rabbitTemplate.convertAndSend(
                TRIP_EXCHANGE,
                "trip.status.changed",
                trip.getId()
        );

        sendTripEvent(saved, "trip.status.changed");

        if (status == TripStatus.COMPLETED) {
            rabbitTemplate.convertAndSend(
                    "user.exchange",
                    "driver.status.update",
                    new DriverStatusEvent(trip.getDriverId(), AVAILABLE)
            );
        }

        return trip;
    }

    private PriceResult calculatePrice(Rates rate, String origin, String destination) {
        try {
            DistanceResult dist = freeDistanceService.getDistance(origin, destination);

            double km = Math.max(dist.getDistanceKm(), 1.0);

            log.info("OSRM: {}km, {}min ({})",
                    String.format("%.1f", km),
                    String.format("%.0f", dist.getDurationMin()),
                    origin + " → " + destination
            );

            double basePrice = switch (rate) {
                case ECONOMY -> 20.0;
                case COMFORT -> 35.0;
                case BUSINESS -> 55.0;
            };

            double price = basePrice * km;

            return new PriceResult(
                    Math.round(price * 100) / 100.0,
                    km,
                    dist.getDurationMin()
            );

        } catch (Exception e) {
            log.warn("Distance calc failed, fallback");

            return new PriceResult(350.0, 10.0, 15.0);
        }
    }

    private DriverResponse getDriver() {
        return userClient.assignDriver();
    }

    public TripStatsResponse getDailyStats(LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Trip> trips = repository.findTripsByDay(start, end);

        long count = trips.size();

        double avgPrice = trips.stream()
                .mapToDouble(Trip::getPrice)
                .average()
                .orElse(0.0);

        return new TripStatsResponse(count, Math.round(avgPrice * 100.0) / 100.0);
    }

    @Transactional
    public Trip rateTrip(Long tripId, RateTripRequest request) {

        Trip trip = get(tripId);

        if (trip.getStatus() != TripStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Trip must be completed before rating"
            );
        }

        if (trip.getRating() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Trip already rated"
            );
        }

        trip.setRating(request.getRating());
        trip.setUpdatedAt(LocalDateTime.now());

        Trip saved = repository.save(trip);

        sendTripEvent(saved, "trip.rate.changed");

        rabbitTemplate.convertAndSend(
                "user.exchange",
                "driver.rating",
                new DriverRatingEvent(
                        trip.getDriverId(),
                        request.getRating()
                )
        );

        return saved;
    }



//    private DriverResponse getDriver() {
//
//        DriverResponse driver;
//
//        int attempts = 0;
//        int maxAttempts = 10;
//
//        do {
//            // DriverResponse cached =
//            //        (DriverResponse) redisTemplate.opsForValue().get(DRIVER_CACHE_KEY);
//
//            DriverResponse cached = null;
//
//            Object obj = redisTemplate.opsForValue().get(DRIVER_CACHE_KEY);
//
//            if (obj != null) {
//                cached = mapper.convertValue(obj, DriverResponse.class);
//            }
//
//            driver = (cached != null) ? cached : userClient.assignDriver();
//
//            String statusKey = DRIVER_STATUS_KEY + driver.getId();
//            String status = (String) redisTemplate.opsForValue().get(statusKey);
//
//            if (status == null || status.equals("AVAILABLE")) {
//
//                redisTemplate.opsForValue().set(DRIVER_CACHE_KEY, driver);
//                return driver;
//            }
//
//            log.warn("Driver {} is BUSY, retrying...", driver.getId());
//
//            attempts++;
//
//        } while (attempts < maxAttempts);
//
//        throw new ResponseStatusException(
//                HttpStatus.CONFLICT,
//                "No available drivers at the moment"
//        );
//    }


    private DriverResponse getDriverFromCache() {
        return (DriverResponse) redisTemplate.opsForValue().get(CACHE_DRIVERS);
    }

    public void delete(Long id){
        repository.deleteById(id);
    }
}
