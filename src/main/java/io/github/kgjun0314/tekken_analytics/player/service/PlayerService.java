package io.github.kgjun0314.tekken_analytics.player.service;

import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import io.github.kgjun0314.tekken_analytics.player.repository.PlayerRepository;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {

    @PersistenceContext
    private EntityManager entityManager;
    private final PlayerRepository playerRepository;

    public Player getOrCreate(ReplayPlayer replayPlayer) {
        Long id =
                playerRepository.upsert(
                        replayPlayer.userId(),
                        replayPlayer.polarisId(),
                        replayPlayer.nickname()
                );

        return entityManager.getReference(
                Player.class,
                id
        );
    }
}