package user_api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import user_api.dto.CreateDriverRequest;
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

    private final DriverRepository repository;
    private final DriverMapper mapper;

    public Driver create(CreateDriverRequest request) {
        Driver d = new Driver();
        d.setName(request.getName());
        d.setEmail(request.getEmail());
        d.setPhone(request.getPhoneNumber());
        d.setLicenseNumber(request.getLicenseNumber());
        d.setStatus(DriverStatus.AVAILABLE);
        d.setCreatedAt(LocalDateTime.now());

        return repository.save(d);
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
            throw new RuntimeException("No drivers available");
        }

        Driver driver = drivers.get(0);
        driver.setStatus(DriverStatus.BUSY);

        return repository.save(driver);
    }

    @Transactional
    public void updateStatus(Long id, DriverStatus status) {
        Driver driver = get(id);
        driver.setStatus(status);
        repository.save(driver);
    }

    @Transactional
    public DriverResponse update(Long id, UpdateDriverRequest request) {  // ✅ request, не driverResponse
        Driver driver = get(id);
        driver.setName(request.getName());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setUpdatedAt(LocalDateTime.now());

        Driver saved = repository.save(driver);
        return mapper.toDto(saved);
    }

    @Transactional
    public void deleteDriver(Long id) {
        repository.deleteById(id);
    }
}

