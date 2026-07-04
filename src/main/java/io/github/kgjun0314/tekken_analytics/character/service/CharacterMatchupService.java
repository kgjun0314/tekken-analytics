package io.github.kgjun0314.tekken_analytics.character.service;

import io.github.kgjun0314.tekken_analytics.character.dto.CharacterMatchupResponse;
import io.github.kgjun0314.tekken_analytics.character.entity.CharacterMatchup;
import io.github.kgjun0314.tekken_analytics.character.model.CharacterMatchupSort;
import io.github.kgjun0314.tekken_analytics.character.repository.CharacterMatchupRepository;
import io.github.kgjun0314.tekken_analytics.character.model.Character;
import io.github.kgjun0314.tekken_analytics.replay.model.Replay;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CharacterMatchupService {

    private final CharacterMatchupRepository repository;

    public void update(Replay replay) {

        update(replay.player1(), replay.player2());
        update(replay.player2(), replay.player1());
    }

    private void update(
            ReplayPlayer me,
            ReplayPlayer opponent
    ) {
        repository.upsert(
                me.characterId(),
                opponent.characterId(),
                me.winner() ? 1L : 0L
        );
    }

    @Transactional(readOnly = true)
    public List<CharacterMatchupResponse> findAll(
            Character character,
            Long minMatches,
            CharacterMatchupSort sort
    ) {
        List<CharacterMatchup> matchups;
        if (minMatches == null) {
            matchups = repository.findByCharacterIdOrderByMatchesDesc(
                    character.getId()
            );
        } else {
            matchups = repository
                    .findByCharacterIdAndMatchesGreaterThanEqualOrderByMatchesDesc(
                            character.getId(),
                            minMatches
                    );
        }
        Comparator<CharacterMatchup> comparator = switch (sort) {
            case MATCHES ->
                    Comparator.comparing(CharacterMatchup::getMatches)
                            .reversed();
            case WIN_RATE ->
                    Comparator.comparing(CharacterMatchup::winRate)
                            .reversed();
            case WINS ->
                    Comparator.comparing(CharacterMatchup::getWins)
                            .reversed();
            case LOSSES ->
                    Comparator.comparing(CharacterMatchup::losses)
                            .reversed();
        };

        return matchups.stream()
                .sorted(comparator)
                .map(this::toResponse)
                .toList();
    }

    private CharacterMatchupResponse toResponse(
            CharacterMatchup matchup
    ) {
        return new CharacterMatchupResponse(
                Character.fromId(
                        matchup.getOpponentCharacterId()
                ).getDisplayName(),
                matchup.getMatches(),
                matchup.getWins(),
                matchup.losses(),
                matchup.winRate()
        );
    }
}