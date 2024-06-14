package io.github.shk95.coclayoutbot.discord.jda;

import io.github.shk95.coclayoutbot.discord.DiscordContext;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

import java.util.Optional;

@Slf4j
public class JdaContext implements DiscordContext {

	private final JDA jda;

	public JdaContext(JDA jda) {
		this.jda = jda;
	}

	@Override
	public SendMessageConsumer message() {
		return (channelId, message) ->
				Optional.ofNullable(jda.getTextChannelById(channelId))
						.ifPresentOrElse(
								channel -> channel
										.sendMessage(message)
										.queue(msg ->
												log.info("Message sent. Discord Channel Id : [{}]", channelId)
										),
								() -> log.warn("Channel not found. Discord Channel Id : [{}]", channelId)
						);
	}

}
