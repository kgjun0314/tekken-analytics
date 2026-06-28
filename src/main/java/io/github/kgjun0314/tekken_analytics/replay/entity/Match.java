package io.github.kgjun0314.tekken_analytics.replay.entity;

import io.github.kgjun0314.tekken_analytics.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "matches")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Match extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "battle_id", nullable = false, unique = true)
    private String battleId;

    @Column(name = "battle_at", nullable = false)
    private Instant battleAt;

    @Column(name = "battle_type", nullable = false)
    private Integer battleType;

    @Column(name = "game_version", nullable = false)
    private Integer gameVersion;

    @Column(name = "stage_id", nullable = false)
    private Integer stageId;
}