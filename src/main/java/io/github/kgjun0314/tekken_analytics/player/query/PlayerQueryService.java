package io.github.kgjun0314.tekken_analytics.player.query;

import io.github.kgjun0314.tekken_analytics.character.model.Character;
import io.github.kgjun0314.tekken_analytics.common.dto.PageResponse;
import io.github.kgjun0314.tekken_analytics.common.exception.PlayerNotFoundException;
import io.github.kgjun0314.tekken_analytics.player.dto.PlayerMatchProjection;
import io.github.kgjun0314.tekken_analytics.player.dto.PlayerMatchResponse;
import io.github.kgjun0314.tekken_analytics.player.dto.PlayerSummaryResponse;
import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import io.github.kgjun0314.tekken_analytics.player.repository.PlayerRepository;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerQueryService {
    private final PlayerRepository playerRepository;
    private final MatchParticipantRepository participantRepository;

    @Transactional(readOnly = true)
    public Player getByUserId(Long userId) {
        return playerRepository.findByUserId(userId)
                .orElseThrow(PlayerNotFoundException::new);
    }

    public PlayerSummaryResponse getSummary(Long userId) {
        Player player = getByUserId(userId);

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

    public PageResponse<PlayerMatchResponse> getMatches(
            Long userId,
            Pageable pageable
    ) {
        getByUserId(userId);

        Page<PlayerMatchProjection> page = participantRepository.findPlayerMatches(
                userId,
                pageable
        );

        Page<PlayerMatchResponse> response =
                page.map(p ->
                        new PlayerMatchResponse(
                                p.battleId(),
                                p.battleAt(),
                                Character.fromId(
                                        p.characterId()
                                ).getDisplayName(),
                                Character.fromId(
                                        p.opponentCharacterId()
                                ).getDisplayName(),
                                p.opponentNickname(),
                                p.winner()
                        )
                );

        return PageResponse.from(response);
    }
}