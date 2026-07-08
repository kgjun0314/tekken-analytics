package io.github.kgjun0314.tekken_analytics.replay.repository;

import io.github.kgjun0314.tekken_analytics.replay.repository.dto.MatchInsertResult;
import io.github.kgjun0314.tekken_analytics.replay.entity.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public Map<String, Long> findOrInsertAll(
            List<Match> matches
    ) {

        if (matches.isEmpty()) {
            return Collections.emptyMap();
        }

        StringBuilder insertSql = new StringBuilder("""
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
            """);

        List<Object> insertParams = new ArrayList<>();

        for (int i = 0; i < matches.size(); i++) {

            if (i > 0) {
                insertSql.append(", ");
            }

            insertSql.append("""
                (?, ?, ?, ?, ?, now(), now())
                """);

            Match match = matches.get(i);

            insertParams.add(match.getBattleId());
            insertParams.add(Timestamp.from(match.getBattleAt()));
            insertParams.add(match.getBattleType());
            insertParams.add(match.getGameVersion());
            insertParams.add(match.getStageId());
        }

        insertSql.append("""
            ON CONFLICT (battle_id)
            DO NOTHING
            """);

        jdbcClient.sql(insertSql.toString())
                .params(insertParams)
                .update();

        StringBuilder selectSql = new StringBuilder("""
            SELECT
                battle_id,
                id
            FROM matches
            WHERE battle_id IN (
            """);

        List<Object> selectParams = new ArrayList<>();

        for (int i = 0; i < matches.size(); i++) {

            if (i > 0) {
                selectSql.append(", ");
            }

            selectSql.append("?");

            selectParams.add(matches.get(i).getBattleId());
        }

        selectSql.append(")");

        List<MatchInsertResult> results =
                jdbcClient.sql(selectSql.toString())
                        .params(selectParams)
                        .query((rs, rowNum) ->
                                new MatchInsertResult(
                                        rs.getString("battle_id"),
                                        rs.getLong("id")
                                ))
                        .list();

        return results.stream()
                .collect(Collectors.toMap(
                        MatchInsertResult::battleId,
                        MatchInsertResult::id
                ));
    }

    @Override
    public Map<String, Long> insertIfAbsentAll(
            List<Match> matches
    ) {

        if (matches.isEmpty()) {
            return Collections.emptyMap();
        }

        StringBuilder sql = new StringBuilder("""
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
            """);

        List<Object> params = new ArrayList<>();

        for (int i = 0; i < matches.size(); i++) {

            if (i > 0) {
                sql.append(", ");
            }

            sql.append("""
                (?, ?, ?, ?, ?, now(), now())
                """);

            Match match = matches.get(i);

            params.add(match.getBattleId());
            params.add(Timestamp.from(match.getBattleAt()));
            params.add(match.getBattleType());
            params.add(match.getGameVersion());
            params.add(match.getStageId());
        }

        sql.append("""
            ON CONFLICT (battle_id)
            DO NOTHING
            RETURNING
                battle_id,
                id
            """);

        List<MatchInsertResult> results =
                jdbcClient.sql(sql.toString())
                        .params(params)
                        .query((rs, rowNum) ->
                                new MatchInsertResult(
                                        rs.getString("battle_id"),
                                        rs.getLong("id")
                                ))
                        .list();

        return results.stream()
                .collect(Collectors.toMap(
                        MatchInsertResult::battleId,
                        MatchInsertResult::id
                ));
    }
}