package user_api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import user_api.dto.CreateDriverRequest;
import user_api.dto.DriverRatingEvent;
import user_api.dto.DriverResponse;
import user_api.dto.UpdateDriverRequest;
import user_api.entity.Driver;
import user_api.entity.DriverStatus;
import user_api.mapper.DriverMapper;
import user_api.repository.DriverRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {
    private static final String DRIVER_STATUS_KEY = "driver:status:";
    private final DriverRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DriverMapper mapper;

    public Driver create(CreateDriverRequest request) {
        Driver d = new Driver();
        d.setName(request.getName());
        d.setEmail(request.getEmail());
        d.setPhone(request.getPhoneNumber());
        d.setLicenseNumber(request.getLicenseNumber());
        d.setStatus(DriverStatus.AVAILABLE);
        d.setCreatedAt(LocalDateTime.now());

        Driver saved = repository.save(d);

        updateDriverStatusInCache(saved.getId(), saved.getStatus());

        return saved;
    }

    private void updateDriverStatusInCache(Long driverId, DriverStatus status) {
        redisTemplate.opsForValue().set(
                DRIVER_STATUS_KEY + driverId,
                status.name()
        );
    }

    @Transactional
    public void updateRating(Long driverId, Integer newRating) {

        Driver driver = repository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Integer count = driver.getRatingCount() == null ? 0 : driver.getRatingCount();
        Double avg = driver.getRating() == null ? 0.0 : driver.getRating();

        double updatedAvg = (avg * count + newRating) / (count + 1);

        driver.setRating(updatedAvg);
        driver.setRatingCount(count + 1);

        repository.save(driver);
    }

    public Driver getDriverRatingEvent(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    public Driver get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    public List<Driver> getAll() {
        return repository.findAll();
    }

    @Transactional
    public Driver assignDriver() {
        List<Driver> drivers =
                repository.findAvailableDrivers(PageRequest.of(0, 1));

        if (drivers.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No available drivers"
            );
        }

        Driver driver = drivers.get(0);
        driver.setStatus(DriverStatus.BUSY);

        Driver saved = repository.save(driver);

        updateDriverStatusInCache(saved.getId(), saved.getStatus());

        return saved;
    }

    @Transactional
    public void updateStatus(Long id, DriverStatus status) {
        Driver driver = get(id);
        driver.setStatus(status);

        repository.save(driver);

        updateDriverStatusInCache(id, status);
    }

    @Transactional
    public DriverResponse update(Long id, UpdateDriverRequest request) {
        Driver driver = get(id);
        driver.setName(request.getName());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setUpdatedAt(LocalDateTime.now());

        Driver saved = repository.save(driver);

        updateDriverStatusInCache(saved.getId(), saved.getStatus());

        return mapper.toDto(saved);
    }

    @Transactional
    public void deleteDriver(Long id) {
        repository.deleteById(id);
    }
}

