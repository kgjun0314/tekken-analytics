package io.github.kgjun0314.tekken_analytics.character.controller;

import io.github.kgjun0314.tekken_analytics.character.dto.CharacterMatchupResponse;
import io.github.kgjun0314.tekken_analytics.character.model.CharacterMatchupSort;
import io.github.kgjun0314.tekken_analytics.character.service.CharacterMatchupService;
import io.github.kgjun0314.tekken_analytics.character.model.Character;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/characters")
@RequiredArgsConstructor
@Tag(
        name = "Character Matchup",
        description = "캐릭터 상성 조회 API"
)
public class CharacterMatchupController {

    private final CharacterMatchupService characterMatchupService;

    @GetMapping("/{character}/matchups")
    @Operation(
            summary = "캐릭터 상성 조회"
    )
    public List<CharacterMatchupResponse> findMatchups(
            @PathVariable Character character,
            @RequestParam(required = false)
            Long minMatches,
            @RequestParam(defaultValue = "MATCHES")
            CharacterMatchupSort sort
    ) {

        return characterMatchupService.findAll(
                character,
                minMatches,
                sort
        );
    }
}