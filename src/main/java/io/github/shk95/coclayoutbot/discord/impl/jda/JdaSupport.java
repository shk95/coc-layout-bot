package io.github.shk95.coclayoutbot.discord.impl.jda;

import io.github.shk95.coclayoutbot.config.ApplicationProperties;
import io.github.shk95.coclayoutbot.discord.domain.DiscordSupport;
import io.github.shk95.coclayoutbot.discord.impl.jda.command.SlashCommand;
import io.github.shk95.coclayoutbot.discord.impl.jda.listener.SimpleListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Slf4j
@Configuration
public class JdaSupport implements DiscordSupport {

	private final JDA jda;

	public JdaSupport(ApplicationProperties applicationProperties,
	                  Set<SlashCommand> slashCommands,
	                  SimpleListener simpleListener
	) {
		final String DISCORD_TOKEN = applicationProperties.getSecret().getDiscordToken();

		this.jda = JDABuilder.createDefault(DISCORD_TOKEN)
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.setStatus(OnlineStatus.ONLINE)
				.setActivity(Activity.customStatus("waiting..."))
				.addEventListeners(simpleListener)
				.setChunkingFilter(ChunkingFilter.NONE)
				.setMemberCachePolicy(MemberCachePolicy.NONE).build()
				.updateCommands()
				.addCommands(Utils.toSlashCommandDataList(slashCommands))
				.getJDA();
	}

	@PostConstruct
	@Override
	public void init() {
		try {
			log.info("Initializing JDA...");
			JDA jda = this.jda.awaitReady();
			if (!jda.getStatus().isInit()) throw new Exception("The JDA is not initialized.");
			log.info("JDA initialized.");
		} catch (Exception e) {
			log.error("JDA initialization failed. Shutting down the application...", e);
			System.exit(1);
		}
	}

	@PreDestroy
	@Override
	public void shutdown() {
		log.info("Shutting down JDA...");
		this.jda.shutdown();
	}

	@Bean
	public JDA jda() {
		return this.jda;
	}

}
