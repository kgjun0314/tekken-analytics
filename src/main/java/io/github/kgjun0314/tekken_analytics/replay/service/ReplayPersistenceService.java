package io.github.kgjun0314.tekken_analytics.replay.service;

import io.github.kgjun0314.tekken_analytics.player.dto.PlayerUpsert;
import io.github.kgjun0314.tekken_analytics.player.service.PlayerService;
import io.github.kgjun0314.tekken_analytics.replay.entity.Match;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import io.github.kgjun0314.tekken_analytics.replay.mapper.ReplayMapper;
import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchParticipantRepository;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    public void saveAll(List<Replay> replays) {
        Map<Long, PlayerUpsert> players = new LinkedHashMap<>();

        for (Replay replay : replays) {

            ReplayPlayer p1 = replay.player1();
            ReplayPlayer p2 = replay.player2();

            players.put(
                    p1.userId(),
                    new PlayerUpsert(
                            p1.userId(),
                            p1.polarisId(),
                            p1.nickname()
                    )
            );

            players.put(
                    p2.userId(),
                    new PlayerUpsert(
                            p2.userId(),
                            p2.polarisId(),
                            p2.nickname()
                    )
            );
        }

        Map<Long, Long> playerIds =
                playerService.upsertAll(
                        new ArrayList<>(players.values())
                );

        for (Replay replay : replays) {

            Optional<Long> matchId =
                    matchRepository.insertIfAbsent(
                            replayMapper.toMatch(replay)
                    );

            if (matchId.isEmpty()) {
                continue;
            }

            ReplayPlayer p1 = replay.player1();
            ReplayPlayer p2 = replay.player2();

            Long player1Id =
                    playerIds.get(
                            p1.userId()
                    );

            Long player2Id =
                    playerIds.get(
                            p2.userId()
                    );

            matchParticipantRepository.insert(
                    matchId.get(),
                    player1Id,
                    p1,
                    player2Id,
                    p2
            );
        }
    }
}