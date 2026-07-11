package io.github.kgjun0314.tekken_analytics.replay.repository;

import io.github.kgjun0314.tekken_analytics.player.repository.PlayerRepository;
import io.github.kgjun0314.tekken_analytics.player.repository.PlayerRepositoryImpl;
import io.github.kgjun0314.tekken_analytics.player.repository.dto.PlayerUpsert;
import io.github.kgjun0314.tekken_analytics.replay.entity.Match;
import io.github.kgjun0314.tekken_analytics.replay.model.ReplayPlayer;
import io.github.kgjun0314.tekken_analytics.replay.repository.dto.MatchParticipantInsert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PlayerRepositoryImpl.class)
@Testcontainers
@ActiveProfiles("test")
class MatchParticipantRepositoryIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17")
                    .withDatabaseName("tekken")
                    .withUsername("postgres")
                    .withPassword("1234");

    @DynamicPropertySource
    static void properties(
            DynamicPropertyRegistry registry
    ) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MatchParticipantRepository participantRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setUp() {
        jdbcClient.sql("""
                TRUNCATE TABLE
                    match_participants,
                    matches,
                    players
                RESTART IDENTITY CASCADE
                """).update();
    }

    @Test
    void insert_insertsTwoParticipants() {

        // given
        Long matchId = createMatch();

        Map<Long, Long> players =
                createPlayers();

        // when
        participantRepository.insert(
                matchId,
                players.get(1L),
                replayPlayer(true),
                players.get(2L),
                replayPlayer(false)
        );

        // then
        assertThat(participantRepository.count())
                .isEqualTo(2);
    }

    @Test
    void insertAll_insertsParticipants() {

        // given
        Long matchId = createMatch();

        Map<Long, Long> players =
                createPlayers();

        // when
        participantRepository.insertAll(
                List.of(
                        participant(
                                matchId,
                                players.get(1L),
                                true
                        ),
                        participant(
                                matchId,
                                players.get(2L),
                                false
                        )
                )
        );

        // then
        assertThat(participantRepository.count())
                .isEqualTo(2);
    }

    @Test
    void insertAll_insertsMultipleParticipants() {

        // given
        Long match1 = createMatch();

        Long match2 = createMatch("battle-2");

        Map<Long, Long> players =
                playerRepository.upsertAll(
                        List.of(
                                player(1L),
                                player(2L),
                                player(3L),
                                player(4L)
                        )
                );

        // when
        participantRepository.insertAll(
                List.of(
                        participant(match1, players.get(1L), true),
                        participant(match1, players.get(2L), false),
                        participant(match2, players.get(3L), true),
                        participant(match2, players.get(4L), false)
                )
        );

        // then
        assertThat(participantRepository.count())
                .isEqualTo(4);
    }

    @Test
    void insertAll_doesNothingWhenEmpty() {

        // when
        participantRepository.insertAll(List.of());

        // then
        assertThat(participantRepository.count())
                .isZero();
    }

    private Long createMatch() {
        return createMatch("battle-1");
    }

    private Long createMatch(
            String battleId
    ) {

        return matchRepository.insertIfAbsent(
                Match.builder()
                        .battleId(battleId)
                        .battleAt(Instant.now())
                        .battleType(1)
                        .gameVersion(100)
                        .stageId(1)
                        .build()
        ).orElseThrow();
    }

    private Map<Long, Long> createPlayers() {

        return playerRepository.upsertAll(
                List.of(
                        player(1L),
                        player(2L)
                )
        );
    }

    private PlayerUpsert player(
            Long userId
    ) {

        return new PlayerUpsert(
                userId,
                "polaris-" + userId,
                "Player-" + userId
        );
    }

    private MatchParticipantInsert participant(
            Long matchId,
            Long playerId,
            boolean winner
    ) {

        return new MatchParticipantInsert(
                matchId,
                playerId,
                6,
                30,
                100000,
                3,
                winner
        );
    }

    private ReplayPlayer replayPlayer(
            boolean winner
    ) {

        return new ReplayPlayer(
                winner ? 1L : 2L,
                "polaris",
                "Player",
                6,
                30,
                100000,
                3,
                0,
                0,
                1,
                1,
                "ko",
                winner
        );
    }
}
