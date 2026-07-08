package io.github.kgjun0314.tekken_analytics.replay.service;

import io.github.kgjun0314.tekken_analytics.player.repository.dto.PlayerUpsert;
import io.github.kgjun0314.tekken_analytics.player.service.PlayerService;
import io.github.kgjun0314.tekken_analytics.replay.repository.dto.MatchParticipantInsert;
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

    private static record ReplayPlayers(
            ReplayPlayer player1,
            ReplayPlayer player2
    ) {}

    private static record ReplayBatchContext(
            List<Match> matches,
            Map<Long, PlayerUpsert> players,
            Map<String, ReplayPlayers> replays
    ) {}

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

    private ReplayBatchContext collectContext(
            List<Replay> replays
    ) {

        List<Match> matches =
                new ArrayList<>(replays.size());

        Map<Long, PlayerUpsert> players =
                new LinkedHashMap<>();

        Map<String, ReplayPlayers> replayMap =
                new LinkedHashMap<>();

        for (Replay replay : replays) {

            ReplayPlayer p1 = replay.player1();
            ReplayPlayer p2 = replay.player2();

            matches.add(
                    replayMapper.toMatch(replay)
            );

            replayMap.put(
                    replay.battleId(),
                    new ReplayPlayers(
                            p1,
                            p2
                    )
            );

            players.put(
                    p1.userId(),
                    replayMapper.toPlayerUpsert(p1)
            );

            players.put(
                    p2.userId(),
                    replayMapper.toPlayerUpsert(p2)
            );
        }

        return new ReplayBatchContext(
                matches,
                players,
                replayMap
        );
    }

    private Map<String, Long> persistMatches(
            ReplayBatchContext context
    ) {

        return matchRepository.insertIfAbsentAll(
                context.matches()
        );
    }

    private Map<Long, Long> persistPlayers(
            ReplayBatchContext context
    ) {

        return playerService.upsertAll(
                new ArrayList<>(
                        context.players().values()
                )
        );
    }

    private List<MatchParticipantInsert> buildParticipants(
            ReplayBatchContext context,
            Map<String, Long> matchIds,
            Map<Long, Long> playerIds
    ) {

        List<MatchParticipantInsert> participants =
                new ArrayList<>(matchIds.size() * 2);

        for (Map.Entry<String, Long> entry
                : matchIds.entrySet()) {

            ReplayPlayers replay =
                    context.replays().get(
                            entry.getKey()
                    );

            ReplayPlayer p1 = replay.player1();
            ReplayPlayer p2 = replay.player2();

            participants.add(
                    replayMapper.toParticipantInsert(
                            entry.getValue(),
                            playerIds.get(p1.userId()),
                            p1
                    )
            );

            participants.add(
                    replayMapper.toParticipantInsert(
                            entry.getValue(),
                            playerIds.get(p2.userId()),
                            p2
                    )
            );
        }

        return participants;
    }

    private void persistParticipants(
            List<MatchParticipantInsert> participants
    ) {

        if (participants.isEmpty()) {
            return;
        }

        matchParticipantRepository.insertAll(
                participants
        );
    }

    @Transactional
    public void saveAll(List<Replay> replays) {

        if (replays.isEmpty()) {
            return;
        }

        ReplayBatchContext context =
                collectContext(replays);

        Map<String, Long> matchIds =
                persistMatches(context);

        if (matchIds.isEmpty()) {
            return;
        }

        Map<Long, Long> playerIds =
                persistPlayers(context);

        persistParticipants(buildParticipants(
                context,
                matchIds,
                playerIds
                )
        );
    }
}