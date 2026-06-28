package io.github.kgjun0314.tekken_analytics.player.service;

import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import io.github.kgjun0314.tekken_analytics.player.repository.PlayerRepository;
import io.github.kgjun0314.tekken_analytics.replay.dto.ReplayPlayer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;

    public Player getOrCreate(ReplayPlayer replayPlayer) {

        return playerRepository.findByUserId(replayPlayer.userId())
                .map(player -> {

                    player.updateNickname(replayPlayer.nickname());

                    return player;
                })
                .orElseGet(() ->
                        playerRepository.save(
                                Player.builder()
                                        .userId(replayPlayer.userId())
                                        .polarisId(replayPlayer.polarisId())
                                        .nickname(replayPlayer.nickname())
                                        .build()
                        )
                );
    }

}