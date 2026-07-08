package io.github.kgjun0314.tekken_analytics.infrastructure.rabbitmq.consumer;

import io.github.kgjun0314.tekken_analytics.infrastructure.benchmark.ReplayBenchmarkService;
import io.github.kgjun0314.tekken_analytics.infrastructure.rabbitmq.config.RabbitMQConfig;
import io.github.kgjun0314.tekken_analytics.replay.batch.ReplayPersistenceAggregator;
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
    private final ReplayPersistenceAggregator aggregator;

    @RabbitListener(
            queues = RabbitMQConfig.PERSIST_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consume(Replay replay) {
//        persistenceService.save(replay);
        aggregator.accumulate(replay);
        benchmarkService.complete(replay.battleId());
    }
}
