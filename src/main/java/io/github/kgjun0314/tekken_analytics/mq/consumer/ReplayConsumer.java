package io.github.kgjun0314.tekken_analytics.mq.consumer;

import io.github.kgjun0314.tekken_analytics.benchmark.ReplayBenchmarkService;
import io.github.kgjun0314.tekken_analytics.mq.config.RabbitMQConfig;
import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import io.github.kgjun0314.tekken_analytics.replay.service.ReplayPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReplayConsumer {
    private final ReplayPersistenceService persistenceService;
    private final ReplayBenchmarkService benchmarkService;

    @RabbitListener(queues = RabbitMQConfig.PERSIST_QUEUE)
    public void consume(Replay replay) {
        persistenceService.save(replay);
        benchmarkService.complete(replay.battleId());
    }
}
