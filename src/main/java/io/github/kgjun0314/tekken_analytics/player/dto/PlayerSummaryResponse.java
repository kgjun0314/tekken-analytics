package io.github.kgjun0314.tekken_analytics.player.dto;

public record PlayerSummaryResponse(
        Long userId,

        String nickname,

        long matches,

        long wins,

        long losses,

        double winRate
) {
}
