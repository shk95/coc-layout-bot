package io.github.shk95.coclayoutbot.discord.impl.jda.command;

import io.github.shk95.coclayoutbot.discord.domain.ApplicationContext;
import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static io.github.shk95.coclayoutbot.discord.impl.jda.Utils.commandEventLogger;
import static io.github.shk95.coclayoutbot.discord.impl.jda.Utils.getSubscriber;

@Component
public class SubscribeChannelCommand extends SlashCommand {

	private static final String OPTION_NAME = "channel_id";

	protected SubscribeChannelCommand(ApplicationContext applicationContext) {
		super(applicationContext);
	}

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
						return OPTION_NAME;
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

	// TODO: subscriber 가 dm 채널 등의 private channel 일때 무시하도록 추가.
	@Transactional
	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();
		commandEventLogger(event);

		Subscriber subscriber = getSubscriber(event);
		Optional<String> youtubeChannelId = Optional
				.ofNullable(event.getOption(OPTION_NAME))
				.map(OptionMapping::getAsString);

		String invalidChannel = "Please provide a valid youtube channel id.";
		String success = """
				Successfully subscribed to the youtube channel.

				-  Channel Title : %s
				""";
		String failed = "Failed to subscribe to the youtube channel.";

		Function<YoutubeChannel, String> subscribe =
				youtubeChannel -> applicationContext
						.subscriberAction()
						.subscribe(subscriber, youtubeChannel)
						? success.formatted(youtubeChannel.channelTitle())
						: failed;

		String message = youtubeChannelId
				.flatMap(applicationContext::findYoutubeChannel)
				.map(subscribe)
				.orElse(invalidChannel);

		event.getHook().sendMessage(message).queue();
	}

}
