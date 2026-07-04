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
    @Query(
        value = """
                INSERT INTO character_stats (
                    character_id,
                    matches,
                    wins
                )
                VALUES (
                    :characterId,
                    1,
                    :win
                )
                ON CONFLICT (character_id)
                DO UPDATE SET
                    matches = character_stats.matches + 1,
                    wins = character_stats.wins + EXCLUDED.wins
                """,
        nativeQuery = true
    )
    void upsert(
            @Param("characterId") Integer characterId,
            @Param("win") Long win
    );
}
