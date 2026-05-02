package user_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import user_api.dto.CreateDriverRequest;
import user_api.dto.DriverResponse;
import user_api.entity.DriverStatus;
import user_api.mapper.DriverMapper;
import user_api.service.DriverService;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
@Tag(name = "Drivers", description = "Управление водителями")
public class DriverController {

    private final DriverService service;
    private final DriverMapper mapper;

    @Operation(summary = "Создать водителя")
    @PostMapping
    public DriverResponse create(
            @RequestBody
            @Parameter(description = "Данные для создания водителя")
            CreateDriverRequest request
    ) {
        return mapper.toDto(service.create(request));
    }

    @Operation(summary = "Получить водителя по ID")
    @GetMapping("/{id}")
    public DriverResponse get(
            @Parameter(description = "ID водителя", example = "1")
            @PathVariable Long id
    ) {
        return mapper.toDto(service.get(id));
    }

    @Operation(summary = "Обновить статус водителя")
    @PatchMapping("/{id}/status")
    public void updateStatus(
            @Parameter(description = "ID водителя", example = "1")
            @PathVariable Long id,

            @Parameter(
                    description = "Новый статус водителя",
                    example = "AVAILABLE"
            )
            @RequestParam DriverStatus status
    ) {
        service.updateStatus(id, status);
    }

    @Operation(summary = "Назначить свободного водителя (используется Trip Service)")
    @PostMapping("/assign")
    public DriverResponse assignDriver() {
        return mapper.toDto(service.assignDriver());
    }
}
