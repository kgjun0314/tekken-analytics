package io.github.kgjun0314.tekken_analytics.character.controller;

import io.github.kgjun0314.tekken_analytics.character.dto.CharacterRankingResponse;
import io.github.kgjun0314.tekken_analytics.character.dto.CharacterStatsResponse;
import io.github.kgjun0314.tekken_analytics.character.service.CharacterStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "Character Statistics",
        description = "캐릭터 통계 조회 API"
)
public class CharacterStatsController {
    private final CharacterStatsService service;

    @GetMapping("/characters")
    @Operation(
            summary = "캐릭터 승률 조회"
    )
    public List<CharacterStatsResponse> findALl() {
        return service.findAll();
    }

    @GetMapping("/ranking")
    @Operation(
            summary = "캐릭터 랭킹 조회"
    )
    public List<CharacterRankingResponse> ranking() {
        return service.getRanking();
    }
}
