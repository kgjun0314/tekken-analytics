package io.github.kgjun0314.tekken_analytics.player.query;

import io.github.kgjun0314.tekken_analytics.player.dto.PlayerSummaryResponse;
import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import io.github.kgjun0314.tekken_analytics.player.repository.PlayerRepository;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerQueryService {
    private final PlayerRepository playerRepository;
    private final MatchParticipantRepository participantRepository;

    public PlayerSummaryResponse getSummary(Long userId) {
        Player player = playerRepository.findByUserId(userId).orElseThrow();

        long matches = participantRepository.countByPlayer(player);

        long wins = participantRepository.countByPlayerAndWinnerTrue(player);

        long losses = matches - wins;

        double winRate = matches == 0 ? 0 : (wins * 100.0) / matches;

        return new PlayerSummaryResponse(
                player.getUserId(),
                player.getNickname(),
                matches,
                wins,
                losses,
                winRate
        );
    }
}