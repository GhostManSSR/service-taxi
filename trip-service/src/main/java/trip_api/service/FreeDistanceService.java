package trip_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import trip_api.dto.LatLon;
import trip_api.dto.NominatimResult;
import trip_api.dto.OSRMResponse;
import trip_api.entity.DistanceResult;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeDistanceService {

    private final RestTemplate restTemplate;

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String GEO_CACHE = "geo:";

    public LatLon geocode(String address) {
        try {
            String key = GEO_CACHE + address.toLowerCase().trim();

            // 🔥 1. Проверяем кэш
            LatLon cached = (LatLon) redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.debug("📦 GEO cache hit: {}", address);
                return cached;
            }

            String encoded = URLEncoder.encode(address.trim(), StandardCharsets.UTF_8);

            String url = "https://nominatim.openstreetmap.org/search" +
                    "?q=" + encoded +
                    "&format=json&limit=1&countrycodes=ru&accept-language=ru";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "TaxiApp/1.0 (artem@example.com)");
            headers.set("Accept", "application/json");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<NominatimResult[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, NominatimResult[].class);

            NominatimResult[] results = response.getBody();

            if (results != null && results.length > 0) {
                NominatimResult r = results[0];

                LatLon coords = new LatLon(
                        Double.parseDouble(r.lat),
                        Double.parseDouble(r.lon)
                );

                log.info("✅ '{}' → [{},{}]", address, coords.getLat(), coords.getLon());

                // 🔥 2. Кладём в кэш
                redisTemplate.opsForValue().set(key, coords, Duration.ofHours(24));

                return coords;
            }

            return fallbackCoords(address);

        } catch (Exception e) {
            log.error("❌ Nominatim '{}': {}", address, e.getMessage());
            return fallbackCoords(address);
        }
    }

    private LatLon fallbackCoords(String address) {
        log.warn("🔄 Fallback '{}'", address);

        String a = address.toLowerCase();

        if (a.contains("шереметьево") || a.contains("sheremetyevo")) {
            return new LatLon(55.9728, 37.4124);
        }

        if (a.contains("тверская")) {
            return new LatLon(55.7597, 37.6090);
        }

        if (a.contains("спб") || a.contains("петербург")) {
            return new LatLon(59.9343, 30.3351);
        }

        return new LatLon(55.7558, 37.6173); // Москва
    }

    public DistanceResult getDistance(double lat1, double lon1, double lat2, double lon2) {
        try {
            String url = String.format(Locale.US,
                    "http://router.project-osrm.org/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=false",
                    lon1, lat1, lon2, lat2
            );

            log.info("OSRM: {}", url);

            OSRMResponse response = restTemplate.getForObject(url, OSRMResponse.class);

            if (response != null && response.routes != null && !response.routes.isEmpty()) {
                var route = response.routes.get(0);

                double km = route.distance / 1000.0;
                double min = route.duration / 60.0;

                log.info(String.format(Locale.US, "%.1fkm → %.0fmin", km, min));

                return new DistanceResult(km, min);
            }

        } catch (Exception e) {
            log.error("❌ OSRM: {}", e.getMessage());
        }

        return new DistanceResult(10.0, 15.0);
    }

    public DistanceResult getDistance(String origin, String destination) {
        LatLon o1 = geocode(origin);
        LatLon o2 = geocode(destination);
        return getDistance(o1.getLat(), o1.getLon(), o2.getLat(), o2.getLon());
    }
}
