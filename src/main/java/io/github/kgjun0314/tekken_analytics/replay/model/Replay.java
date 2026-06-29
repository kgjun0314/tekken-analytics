package io.github.kgjun0314.tekken_analytics.replay.model;

import io.github.kgjun0314.tekken_analytics.replay.dto.ReplayPlayer;

import java.time.Instant;

public record Replay(

        String battleId,

        Instant battleAt,

        Integer battleType,

        Integer gameVersion,

        Integer stageId,

        ReplayPlayer player1,

        ReplayPlayer player2

) {
}