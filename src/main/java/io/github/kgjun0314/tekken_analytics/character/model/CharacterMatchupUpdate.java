package io.github.kgjun0314.tekken_analytics.character.model;

public record CharacterMatchupUpdate(
        Integer characterId,
        Integer opponentCharacterId,
        long matches,
        long wins
) {
}
