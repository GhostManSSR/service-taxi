package user_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление пассажира")
public class UpdatePassengerRequest {

    @NotBlank(message = "Имя не может быть пустым")
    @Schema(example = "Art", description = "Имя пассажира")
    private String name;

    @Email(message = "Некорректный email")
    @Schema(example = "example@mail.ru", description = "Email пассажира")
    private String email;

    @NotBlank(message = "Телефон не может быть пустым")
    @Schema(example = "79135672345", description = "Телефон пассажира")
    private String phone;
}
