package io.github.kgjun0314.tekken_analytics.benchmark;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class RepositoryMetrics {

    private final Map<String, AtomicLong> counters =
            new ConcurrentHashMap<>();

    public void increment(String key) {

        counters.computeIfAbsent(
                key,
                k -> new AtomicLong()
        ).incrementAndGet();
    }

    public void clear() {
        counters.clear();
    }

    public void print() {

        log.info("");
        log.info("Repository Call Statistics");
        log.info("----------------------------------------");

        counters.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        log.info("{} : {}",
                                entry.getKey(),
                                entry.getValue().get())
                );

        log.info("----------------------------------------");
    }
}