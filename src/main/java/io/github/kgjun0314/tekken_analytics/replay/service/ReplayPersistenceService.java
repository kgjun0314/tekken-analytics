package io.github.kgjun0314.tekken_analytics.replay.service;

import io.github.kgjun0314.tekken_analytics.player.dto.PlayerUpsert;
import io.github.kgjun0314.tekken_analytics.player.service.PlayerService;
import io.github.kgjun0314.tekken_analytics.replay.dto.MatchParticipantInsert;
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

    @Transactional
    public void saveAll(List<Replay> replays) {

        if (replays.isEmpty()) {
            return;
        }

        List<Match> matches =
                new ArrayList<>(replays.size());

        Map<Long, PlayerUpsert> players =
                new LinkedHashMap<>();

        Map<String, Replay> replayMap =
                new LinkedHashMap<>();

        for (Replay replay : replays) {

            replayMap.put(
                    replay.battleId(),
                    replay
            );

            matches.add(
                    replayMapper.toMatch(replay)
            );

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

        Map<String, Long> insertedMatches =
                matchRepository.insertIfAbsentAll(
                        matches
                );

        if (insertedMatches.isEmpty()) {
            return;
        }

        Map<Long, Long> playerIds =
                playerService.upsertAll(
                        new ArrayList<>(players.values())
                );

        List<MatchParticipantInsert> participants =
                new ArrayList<>(
                        insertedMatches.size() * 2
                );

        for (Map.Entry<String, Long> entry
                : insertedMatches.entrySet()) {

            Replay replay =
                    replayMap.get(
                            entry.getKey()
                    );

            ReplayPlayer p1 = replay.player1();
            ReplayPlayer p2 = replay.player2();

            participants.add(
                    new MatchParticipantInsert(
                            entry.getValue(),
                            playerIds.get(p1.userId()),
                            p1.characterId(),
                            p1.rank(),
                            p1.power(),
                            p1.rounds(),
                            p1.winner()
                    )
            );

            participants.add(
                    new MatchParticipantInsert(
                            entry.getValue(),
                            playerIds.get(p2.userId()),
                            p2.characterId(),
                            p2.rank(),
                            p2.power(),
                            p2.rounds(),
                            p2.winner()
                    )
            );
        }

        matchParticipantRepository.insertAll(
                participants
        );
    }
}