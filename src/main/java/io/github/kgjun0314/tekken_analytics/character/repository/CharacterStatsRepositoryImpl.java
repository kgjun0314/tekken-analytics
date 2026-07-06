package io.github.kgjun0314.tekken_analytics.character.repository;

import io.github.kgjun0314.tekken_analytics.benchmark.RepositoryMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CharacterStatsRepositoryImpl
        implements CharacterStatsRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public void upsert(
            Integer characterId,
            long matches,
            long wins
    ) {

        jdbcClient.sql("""
        INSERT INTO character_stats
            (character_id, matches, wins)
        VALUES
            (?, ?, ?)
        ON CONFLICT (character_id)
        DO UPDATE SET
            matches = character_stats.matches + EXCLUDED.matches,
            wins = character_stats.wins + EXCLUDED.wins
        """)
                .params(
                        characterId,
                        matches,
                        wins
                )
                .update();
    }
}
