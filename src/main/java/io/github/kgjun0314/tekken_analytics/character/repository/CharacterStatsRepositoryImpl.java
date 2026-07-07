package io.github.kgjun0314.tekken_analytics.character.repository;

import io.github.kgjun0314.tekken_analytics.benchmark.RepositoryMetrics;
import io.github.kgjun0314.tekken_analytics.character.model.CharacterStatUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void upsertAll(
            List<CharacterStatUpdate> updates
    ) {

        if (updates.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("""
                INSERT INTO character_stats
                (
                    character_id,
                    matches,
                    wins
                )
                VALUES
                """);

        List<Object> params = new ArrayList<>();

        for (int i = 0; i < updates.size(); i++) {

            if (i > 0) {
                sql.append(", ");
            }

            sql.append("(?, ?, ?)");

            CharacterStatUpdate update = updates.get(i);

            params.add(update.characterId());
            params.add(update.matches());
            params.add(update.wins());
        }

        sql.append("""
                ON CONFLICT (character_id)
                DO UPDATE SET
                    matches = character_stats.matches + EXCLUDED.matches,
                    wins = character_stats.wins + EXCLUDED.wins
                """);

        jdbcClient.sql(sql.toString())
                .params(params)
                .update();
    }
}
