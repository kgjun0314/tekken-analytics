package io.github.kgjun0314.tekken_analytics.character.repository;

import io.github.kgjun0314.tekken_analytics.character.model.CharacterStatUpdate;

import java.util.List;

public interface CharacterStatsRepositoryCustom {
    void upsert(
            Integer characterId,
            long matches,
            long wins
    );

    void upsertAll(
            List<CharacterStatUpdate> updates
    );
}
