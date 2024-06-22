package io.github.shk95.coclayoutbot.discord.impl.jda.command;

import io.github.shk95.coclayoutbot.discord.domain.ApplicationContext;
import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static io.github.shk95.coclayoutbot.discord.impl.jda.Utils.commandEventLogger;
import static io.github.shk95.coclayoutbot.discord.impl.jda.Utils.getSubscriber;

@Component
public class UnsubscribeChannelCommand extends SlashCommand {

	protected UnsubscribeChannelCommand(ApplicationContext applicationContext) {
		super(applicationContext);
	}

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
		event.deferReply(true).queue();
		commandEventLogger(event);

		Subscriber subscriber = getSubscriber(event);
		Optional<String> youtubeChannelId = Optional
				.ofNullable(event.getOption("channel_id"))
				.map(OptionMapping::getAsString);

		String invalidChannel = "Please provide a valid youtube channel id.";
		String success = """
				Successfully unsubscribed to the youtube channel.

				-  Channel Title : %s
				""";
		String failed = """
				Failed to unsubscribe to the youtube channel.

				-  Channel Title : %s
				""";

		Function<YoutubeChannel, String> unsubscribe =
				youtubeChannel -> applicationContext
						.subscriberAction()
						.unsubscribe(subscriber, youtubeChannel)
						? success.formatted(youtubeChannel.channelTitle())
						: failed.formatted(youtubeChannel.channelTitle());

		String message = youtubeChannelId
				.flatMap(applicationContext::findYoutubeChannel)
				.map(unsubscribe)
				.orElse(invalidChannel);

		event.getHook().sendMessage(message).queue();
	}

}
