package io.github.kgjun0314.tekken_analytics.character.service;

import io.github.kgjun0314.tekken_analytics.character.model.Character;
import io.github.kgjun0314.tekken_analytics.character.dto.CharacterMatchupResponse;
import io.github.kgjun0314.tekken_analytics.character.entity.CharacterMatchup;
import io.github.kgjun0314.tekken_analytics.character.model.CharacterMatchupSort;
import io.github.kgjun0314.tekken_analytics.character.repository.CharacterMatchupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CharacterMatchupServiceTest {

    @Mock
    private CharacterMatchupRepository repository;

    @InjectMocks
    private CharacterMatchupService service;

    private CharacterMatchup matchup(
            int opponentCharacterId,
            long matches,
            long wins
    ) {
        return CharacterMatchup.builder()
                .characterId(Character.JIN.getId())
                .opponentCharacterId(opponentCharacterId)
                .matches(matches)
                .wins(wins)
                .build();
    }

    @Test
    void findAll_sortsByMatches() {

        // given
        given(repository.findByCharacterIdOrderByMatchesDesc(
                Character.JIN.getId()
        )).willReturn(List.of(
                matchup(Character.PAUL.getId(), 50, 30),
                matchup(Character.KAZUYA.getId(), 100, 60)
        ));

        // when
        List<CharacterMatchupResponse> result =
                service.findAll(
                        Character.JIN,
                        null,
                        CharacterMatchupSort.MATCHES
                );

        // then
        assertThat(result).hasSize(2);

        CharacterMatchupResponse response =
                result.getFirst();

        assertThat(response.opponent())
                .isEqualTo("Kazuya");
        assertThat(response.matches())
                .isEqualTo(100);
        assertThat(response.wins())
                .isEqualTo(60);
        assertThat(response.losses())
                .isEqualTo(40);
        assertThat(response.winRate())
                .isEqualTo(60.0);
    }

    @Test
    void findAll_sortsByWinRate() {

        // given
        given(repository.findByCharacterIdOrderByMatchesDesc(
                Character.JIN.getId()
        )).willReturn(List.of(
                matchup(Character.PAUL.getId(), 100, 90),
                matchup(Character.KAZUYA.getId(), 100, 50)
        ));

        // when
        List<CharacterMatchupResponse> result =
                service.findAll(
                        Character.JIN,
                        null,
                        CharacterMatchupSort.WIN_RATE
                );

        // then
        assertThat(result)
                .extracting(CharacterMatchupResponse::opponent)
                .containsExactly(
                        "Paul",
                        "Kazuya"
                );
    }

    @Test
    void findAll_sortsByWins() {

        // given
        given(repository.findByCharacterIdOrderByMatchesDesc(
                Character.JIN.getId()
        )).willReturn(List.of(
                matchup(Character.PAUL.getId(), 100, 60),
                matchup(Character.KAZUYA.getId(), 100, 80)
        ));

        // when
        List<CharacterMatchupResponse> result =
                service.findAll(
                        Character.JIN,
                        null,
                        CharacterMatchupSort.WINS
                );

        // then
        assertThat(result)
                .extracting(CharacterMatchupResponse::opponent)
                .containsExactly(
                        "Kazuya",
                        "Paul"
                );
    }

    @Test
    void findAll_sortsByLosses() {

        // given
        given(repository.findByCharacterIdOrderByMatchesDesc(
                Character.JIN.getId()
        )).willReturn(List.of(
                matchup(Character.PAUL.getId(), 100, 90),
                matchup(Character.KAZUYA.getId(), 100, 50)
        ));

        // when
        List<CharacterMatchupResponse> result =
                service.findAll(
                        Character.JIN,
                        null,
                        CharacterMatchupSort.LOSSES
                );

        // then
        assertThat(result)
                .extracting(CharacterMatchupResponse::opponent)
                .containsExactly(
                        "Kazuya",
                        "Paul"
                );
    }

    @Test
    void findAll_usesMinMatchesQuery() {

        // given
        given(
                repository.findByCharacterIdAndMatchesGreaterThanEqualOrderByMatchesDesc(
                        Character.JIN.getId(),
                        100L
                )
        ).willReturn(List.of());

        // when
        service.findAll(
                Character.JIN,
                100L,
                CharacterMatchupSort.MATCHES
        );

        // then
        verify(repository)
                .findByCharacterIdAndMatchesGreaterThanEqualOrderByMatchesDesc(
                        Character.JIN.getId(),
                        100L
                );

        verify(repository, never())
                .findByCharacterIdOrderByMatchesDesc(anyInt());
    }

    @Test
    void findAll_usesDefaultQueryWhenMinMatchesIsNull() {

        // given
        given(repository.findByCharacterIdOrderByMatchesDesc(
                Character.JIN.getId()
        )).willReturn(List.of());

        // when
        service.findAll(
                Character.JIN,
                null,
                CharacterMatchupSort.MATCHES
        );

        // then
        verify(repository)
                .findByCharacterIdOrderByMatchesDesc(
                        Character.JIN.getId()
                );

        verify(repository, never())
                .findByCharacterIdAndMatchesGreaterThanEqualOrderByMatchesDesc(
                        anyInt(),
                        anyLong()
                );
    }
}