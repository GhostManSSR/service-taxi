package trip_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trip_api.entity.Trip;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByPassengerId(Long passengerId);

    @Query("""
        SELECT t FROM Trip t
        WHERE t.createdAt >= :start AND t.createdAt < :end
    """)
    List<Trip> findTripsByDay(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}

