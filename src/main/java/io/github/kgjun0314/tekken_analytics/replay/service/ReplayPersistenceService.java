package io.github.kgjun0314.tekken_analytics.replay.service;

import io.github.kgjun0314.tekken_analytics.player.service.PlayerService;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import io.github.kgjun0314.tekken_analytics.replay.mapper.ReplayMapper;
import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchParticipantRepository;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplayPersistenceService {

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

        Long player1Id = playerService.upsert(p1);
        Long player2Id = playerService.upsert(p2);

        matchParticipantRepository.insert(
                matchId.get(),
                player1Id,
                p1,
                player2Id,
                p2
        );
    }
}