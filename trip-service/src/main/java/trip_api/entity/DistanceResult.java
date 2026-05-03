package trip_api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DistanceResult {
    private double distanceKm;
    private double durationMin;
}