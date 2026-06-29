package io.github.kgjun0314.tekken_analytics.replay.service;

import io.github.kgjun0314.tekken_analytics.replay.client.WankApiClient;
import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplayService {
    private final WankApiClient client;
    private final ReplayPersistenceService persistenceService;

    public List<WankReplayResponse> getLatestReplays() {
        return client.getLatestReplays();
    }

//    public void saveLatestReplay() {
//        List<WankReplayResponse> responses = client.getLatestReplays();
//
//        responses.forEach(persistenceService::save);
//    }
}
