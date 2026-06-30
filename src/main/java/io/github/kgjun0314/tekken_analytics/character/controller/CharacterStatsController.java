package io.github.kgjun0314.tekken_analytics.character.controller;

import io.github.kgjun0314.tekken_analytics.character.dto.CharacterStatsResponse;
import io.github.kgjun0314.tekken_analytics.character.service.CharacterStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CharacterStatsController {
    private final CharacterStatsService service;

    @GetMapping("/characters")
    public List<CharacterStatsResponse> findALl() {
        return service.findAll();
    }
}
