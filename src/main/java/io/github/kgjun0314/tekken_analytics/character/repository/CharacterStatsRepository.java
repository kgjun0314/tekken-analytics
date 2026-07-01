package io.github.kgjun0314.tekken_analytics.character.repository;

import io.github.kgjun0314.tekken_analytics.character.entity.CharacterStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterStatsRepository extends JpaRepository<CharacterStats, Integer> {
    List<CharacterStats> findAllByOrderByMatchesDesc();
}
