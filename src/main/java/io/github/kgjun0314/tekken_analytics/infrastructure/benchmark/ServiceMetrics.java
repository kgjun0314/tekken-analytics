package io.github.kgjun0314.tekken_analytics.infrastructure.benchmark;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class ServiceMetrics {

    private record Metric(
            AtomicLong count,
            AtomicLong totalNanos,
            AtomicLong maxNanos
    ) {
    }

    private final Map<String, Metric> metrics =
            new ConcurrentHashMap<>();

    public void record(
            String key,
            long elapsedNanos
    ) {

        Metric metric = metrics.computeIfAbsent(
                key,
                k -> new Metric(
                        new AtomicLong(),
                        new AtomicLong(),
                        new AtomicLong()
                )
        );

        metric.count().incrementAndGet();
        metric.totalNanos().addAndGet(elapsedNanos);

        metric.maxNanos().updateAndGet(
                current -> Math.max(current, elapsedNanos)
        );
    }

    public void clear() {
        metrics.clear();
    }

    public void print() {

        log.info("");
        log.info("Service Call Statistics");
        log.info("------------------------------------------------------------------------------------------------------------------------------------------------");

        log.info(String.format(
                "%-60s %10s %15s %10s %15s %15s",
                "Method",
                "Count",
                "Total(ms)",
                "Total(%)",
                "Avg(ms)",
                "Max(ms)"
        ));

        log.info("------------------------------------------------------------------------------------------------------------------------------------------------");

        long totalNanos = metrics.values()
                .stream()
                .mapToLong(metric -> metric.totalNanos().get())
                .sum();

        metrics.entrySet()
                .stream()
                .sorted(
                        Comparator.comparingLong(
                                (Map.Entry<String, Metric> entry) ->
                                        entry.getValue().totalNanos().get()
                        ).reversed()
                )
                .forEach(entry -> {

                    Metric metric = entry.getValue();

                    long count = metric.count().get();

                    double totalMs =
                            metric.totalNanos().get() / 1_000_000.0;

                    double avgMs =
                            totalMs / count;

                    double maxMs =
                            metric.maxNanos().get() / 1_000_000.0;

                    double percent =
                            totalNanos == 0
                                    ? 0
                                    : metric.totalNanos().get()
                                      * 100.0
                                      / totalNanos;

                    log.info(String.format(
                            "%-60s %10d %15.3f %9.2f%% %15.3f %15.3f",
                            entry.getKey(),
                            count,
                            totalMs,
                            percent,
                            avgMs,
                            maxMs
                    ));
                });

        log.info("------------------------------------------------------------------------------------------------------------------------------------------------");
    }
}