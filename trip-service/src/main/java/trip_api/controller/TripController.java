package trip_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import trip_api.dto.CreateTripRequest;
import trip_api.dto.RateTripRequest;
import trip_api.dto.TripResponse;
import trip_api.dto.TripStatsResponse;
import trip_api.entity.Trip;
import trip_api.entity.TripStatus;
import trip_api.mapper.TripMapper;
import trip_api.service.TripService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    public TripResponse create(
            @RequestBody @Valid
            @Parameter(description = "Данные поездки")
            CreateTripRequest request
    ) {
        log.info("POST /trips - passengerId: {}", request.getPassengerId());

        TripResponse result = mapper.toDto(service.create(request));
        log.info("Trip created: id={}, driverId={}",
                result.getId(), result.getDriverId());

        return result;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить поездку по ID")
    public TripResponse get(
            @Parameter(description = "ID поездки", example = "1")
            @PathVariable Long id
    ) {
        log.debug("GET /trips/{} ", id);
        TripResponse result = mapper.toDto(service.get(id));
        return result;
    }

    @GetMapping
    @Operation(summary = "Получить все поездки")
    public List<TripResponse> list(){
        return mapper.toDto(service.getAll());
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Статус сервиса")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "trip-service",
                "timestamp", java.time.Instant.now().toString()
        );
    }

    @GetMapping("/passenger")
    @Operation(summary = "Поездки пассажира")
    public List<TripResponse> getByPassenger(
            @Parameter(description = "ID пассажира", example = "1")
            @RequestParam Long passengerId
    ) {
        log.debug("GET /trips?passengerId={}", passengerId);

        List<TripResponse> result = service.getByPassenger(passengerId)
                .stream()
                .map(mapper::toDto)
                .toList();

        log.info("Found {} trips for passenger {}", result.size(), passengerId);
        return result;
    }

    @PatchMapping("/{id}/rate")
    public Trip rateTrip(
            @PathVariable Long id,
            @RequestBody @Valid RateTripRequest request
    ) {
        return service.rateTrip(id, request);
    }

    @GetMapping("/stats/daily")
    @Operation(summary = "Статистика поездок за день")
    public TripStatsResponse getDailyStats(
            @RequestParam String date
    ) {
        log.info("GET /trips/stats/daily?date={}", date);

        return service.getDailyStats(LocalDate.parse(date));
    }

    @PatchMapping("/{id}/status/{status}")
    @Operation(summary = "Обновить статус поездки")
    public TripResponse updateStatus(
            @Parameter(description = "ID поездки", example = "1")
            @PathVariable Long id,

            @Parameter(description = "Новый статус", example = "COMPLETED")
            @PathVariable TripStatus status
    ) {
        log.info("PATCH /trips/{}/status/{} ", id, status);

        TripResponse result = mapper.toDto(service.updateStatus(id, status));
        log.info("Trip {} status → {}", id, status);

        return result;
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление поездки")
    public void delete(
            @Parameter(description = "ID поездки", example = "1")
            @PathVariable Long id
    ){
        service.delete(id);
    }
}