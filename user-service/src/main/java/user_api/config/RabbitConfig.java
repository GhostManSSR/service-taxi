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

    public static final String USER_EXCHANGE = "user.exchange";

    public static final String DRIVER_STATUS_QUEUE = "driver.status.queue";
    public static final String DRIVER_RATING_QUEUE = "driver.rating.queue";

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue driverStatusQueue() {
        return new Queue(DRIVER_STATUS_QUEUE, true);
    }

    @Bean
    public Queue driverRatingQueue() {
        return new Queue(DRIVER_RATING_QUEUE, true);
    }

    @Bean
    public Binding driverStatusBinding(
            Queue driverStatusQueue,
            TopicExchange userExchange
    ) {
        return BindingBuilder
                .bind(driverStatusQueue)
                .to(userExchange)
                .with("driver.status.update");
    }

    @Bean
    public Binding driverRatingBinding(
            Queue driverRatingQueue,
            TopicExchange userExchange
    ) {
        return BindingBuilder
                .bind(driverRatingQueue)
                .to(userExchange)
                .with("driver.rating");
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory
    ) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonMessageConverter());

        return factory;
    }
}
