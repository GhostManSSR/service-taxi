package user_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание пассажира")
public class CreatePassengerRequest {

    @NotBlank
    @Schema(example = "Art")
    private String name;

    @Schema(example = "example@mail.ru")
    private String email;
    @Schema(example = "79135672345")
    private String phone;
}
