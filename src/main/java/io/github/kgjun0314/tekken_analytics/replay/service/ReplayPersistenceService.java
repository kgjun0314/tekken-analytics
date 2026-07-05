package io.github.kgjun0314.tekken_analytics.replay.service;

import io.github.kgjun0314.tekken_analytics.benchmark.ReplayPersistenceMetrics;
import io.github.kgjun0314.tekken_analytics.character.service.CharacterStatsService;
import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import io.github.kgjun0314.tekken_analytics.player.service.PlayerService;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import io.github.kgjun0314.tekken_analytics.replay.entity.Match;
import io.github.kgjun0314.tekken_analytics.replay.mapper.ReplayMapper;
import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchParticipantRepository;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplayPersistenceService {
    private final PlayerService playerService;
    private final ReplayMapper replayMapper;
    private final MatchRepository matchRepository;
    private final MatchParticipantRepository matchParticipantRepository;

    private final ReplayPersistenceMetrics metrics;

    public void save(Replay replay) {

        long start;

        start = System.nanoTime();

        if (matchRepository.existsByBattleId(replay.battleId())) {
            metrics.recordExists(System.nanoTime() - start);
            return;
        }

        metrics.recordExists(System.nanoTime() - start);

        ReplayPlayer p1 = replay.player1();
        ReplayPlayer p2 = replay.player2();

        start = System.nanoTime();
        Player player1 = playerService.getOrCreate(p1);
        metrics.recordPlayer1(System.nanoTime() - start);

        start = System.nanoTime();
        Player player2 = playerService.getOrCreate(p2);
        metrics.recordPlayer2(System.nanoTime() - start);

        Match match = replayMapper.toMatch(replay);

        start = System.nanoTime();
        matchRepository.save(match);
        metrics.recordMatch(System.nanoTime() - start);

        start = System.nanoTime();
        matchParticipantRepository.save(
                replayMapper.toParticipant(match, player1, p1)
        );
        metrics.recordParticipant1(System.nanoTime() - start);

        start = System.nanoTime();
        matchParticipantRepository.save(
                replayMapper.toParticipant(match, player2, p2)
        );
        metrics.recordParticipant2(System.nanoTime() - start);

        metrics.increaseCount();
    }
}
