package io.github.kgjun0314.tekken_analytics.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String REPLAY_EXCHANGE = "replay.exchange";
    public static final String REPLAY_QUEUE = "replay.persist.queue";
    public static final String REPLAY_ROUTING_KEY = "replay.persist";

    @Bean
    DirectExchange replayExchange() {
        return new DirectExchange(REPLAY_EXCHANGE);
    }

    @Bean
    Queue replayQueue() {
        return new Queue(REPLAY_QUEUE);
    }

    @Bean
    Binding replayBinding() {
        return BindingBuilder
                .bind(replayQueue())
                .to(replayExchange())
                .with(REPLAY_ROUTING_KEY);
    }
}
