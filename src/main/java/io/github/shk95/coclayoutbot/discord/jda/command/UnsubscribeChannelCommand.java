package io.github.shk95.coclayoutbot.discord.jda.command;

import io.github.shk95.coclayoutbot.discord.jda.SlashCommand;
import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import io.github.shk95.coclayoutbot.domain.user.SubscriberAction;
import io.github.shk95.coclayoutbot.repository.jpa.entity.YoutubeChannelEntity;
import io.github.shk95.coclayoutbot.repository.jpa.entity.YoutubeChannelRepository;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static io.github.shk95.coclayoutbot.discord.jda.command.Utils.commandEventLogger;

@RequiredArgsConstructor
@Component
public class UnsubscribeChannelCommand implements SlashCommand {

	private final SubscriberAction subscriberAction;
	private final YoutubeChannelRepository youtubeChannelRepository;

	@Override
	public String getCommandName() {
		return "unsubscribe_channel";
	}

	@Override
	public String getDescription() {
		return "Unsubscribe to a youtube channel";
	}

	@Override
	public Set<Option> getOptions() {
		return Set.of(
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
						return "The id of the channel to unsubscribe from";
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
		);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		commandEventLogger(event);

		event.deferReply(true).queue();

		String baseMessage;
		long channelId = event.getChannel().getIdLong();
		Long guildId = event.getGuild() == null ? null : event.getGuild().getIdLong();
		var option = Optional.ofNullable(event.getOption("channel_id"));

		if (option.isEmpty()) {
			baseMessage = "Please input the youtube channel id.";
		} else {
			String youtubeChannelIdToUnsubscribe = option.get().getAsString();

			Optional<YoutubeChannel> youtubeChannelToUnsubscribe = youtubeChannelRepository
					.findByChannelId(youtubeChannelIdToUnsubscribe)
					.map(YoutubeChannelEntity::toDomain);

			if (youtubeChannelToUnsubscribe.isEmpty()) {
				baseMessage = "The youtube channel id is not registered.";
			} else {
				var subscriber = new Subscriber(channelId, guildId);
				boolean success = subscriberAction.unsubscribe(subscriber, youtubeChannelToUnsubscribe.get());
				if (success) {
					baseMessage = """
							Successfully unsubscribed to the youtube channel.

							-  Channel Title : %s
							""".formatted(youtubeChannelToUnsubscribe.get().channelTitle());
				} else {
					baseMessage = """
							Failed to unsubscribe to the youtube channel.

							-  Channel Title : %s
							""".formatted(youtubeChannelToUnsubscribe.get().channelTitle());
				}
			}
		}
		String message = baseMessage;
		event.getHook().sendMessage(message).queue();
	}

}
