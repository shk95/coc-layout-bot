package io.github.shk95.coclayoutbot.discord.jda.command;

import io.github.shk95.coclayoutbot.discord.jda.SlashCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.Set;

import static io.github.shk95.coclayoutbot.discord.jda.command.Utils.commandEventLogger;

@Slf4j
@Component
public class HelpCommand implements SlashCommand {

	@Override
	public String getCommandName() {
		return "help";
	}

	@Override
	public String getDescription() {
		return "show help message.";
	}

	@Override
	public Set<Option> getOptions() {
		return Set.of();
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		commandEventLogger(event);

		event.reply("""
				all commands are available in the bot.
				subscribe to a youtube channel to get a clash of clans layout in the channel.
				when a new video is uploaded to the youtube channel, the bot will send a message to the channel.
				- /help : show this message.
				- /status : show the subscribed youtube channel list.
				- /list_channel : show the list of youtube channels available to subscribe.
				- /subscribe_channel : subscribe to a youtube channel.
				- /unsubscribe_channel : unsubscribe to a youtube channel.
				""").setEphemeral(true).queue();
	}

}
