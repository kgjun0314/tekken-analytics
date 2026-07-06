package io.github.kgjun0314.tekken_analytics.character.repository;

public interface CharacterMatchupRepositoryCustom {
    void upsert(
            Integer characterId,
            Integer opponentCharacterId,
            long matches,
            long wins
    );
}
