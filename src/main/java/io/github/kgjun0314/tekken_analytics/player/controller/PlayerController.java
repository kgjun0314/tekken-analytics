package io.github.kgjun0314.tekken_analytics.player.controller;

import io.github.kgjun0314.tekken_analytics.player.dto.PlayerSummaryResponse;
import io.github.kgjun0314.tekken_analytics.player.query.PlayerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerQueryService queryService;

    @GetMapping("/{userId}")
    public PlayerSummaryResponse getSummary(
            @PathVariable Long userId
    ) {
        return queryService.getSummary(userId);
    }
}