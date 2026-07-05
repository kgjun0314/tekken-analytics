package io.github.kgjun0314.tekken_analytics.player.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

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
}