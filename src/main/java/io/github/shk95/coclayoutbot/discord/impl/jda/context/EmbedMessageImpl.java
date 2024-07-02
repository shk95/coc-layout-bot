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
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class EmbedMessageImpl implements EmbedMessage {

	private final BiConsumer<Long, Consumer<TextChannel>> action;

	// Embed 메시지는 최대 10개씩만 보낼수 있음을 주의.
	private static List<List<MessageEmbed>> partitioned(Collection<MessageContent.EmbedContent> contents) {
		return contents.stream()
				.collect(Collectors.groupingBy(MessageContent.EmbedContent::getUrl))
				.values()
				.stream()
				.sorted(Comparator.comparing(l -> l.get(0)))
				.map(list -> list.stream()
						.sorted(MessageContent::compareTo)
						.toList()
				)
				.map(list -> list.stream()
						.map(EmbedMessageImpl::createMessageEmbed)
						.toList()
				)
				.toList();
	}

	private static MessageEmbed createMessageEmbed(MessageContent.EmbedContent content) {
		return new EmbedBuilder()
				.setTitle(content.getTitle(), content.getUrl())
				.setDescription(content.getDescription())
				.setImage(content.getImageUrl())
				.build();
	}

	@Override
	public void send(MessageCreation<MessageContent.EmbedContent> messageCreation) {
		long channelId = messageCreation.getChannelId();
		List<List<MessageEmbed>> messageEmbeds = partitioned(messageCreation.getContents());

		BiConsumer<TextChannel, List<MessageEmbed>> queue = (channel, embeds) ->
				channel
						.sendMessageEmbeds(embeds)
						.queue(
								success ->
										log.info("Message sent. Discord Channel Id : [{}]", channel.getIdLong())
								,
								failure ->
										log.error("Error occurred while sending message : [{}]",
												failure.getMessage(), failure)
						);

		Consumer<TextChannel> send =
				channel ->
						messageEmbeds.forEach(
								embeds -> {
									if (embeds.size() > 10) {
										ListUtils.partition(embeds, 10)
												.forEach(partition -> queue.accept(channel, partition));
									} else {
										queue.accept(channel, embeds);
									}
								}
				);

		assert this.action != null;
		this.action.accept(channelId, send);
	}

}
