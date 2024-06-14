package io.github.shk95.coclayoutbot.discord.jda.command;

import io.github.shk95.coclayoutbot.discord.jda.SlashCommand;
import io.github.shk95.coclayoutbot.domain.Subscriber;
import io.github.shk95.coclayoutbot.domain.SubscriberAction;
import io.github.shk95.coclayoutbot.domain.YoutubeChannel;
import io.github.shk95.coclayoutbot.repository.jpa.entity.YoutubeChannelEntity;
import io.github.shk95.coclayoutbot.repository.jpa.entity.YoutubeChannelRepository;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static io.github.shk95.coclayoutbot.discord.jda.command.Utils.commandEventLogger;

@RequiredArgsConstructor
@Component
public class SubscribeChannelCommand implements SlashCommand {

	private final SubscriberAction subscriberAction;
	private final YoutubeChannelRepository youtubeChannelRepository;

	@Override
	public String getCommandName() {
		return "subscribe_channel";
	}

	@Override
	public String getDescription() {
		return "Subscribe to a youtube channel. Input the youtube channel id.";
	}

	@Override
	public Set<Option> getOptions() {
		return new HashSet<>(Set.of(
				new Option() {
					@Override
					public OptionType getType() {
						return OptionType.STRING;
					}

					@Override
					public String getName() {
						return "channel_id";
					}

					@Override
					public String getDescription() {
						return "Youtube Channel ID";
					}

					@Override
					public boolean isRequired() {
						return true;
					}

					@Override
					public boolean isAutocomplete() {
						return false;
					}
				}
		));
	}

	@Transactional
	@Override
	public void execute(SlashCommandInteractionEvent event) {
		commandEventLogger(event);

		event.deferReply(true).queue();

		String baseMessage;
		long channelId = event.getChannelIdLong();
		Long guildId = event.getGuild() == null ? null : event.getGuild().getIdLong();
		var option = Optional.ofNullable(event.getOption("channel_id"));

		if (option.isEmpty()) {
			baseMessage = "Please input the youtube channel id.";
		} else {
			String youtubeChannelIdToSubscribe = option.get().getAsString();

			Optional<YoutubeChannel> youtubeChannelToSubscribe = youtubeChannelRepository
					.findByChannelId(youtubeChannelIdToSubscribe)
					.map(YoutubeChannelEntity::toDomain);

			if (youtubeChannelToSubscribe.isEmpty()) {
				baseMessage = "No youtube channel found with the given id.";
			} else {
				var subscriber = new Subscriber(channelId, guildId);
				boolean success = subscriberAction.subscribe(subscriber, youtubeChannelToSubscribe.get());
				if (!success) {
					baseMessage = "Failed to subscribe to the youtube channel.";
				} else {
					baseMessage = """
							Successfully subscribed to the youtube channel.

							-  Channel Title : %s
							""".formatted(youtubeChannelToSubscribe.get().channelTitle());
				}
			}
		}

		final String message = baseMessage;
		event.getHook().sendMessage(message).queue();
	}

}
