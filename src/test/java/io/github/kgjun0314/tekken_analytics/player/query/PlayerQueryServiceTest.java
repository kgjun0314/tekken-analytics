package io.github.kgjun0314.tekken_analytics.player.query;

import io.github.kgjun0314.tekken_analytics.character.model.Character;
import io.github.kgjun0314.tekken_analytics.common.dto.PageResponse;
import io.github.kgjun0314.tekken_analytics.common.exception.PlayerNotFoundException;
import io.github.kgjun0314.tekken_analytics.player.dto.PlayerMatchProjection;
import io.github.kgjun0314.tekken_analytics.player.dto.PlayerMatchResponse;
import io.github.kgjun0314.tekken_analytics.player.dto.PlayerSummaryResponse;
import io.github.kgjun0314.tekken_analytics.player.entity.Player;
import io.github.kgjun0314.tekken_analytics.player.repository.PlayerRepository;
import io.github.kgjun0314.tekken_analytics.replay.repository.MatchParticipantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PlayerQueryServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private MatchParticipantRepository participantRepository;

    @InjectMocks
    private PlayerQueryService queryService;

    private Player player() {
        return Player.builder()
                .id(1L)
                .userId(100L)
                .nickname("Jun")
                .build();
    }

    @Test
    void getByUserId_returnsPlayer() {

        // given
        Player player = player();

        given(playerRepository.findByUserId(100L))
                .willReturn(Optional.of(player));

        // when
        Player result = queryService.getByUserId(100L);

        // then
        assertThat(result).isSameAs(player);
    }

    @Test
    void getByUserId_throwsException_whenPlayerNotFound() {

        // given
        given(playerRepository.findByUserId(100L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> queryService.getByUserId(100L))
                .isInstanceOf(PlayerNotFoundException.class);
    }

    @Test
    void getSummary_returnsSummary() {

        // given
        Player player = player();

        given(playerRepository.findByUserId(100L))
                .willReturn(Optional.of(player));

        given(participantRepository.countByPlayer(player))
                .willReturn(10L);

        given(participantRepository.countByPlayerAndWinnerTrue(player))
                .willReturn(7L);

        // when
        PlayerSummaryResponse response =
                queryService.getSummary(100L);

        // then
        assertThat(response.userId()).isEqualTo(100L);
        assertThat(response.nickname()).isEqualTo("Jun");
        assertThat(response.matches()).isEqualTo(10);
        assertThat(response.wins()).isEqualTo(7);
        assertThat(response.losses()).isEqualTo(3);
        assertThat(response.winRate()).isEqualTo(70.0);
    }

    @Test
    void getSummary_returnsZeroWinRate_whenNoMatches() {

        // given
        Player player = player();

        given(playerRepository.findByUserId(100L))
                .willReturn(Optional.of(player));

        given(participantRepository.countByPlayer(player))
                .willReturn(0L);

        given(participantRepository.countByPlayerAndWinnerTrue(player))
                .willReturn(0L);

        // when
        PlayerSummaryResponse response =
                queryService.getSummary(100L);

        // then
        assertThat(response.matches()).isZero();
        assertThat(response.wins()).isZero();
        assertThat(response.losses()).isZero();
        assertThat(response.winRate()).isZero();
    }

    @Test
    void getSummary_throwsException_whenPlayerNotFound() {

        // given
        given(playerRepository.findByUserId(100L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> queryService.getSummary(100L))
                .isInstanceOf(PlayerNotFoundException.class);
    }

    @Test
    void getMatches_returnsPageResponse() {

        // given
        Player player = player();

        given(playerRepository.findByUserId(100L))
                .willReturn(Optional.of(player));

        PlayerMatchProjection projection =
                new PlayerMatchProjection(
                        "battle-1",
                        Instant.now(),
                        Character.JIN.getId(),
                        Character.KAZUYA.getId(),
                        "Kazuya",
                        true
                );

        Page<PlayerMatchProjection> page =
                new PageImpl<>(List.of(projection));

        Pageable pageable =
                PageRequest.of(0, 20);

        given(
                participantRepository.findPlayerMatches(
                        100L,
                        pageable
                )
        ).willReturn(page);

        // when
        PageResponse<PlayerMatchResponse> response =
                queryService.getMatches(
                        100L,
                        pageable
                );

        // then
        assertThat(response.content()).hasSize(1);

        PlayerMatchResponse match =
                response.content().getFirst();

        assertThat(match.character()).isEqualTo("Jin");
        assertThat(match.opponentCharacter())
                .isEqualTo("Kazuya");
        assertThat(match.opponentNickname())
                .isEqualTo("Kazuya");
        assertThat(match.winner()).isTrue();
    }

    @Test
    void getMatches_returnsEmptyPage() {

        // given
        Player player = player();

        Pageable pageable =
                PageRequest.of(0, 20);

        given(playerRepository.findByUserId(100L))
                .willReturn(Optional.of(player));

        given(
                participantRepository.findPlayerMatches(
                        100L,
                        pageable
                )
        ).willReturn(Page.empty(pageable));

        // when
        PageResponse<PlayerMatchResponse> response =
                queryService.getMatches(
                        100L,
                        pageable
                );

        // then
        assertThat(response.content()).isEmpty();
    }

    @Test
    void getMatches_throwsException_whenPlayerNotFound() {

        // given
        Pageable pageable =
                PageRequest.of(0, 20);

        given(playerRepository.findByUserId(100L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                queryService.getMatches(
                        100L,
                        pageable
                )
        ).isInstanceOf(PlayerNotFoundException.class);
    }
}