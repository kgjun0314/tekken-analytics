package io.github.kgjun0314.tekken_analytics.character.controller;

import io.github.kgjun0314.tekken_analytics.character.dto.CharacterMatchupResponse;
import io.github.kgjun0314.tekken_analytics.character.service.CharacterMatchupService;
import io.github.kgjun0314.tekken_analytics.character.model.Character;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/characters")
@RequiredArgsConstructor
public class CharacterMatchupController {

    private final CharacterMatchupService characterMatchupService;

    @GetMapping("/{character}/matchups")
    public List<CharacterMatchupResponse> findMatchups(
            @PathVariable Character character
    ) {
        return characterMatchupService.findAll(character);
    }
}