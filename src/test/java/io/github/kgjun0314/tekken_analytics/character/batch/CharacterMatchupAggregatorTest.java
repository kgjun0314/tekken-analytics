package io.github.kgjun0314.tekken_analytics.character.batch;

import io.github.kgjun0314.tekken_analytics.character.model.Character;
import io.github.kgjun0314.tekken_analytics.character.model.CharacterMatchupUpdate;
import io.github.kgjun0314.tekken_analytics.character.repository.CharacterMatchupRepository;
import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterMatchupAggregatorTest {

    @Mock
    private CharacterMatchupRepository repository;

    @InjectMocks
    private CharacterMatchupAggregator aggregator;

    @Captor
    private ArgumentCaptor<List<CharacterMatchupUpdate>> updatesCaptor;

    private Replay replay() {

        ReplayPlayer jin = new ReplayPlayer(
                1L,
                "polaris-1",
                "Jin",
                Character.JIN.getId(),
                30,
                100_000,
                3,
                2000,
                25,
                1,
                1,
                "ko",
                true
        );

        ReplayPlayer kazuya = new ReplayPlayer(
                2L,
                "polaris-2",
                "Kazuya",
                Character.KAZUYA.getId(),
                30,
                100_000,
                1,
                1950,
                -25,
                1,
                1,
                "ko",
                false
        );

        return new Replay(
                "battle-1",
                Instant.now(),
                1,
                1,
                1,
                jin,
                kazuya
        );
    }

    @Test
    void accumulate_createsBidirectionalUpdates() {

        // given
        aggregator.accumulate(replay());

        // when
        aggregator.flush();

        // then
        verify(repository)
                .upsertAll(updatesCaptor.capture());

        List<CharacterMatchupUpdate> updates =
                updatesCaptor.getValue();

        assertThat(updates)
                .hasSize(2);

        assertThat(updates)
                .extracting(
                        CharacterMatchupUpdate::characterId,
                        CharacterMatchupUpdate::opponentCharacterId,
                        CharacterMatchupUpdate::matches,
                        CharacterMatchupUpdate::wins
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                Character.JIN.getId(),
                                Character.KAZUYA.getId(),
                                1L,
                                1L
                        ),
                        tuple(
                                Character.KAZUYA.getId(),
                                Character.JIN.getId(),
                                1L,
                                0L
                        )
                );
    }

    @Test
    void flush_clearsBufferedUpdates() {

        aggregator.accumulate(replay());

        aggregator.flush();
        aggregator.flush();

        verify(repository, times(1))
                .upsertAll(any());
    }

    @Test
    void flush_doesNothingWhenEmpty() {

        aggregator.flush();

        verifyNoInteractions(repository);
    }

    @Test
    void accumulate_mergesMultipleReplays() {

        aggregator.accumulate(replay());
        aggregator.accumulate(replay());

        aggregator.flush();

        verify(repository)
                .upsertAll(updatesCaptor.capture());

        assertThat(updatesCaptor.getValue())
                .extracting(CharacterMatchupUpdate::matches)
                .containsOnly(2L);
    }
}