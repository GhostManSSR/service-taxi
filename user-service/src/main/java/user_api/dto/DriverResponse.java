package user_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import user_api.entity.DriverStatus;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с информацией о водителе")
public class DriverResponse {

    @Schema(description = "ID водителя", example = "1")
    private Long id;

    @Schema(description = "Имя водителя", example = "Art")
    private String name;

    @Schema(
            description = "Текущий статус водителя",
            example = "AVAILABLE",
            allowableValues = {"AVAILABLE", "BUSY", "OFFLINE", "INACTIVE"}
    )
    private DriverStatus status;
}
