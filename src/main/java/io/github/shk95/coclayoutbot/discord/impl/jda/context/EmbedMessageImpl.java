package io.github.shk95.coclayoutbot.discord.impl.jda.context;

import io.github.shk95.coclayoutbot.domain.user.message.MessageContent;
import io.github.shk95.coclayoutbot.domain.user.message.MessageCreation;
import io.github.shk95.coclayoutbot.domain.user.message.type.EmbedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.commons.collections4.ListUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class EmbedMessageImpl implements EmbedMessage {

	private static final int MAX_EMBEDS = 10;
	private final BiConsumer<Long, Consumer<TextChannel>> action;

	private static List<List<MessageEmbed>> partitioned(Collection<MessageContent.EmbedContent> contents) {
		return ListUtils.partition(
				contents
						.stream()
						.map(content ->
								new EmbedBuilder()
										.setTitle(content.getTitle(), content.getUrl())
										.setDescription(content.getDescription())
										.setImage(content.getImageUrl())
										.build()
						)
						.toList()
				,
				MAX_EMBEDS);
	}

	@Override
	public void send(MessageCreation<MessageContent.EmbedContent> messageCreation) {
		long channelId = messageCreation.getChannelId();
		List<List<MessageEmbed>> messageEmbeds = partitioned(messageCreation.getContents());

		Consumer<TextChannel> send = channel ->
				messageEmbeds.forEach(
						embeds ->
								channel
										.sendMessageEmbeds(embeds)
										.queue(
												success ->
														log.info("Message sent. Discord Channel Id : [{}]", channelId)
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
