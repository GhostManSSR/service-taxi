package user_api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    @Bean
    public Queue driverQueue() {
        return new Queue("driver.status.queue", true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("user.exchange");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(driverQueue())
                .to(exchange())
                .with("driver.status.*");
    }
}

