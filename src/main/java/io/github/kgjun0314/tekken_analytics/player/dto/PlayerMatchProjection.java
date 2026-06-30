package io.github.kgjun0314.tekken_analytics.player.dto;

import java.time.Instant;

public record PlayerMatchProjection(
        String battleId,
        Instant battleAt,
        Integer characterId,
        Integer opponentCharacterId,
        String opponentNickname,
        boolean winner
) {
}
