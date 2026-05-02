package user_api.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import user_api.entity.Driver;
import user_api.entity.DriverStatus;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Driver d WHERE d.status = 'AVAILABLE'")
    List<Driver> findAvailableDrivers(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Driver d WHERE d.status = 'AVAILABLE'")
    List<Driver> findOneAvailableDriver(Pageable pageable);

    List<Driver> findByStatus(DriverStatus status);

    boolean existsById(Long id);

    Page<Driver> findAllByStatus(DriverStatus status, Pageable pageable);
}
