package user_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import user_api.dto.CreatePassengerRequest;
import user_api.dto.UpdatePassengerRequest;
import user_api.entity.Passenger;
import user_api.service.PassengerService;

import java.util.List;
import java.util.Map;

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

    @Operation(summary = "Получить всех пассажиров")
    @GetMapping
    public List<Passenger> findAll() {
        return service.getAll();
    }

    @Operation(summary = "Получить пассажира по ID")
    @GetMapping("/{id}")
    public Passenger get(
            @Parameter(description = "ID пассажира", example = "1")
            @PathVariable Long id
    ) {
        return service.get(id);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check сервиса", description = "Проверка готовности сервиса")
    public Map<String, String> health() {
        return Map.of("status", "OK", "service", "passenger-service");
    }

    @PatchMapping("/{id}")
    public Passenger update(
            @Parameter(description = "ID пассажира", example = "1")
            @PathVariable Long id,
            UpdatePassengerRequest request
    ){
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "ID пассажира", example = "1")
            @PathVariable Long id
    ){
        service.deletePassenger(id);
    }
}
