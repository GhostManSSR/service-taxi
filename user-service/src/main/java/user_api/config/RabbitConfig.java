package user_api.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ===== EXCHANGE =====
    public static final String USER_EXCHANGE = "user.exchange";

    // ===== QUEUE =====
    public static final String DRIVER_STATUS_QUEUE = "driver.status.queue";

    // ===== EXCHANGE =====
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new JacksonJsonMessageConverter());

        return factory;
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    // ===== QUEUE =====

    @Bean
    public Queue driverStatusQueue() {
        return new Queue(DRIVER_STATUS_QUEUE, true);
    }

    // ===== BINDING =====

    @Bean
    public Binding driverStatusBinding(Queue driverStatusQueue, TopicExchange userExchange) {
        return BindingBuilder
                .bind(driverStatusQueue)
                .to(userExchange)
                .with("driver.status.update");
    }
}
