package io.github.kgjun0314.tekken_analytics.replay.repository;

import io.github.kgjun0314.tekken_analytics.replay.entity.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MatchRepositoryImpl
        implements MatchRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public Optional<Long> insertIfAbsent(Match match) {

        return jdbcClient.sql("""
                INSERT INTO matches
                    (
                        battle_id,
                        battle_at,
                        battle_type,
                        game_version,
                        stage_id,
                        created_at,
                        updated_at
                    )
                VALUES
                    (?, ?, ?, ?, ?, now(), now())
                ON CONFLICT (battle_id)
                DO NOTHING
                RETURNING id
                """)
                .params(
                        match.getBattleId(),
                        Timestamp.from(match.getBattleAt()),
                        match.getBattleType(),
                        match.getGameVersion(),
                        match.getStageId()
                )
                .query(Long.class)
                .optional();
    }
}