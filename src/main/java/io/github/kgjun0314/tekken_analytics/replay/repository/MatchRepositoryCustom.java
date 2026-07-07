package io.github.kgjun0314.tekken_analytics.replay.repository;

import io.github.kgjun0314.tekken_analytics.replay.entity.Match;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MatchRepositoryCustom {
    Optional<Long> insertIfAbsent(Match match);
    Map<String, Long> findOrInsertAll(
            List<Match> matches
    );
}
