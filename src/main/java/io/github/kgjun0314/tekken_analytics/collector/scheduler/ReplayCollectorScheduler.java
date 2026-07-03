package io.github.kgjun0314.tekken_analytics.collector.scheduler;

import io.github.kgjun0314.tekken_analytics.collector.service.ReplayCollectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!fixture")
public class ReplayCollectorScheduler {

    private final ReplayCollectorService collectorService;

    @Scheduled(fixedDelay = 30000)
    public void collect() {

        collectorService.collect();

    }

}