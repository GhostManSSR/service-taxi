package trip_api.mapper;

import org.springframework.stereotype.Component;
import trip_api.dto.TripResponse;
import trip_api.entity.Trip;

import java.util.List;

@Component
public class TripMapper {

    public TripResponse toDto(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getPassengerId(),
                trip.getDriverId(),
                trip.getStatus().name(),
                trip.getOrigin(),
                trip.getDistanceKm(),
                trip.getDurationMin(),
                trip.getDestination(),
                trip.getPrice(),
                trip.getRating()
        );
    }

    public List<TripResponse> toDto(List<Trip> drivers) {
        return drivers.stream()
                .map(this::toDto)
                .toList();
    }
}
