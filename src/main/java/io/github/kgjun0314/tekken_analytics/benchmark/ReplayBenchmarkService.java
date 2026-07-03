package io.github.kgjun0314.tekken_analytics.benchmark;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class ReplayBenchmarkService {

    private static final int TOTAL_PIPELINES = 3;

    private final ConcurrentHashMap<String, AtomicInteger> progress =
            new ConcurrentHashMap<>();

    private final AtomicInteger processed = new AtomicInteger();

    private final StopWatch stopWatch = new StopWatch();

    private volatile int totalReplayCount;

    public synchronized void start(int totalReplayCount) {

        progress.clear();
        processed.set(0);

        this.totalReplayCount = totalReplayCount;

        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }

        stopWatch.start();

        log.info("========================================");
        log.info("Replay Benchmark Started");
        log.info("Target Replay Count : {}", totalReplayCount);
        log.info("========================================");
    }

    public void complete(String battleId) {

        AtomicInteger completed =
                progress.computeIfAbsent(
                        battleId,
                        id -> new AtomicInteger()
                );

        int current = completed.incrementAndGet();

        if (current != TOTAL_PIPELINES) {
            return;
        }

        progress.remove(battleId);

        int replayCount = processed.incrementAndGet();

        if (replayCount != totalReplayCount) {
            return;
        }

        stopWatch.stop();

        double seconds = stopWatch.getTotalTimeSeconds();
        double throughput = replayCount / seconds;

        log.info("");
        log.info("========================================");
        log.info("Replay Benchmark Finished");
        log.info("========================================");
        log.info("Processed  : {}", replayCount);
        log.info("Elapsed    : {} sec",
                String.format("%.3f", seconds));
        log.info("Throughput : {} replay/sec",
                String.format("%.2f", throughput));
        log.info("========================================");
    }
}