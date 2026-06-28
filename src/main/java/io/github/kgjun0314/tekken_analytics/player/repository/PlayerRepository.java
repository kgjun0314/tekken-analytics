package io.github.kgjun0314.tekken_analytics.player.repository;

import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player,Long> {
    Optional<Player> findByUserId(Long userId);
}
