package api_gateway.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class GatewayConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
    }

    @Bean
    public ReactorClientHttpConnector reactorClientHttpConnector(HttpClient httpClient) {
        return new ReactorClientHttpConnector(httpClient);
    }
}

