package io.github.kgjun0314.tekken_analytics.character.service;

import io.github.kgjun0314.tekken_analytics.character.dto.CharacterStatsResponse;
import io.github.kgjun0314.tekken_analytics.character.entity.CharacterStats;
import io.github.kgjun0314.tekken_analytics.character.model.Character;
import io.github.kgjun0314.tekken_analytics.character.repository.CharacterStatsRepository;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CharacterStatsService {
    private final CharacterStatsRepository repository;

    public void update(ReplayPlayer player) {

        CharacterStats stats = repository.findById(player.characterId())
                .orElseGet(() ->
                        CharacterStats.builder()
                                .characterId(player.characterId())
                                .matches(0L)
                                .wins(0L)
                                .build()
                );

        stats.increase(player.winner());

        repository.save(stats);
    }

    @Transactional(readOnly = true)
    public List<CharacterStatsResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(stats -> new CharacterStatsResponse(
                        Character.fromId(stats.getCharacterId()).displayName(),
                        stats.getMatches(),
                        stats.getWins(),
                        stats.winRate()
                ))
                .toList();
    }
}
