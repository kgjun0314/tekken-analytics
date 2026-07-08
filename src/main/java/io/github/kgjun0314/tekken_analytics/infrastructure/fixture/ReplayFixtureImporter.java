package io.github.kgjun0314.tekken_analytics.infrastructure.fixture;

import io.github.kgjun0314.tekken_analytics.infrastructure.benchmark.ReplayBenchmarkService;
import io.github.kgjun0314.tekken_analytics.infrastructure.rabbitmq.producer.ReplayProducer;
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
    private final ReplayBenchmarkService benchmarkService;

    @Override
    public void run(String... args) throws Exception {

        ClassPathResource resource =
                new ClassPathResource("fixtures/response.json");

        List<WankReplayResponse> responses;

        try (InputStream inputStream = resource.getInputStream()) {
            responses = objectMapper.readValue(
                    inputStream,
                    new TypeReference<>() {}
            );
        }

        log.info("Loaded {} replay fixtures.", responses.size());

//        StopWatch stopWatch = new StopWatch("Replay Fixture Import");

//        stopWatch.start();

        benchmarkService.start(responses.size());

        responses.stream()
                .map(replayMapper::toReplay)
                .forEach(replayProducer::publish);

//        stopWatch.stop();

//        log.info("Replay fixture import completed.");
//        log.info("Published {} replay events.", responses.size());
//        log.info("Elapsed Time : {} ms", stopWatch.getTotalTimeMillis());
//        log.info("Elapsed Time : {} sec", stopWatch.getTotalTimeSeconds());
    }
}