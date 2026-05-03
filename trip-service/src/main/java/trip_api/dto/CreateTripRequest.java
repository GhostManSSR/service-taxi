package trip_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import trip_api.entity.Rates;

@Data
@Schema(description = "Создание поездки")
public class CreateTripRequest {

    @Schema(example = "1")
    private Long passengerId;

    private String origin;
    private Rates rate;
    private String destination;
}
