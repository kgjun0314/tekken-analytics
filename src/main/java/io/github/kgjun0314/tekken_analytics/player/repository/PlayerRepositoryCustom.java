package io.github.kgjun0314.tekken_analytics.player.repository;

public interface PlayerRepositoryCustom {
    Long upsert(
            Long userId,
            String polarisId,
            String nickname
    );
}
