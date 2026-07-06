package io.github.kgjun0314.tekken_analytics.character.stats;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;

@Getter
public class CharacterStatDelta {

    private final AtomicLong matches = new AtomicLong();
    private final AtomicLong wins = new AtomicLong();

    public void increase(boolean winner) {
        matches.incrementAndGet();

        if (winner) {
            wins.incrementAndGet();
        }
    }

    public long drainMatches() {
        return matches.getAndSet(0);
    }

    public long drainWins() {
        return wins.getAndSet(0);
    }
}
