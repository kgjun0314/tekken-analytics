package io.github.kgjun0314.tekken_analytics.benchmark;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
@Component
public class ReplayPersistenceMetrics {

    private final LongAdder player1 = new LongAdder();
    private final LongAdder player2 = new LongAdder();
    private final LongAdder match = new LongAdder();
    private final LongAdder participant1 = new LongAdder();
    private final LongAdder participant2 = new LongAdder();

    private final AtomicLong count = new AtomicLong();

    public void recordPlayer1(long nanos) {
        player1.add(nanos);
    }

    public void recordPlayer2(long nanos) {
        player2.add(nanos);
    }

    public void recordMatch(long nanos) {
        match.add(nanos);
    }

    public void recordParticipant1(long nanos) {
        participant1.add(nanos);
    }

    public void recordParticipant2(long nanos) {
        participant2.add(nanos);
    }

    public void increaseCount() {
        count.incrementAndGet();
    }

    public void clear() {
        player1.reset();
        player2.reset();
        match.reset();
        participant1.reset();
        participant2.reset();
        count.set(0);
    }

    public void print() {

        long total = count.get();

        log.info("");
        log.info("ReplayPersistence Breakdown");
        log.info("--------------------------------------------------------------------------------");
        log.info(String.format("%-20s %12s %12s",
                "Stage",
                "Total(ms)",
                "Avg(ms)"));
        log.info("--------------------------------------------------------------------------------");

        print("player1", player1.sum(), total);
        print("player2", player2.sum(), total);
        print("match", match.sum(), total);
        print("participant1", participant1.sum(), total);
        print("participant2", participant2.sum(), total);

        log.info("--------------------------------------------------------------------------------");
    }

    private void print(
            String name,
            long nanos,
            long count
    ) {

        double totalMs = nanos / 1_000_000.0;
        double avgMs = count == 0 ? 0 : totalMs / count;

        log.info(String.format(
                "%-20s %12.3f %12.3f",
                name,
                totalMs,
                avgMs
        ));
    }
}