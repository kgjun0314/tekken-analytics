package io.github.kgjun0314.tekken_analytics.character.dto;

public record CharacterStatsResponse(
        Integer characterId,
        Long matches,
        Long wins,
        double winRate
) {
}
