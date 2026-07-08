package io.github.kgjun0314.tekken_analytics.infrastructure.rabbitmq.producer;

import io.github.kgjun0314.tekken_analytics.infrastructure.rabbitmq.config.RabbitMQConfig;
import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplayProducer {
    private final RabbitTemplate rabbitTemplate;

    public void publish(Replay replay) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.PERSIST_ROUTING_KEY,
                replay
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.CHARACTER_STATS_ROUTING_KEY,
                replay
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.CHARACTER_MATCHUP_ROUTING_KEY,
                replay
        );
    }
}
