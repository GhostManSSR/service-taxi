package user_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriverRatingResponse {
    private Long driverId;
    private Double rating;
    private Integer ratingCount;
}