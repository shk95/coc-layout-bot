package io.github.shk95.coclayoutbot.discord.impl.jda.context;

import io.github.shk95.coclayoutbot.domain.user.message.MessageContent;
import io.github.shk95.coclayoutbot.domain.user.message.MessageCreation;
import io.github.shk95.coclayoutbot.domain.user.message.type.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class TextMessageImpl implements TextMessage {

	private final BiConsumer<Long, Consumer<TextChannel>> action;

	@Override
	public void send(MessageCreation<MessageContent.TextContent> messageCreation) {
		long channelId = messageCreation.getChannelId();

		Consumer<TextChannel> send =
				channel ->
						messageCreation.getContents()
								.forEach(content ->
										channel
												.sendMessage(content.getTextContent())
												.queue(
														success ->
																log.info("Message sent. Discord Channel Id : [{}]",
																		channelId)
														,
														failure ->
																log.error("Error occurred while sending message : [{}]",
																		failure.getMessage(), failure)
												)
								);

		assert this.action != null;
		this.action.accept(channelId, send);
	}

}
