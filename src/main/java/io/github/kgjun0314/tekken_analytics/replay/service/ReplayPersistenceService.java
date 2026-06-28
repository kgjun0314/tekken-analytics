package io.github.kgjun0314.tekken_analytics.replay.service;

import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import io.github.kgjun0314.tekken_analytics.player.repository.PlayerRepository;
import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchParticipantRepository;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplayPersistenceService {
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final MatchParticipantRepository matchParticipantRepository;

    private Player getOrCreatePlayer(Long userId, String polarisId, String nickname) {
        return playerRepository.findByUserId(userId)
                .map(player -> {

                    player.updateNickname(nickname);

                    return player;
                })
                .orElseGet(() ->

                        playerRepository.save(

                                Player.builder()
                                        .userId(userId)
                                        .polarisId(polarisId)
                                        .nickname(nickname)
                                        .build()
                        )
                );
    }

    public void save(WankReplayResponse response) {
        if(matchRepository.existsByBattleId(response.battleId())) {
            return;
        }

        Player player1 =
                getOrCreatePlayer(
                        response.p1UserId(),
                        response.p1PolarisId(),
                        response.p1Name()
                );

        Player player2 =
                getOrCreatePlayer(
                        response.p2UserId(),
                        response.p2PolarisId(),
                        response.p2Name()
                );
    }
}
