package io.github.kgjun0314.tekken_analytics.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "replay.exchange";
    public static final String QUEUE = "replay.persist.queue";
    public static final String ROUTING_KEY = "replay.persist";

    @Bean
    DirectExchange replayExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    Queue replayQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    Binding replayBinding() {
        return BindingBuilder.bind(replayQueue())
                .to(replayExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    JacksonJsonMessageConverter jackson2JsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter converter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
