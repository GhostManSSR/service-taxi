package user_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание водителя")
public class CreateDriverRequest {

    @NotBlank
    @Schema(example = "Art")
    private String name;

    @Schema(example = "ABC123")
    private String licenseNumber;
}
