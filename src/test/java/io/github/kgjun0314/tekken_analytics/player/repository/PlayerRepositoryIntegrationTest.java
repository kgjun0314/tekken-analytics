package io.github.kgjun0314.tekken_analytics.player.repository;

import io.github.kgjun0314.tekken_analytics.player.repository.dto.PlayerUpsert;
import io.github.kgjun0314.tekken_analytics.player.entity.Player;
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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PlayerRepositoryImpl.class)
@Testcontainers
@ActiveProfiles("test")
class PlayerRepositoryIntegrationTest {

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
        registry.add(
                "spring.datasource.url",
                POSTGRES::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                POSTGRES::getUsername
        );
        registry.add(
                "spring.datasource.password",
                POSTGRES::getPassword
        );
        registry.add(
                "spring.datasource.driver-class-name",
                POSTGRES::getDriverClassName
        );
        registry.add(
                "spring.jpa.hibernate.ddl-auto",
                () -> "create-drop"
        );
    }

    @Autowired
    private PlayerRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setUp() {

        jdbcClient.sql("""
                TRUNCATE TABLE
                    players
                RESTART IDENTITY CASCADE
                """).update();
    }

    @Test
    void upsert_insertsPlayer() {

        // when
        Long id =
                repository.upsert(
                        1L,
                        "polaris-1",
                        "Jun"
                );

        // then
        assertThat(id).isNotNull();

        assertThat(repository.count())
                .isEqualTo(1);

        Player player =
                repository.findByUserId(1L)
                        .orElseThrow();

        assertThat(player.getNickname())
                .isEqualTo("Jun");
    }

    @Test
    void upsert_updatesNickname() {

        // given
        repository.upsert(
                1L,
                "polaris-1",
                "Jun"
        );

        // when
        repository.upsert(
                1L,
                "polaris-1",
                "NewJun"
        );

        // then
        assertThat(repository.count())
                .isEqualTo(1);

        Player player =
                repository.findByUserId(1L)
                        .orElseThrow();

        assertThat(player.getNickname())
                .isEqualTo("NewJun");
    }

    @Test
    void upsertAll_insertsPlayers() {

        // when
        Map<Long, Long> ids =
                repository.upsertAll(
                        List.of(
                                player(1L, "Jun"),
                                player(2L, "Kazuya")
                        )
                );

        // then
        assertThat(repository.count())
                .isEqualTo(2);

        assertThat(ids)
                .hasSize(2)
                .containsKeys(1L, 2L);

        assertThat(ids.values())
                .allMatch(id -> id != null);
    }

    @Test
    void upsertAll_updatesExistingPlayers() {

        // given
        repository.upsertAll(
                List.of(
                        player(1L, "Jun"),
                        player(2L, "Kazuya")
                )
        );

        // when
        Map<Long, Long> ids =
                repository.upsertAll(
                        List.of(
                                player(1L, "NewJun"),
                                player(2L, "NewKazuya")
                        )
                );

        // then
        assertThat(repository.count())
                .isEqualTo(2);

        assertThat(
                repository.findByUserId(1L)
                        .orElseThrow()
                        .getNickname()
        ).isEqualTo("NewJun");

        assertThat(
                repository.findByUserId(2L)
                        .orElseThrow()
                        .getNickname()
        ).isEqualTo("NewKazuya");

        assertThat(ids)
                .hasSize(2)
                .containsKeys(1L, 2L);
    }

    @Test
    void upsertAll_returnsEmptyMap_whenPlayersIsEmpty() {

        // when
        Map<Long, Long> ids =
                repository.upsertAll(
                        List.of()
                );

        // then
        assertThat(ids).isEmpty();

        assertThat(repository.count())
                .isZero();
    }

    private PlayerUpsert player(
            Long userId,
            String nickname
    ) {

        return new PlayerUpsert(
                userId,
                "polaris-" + userId,
                nickname
        );
    }
}