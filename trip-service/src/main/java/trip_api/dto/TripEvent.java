package trip_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripEvent implements Serializable {

    private Long tripId;
    private Long passengerId;
    private Long driverId;

    private String origin;
    private String destination;

    private Double price;
    private Integer rating;
    private Double distanceKm;
    private Double durationMin;

    private String status;
}

