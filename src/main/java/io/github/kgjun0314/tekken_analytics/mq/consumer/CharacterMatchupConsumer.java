package io.github.kgjun0314.tekken_analytics.mq.consumer;

import io.github.kgjun0314.tekken_analytics.character.service.CharacterMatchupService;
import io.github.kgjun0314.tekken_analytics.mq.config.RabbitMQConfig;
import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CharacterMatchupConsumer {

    private final CharacterMatchupService characterMatchupService;

    @RabbitListener(queues = RabbitMQConfig.CHARACTER_MATCHUP_QUEUE)
    public void consume(Replay replay) {

        log.debug(
                "Character matchup event received. battleId={}",
                replay.battleId()
        );

        characterMatchupService.update(replay);
    }
}
