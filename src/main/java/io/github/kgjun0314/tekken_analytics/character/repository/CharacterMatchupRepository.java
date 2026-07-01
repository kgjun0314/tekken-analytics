package io.github.kgjun0314.tekken_analytics.character.repository;

import io.github.kgjun0314.tekken_analytics.character.entity.CharacterMatchup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CharacterMatchupRepository extends JpaRepository<CharacterMatchup, Long> {
    Optional<CharacterMatchup> findByCharacterIdAndOpponentCharacterId(
            Integer characterId,
            Integer opponentCharacterId
    );

    List<CharacterMatchup> findByCharacterIdOrderByMatchesDesc(
            Integer characterId
    );
}
