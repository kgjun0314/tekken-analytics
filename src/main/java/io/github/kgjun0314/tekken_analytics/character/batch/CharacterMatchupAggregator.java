package io.github.kgjun0314.tekken_analytics.character.batch;

import io.github.kgjun0314.tekken_analytics.character.model.CharacterMatchupUpdate;
import io.github.kgjun0314.tekken_analytics.character.repository.CharacterMatchupRepository;
import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Transactional
public class CharacterMatchupAggregator {

    private static final int FLUSH_THRESHOLD = 256;

    private final CharacterMatchupRepository repository;

    private final ConcurrentHashMap<
            CharacterMatchupKey,
            CharacterMatchupDelta
            > deltas = new ConcurrentHashMap<>();

    private final AtomicInteger buffered =
            new AtomicInteger();

    private int compare(
            ReplayPlayer left,
            ReplayPlayer right
    ) {
        int result = Integer.compare(
                left.characterId(),
                right.characterId()
        );

        if (result != 0) {
            return result;
        }

        return Long.compare(
                left.userId(),
                right.userId()
        );
    }

    public void accumulate(Replay replay) {

        ReplayPlayer p1 = replay.player1();
        ReplayPlayer p2 = replay.player2();

        if (compare(p1, p2) <= 0) {
            increase(p1, p2);
            increase(p2, p1);
        } else {
            increase(p2, p1);
            increase(p1, p2);
        }

        if (buffered.addAndGet(2) >= FLUSH_THRESHOLD) {
            flush();
        }
    }

    private void increase(
            ReplayPlayer me,
            ReplayPlayer opponent
    ) {

        CharacterMatchupKey key =
                new CharacterMatchupKey(
                        me.characterId(),
                        opponent.characterId()
                );

        deltas.computeIfAbsent(
                key,
                k -> new CharacterMatchupDelta()
        ).increase(me.winner());
    }

    @Scheduled(fixedDelay = 1000)
    public synchronized void flush() {

        if (buffered.get() == 0) {
            return;
        }

        buffered.set(0);

        List<CharacterMatchupUpdate> updates = new ArrayList<>();

        deltas.forEach((key, delta) -> {

            long matches = delta.drainMatches();
            long wins = delta.drainWins();

            if (matches == 0) {
                return;
            }

            updates.add(
                    new CharacterMatchupUpdate(
                            key.characterId(),
                            key.opponentCharacterId(),
                            matches,
                            wins
                    )
            );
        });

        if (!updates.isEmpty()) {
            repository.upsertAll(updates);
        }
    }

    @PreDestroy
    public void destroy() {
        flush();
    }
}