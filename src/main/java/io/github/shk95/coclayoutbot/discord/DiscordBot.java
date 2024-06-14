package io.github.shk95.coclayoutbot.discord;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DiscordBot {

	private final DiscordSupport discordSupport;

	public DiscordBot(@Qualifier("jdaSupport") DiscordSupport discordSupport) {
		this.discordSupport = discordSupport;
	}

	@PostConstruct
	public void startBot() {
		try {
			discordSupport.start();
		} catch (InterruptedException e) {
			log.error("Thread was interrupted while starting the Discord bot.", e);
		} catch (Exception e) {
			log.error("Failed to initialize Discord service. {}", e.getMessage(), e);
		}
	}

	@PreDestroy
	public void stopBot() {
		log.info("Stopping the Discord bot...");
		discordSupport.stop();
	}

}
