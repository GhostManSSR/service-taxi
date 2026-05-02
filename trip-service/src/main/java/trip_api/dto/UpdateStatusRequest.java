package trip_api.dto;

import lombok.Data;
import trip_api.entity.TripStatus;

@Data
public class UpdateStatusRequest {
    private TripStatus status;
}
