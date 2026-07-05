package io.github.kgjun0314.tekken_analytics.player.service;

import io.github.kgjun0314.tekken_analytics.player.repository.PlayerRepository;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {
    private final PlayerRepository playerRepository;

    public Long upsert(ReplayPlayer replayPlayer) {

        return playerRepository.upsert(
                replayPlayer.userId(),
                replayPlayer.polarisId(),
                replayPlayer.nickname()
        );
    }
}