package user_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_api.entity.DriverStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverStatusEvent {
    private Long driverId;
    private DriverStatus status;
}
