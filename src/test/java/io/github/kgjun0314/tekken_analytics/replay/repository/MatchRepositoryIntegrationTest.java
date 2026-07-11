package io.github.kgjun0314.tekken_analytics.replay.repository;

import io.github.kgjun0314.tekken_analytics.player.repository.PlayerRepositoryImpl;
import io.github.kgjun0314.tekken_analytics.replay.entity.Match;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PlayerRepositoryImpl.class)
@Testcontainers
@ActiveProfiles("test")
class MatchRepositoryIntegrationTest {

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
    private MatchRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setUp() {
        jdbcClient.sql("""
                TRUNCATE TABLE matches
                RESTART IDENTITY CASCADE
                """).update();
    }

    @Test
    void insertIfAbsent_insertsMatch() {

        Optional<Long> id =
                repository.insertIfAbsent(match("battle-1"));

        assertThat(id).isPresent();
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void insertIfAbsent_returnsEmptyWhenDuplicate() {

        repository.insertIfAbsent(match("battle-1"));

        Optional<Long> id =
                repository.insertIfAbsent(match("battle-1"));

        assertThat(id).isEmpty();
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void insertIfAbsentAll_insertsMatches() {

        Map<String, Long> result =
                repository.insertIfAbsentAll(
                        List.of(
                                match("battle-1"),
                                match("battle-2")
                        )
                );

        assertThat(repository.count()).isEqualTo(2);

        assertThat(result)
                .hasSize(2)
                .containsKeys(
                        "battle-1",
                        "battle-2"
                );
    }

    @Test
    void insertIfAbsentAll_returnsOnlyInsertedMatches() {

        repository.insertIfAbsent(match("battle-1"));

        Map<String, Long> result =
                repository.insertIfAbsentAll(
                        List.of(
                                match("battle-1"),
                                match("battle-2")
                        )
                );

        assertThat(repository.count()).isEqualTo(2);

        assertThat(result)
                .hasSize(1)
                .containsKey("battle-2")
                .doesNotContainKey("battle-1");
    }

    @Test
    void insertIfAbsentAll_returnsEmptyMapWhenEmpty() {

        Map<String, Long> result =
                repository.insertIfAbsentAll(
                        List.of()
                );

        assertThat(result).isEmpty();
        assertThat(repository.count()).isZero();
    }

    private Match match(
            String battleId
    ) {

        return Match.builder()
                .battleId(battleId)
                .battleAt(Instant.now())
                .battleType(1)
                .gameVersion(100)
                .stageId(1)
                .build();
    }
}