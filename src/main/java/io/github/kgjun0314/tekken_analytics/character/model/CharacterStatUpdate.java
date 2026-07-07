package io.github.kgjun0314.tekken_analytics.character.model;

public record CharacterStatUpdate(
        Integer characterId,
        long matches,
        long wins
) {
}
