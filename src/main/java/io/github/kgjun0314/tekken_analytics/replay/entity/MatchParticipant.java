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
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(nullable = false)
    private Integer characterId;

    @Column(nullable = false)
    private Integer rank;

    @Column(nullable = false)
    private Integer power;

    @Column(nullable = false)
    private Integer rounds;

    @Column(nullable = false)
    private Boolean winner;
}