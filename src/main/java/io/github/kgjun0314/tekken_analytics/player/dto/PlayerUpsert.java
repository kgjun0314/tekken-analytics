package io.github.kgjun0314.tekken_analytics.player.dto;

public record PlayerUpsert(
        Long userId,
        String polarisId,
        String nickname
) {
}