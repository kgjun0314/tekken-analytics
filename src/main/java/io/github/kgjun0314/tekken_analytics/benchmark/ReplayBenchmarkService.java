package io.github.kgjun0314.tekken_analytics.benchmark;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplayBenchmarkService {

    private static final int TOTAL_PIPELINES = 3;

    private final ConcurrentHashMap<String, AtomicInteger> progress =
            new ConcurrentHashMap<>();

    private final AtomicInteger processed = new AtomicInteger();

    private final StopWatch stopWatch = new StopWatch();

    private volatile int totalReplayCount;

    private final EntityManagerFactory entityManagerFactory;
    private final RepositoryMetrics repositoryMetrics;
    private final ServiceMetrics serviceMetrics;

    public synchronized void start(int totalReplayCount) {
        serviceMetrics.clear();
        repositoryMetrics.clear();

        progress.clear();
        processed.set(0);

        this.totalReplayCount = totalReplayCount;

        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }

        SessionFactory sessionFactory =
                entityManagerFactory.unwrap(SessionFactory.class);

        Statistics statistics = sessionFactory.getStatistics();

        statistics.clear();

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

        SessionFactory sessionFactory =
                entityManagerFactory.unwrap(SessionFactory.class);

        Statistics statistics = sessionFactory.getStatistics();

        log.info("");
        log.info("Hibernate Statistics");
        log.info("----------------------------------------");
        log.info("Prepare Statements : {}", statistics.getPrepareStatementCount());
        log.info("Entity Inserts     : {}", statistics.getEntityInsertCount());
        log.info("Entity Updates     : {}", statistics.getEntityUpdateCount());
        log.info("Entity Deletes     : {}", statistics.getEntityDeleteCount());
        log.info("Entity Loads       : {}", statistics.getEntityLoadCount());
        log.info("Entity Fetches     : {}", statistics.getEntityFetchCount());
        log.info("Flush Count        : {}", statistics.getFlushCount());
        log.info("----------------------------------------");

        repositoryMetrics.print();
        serviceMetrics.print();
    }
}