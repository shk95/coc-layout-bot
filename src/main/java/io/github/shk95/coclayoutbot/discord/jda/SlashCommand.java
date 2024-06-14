package io.github.shk95.coclayoutbot.discord.jda;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Set;

public interface SlashCommand {

	String getCommandName();

	String getDescription();

	Set<Option> getOptions();

	void execute(SlashCommandInteractionEvent event);

	interface Option {

		OptionType getType();

		String getName();

		String getDescription();

		boolean isRequired();

		boolean isAutocomplete();

	}

}
