package io.github.kgjun0314.tekken_analytics.infrastructure.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "replay.exchange";

    public static final String PERSIST_QUEUE = "replay.persist.queue";
    public static final String CHARACTER_STATS_QUEUE = "replay.character-stats.queue";
    public static final String CHARACTER_MATCHUP_QUEUE = "replay.character-matchup.queue";

    public static final String PERSIST_ROUTING_KEY = "replay.persist";
    public static final String CHARACTER_STATS_ROUTING_KEY = "replay.character.stats";
    public static final String CHARACTER_MATCHUP_ROUTING_KEY = "replay.character.matchup";

    @Bean
    public DirectExchange replayExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue persistQueue() {
        return new Queue(PERSIST_QUEUE, true);
    }

    @Bean
    public Queue characterStatsQueue() {
        return new Queue(CHARACTER_STATS_QUEUE, true);
    }

    @Bean
    public Queue characterMatchupQueue() {
        return new Queue(CHARACTER_MATCHUP_QUEUE, true);
    }

    @Bean
    public Binding persistBinding() {
        return BindingBuilder.bind(persistQueue())
                .to(replayExchange())
                .with(PERSIST_ROUTING_KEY);
    }

    @Bean
    public Binding characterStatsBinding() {
        return BindingBuilder.bind(characterStatsQueue())
                .to(replayExchange())
                .with(CHARACTER_STATS_ROUTING_KEY);
    }

    @Bean
    public Binding characterMatchupBinding() {
        return BindingBuilder.bind(characterMatchupQueue())
                .to(replayExchange())
                .with(CHARACTER_MATCHUP_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter converter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter converter
    ) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);

        factory.setMessageConverter(converter);

        factory.setConcurrentConsumers(4);
        factory.setMaxConcurrentConsumers(8);

        factory.setPrefetchCount(100);

        return factory;
    }
}