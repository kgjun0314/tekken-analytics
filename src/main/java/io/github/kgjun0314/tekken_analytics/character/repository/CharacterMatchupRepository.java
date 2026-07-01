package io.github.kgjun0314.tekken_analytics.character.repository;

import io.github.kgjun0314.tekken_analytics.character.entity.CharacterMatchup;
import io.github.kgjun0314.tekken_analytics.character.model.CharacterMatchupSort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<CharacterMatchup> findByCharacterIdAndMatchesGreaterThanEqualOrderByMatchesDesc(
            Integer characterId,
            Long matches
    );

    @Query("""
    select cm
    from CharacterMatchup cm
    where cm.characterId = :characterId
    and (:minMatches is null or cm.matches >= :minMatches)
    order by (cm.wins * 1.0 / cm.matches) desc
    """)
    List<CharacterMatchup> findByWinRate(
            Integer characterId,
            Long minMatches
    );
}
