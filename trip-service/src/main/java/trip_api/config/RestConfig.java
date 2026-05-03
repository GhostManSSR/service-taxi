package trip_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate rt = new RestTemplate();

        rt.setInterceptors(java.util.Collections.singletonList(
                (ClientHttpRequestInterceptor) (request, body, execution) -> {
                    request.getHeaders().set("User-Agent", "TaxiApp/1.0 (artem@example.com)");
                    return execution.execute(request, body);
                }
        ));

        return rt;
    }
}