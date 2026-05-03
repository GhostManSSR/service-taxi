package trip_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Информация о поездке")
public class TripResponse {

    private Long id;
    private Long passengerId;
    private Long driverId;
    private String status;
    private String origin;
    private Double distanceKm;
    private Double durationMin;
    private String destination;
    private Double price;
}