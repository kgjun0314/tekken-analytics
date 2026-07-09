package io.github.kgjun0314.tekken_analytics.character.service;

import io.github.kgjun0314.tekken_analytics.character.model.Character;
import io.github.kgjun0314.tekken_analytics.character.dto.CharacterRankingResponse;
import io.github.kgjun0314.tekken_analytics.character.dto.CharacterStatsResponse;
import io.github.kgjun0314.tekken_analytics.character.entity.CharacterStats;
import io.github.kgjun0314.tekken_analytics.character.repository.CharacterStatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CharacterStatsServiceTest {

    @Mock
    private CharacterStatsRepository repository;

    @InjectMocks
    private CharacterStatsService service;

    private CharacterStats stats(
            int characterId,
            long matches,
            long wins
    ) {
        return CharacterStats.builder()
                .characterId(characterId)
                .matches(matches)
                .wins(wins)
                .build();
    }

    @Test
    void findAll_returnsCharacterStatsResponses() {

        // given
        given(repository.findAll())
                .willReturn(List.of(
                        stats(Character.JIN.getId(), 100, 60),
                        stats(Character.KAZUYA.getId(), 50, 20)
                ));

        // when
        List<CharacterStatsResponse> result =
                service.findAll();

        // then
        assertThat(result).hasSize(2);

        CharacterStatsResponse first = result.getFirst();

        assertThat(first.character())
                .isEqualTo("Jin");
        assertThat(first.matches())
                .isEqualTo(100);
        assertThat(first.wins())
                .isEqualTo(60);
        assertThat(first.winRate())
                .isEqualTo(60.0);

        CharacterStatsResponse second = result.get(1);

        assertThat(second.character())
                .isEqualTo("Kazuya");
        assertThat(second.matches())
                .isEqualTo(50);
        assertThat(second.wins())
                .isEqualTo(20);
        assertThat(second.winRate())
                .isEqualTo(40.0);
    }

    @Test
    void getRanking_returnsRankingResponses() {

        // given
        given(repository.findAllByOrderByMatchesDesc())
                .willReturn(List.of(
                        stats(Character.JIN.getId(), 100, 60),
                        stats(Character.KAZUYA.getId(), 80, 40),
                        stats(Character.PAUL.getId(), 50, 45)
                ));

        // when
        List<CharacterRankingResponse> result =
                service.getRanking();

        // then
        assertThat(result).hasSize(3);

        CharacterRankingResponse first = result.get(0);

        assertThat(first.rank())
                .isEqualTo(1);
        assertThat(first.character())
                .isEqualTo("Jin");
        assertThat(first.matches())
                .isEqualTo(100);
        assertThat(first.wins())
                .isEqualTo(60);
        assertThat(first.losses())
                .isEqualTo(40);
        assertThat(first.winRate())
                .isEqualTo(60.0);

        CharacterRankingResponse second = result.get(1);

        assertThat(second.rank())
                .isEqualTo(2);
        assertThat(second.character())
                .isEqualTo("Kazuya");

        CharacterRankingResponse third = result.get(2);

        assertThat(third.rank())
                .isEqualTo(3);
        assertThat(third.character())
                .isEqualTo("Paul");
        assertThat(third.losses())
                .isEqualTo(5);
        assertThat(third.winRate())
                .isEqualTo(90.0);
    }

    @Test
    void findAll_returnsEmptyList_whenRepositoryIsEmpty() {

        // given
        given(repository.findAll())
                .willReturn(List.of());

        // when
        List<CharacterStatsResponse> result =
                service.findAll();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void getRanking_returnsEmptyList_whenRepositoryIsEmpty() {

        // given
        given(repository.findAllByOrderByMatchesDesc())
                .willReturn(List.of());

        // when
        List<CharacterRankingResponse> result =
                service.getRanking();

        // then
        assertThat(result).isEmpty();
    }
}