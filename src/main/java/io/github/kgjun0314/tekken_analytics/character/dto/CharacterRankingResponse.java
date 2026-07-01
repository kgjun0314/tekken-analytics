package io.github.kgjun0314.tekken_analytics.character.dto;

public record CharacterRankingResponse(
        int rank,
        String character,
        long matches,
        long wins,
        long losses,
        double winRate
) {
}
