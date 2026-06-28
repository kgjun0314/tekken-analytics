package io.github.kgjun0314.tekken_analytics.replay.entity;

import io.github.kgjun0314.tekken_analytics.common.entity.BaseEntity;
import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "match_participants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MatchParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(name = "character_id")
    private Integer characterId;

    private Integer rank;

    private Integer power;

    private Integer rounds;

    @Column(name = "rating_before")
    private Integer ratingBefore;

    @Column(name = "rating_change")
    private Integer ratingChange;

    @Column(name = "region_id")
    private Integer regionId;

    @Column(name = "area_id")
    private Integer areaId;

    private String language;

    private boolean winner;
}