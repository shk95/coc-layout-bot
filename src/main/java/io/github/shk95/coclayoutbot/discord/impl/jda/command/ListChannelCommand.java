package io.github.shk95.coclayoutbot.discord.impl.jda.command;

import io.github.shk95.coclayoutbot.discord.domain.ApplicationContext;
import io.github.shk95.coclayoutbot.discord.domain.YoutubeChannelAvailable;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.Set;

import static io.github.shk95.coclayoutbot.discord.impl.jda.Utils.commandEventLogger;

@Slf4j
@Component
public class ListChannelCommand extends SlashCommand {

	protected ListChannelCommand(ApplicationContext applicationContext) {
		super(applicationContext);
	}

	@Override
	public String getCommandName() {
		return "list_channel";
	}

	@Override
	public String getDescription() {
		return "List all channels available to subscribe";
	}

	@Override
	public Set<Option> getOptions() {
		return Set.of();
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();
		commandEventLogger(event);

		String message = """
				Available Youtube Channels

				""";

		message = applicationContext
				.getYoutubeChannels()
				.stream()
				.map(YoutubeChannelAvailable::from)
				.sorted()
				.map(YoutubeChannelAvailable::toString)
				.reduce(message, String::concat);

		event.getHook().sendMessage(message).queue();
	}

}
