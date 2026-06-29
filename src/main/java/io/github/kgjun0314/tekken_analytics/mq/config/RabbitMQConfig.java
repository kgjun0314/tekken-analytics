package io.github.kgjun0314.tekken_analytics.mq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String REPLAY_QUEUE = "replay.queue";

    @Bean
    public Queue replayQueue() {
        return new Queue(REPLAY_QUEUE);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter converter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }
}
