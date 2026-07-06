package io.github.kgjun0314.tekken_analytics.character.service;

import io.github.kgjun0314.tekken_analytics.character.dto.CharacterRankingResponse;
import io.github.kgjun0314.tekken_analytics.character.dto.CharacterStatsResponse;
import io.github.kgjun0314.tekken_analytics.character.entity.CharacterStats;
import io.github.kgjun0314.tekken_analytics.character.model.Character;
import io.github.kgjun0314.tekken_analytics.character.repository.CharacterStatsRepository;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CharacterStatsService {
    private final CharacterStatsRepository repository;

//    public void update(ReplayPlayer player) {
//        repository.upsert(
//                player.characterId(),
//                player.winner()
//        );
//    }

    @Transactional(readOnly = true)
    public List<CharacterStatsResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(stats -> new CharacterStatsResponse(
                        Character.fromId(stats.getCharacterId()).getDisplayName(),
                        stats.getMatches(),
                        stats.getWins(),
                        stats.winRate()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CharacterRankingResponse> getRanking() {
        List<CharacterStats> statsList =
                repository.findAllByOrderByMatchesDesc();
        List<CharacterRankingResponse> result = new ArrayList<>();
        int rank = 1;
        for (CharacterStats stats : statsList) {
            result.add(
                    new CharacterRankingResponse(
                            rank++,
                            Character.fromId(
                                    stats.getCharacterId()
                            ).getDisplayName(),
                            stats.getMatches(),
                            stats.getWins(),
                            stats.getMatches() - stats.getWins(),
                            stats.winRate()
                    )
            );
        }

        return result;
    }
}
