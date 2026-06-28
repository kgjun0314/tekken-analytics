package io.github.kgjun0314.tekken_analytics.replay.dto;

public record ReplayPlayer(

        Long userId,
        String polarisId,
        String nickname,

        Integer characterId,
        Integer rank,
        Integer power,
        Integer rounds,

        Integer ratingBefore,
        Integer ratingChange,

        Integer regionId,
        Integer areaId,

        String language,

        boolean winner

) {
}