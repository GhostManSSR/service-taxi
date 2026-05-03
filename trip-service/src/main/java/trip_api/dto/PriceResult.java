package trip_api.dto;

public record PriceResult(
        double price,
        double distanceKm,
        double durationMin
) {}
