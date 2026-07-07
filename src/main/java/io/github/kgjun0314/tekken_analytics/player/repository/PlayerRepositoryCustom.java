package io.github.kgjun0314.tekken_analytics.player.repository;

import io.github.kgjun0314.tekken_analytics.player.dto.PlayerUpsert;

import java.util.List;
import java.util.Map;

public interface PlayerRepositoryCustom {
    Long upsert(
            Long userId,
            String polarisId,
            String nickname
    );

    Map<Long, Long> upsertAll(
            List<PlayerUpsert> players
    );
}
