package io.github.kgjun0314.tekken_analytics.character.matchup;

public record CharacterMatchupKey(
        Integer characterId,
        Integer opponentCharacterId
) {
}
