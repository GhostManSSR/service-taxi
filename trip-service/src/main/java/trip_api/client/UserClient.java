package trip_api.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import user_api.dto.DriverResponse;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    private final String USER_SERVICE = "http://localhost:8081";

    public void checkPassenger(Long id) {
        restTemplate.getForObject(
                USER_SERVICE + "/passengers/" + id,
                Object.class
        );
    }

    public DriverResponse assignDriver() {
        return restTemplate.postForObject(
                USER_SERVICE + "/drivers/assign",
                null,
                DriverResponse.class
        );
    }
}

