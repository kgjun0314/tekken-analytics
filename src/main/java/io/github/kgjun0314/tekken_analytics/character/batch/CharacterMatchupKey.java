package io.github.kgjun0314.tekken_analytics.character.batch;

public record CharacterMatchupKey(
        Integer characterId,
        Integer opponentCharacterId
) {
}
