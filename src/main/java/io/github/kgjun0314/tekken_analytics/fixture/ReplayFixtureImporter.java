package io.github.kgjun0314.tekken_analytics.fixture;

import io.github.kgjun0314.tekken_analytics.mq.producer.ReplayProducer;
import io.github.kgjun0314.tekken_analytics.replay.dto.WankReplayResponse;
import io.github.kgjun0314.tekken_analytics.replay.mapper.ReplayMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@Profile("fixture")
@RequiredArgsConstructor
public class ReplayFixtureImporter implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final ReplayMapper replayMapper;
    private final ReplayProducer replayProducer;

    @Override
    public void run(String... args) throws Exception {

        ClassPathResource resource =
                new ClassPathResource(
                        "fixtures/response.json"
                );

        try (InputStream is = resource.getInputStream()) {

            List<WankReplayResponse> responses =
                    objectMapper.readValue(
                            is,
                            new TypeReference<>() {
                            }
                    );

            log.info(
                    "Loaded {} replay fixtures.",
                    responses.size()
            );

            responses.stream()
                    .map(replayMapper::toReplay)
                    .forEach(replayProducer::publish);

            log.info("Replay fixture import completed.");
        }
    }
}