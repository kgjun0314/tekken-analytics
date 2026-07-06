package io.github.kgjun0314.tekken_analytics.character.repository;

public interface CharacterStatsRepositoryCustom {
    void upsert(
            Integer characterId,
            long matches,
            long wins
    );
}
