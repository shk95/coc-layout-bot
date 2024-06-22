package io.github.shk95.coclayoutbot.discord.impl.jda.command;

import io.github.shk95.coclayoutbot.discord.domain.ApplicationContext;
import io.github.shk95.coclayoutbot.discord.domain.SubscribedYoutubeChannel;
import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static io.github.shk95.coclayoutbot.discord.impl.jda.Utils.commandEventLogger;
import static io.github.shk95.coclayoutbot.discord.impl.jda.Utils.getSubscriber;

@Slf4j
@Component
public class StatusCommand extends SlashCommand {

	protected StatusCommand(ApplicationContext applicationContext) {
		super(applicationContext);
	}

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

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();
		commandEventLogger(event);

		Subscriber subscriber = getSubscriber(event);
		String message = """
				Subscribed Youtube Channels

				""";

		List<SubscribedYoutubeChannel> subscribedYoutubeChannels = applicationContext
				.getSubscribedYoutubeChannels(subscriber)
				.stream()
				.map(SubscribedYoutubeChannel::from)
				.toList();
		if (subscribedYoutubeChannels.isEmpty())
			message = message.concat("-  No subscribed youtube channel.");
		else {
			message = subscribedYoutubeChannels
					.stream()
					.map(SubscribedYoutubeChannel::toString)
					.reduce(message, String::concat);
		}

		event.getHook().sendMessage(message).queue();
	}

}
