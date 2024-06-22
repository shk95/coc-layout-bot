package io.github.shk95.coclayoutbot.discord.impl.jda.command;

import io.github.shk95.coclayoutbot.discord.domain.ApplicationContext;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Set;

public abstract class SlashCommand {

	protected final ApplicationContext applicationContext;

	protected SlashCommand(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public abstract String getCommandName();

	public abstract String getDescription();

	public abstract Set<Option> getOptions();

	public abstract void execute(SlashCommandInteractionEvent event);

	public interface Option {

		OptionType getType();

		String getName();

		String getDescription();

		boolean isRequired();

		boolean isAutocomplete();

	}

}
