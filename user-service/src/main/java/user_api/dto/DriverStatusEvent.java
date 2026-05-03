package user_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_api.entity.DriverStatus;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatusEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long driverId;
    private DriverStatus status;
}