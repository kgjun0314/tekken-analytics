package io.github.kgjun0314.tekken_analytics.replay.controller;

import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import io.github.kgjun0314.tekken_analytics.replay.service.ReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReplayController {
    private final ReplayService replayService;

    @GetMapping("/replays")
    public List<WankReplayResponse> getReplays() {
        return replayService.getLatestReplays();
    }
}
