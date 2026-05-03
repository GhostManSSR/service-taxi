package trip_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TripStatsResponse {
    private long totalTrips;
    private double avgPrice;
}
