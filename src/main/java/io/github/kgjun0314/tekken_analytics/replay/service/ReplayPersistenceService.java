package io.github.kgjun0314.tekken_analytics.replay.service;

import io.github.kgjun0314.tekken_analytics.benchmark.ReplayPersistenceMetrics;
import io.github.kgjun0314.tekken_analytics.character.service.CharacterStatsService;
import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import io.github.kgjun0314.tekken_analytics.player.service.PlayerService;
import io.github.kgjun0314.tekken_analytics.replay.entity.MatchParticipant;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import io.github.kgjun0314.tekken_analytics.replay.entity.Match;
import io.github.kgjun0314.tekken_analytics.replay.mapper.ReplayMapper;
import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchParticipantRepository;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplayPersistenceService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PlayerService playerService;
    private final ReplayMapper replayMapper;

    private final MatchRepository matchRepository;
    private final MatchParticipantRepository matchParticipantRepository;

    public void save(Replay replay) {

        Optional<Long> matchId =
                matchRepository.insertIfAbsent(
                        replayMapper.toMatch(replay)
                );

        if (matchId.isEmpty()) {
            return;
        }

        ReplayPlayer p1 = replay.player1();
        ReplayPlayer p2 = replay.player2();

        Player player1 = playerService.getOrCreate(p1);
        Player player2 = playerService.getOrCreate(p2);

        Match match =
                entityManager.getReference(
                        Match.class,
                        matchId.get()
                );

        MatchParticipant participant1 =
                replayMapper.toParticipant(
                        match,
                        player1,
                        p1
                );

        MatchParticipant participant2 =
                replayMapper.toParticipant(
                        match,
                        player2,
                        p2
                );

        matchParticipantRepository.save(participant1);
        matchParticipantRepository.save(participant2);
    }
}