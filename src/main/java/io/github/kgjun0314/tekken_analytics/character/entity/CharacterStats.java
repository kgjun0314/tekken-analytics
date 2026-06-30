package io.github.kgjun0314.tekken_analytics.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "character_stats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CharacterStats {
    @Id
    private Integer characterId;

    @Column(nullable = false)
    private Long matches;

    @Column(nullable = false)
    private Long wins;

    public void increase(boolean winner) {
        this.matches++;

        if (winner) {
            this.wins++;
        }
    }

    public double winRate() {
        if (matches == 0) {
            return 0;
        }

        return wins * 100.0 / matches;
    }
}
