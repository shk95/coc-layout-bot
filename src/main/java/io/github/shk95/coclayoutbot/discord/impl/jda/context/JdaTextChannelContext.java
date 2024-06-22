package io.github.shk95.coclayoutbot.discord.impl.jda.context;

import io.github.shk95.coclayoutbot.discord.domain.DiscordContext;
import io.github.shk95.coclayoutbot.domain.user.message.type.EmbedMessage;
import io.github.shk95.coclayoutbot.domain.user.message.type.TextMessage;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class JdaTextChannelContext implements DiscordContext.TextChannelContext {

	private final BiConsumer<Long, Consumer<TextChannel>> action;

	public JdaTextChannelContext(@NotNull Function<Long, Optional<TextChannel>> channelById) {
		this.action = (channelId, consumer) ->
				channelById
						.apply(channelId)
						.ifPresentOrElse(
								consumer,
								() -> log.warn("Channel not found. Discord Channel Id : [{}]", channelId)
						);
	}

	@Override
	public EmbedMessage embedMessage() {
		return new EmbedMessageImpl(action);
	}

	@Override
	public TextMessage textMessage() {
		return new TextMessageImpl(action);
	}

}
