package io.github.kgjun0314.tekken_analytics.collector.service;

import io.github.kgjun0314.tekken_analytics.mq.producer.ReplayProducer;
import io.github.kgjun0314.tekken_analytics.replay.client.WankApiClient;
import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import io.github.kgjun0314.tekken_analytics.replay.mapper.ReplayEventMapper;
import io.github.kgjun0314.tekken_analytics.replay.mapper.ReplayMapper;
import io.github.kgjun0314.tekken_analytics.replay.service.ReplayPersistenceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplayCollectorService {
    private final WankApiClient client;
    private final ReplayMapper replayMapper;
    private final ReplayPersistenceService replayPersistenceService;

    @Transactional
    public void collect() {

        List<WankReplayResponse> responses =
                client.getLatestReplays();

        responses.stream()
                .map(replayMapper::toReplay)
                .forEach(replayPersistenceService::save);
    }
}
