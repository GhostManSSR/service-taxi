package trip_api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value; // ✅ Добавлен импорт
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import trip_api.dto.LatLon;
import trip_api.dto.NominatimResult;
import trip_api.dto.OSRMResponse;
import trip_api.entity.DistanceResult;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

@Service
@Slf4j
public class FreeDistanceService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${api.locationiq.key}")
    private String apiKey;

    private static final String GEO_CACHE = "geo:";
    private static final String BASE_URL = "https://us1.locationiq.com/v1";

    public FreeDistanceService(RestTemplate restTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    public LatLon geocode(String address) {
        String key = GEO_CACHE + address.toLowerCase().trim();
        LatLon cached = (LatLon) redisTemplate.opsForValue().get(key);
        if (cached != null) return cached;

        String url = UriComponentsBuilder.fromUriString(BASE_URL + "/search.php")
                .queryParam("key", apiKey)
                .queryParam("q", address) // Spring закодирует этот параметр сам
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .queryParam("countrycodes", "ru")
                .build()
                .toUriString(); // .toUriString() тут лучше, чем .toUri()

        log.info("Final URL: {}", url); // Сравните этот вывод с тем, что вы писали в curl

        try {
            NominatimResult[] results = restTemplate.getForObject(url, NominatimResult[].class);

            if (results != null && results.length > 0) {
                NominatimResult r = results[0];
                LatLon coords = new LatLon(Double.parseDouble(r.lat), Double.parseDouble(r.lon));
                redisTemplate.opsForValue().set(key, coords, Duration.ofDays(7));
                return coords;
            }
        } catch (Exception e) {
            log.error("API error: {}", e.getMessage());
        }
        return fallbackCoords(address);
    }

    public DistanceResult getDistance(String origin, String destination) {
        LatLon o1 = geocode(origin);
        LatLon o2 = geocode(destination);
        return getDistance(o1.getLat(), o1.getLon(), o2.getLat(), o2.getLon());
    }

    public DistanceResult getDistance(double lat1, double lon1, double lat2, double lon2) {
        String url = String.format(Locale.US,
                "%s/directions/driving/%f,%f;%f,%f?key=%s&overview=false",
                BASE_URL, lon1, lat1, lon2, lat2, apiKey
        );

        try {
            OSRMResponse response = restTemplate.getForObject(url, OSRMResponse.class);
            if (response != null && response.routes != null && !response.routes.isEmpty()) {
                var route = response.routes.get(0);
                return new DistanceResult(route.distance / 1000.0, route.duration / 60.0);
            }
        } catch (Exception e) {
            log.error("LocationIQ Routing failed: {}", e.getMessage());
        }
        return new DistanceResult(10.0, 15.0);
    }

    private LatLon fallbackCoords(String address) {
        log.warn("Using fallback coordinates for: {}", address);
        String a = address.toLowerCase();
        if (a.contains("шереметьево")) return new LatLon(55.9728, 37.4124);
        if (a.contains("тверская")) return new LatLon(55.7597, 37.6090);
        return new LatLon(55.7558, 37.6173);
    }
}