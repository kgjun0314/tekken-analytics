package io.github.kgjun0314.tekken_analytics.replay.repository.dto;

public record MatchParticipantInsert(
        Long matchId,
        Long playerId,
        Integer characterId,
        Integer rank,
        Integer power,
        Integer rounds,
        boolean winner
) {
}
