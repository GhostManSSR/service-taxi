package user_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_api.entity.DriverStatus;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с информацией о водителе")
public class DriverResponse implements Serializable {

    private static final long serialVersionUID = 1L;

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