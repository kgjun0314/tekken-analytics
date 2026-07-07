package io.github.kgjun0314.tekken_analytics.player.repository;

import io.github.kgjun0314.tekken_analytics.player.dto.PlayerUpsertResult;
import io.github.kgjun0314.tekken_analytics.player.dto.PlayerUpsert;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PlayerRepositoryImpl
        implements PlayerRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public Long upsert(
            Long userId,
            String polarisId,
            String nickname
    ) {

        return jdbcClient.sql("""
                INSERT INTO players
                    (user_id, polaris_id, nickname, created_at, updated_at)
                VALUES
                    (?, ?, ?, now(), now())
                ON CONFLICT (user_id)
                DO UPDATE SET
                    nickname = EXCLUDED.nickname,
                    updated_at = now()
                RETURNING id
                """)
                .params(
                        userId,
                        polarisId,
                        nickname
                )
                .query(Long.class)
                .single();
    }

    @Override
    public Map<Long, Long> upsertAll(
            List<PlayerUpsert> players
    ) {

        if (players.isEmpty()) {
            return Collections.emptyMap();
        }

        StringBuilder sql = new StringBuilder("""
                INSERT INTO players
                (
                    user_id,
                    polaris_id,
                    nickname,
                    created_at,
                    updated_at
                )
                VALUES
                """);

        List<Object> params = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {

            if (i > 0) {
                sql.append(", ");
            }

            sql.append("""
                    (?, ?, ?, now(), now())
                    """);

            PlayerUpsert player = players.get(i);

            params.add(player.userId());
            params.add(player.polarisId());
            params.add(player.nickname());
        }

        sql.append("""
                ON CONFLICT (user_id)
                DO UPDATE SET
                    nickname = EXCLUDED.nickname,
                    updated_at = now()
                RETURNING
                    id,
                    user_id
                """);

        List<PlayerUpsertResult> results =
                jdbcClient.sql(sql.toString())
                        .params(params)
                        .query((rs, rowNum) ->
                                new PlayerUpsertResult(
                                        rs.getLong("id"),
                                        rs.getLong("user_id")
                                ))
                        .list();

        return results.stream()
                .collect(Collectors.toMap(
                        PlayerUpsertResult::userId,
                        PlayerUpsertResult::id
                ));
    }
}