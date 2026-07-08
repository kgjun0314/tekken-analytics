package io.github.kgjun0314.tekken_analytics.infrastructure.client;

import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WankApiClient {
    private static final String BASE_URL = "https://wank.wavu.wiki/api";

    private final RestClient restClient;

    public List<WankReplayResponse> getLatestReplays() {
        return restClient.get()
                .uri(BASE_URL + "/replays")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
