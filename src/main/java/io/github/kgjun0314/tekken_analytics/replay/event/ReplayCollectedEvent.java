package io.github.kgjun0314.tekken_analytics.replay.event;

import io.github.kgjun0314.tekken_analytics.replay.dto.ReplayPlayer;

public record ReplayCollectedEvent(

        String battleId,

        long battleAt,

        ReplayPlayer player1,

        ReplayPlayer player2,

        int stageId,

        int battleType,

        int gameVersion

) {
}