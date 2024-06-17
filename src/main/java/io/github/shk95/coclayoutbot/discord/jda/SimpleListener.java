package io.github.shk95.coclayoutbot.discord.jda;

import io.github.shk95.coclayoutbot.domain.Subscriber;
import io.github.shk95.coclayoutbot.domain.SubscriberAction;
import io.github.shk95.coclayoutbot.repository.jpa.entity.SubscribedChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class SimpleListener extends ListenerAdapter {

	private final Set<SlashCommand> slashCommands;
	private final SubscriberAction subscriberAction;
	private final SubscribedChannelRepository subscribedChannelRepository;

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

	@Transactional
	@Override
	public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
		super.onChannelDelete(event);
		long channelId = event.getChannel().getIdLong();
		long guildId = event.getGuild().getIdLong();

		if (!subscribedChannelRepository.existsBySubscribedChannelId_SubscriberChannelId(channelId)) {
			log.info("Catch the channel delete event but the channel is not subscribed. " +
					"Channel Id : [{}], Guild Id : [{}]", channelId, guildId);
			return;
		}

		log.info("Catch the channel delete event and unsubscribed all the channels." +
				" Channel Id : [{}], Guild Id : [{}]", channelId, guildId);
		Subscriber subscriber = Subscriber.builder()
				.channelId(channelId)
				.guildId(guildId).build();
		subscriberAction.unsubscribeAll(subscriber);
	}

}
