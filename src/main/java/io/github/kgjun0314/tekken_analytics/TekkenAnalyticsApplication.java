package io.github.kgjun0314.tekken_analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class TekkenAnalyticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TekkenAnalyticsApplication.class, args);
	}

}
