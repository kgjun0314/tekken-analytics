package io.github.kgjun0314.tekken_analytics.mq.consumer;

import io.github.kgjun0314.tekken_analytics.mq.config.RabbitMQConfig;
import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import io.github.kgjun0314.tekken_analytics.replay.service.ReplayPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReplayConsumer {
    private final ReplayPersistenceService persistenceService;

    @RabbitListener(queues = RabbitMQConfig.REPLAY_QUEUE)
    public void consume(WankReplayResponse replay) {
        persistenceService.save(replay);
    }
}
