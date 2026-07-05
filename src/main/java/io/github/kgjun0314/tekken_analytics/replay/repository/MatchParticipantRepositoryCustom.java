package io.github.kgjun0314.tekken_analytics.replay.repository;

import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;

public interface MatchParticipantRepositoryCustom {
    void insert(
            Long matchId,
            Long player1Id,
            ReplayPlayer player1,
            Long player2Id,
            ReplayPlayer player2
    );
}
