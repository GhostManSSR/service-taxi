package trip_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import trip_api.dto.CreateTripRequest;
import trip_api.dto.TripResponse;
import trip_api.entity.TripStatus;
import trip_api.mapper.TripMapper;
import trip_api.service.TripService;

import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
@Tag(name = "Trips", description = "Управление поездками")
@Slf4j
public class TripController {

    private final TripService service;
    private final TripMapper mapper;

    @PostMapping
    @Operation(summary = "Создать поездку")
    public TripResponse create(@RequestBody CreateTripRequest request) {
        log.info("POST /trips - create trip request: {}", request);

        var result = mapper.toDto(service.create(request));

        log.info("Trip created successfully: {}", result);
        return result;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить поездку по id")
    public TripResponse get(@PathVariable Long id) {
        log.info("GET /trips/{} - fetch trip", id);

        var result = mapper.toDto(service.get(id));

        log.info("Trip fetched: {}", result);
        return result;
    }

    @GetMapping
    @Operation(summary = "Получить поездки пассажира")
    public List<TripResponse> getByPassenger(@RequestParam Long passengerId) {
        log.info("GET /trips?passengerId={} - fetch trips", passengerId);

        var result = service.getByPassenger(passengerId)
                .stream()
                .map(mapper::toDto)
                .toList();

        log.info("Found {} trips for passengerId={}", result.size(), passengerId);
        return result;
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить статус поездки")
    public TripResponse updateStatus(
            @PathVariable Long id,
            @RequestParam TripStatus status
    ) {
        log.info("PATCH /trips/{} - update status to {}", id, status);

        var result = mapper.toDto(service.updateStatus(id, status));

        log.info("Trip updated: id={}, new status={}", id, status);
        return result;
    }
}
