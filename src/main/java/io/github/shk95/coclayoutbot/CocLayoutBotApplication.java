package io.github.shk95.coclayoutbot;

import io.github.shk95.coclayoutbot.config.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableConfigurationProperties(ApplicationProperties.class)
@SpringBootApplication
public class CocLayoutBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(CocLayoutBotApplication.class, args);
	}
	// TODO: 사용하는 api 의 상태를 확인하여 예외처리를 추가(youtube api, yt media extractor api. api 사용불가시 레이아웃 처리 관련)

}
