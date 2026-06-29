package io.github.kgjun0314.tekken_analytics.replay.repository;

import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import io.github.kgjun0314.tekken_analytics.replay.entity.MatchParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {
    long countByPlayer(Player player);
    long countByPlayerAndWinnerTrue(Player player);
}
