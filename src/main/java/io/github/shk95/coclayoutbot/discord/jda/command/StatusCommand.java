package io.github.shk95.coclayoutbot.discord.jda.command;

import io.github.shk95.coclayoutbot.discord.jda.SlashCommand;
import io.github.shk95.coclayoutbot.repository.jpa.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static io.github.shk95.coclayoutbot.discord.jda.command.Utils.commandEventLogger;

@Slf4j
@RequiredArgsConstructor
@Component
public class StatusCommand implements SlashCommand {

	private final SubscriberDiscordChannelRepository subscriberDiscordChannelRepository;
	private final SubscribedChannelRepository subscribedChannelRepository;

	public String getCommandName() {
		return "status";
	}

	@Override
	public String getDescription() {
		return "Show the subscribed youtube channel list.";
	}

	@Override
	public Set<Option> getOptions() {
		return Set.of();
	}

	@Transactional(readOnly = true)
	@Override
	public void execute(SlashCommandInteractionEvent event) {
		commandEventLogger(event);

		event.deferReply(true).queue();

		long channelId = event.getChannelIdLong();
		String baseMessage = """
				Subscribed Youtube Channels

				""";

		Optional<SubscriberDiscordChannelEntity> subscriber = subscriberDiscordChannelRepository.findById(channelId);
		if (subscriber.isEmpty()) {
			baseMessage = baseMessage.concat("-  No subscribed youtube channel.");
		} else {
			Set<SubscribedChannelEntity> subscribedYoutubeChannels = subscribedChannelRepository
					.findAllBySubscriber_ChannelIdAndSetupTrue(
							subscriber.get().getChannelId()
					);
			if (subscribedYoutubeChannels.isEmpty())
				baseMessage = baseMessage.concat("-  No subscribed youtube channel.");
			else {
				baseMessage = subscribedYoutubeChannels.stream()
						.map(SubscribedChannelEntity::getYoutubeChannel)
						.map(SubscribedChannel::from)
						.map(SubscribedChannel::toString)
						.reduce(baseMessage, String::concat);
			}
		}

		final String message = baseMessage;
		event.getHook().sendMessage(message).queue();
	}

	private record SubscribedChannel(
			String channelTitle,
			String channelId,
			Instant lastUpdateAt) {

		private static SubscribedChannel from(YoutubeChannelEntity youtubeChannelEntity) {
			return new SubscribedChannel(
					youtubeChannelEntity.getChannelTitle(),
					youtubeChannelEntity.getChannelId(),
					youtubeChannelEntity.getLastUpdateAt()
			);
		}

		@Override
		public String toString() {
			return """
					-  Channel Title : %s  |  Last Update : %s  |  Channel Id : %s
					""".formatted(channelTitle, lastUpdateAt, channelId);
		}

	}

}
