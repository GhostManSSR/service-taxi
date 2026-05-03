package trip_api.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import user_api.dto.DriverResponse;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    private final String USER_SERVICE = "http://user-service:8081";

    public void checkPassenger(Long id) {
        restTemplate.getForObject(
                USER_SERVICE + "/passengers/" + id,
                Object.class
        );
    }

    public DriverResponse assignDriver() {
        try {
            return restTemplate.postForObject(
                    USER_SERVICE + "/drivers/assign",
                    null,
                    DriverResponse.class
            );
        } catch (HttpClientErrorException | HttpServerErrorException ex) {

            if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No available drivers at the moment"
                );
            }

            throw ex;
        }
    }
}

