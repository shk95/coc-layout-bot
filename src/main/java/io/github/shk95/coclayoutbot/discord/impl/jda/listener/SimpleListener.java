package io.github.shk95.coclayoutbot.discord.impl.jda.listener;

import io.github.shk95.coclayoutbot.discord.domain.ApplicationContext;
import io.github.shk95.coclayoutbot.discord.impl.jda.Utils;
import io.github.shk95.coclayoutbot.discord.impl.jda.command.SlashCommand;
import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class SimpleListener extends ListenerAdapter {

	private final ApplicationContext applicationContext;
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

	@Override
	public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
		super.onChannelDelete(event);
		Subscriber subscriber = Utils.getSubscriber(event);
		applicationContext.deleteSubscriber(subscriber);
	}

}
