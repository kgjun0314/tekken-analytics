package io.github.kgjun0314.tekken_analytics.replay.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WankReplayResponse(

        @JsonProperty("battle_at")
        long battleAt,

        @JsonProperty("battle_id")
        String battleId,

        @JsonProperty("battle_type")
        int battleType,

        @JsonProperty("game_version")
        int gameVersion,

        @JsonProperty("p1_area_id")
        Integer p1AreaId,

        @JsonProperty("p1_chara_id")
        int p1CharacterId,

        @JsonProperty("p1_lang")
        String p1Language,

        @JsonProperty("p1_name")
        String p1Name,

        @JsonProperty("p1_polaris_id")
        String p1PolarisId,

        @JsonProperty("p1_power")
        int p1Power,

        @JsonProperty("p1_rank")
        int p1Rank,

        @JsonProperty("p1_rating_before")
        Integer p1RatingBefore,

        @JsonProperty("p1_rating_change")
        Integer p1RatingChange,

        @JsonProperty("p1_region_id")
        Integer p1RegionId,

        @JsonProperty("p1_rounds")
        int p1Rounds,

        @JsonProperty("p1_user_id")
        long p1UserId,

        @JsonProperty("p2_area_id")
        Integer p2AreaId,

        @JsonProperty("p2_chara_id")
        int p2CharacterId,

        @JsonProperty("p2_lang")
        String p2Language,

        @JsonProperty("p2_name")
        String p2Name,

        @JsonProperty("p2_polaris_id")
        String p2PolarisId,

        @JsonProperty("p2_power")
        int p2Power,

        @JsonProperty("p2_rank")
        int p2Rank,

        @JsonProperty("p2_rating_before")
        Integer p2RatingBefore,

        @JsonProperty("p2_rating_change")
        Integer p2RatingChange,

        @JsonProperty("p2_region_id")
        Integer p2RegionId,

        @JsonProperty("p2_rounds")
        int p2Rounds,

        @JsonProperty("p2_user_id")
        long p2UserId,

        @JsonProperty("stage_id")
        int stageId,

        @JsonProperty("winner")
        int winner

) {
}