package io.github.kgjun0314.tekken_analytics.player.entity;

import io.github.kgjun0314.tekken_analytics.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "players",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Player extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "polaris_id", nullable = false)
    private String polarisId;

    @Column(nullable = false)
    private String nickname;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}