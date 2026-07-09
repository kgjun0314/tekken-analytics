package io.github.kgjun0314.tekken_analytics.character.entity;

import io.github.kgjun0314.tekken_analytics.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "character_matchups",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "character_id",
                                "opponent_character_id"
                        }
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CharacterMatchup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "character_id", nullable = false)
    private Integer characterId;

    @Column(name = "opponent_character_id", nullable = false)
    private Integer opponentCharacterId;

    @Column(nullable = false)
    private Long matches;

    @Column(nullable = false)
    private Long wins;

    private CharacterMatchup(
            Integer characterId,
            Integer opponentCharacterId
    ) {
        this.characterId = characterId;
        this.opponentCharacterId = opponentCharacterId;
        this.matches = 0L;
        this.wins = 0L;
    }

    public static CharacterMatchup create(
            Integer characterId,
            Integer opponentCharacterId
    ) {
        return new CharacterMatchup(
                characterId,
                opponentCharacterId
        );
    }

    public void increase(boolean winner) {
        matches++;

        if (winner) {
            wins++;
        }
    }

    public long losses() {
        return matches - wins;
    }

    public double winRate() {
        if (matches == 0) {
            return 0D;
        }

        return wins * 100.0 / matches;
    }
}