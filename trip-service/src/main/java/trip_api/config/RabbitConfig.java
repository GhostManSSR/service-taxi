package trip_api.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ===== EXCHANGES =====
    public static final String TRIP_EXCHANGE = "trip.exchange";
    public static final String USER_EXCHANGE = "user.exchange";

    // ===== QUEUES =====
    public static final String TRIP_QUEUE = "trip.queue";
    public static final String DRIVER_STATUS_QUEUE = "driver.status.updated.queue";

    // ===== EXCHANGES =====

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new JacksonJsonMessageConverter());
        return template;
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public TopicExchange tripExchange() {
        return new TopicExchange(TRIP_EXCHANGE);
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    // ===== QUEUES =====

    @Bean
    public Queue tripQueue() {
        return new Queue(TRIP_QUEUE, true);
    }

    @Bean
    public Queue driverStatusQueue() {
        return new Queue(DRIVER_STATUS_QUEUE, true);
    }

    // ===== BINDINGS =====

    // события трипов
    @Bean
    public Binding tripBinding(Queue tripQueue, TopicExchange tripExchange) {
        return BindingBuilder
                .bind(tripQueue)
                .to(tripExchange)
                .with("trip.*");
    }

    // 🔥 слушаем подтверждение от user-service
    @Bean
    public Binding driverStatusBinding(Queue driverStatusQueue, TopicExchange userExchange) {
        return BindingBuilder
                .bind(driverStatusQueue)
                .to(userExchange)
                .with("driver.status.updated");
    }
}
