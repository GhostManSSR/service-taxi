package trip_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trip_api.entity.Trip;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByPassengerId(Long passengerId);
}

