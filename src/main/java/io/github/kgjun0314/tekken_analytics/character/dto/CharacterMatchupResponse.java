package io.github.kgjun0314.tekken_analytics.character.dto;

public record CharacterMatchupResponse(
        String opponent,
        long matches,
        long wins,
        long losses,
        double winRate
) {
}