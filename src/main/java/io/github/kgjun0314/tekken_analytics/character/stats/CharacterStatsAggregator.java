package io.github.kgjun0314.tekken_analytics.character.stats;

import io.github.kgjun0314.tekken_analytics.character.repository.CharacterStatsRepository;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Transactional
public class CharacterStatsAggregator {

    private static final int FLUSH_THRESHOLD = 256;

    private final CharacterStatsRepository repository;

    private final ConcurrentHashMap<Integer, CharacterStatDelta> deltas =
            new ConcurrentHashMap<>();

    private final AtomicInteger buffered = new AtomicInteger();

    public void accumulate(ReplayPlayer player) {

        deltas.computeIfAbsent(
                player.characterId(),
                id -> new CharacterStatDelta()
        ).increase(player.winner());

        if (buffered.incrementAndGet() >= FLUSH_THRESHOLD) {
            flush();
        }
    }

    @Scheduled(fixedRate = 1000)
    public synchronized void flush() {

        if (buffered.get() == 0) {
            return;
        }

        buffered.set(0);

        deltas.forEach((characterId, delta) -> {

            long matches = delta.drainMatches();
            long wins = delta.drainWins();

            if (matches == 0) {
                return;
            }

            repository.upsert(
                    characterId,
                    matches,
                    wins
            );
        });
    }

    @PreDestroy
    public void destroy() {
        flush();
    }
}