package io.github.shk95.coclayoutbot.discord.jda.command;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

@Slf4j
class Utils {

	static void commandEventLogger(GenericCommandInteractionEvent event) {
		long channelId = event.getChannelIdLong();
		Long guildId = null;
		var guild = event.getGuild();
		if (guild != null) {
			guildId = guild.getIdLong();
		}
		String commandName = event.getCommandString();
		log.info("command : [{}] | channelId : [{}] | guildId : [{}]", commandName, channelId, guildId);
	}

}
