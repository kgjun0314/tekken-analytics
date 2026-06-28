package io.github.kgjun0314.tekken_analytics.replay.mapper;

import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import io.github.kgjun0314.tekken_analytics.replay.dto.ReplayPlayer;
import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import io.github.kgjun0314.tekken_analytics.replay.entity.Match;
import io.github.kgjun0314.tekken_analytics.replay.entity.MatchParticipant;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ReplayMapper {
    public Match toMatch(WankReplayResponse response) {
        return Match.builder()
                .battleId(response.battleId())
                .battleAt(Instant.ofEpochSecond(response.battleAt()))
                .battleType(response.battleType())
                .gameVersion(response.gameVersion())
                .stageId(response.stageId())
                .build();
    }

    public MatchParticipant toParticipant(
            Match match,
            Player player,
            ReplayPlayer replayPlayer
    ) {

        return MatchParticipant.builder()
                .match(match)
                .player(player)
                .characterId(replayPlayer.characterId())
                .rank(replayPlayer.rank())
                .power(replayPlayer.power())
                .rounds(replayPlayer.rounds())
                .ratingBefore(replayPlayer.ratingBefore())
                .ratingChange(replayPlayer.ratingChange())
                .regionId(replayPlayer.regionId())
                .areaId(replayPlayer.areaId())
                .language(replayPlayer.language())
                .winner(replayPlayer.winner())
                .build();
    }
}
