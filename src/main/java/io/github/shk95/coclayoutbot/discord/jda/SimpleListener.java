package io.github.shk95.coclayoutbot.discord.jda;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class SimpleListener extends ListenerAdapter {

	private final Set<SlashCommand> slashCommands;

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		slashCommands.stream()
				.filter(slashCommand -> slashCommand.getCommandName().equals(event.getName()))
				.findFirst()
				.ifPresent(slashCommand -> {
					try {
						slashCommand.execute(event);
					} catch (Exception e) {
						log.error("Error occurred while executing slash command : [{}]", e.getMessage(), e);
					}
				});
	}

}
