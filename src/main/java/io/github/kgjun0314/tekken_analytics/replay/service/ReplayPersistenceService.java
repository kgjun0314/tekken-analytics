package io.github.kgjun0314.tekken_analytics.replay.service;

import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import io.github.kgjun0314.tekken_analytics.player.repository.PlayerRepository;
import io.github.kgjun0314.tekken_analytics.player.service.PlayerService;
import io.github.kgjun0314.tekken_analytics.replay.dto.ReplayPlayer;
import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import io.github.kgjun0314.tekken_analytics.replay.entity.Match;
import io.github.kgjun0314.tekken_analytics.replay.mapper.ReplayMapper;
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

    public void save(WankReplayResponse response) {

        if (matchRepository.existsByBattleId(response.battleId())) {
            return;
        }

        ReplayPlayer p1 = response.player1();
        ReplayPlayer p2 = response.player2();

        Player player1 = playerService.getOrCreate(p1);
        Player player2 = playerService.getOrCreate(p2);

        Match match = replayMapper.toMatch(response);

        matchRepository.save(match);

        matchParticipantRepository.save(
                replayMapper.toParticipant(match, player1, p1)
        );

        matchParticipantRepository.save(
                replayMapper.toParticipant(match, player2, p2)
        );
    }


}
