package io.github.kgjun0314.tekken_analytics.mq.consumer;

import io.github.kgjun0314.tekken_analytics.benchmark.ReplayBenchmarkService;
import io.github.kgjun0314.tekken_analytics.character.service.CharacterStatsService;
import io.github.kgjun0314.tekken_analytics.mq.config.RabbitMQConfig;
import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterStatsConsumer {
    private final CharacterStatsService service;
    private final ReplayBenchmarkService benchmarkService;

    @RabbitListener(
            queues = RabbitMQConfig.CHARACTER_STATS_QUEUE
    )
    public void consume(Replay replay) {

        service.update(replay.player1());
        service.update(replay.player2());

        benchmarkService.complete(replay.battleId());
    }
}
