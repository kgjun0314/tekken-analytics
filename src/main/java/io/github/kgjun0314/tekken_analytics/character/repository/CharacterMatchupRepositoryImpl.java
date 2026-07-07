package io.github.kgjun0314.tekken_analytics.character.repository;

import io.github.kgjun0314.tekken_analytics.character.model.CharacterMatchupUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CharacterMatchupRepositoryImpl
        implements CharacterMatchupRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public void upsert(
            Integer characterId,
            Integer opponentCharacterId,
            long matches,
            long wins
    ) {

        jdbcClient.sql("""
                INSERT INTO character_matchups
                    (
                        character_id,
                        opponent_character_id,
                        matches,
                        wins,
                        created_at,
                        updated_at
                    )
                VALUES
                    (?, ?, ?, ?, now(), now())
                ON CONFLICT
                    (
                        character_id,
                        opponent_character_id
                    )
                DO UPDATE SET
                    matches =
                        character_matchups.matches
                        + EXCLUDED.matches,
                    wins =
                        character_matchups.wins
                        + EXCLUDED.wins,
                    updated_at = now()
                """)
                .params(
                        characterId,
                        opponentCharacterId,
                        matches,
                        wins
                )
                .update();
    }


    @Override
    public void upsertAll(
            List<CharacterMatchupUpdate> updates
    ) {

        if (updates.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("""
                INSERT INTO character_matchups
                (
                    character_id,
                    opponent_character_id,
                    matches,
                    wins,
                    created_at,
                    updated_at
                )
                VALUES
                """);

        List<Object> params = new ArrayList<>();

        for (int i = 0; i < updates.size(); i++) {

            if (i > 0) {
                sql.append(", ");
            }

            sql.append("""
                    (?, ?, ?, ?, now(), now())
                    """);

            CharacterMatchupUpdate update =
                    updates.get(i);

            params.add(update.characterId());
            params.add(update.opponentCharacterId());
            params.add(update.matches());
            params.add(update.wins());
        }

        sql.append("""
                ON CONFLICT
                (
                    character_id,
                    opponent_character_id
                )
                DO UPDATE SET
                    matches =
                        character_matchups.matches
                        + EXCLUDED.matches,
                    wins =
                        character_matchups.wins
                        + EXCLUDED.wins,
                    updated_at = now()
                """);

        jdbcClient.sql(sql.toString())
                .params(params)
                .update();
    }
}