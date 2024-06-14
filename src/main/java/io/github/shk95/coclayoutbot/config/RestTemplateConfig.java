package io.github.shk95.coclayoutbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
				.setReadTimeout(Duration.ofSeconds(50))
				.setConnectTimeout(Duration.ofSeconds(50))
				.build();
	}

	@Primary
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

}
