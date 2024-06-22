package io.github.shk95.coclayoutbot.discord.impl.jda;

import io.github.shk95.coclayoutbot.discord.impl.jda.command.SlashCommand;
import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.channel.GenericChannelEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collection;
import java.util.List;

@Slf4j
public class Utils {

	public static List<SlashCommandData> toSlashCommandDataList(Collection<SlashCommand> slashCommands) {
		return slashCommands
				.stream()
				.distinct()
				.map(slashCommand -> {
							var commandData = Commands.slash(
									slashCommand.getCommandName(),
									slashCommand.getDescription()
							);
							slashCommand.getOptions()
									.forEach(option ->
											commandData.addOption(
													option.getType(),
													option.getName(),
													option.getDescription(),
													option.isRequired(),
													option.isAutocomplete()
											)
									);
							return commandData;
						}
				)
				.toList();
	}

	public static <T extends CommandInteractionPayload> void commandEventLogger(T event) {
		Subscriber subscriber = getSubscriber(event);
		String commandName = event.getCommandString();
		log.info("command : [{}] | channelId : [{}] | guildId : [{}]",
				commandName, subscriber.channelId(), subscriber.guildId()
		);
	}

	public static <T extends Interaction> Subscriber getSubscriber(T event) {
		long channelId = event.getChannelIdLong();
		Long guildId = null;
		var guild = event.getGuild();
		if (guild != null) {
			guildId = guild.getIdLong();
		}
		return createSubscriber(channelId, guildId);
	}

	public static <T extends GenericChannelEvent> Subscriber getSubscriber(T event) {
		long channelId = event.getChannel().getIdLong();
		long guildId = event.getGuild().getIdLong();
		return createSubscriber(channelId, guildId);
	}

	private static Subscriber createSubscriber(long channelId, Long guildId) {
		return Subscriber.builder()
				.channelId(channelId)
				.guildId(guildId).build();
	}

}
