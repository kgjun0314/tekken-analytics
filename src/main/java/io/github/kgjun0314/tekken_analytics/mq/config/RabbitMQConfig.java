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

    public static final String PERSIST_QUEUE = "replay.persist.queue";
    public static final String CHARACTER_QUEUE = "replay.character.queue";

    public static final String PERSIST_ROUTING_KEY = "replay.persist";
    public static final String CHARACTER_ROUTING_KEY = "replay.character";

    @Bean
    DirectExchange replayExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    Queue replayQueue() {
        return new Queue(PERSIST_QUEUE, true);
    }

    @Bean
    Queue characterQueue() {return new Queue(CHARACTER_QUEUE, true);}

    @Bean
    Binding replayBinding() {
        return BindingBuilder.bind(replayQueue())
                .to(replayExchange())
                .with(PERSIST_ROUTING_KEY);
    }

    @Bean
    Binding characterBinding() {
        return BindingBuilder.bind(characterQueue())
                .to(replayExchange())
                .with(CHARACTER_ROUTING_KEY);
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
