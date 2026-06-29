package io.github.kgjun0314.tekken_analytics.collector.service;

import io.github.kgjun0314.tekken_analytics.mq.producer.ReplayProducer;
import io.github.kgjun0314.tekken_analytics.replay.client.WankApiClient;
import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import io.github.kgjun0314.tekken_analytics.replay.mapper.ReplayEventMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplayCollectorService {
    private final WankApiClient client;
    private final ReplayEventMapper replayEventMapper;
    private final ReplayProducer replayProducer;

    @Transactional
    public void collect() {

        List<WankReplayResponse> responses =
                client.getLatestReplays();

        responses.stream()
                .map(replayEventMapper::toEvent)
                .forEach(replayProducer::publish);
    }
}
