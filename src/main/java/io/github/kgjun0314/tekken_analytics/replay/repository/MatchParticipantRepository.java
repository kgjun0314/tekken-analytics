package io.github.kgjun0314.tekken_analytics.replay.repository;

import io.github.kgjun0314.tekken_analytics.player.dto.PlayerMatchResponse;
import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import io.github.kgjun0314.tekken_analytics.replay.entity.MatchParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {
    long countByPlayer(Player player);
    long countByPlayerAndWinnerTrue(Player player);
    @Query("""
    SELECT new io.github.kgjun0314.tekken_analytics.player.dto.PlayerMatchResponse(
        m.battleId,
        m.battleAt,
        mp.characterId,
        opponent.characterId,
        opponent.player.nickname,
        mp.winner
    )
    FROM MatchParticipant mp
    JOIN mp.match m
    JOIN MatchParticipant opponent
        ON opponent.match = m
       AND opponent.player <> mp.player
    WHERE mp.player.userId = :userId
    ORDER BY m.battleAt DESC
    """)
    Page<PlayerMatchResponse> findPlayerMatches(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
