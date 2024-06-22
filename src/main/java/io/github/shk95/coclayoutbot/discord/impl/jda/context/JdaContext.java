package io.github.shk95.coclayoutbot.discord.impl.jda.context;

import io.github.shk95.coclayoutbot.discord.domain.DiscordContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Component
public class JdaContext implements DiscordContext {

	private final JDA jda;

	@Override
	public TextChannelContext textChannel() {
		BiFunction<JDA, Long, Optional<TextChannel>> channel =
				(jda, channelId) -> Optional.ofNullable(jda.getTextChannelById(channelId));
		Function<Long, Optional<TextChannel>> channelById = channelId -> channel.apply(this.jda, channelId);
		return new JdaTextChannelContext(channelById);
	}

}
