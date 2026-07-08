package io.github.kgjun0314.tekken_analytics.replay.mapper;

import io.github.kgjun0314.tekken_analytics.player.repository.dto.PlayerUpsert;
import io.github.kgjun0314.tekken_analytics.replay.repository.dto.MatchParticipantInsert;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import io.github.kgjun0314.tekken_analytics.replay.entity.Match;
import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ReplayMapper {
    public Match toMatch(Replay replay) {
        return Match.builder()
                .battleId(replay.battleId())
                .battleAt(replay.battleAt())
                .battleType(replay.battleType())
                .gameVersion(replay.gameVersion())
                .stageId(replay.stageId())
                .build();
    }

    public Replay toReplay(WankReplayResponse response) {
        return new Replay(
                response.battleId(),
                Instant.ofEpochSecond(response.battleAt()),
                response.battleType(),
                response.gameVersion(),
                response.stageId(),
                response.player1(),
                response.player2()
        );
    }

    public MatchParticipantInsert toParticipantInsert(
            Long matchId,
            Long playerId,
            ReplayPlayer replayPlayer
    ) {

        return new MatchParticipantInsert(
                matchId,
                playerId,
                replayPlayer.characterId(),
                replayPlayer.rank(),
                replayPlayer.power(),
                replayPlayer.rounds(),
                replayPlayer.winner()
        );
    }

    public PlayerUpsert toPlayerUpsert(
            ReplayPlayer player
    ) {

        return new PlayerUpsert(
                player.userId(),
                player.polarisId(),
                player.nickname()
        );
    }
}
