package io.github.kgjun0314.tekken_analytics.replay.aggregator;

import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import io.github.kgjun0314.tekken_analytics.replay.service.ReplayPersistenceService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class ReplayPersistenceAggregator {

    private static final int FLUSH_THRESHOLD = 256;

    private final ReplayPersistenceService service;

    private final List<Replay> buffer =
            Collections.synchronizedList(new ArrayList<>());

    public void accumulate(Replay replay) {

        buffer.add(replay);

        if (buffer.size() >= FLUSH_THRESHOLD) {
            flush();
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void scheduledFlush() {
        flush();
    }

    @PreDestroy
    public void destroy() {
        flush();
    }

    public synchronized void flush() {

        if (buffer.isEmpty()) {
            return;
        }

        List<Replay> batch =
                new ArrayList<>(buffer);

        buffer.clear();

        service.saveAll(batch);
    }
}