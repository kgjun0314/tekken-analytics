package io.github.kgjun0314.tekken_analytics.replay.repository;

import io.github.kgjun0314.tekken_analytics.replay.entity.Match;

import java.util.Optional;

public interface MatchRepositoryCustom {
    Optional<Long> insertIfAbsent(Match match);
}
