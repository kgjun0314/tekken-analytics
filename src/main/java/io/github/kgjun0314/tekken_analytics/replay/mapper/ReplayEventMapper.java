package io.github.kgjun0314.tekken_analytics.replay.mapper;

import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import io.github.kgjun0314.tekken_analytics.replay.event.ReplayCollectedEvent;
import org.springframework.stereotype.Component;

@Component
public class ReplayEventMapper {

    public ReplayCollectedEvent toEvent(
            WankReplayResponse response
    ) {

        return new ReplayCollectedEvent(

                response.battleId(),

                response.battleAt(),

                response.player1(),

                response.player2(),

                response.stageId(),

                response.battleType(),

                response.gameVersion()

        );

    }

}