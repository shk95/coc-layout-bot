package io.github.shk95.coclayoutbot.discord.jda;

import io.github.shk95.coclayoutbot.config.ApplicationProperties;
import io.github.shk95.coclayoutbot.discord.DiscordContext;
import io.github.shk95.coclayoutbot.discord.DiscordSupport;
import jakarta.validation.constraints.NotNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("jdaSupport")
public class JdaSupport implements DiscordSupport {

	private final String DISCORD_TOKEN;
	private final SimpleListener simpleListener;
	private final Set<SlashCommand> slashCommands;

	private JDA jda;
	private JdaContext context;

	public JdaSupport(ApplicationProperties properties,
	                  SimpleListener simpleListener,
	                  Set<SlashCommand> slashCommands) {
		this.DISCORD_TOKEN = properties.getSecret().getDiscordToken();
		this.simpleListener = simpleListener;
		this.slashCommands = slashCommands;
	}

	@Override
	public void start() throws InterruptedException {
		List<SlashCommandData> commandDataList = getSlashCommandDataList();

		this.jda = this.jdaBuilder().build();
		this.jda.updateCommands().addCommands(commandDataList).queue();
		this.jda.awaitReady();
	}

	@Override
	public void stop() {
		if (this.jda != null) {
			this.jda.shutdown();
		}
	}

	@Override
	public DiscordContext getContext() {
		if (this.context == null) {
			this.context = new JdaContext(this.jda);
		}
		return this.context;
	}

	private JDABuilder jdaBuilder() {
		return JDABuilder.createDefault(DISCORD_TOKEN)
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.setStatus(OnlineStatus.ONLINE)
				.setActivity(Activity.customStatus("waiting..."))
				.addEventListeners(simpleListener)
				.setChunkingFilter(ChunkingFilter.NONE)
				.setMemberCachePolicy(MemberCachePolicy.NONE);
	}

	private @NotNull List<SlashCommandData> getSlashCommandDataList() {
		return this.slashCommands
				.stream()
				.map(slashCommand -> {
							final var commandData = Commands.slash(
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

}
