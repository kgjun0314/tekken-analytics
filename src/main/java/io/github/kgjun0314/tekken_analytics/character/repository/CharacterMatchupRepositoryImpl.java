package io.github.kgjun0314.tekken_analytics.character.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CharacterMatchupRepositoryImpl
        implements CharacterMatchupRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public void upsert(
            Integer characterId,
            Integer opponentCharacterId,
            boolean winner
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
                    (?, ?, 1, ?, now(), now())
                ON CONFLICT
                    (character_id, opponent_character_id)
                DO UPDATE SET
                    matches = character_matchups.matches + 1,
                    wins = character_matchups.wins + EXCLUDED.wins,
                    updated_at = now()
                """)
                .params(
                        characterId,
                        opponentCharacterId,
                        winner ? 1 : 0
                )
                .update();
    }
}