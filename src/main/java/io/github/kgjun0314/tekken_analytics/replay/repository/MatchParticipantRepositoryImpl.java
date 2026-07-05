package io.github.kgjun0314.tekken_analytics.replay.repository;

import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MatchParticipantRepositoryImpl
        implements MatchParticipantRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public void insert(
            Long matchId,
            Long player1Id,
            ReplayPlayer player1,
            Long player2Id,
            ReplayPlayer player2
    ) {

        jdbcClient.sql("""
                INSERT INTO match_participants
                (
                    match_id,
                    player_id,
                    character_id,
                    rank,
                    power,
                    rounds,
                    winner,
                    created_at,
                    updated_at
                )
                VALUES
                (?, ?, ?, ?, ?, ?, ?, now(), now()),
                (?, ?, ?, ?, ?, ?, ?, now(), now())
                """)
                .params(
                        matchId,
                        player1Id,
                        player1.characterId(),
                        player1.rank(),
                        player1.power(),
                        player1.rounds(),
                        player1.winner(),

                        matchId,
                        player2Id,
                        player2.characterId(),
                        player2.rank(),
                        player2.power(),
                        player2.rounds(),
                        player2.winner()
                )
                .update();
    }
}