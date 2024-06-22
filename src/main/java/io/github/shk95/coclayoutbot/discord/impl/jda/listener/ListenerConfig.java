package io.github.shk95.coclayoutbot.discord.impl.jda.listener;

import io.github.shk95.coclayoutbot.discord.domain.ApplicationContext;
import io.github.shk95.coclayoutbot.discord.impl.jda.command.SlashCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class ListenerConfig {

	private final SimpleListener simpleListener;

	public ListenerConfig(ApplicationContext applicationContext, Set<SlashCommand> slashCommands) {
		this.simpleListener = new SimpleListener(applicationContext, slashCommands);
	}

	@Bean
	public SimpleListener listener() {
		return simpleListener;
	}

}
