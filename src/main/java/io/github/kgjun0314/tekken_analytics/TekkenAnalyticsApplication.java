package io.github.kgjun0314.tekken_analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TekkenAnalyticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TekkenAnalyticsApplication.class, args);
	}

}
