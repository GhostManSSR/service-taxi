package user_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import user_api.dto.CreatePassengerRequest;
import user_api.entity.Passenger;
import user_api.service.PassengerService;

@RestController
@RequestMapping("/passengers")
@RequiredArgsConstructor
@Tag(name = "Passengers", description = "Управление пассажирами")
public class PassengerController {

    private final PassengerService service;

    @Operation(summary = "Создать пассажира")
    @PostMapping
    public Passenger create(
            @RequestBody
            @Valid
            @Parameter(description = "Данные для создания пассажира")
            CreatePassengerRequest request
    ) {
        return service.create(request);
    }

    @Operation(summary = "Получить пассажира по ID")
    @GetMapping("/{id}")
    public Passenger get(
            @Parameter(description = "ID пассажира", example = "1")
            @PathVariable Long id
    ) {
        return service.get(id);
    }
}
