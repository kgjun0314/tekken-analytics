package io.github.kgjun0314.tekken_analytics.mq.producer;

import io.github.kgjun0314.tekken_analytics.mq.config.RabbitMQConfig;
import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplayProducer {
    private final RabbitTemplate rabbitTemplate;

    public void publish(WankReplayResponse replay) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REPLAY_QUEUE,
                replay
        );
    }
}
