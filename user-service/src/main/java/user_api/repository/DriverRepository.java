package user_api.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import user_api.entity.Driver;
import org.springframework.data.domain.Pageable;
import user_api.entity.DriverStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Driver d WHERE d.status = 'AVAILABLE'")
    List<Driver> findAvailableDrivers(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Driver d WHERE d.status = 'AVAILABLE'")
    Optional<Driver> findOneAvailableDriver(Pageable pageable);

    List<Driver> findByStatus(DriverStatus status);

    boolean existsById(Long id);

    Page<Driver> findAllByStatus(DriverStatus status, Pageable pageable);
}

