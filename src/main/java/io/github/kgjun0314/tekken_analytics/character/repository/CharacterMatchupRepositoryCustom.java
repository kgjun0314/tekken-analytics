package io.github.kgjun0314.tekken_analytics.character.repository;

import io.github.kgjun0314.tekken_analytics.character.model.CharacterMatchupUpdate;

import java.util.List;

public interface CharacterMatchupRepositoryCustom {
    void upsert(
            Integer characterId,
            Integer opponentCharacterId,
            long matches,
            long wins
    );

    void upsertAll(
            List<CharacterMatchupUpdate> updates
    );
}
