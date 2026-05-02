package user_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import user_api.entity.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}

