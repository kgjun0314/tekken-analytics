package io.github.kgjun0314.tekken_analytics.character.batch;

import io.github.kgjun0314.tekken_analytics.character.model.Character;
import io.github.kgjun0314.tekken_analytics.character.model.CharacterStatUpdate;
import io.github.kgjun0314.tekken_analytics.character.repository.CharacterStatsRepository;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterStatsAggregatorTest {

    @Mock
    private CharacterStatsRepository repository;

    @InjectMocks
    private CharacterStatsAggregator aggregator;

    @Captor
    private ArgumentCaptor<List<CharacterStatUpdate>> updatesCaptor;

    private ReplayPlayer player(
            int characterId,
            boolean winner
    ) {
        return new ReplayPlayer(
                1L,
                "polaris",
                "Jun",
                characterId,
                30,
                100_000,
                3,
                2000,
                25,
                1,
                1,
                "ko",
                winner
        );
    }

    @Test
    void flush_persistsAccumulatedStats() {

        // given
        aggregator.accumulate(player(Character.JIN.getId(), true));

        // when
        aggregator.flush();

        // then
        verify(repository).upsertAll(updatesCaptor.capture());

        List<CharacterStatUpdate> updates =
                updatesCaptor.getValue();

        assertThat(updates).hasSize(1);

        CharacterStatUpdate update =
                updates.getFirst();

        assertThat(update.characterId())
                .isEqualTo(Character.JIN.getId());

        assertThat(update.matches())
                .isEqualTo(1);

        assertThat(update.wins())
                .isEqualTo(1);
    }

    @Test
    void accumulate_mergesMultipleUpdates() {

        // given
        aggregator.accumulate(player(Character.JIN.getId(), true));
        aggregator.accumulate(player(Character.JIN.getId(), true));
        aggregator.accumulate(player(Character.JIN.getId(), false));

        // when
        aggregator.flush();

        // then
        verify(repository).upsertAll(updatesCaptor.capture());

        CharacterStatUpdate update =
                updatesCaptor.getValue().getFirst();

        assertThat(update.matches())
                .isEqualTo(3);

        assertThat(update.wins())
                .isEqualTo(2);
    }

    @Test
    void flush_clearsBufferedStats() {

        // given
        aggregator.accumulate(player(Character.JIN.getId(), true));

        // when
        aggregator.flush();
        aggregator.flush();

        // then
        verify(repository, times(1))
                .upsertAll(any());
    }

    @Test
    void flush_doesNothingWhenEmpty() {

        // when
        aggregator.flush();

        // then
        verifyNoInteractions(repository);
    }
}
