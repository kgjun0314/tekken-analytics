package io.github.kgjun0314.tekken_analytics.character.repository;

import io.github.kgjun0314.tekken_analytics.character.entity.CharacterStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CharacterStatsRepository extends JpaRepository<CharacterStats, Integer> {
    List<CharacterStats> findAllByOrderByMatchesDesc();
    @Modifying
    @Query("""
    update CharacterStats cs
    set
        cs.matches = cs.matches + 1,
        cs.wins = cs.wins + :win
    where
        cs.characterId = :characterId
    """)
    int increase(
            @Param("characterId") Integer characterId,
            @Param("win") Long win
    );
}
