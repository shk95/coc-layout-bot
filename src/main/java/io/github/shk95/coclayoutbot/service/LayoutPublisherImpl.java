package io.github.shk95.coclayoutbot.service;

import io.github.shk95.coclayoutbot.discord.domain.DiscordContext;
import io.github.shk95.coclayoutbot.domain.LayoutPublisher;
import io.github.shk95.coclayoutbot.domain.layout.Layout;
import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import io.github.shk95.coclayoutbot.domain.user.message.MessageContent;
import io.github.shk95.coclayoutbot.domain.user.message.MessageCreation;
import io.github.shk95.coclayoutbot.repository.jpa.entity.LayoutEntity;
import io.github.shk95.coclayoutbot.repository.jpa.entity.LayoutRepository;
import io.github.shk95.coclayoutbot.repository.jpa.entity.SubscribedChannelEntity;
import io.github.shk95.coclayoutbot.repository.jpa.entity.SubscribedChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LayoutPublisherImpl implements LayoutPublisher {

	private final DiscordContext discordContext;
	private final SubscribedChannelRepository subscribedChannelRepository;
	private final LayoutRepository layoutRepository;

	private static @NotNull String createMessage(Layout layout) {
		String channelTitle = layout.youtubeVideo().youtubeVideoInfoPart().youtubeChannel().channelTitle();
		String publishedAt = layout.youtubeVideo().youtubeVideoInfoPart().publishedAt().toString();
		String timestamp = layout.detail().timestamp().toString();
		int part = layout.detail().imgPart();

		// TODO: Implement a better way to publish the layout
		return """
				Channel Title : %s
				Published Date : %s
				Timestamp : %s
				Layout Part : %s
				""".formatted(channelTitle, publishedAt, timestamp, part);
	}

	@Transactional
	@Override
	public void publish() {
		List<SubscribedChannelEntity> subscribedChannelEntities = subscribedChannelRepository.findAll();
		subscribedChannelEntities
				.forEach(subscribedChannelEntity -> {
					if (!subscribedChannelEntity.isSetup()) return;

					Instant now = Instant.now();
					Subscriber subscriber = subscribedChannelEntity.toSubscriberDomain();
					YoutubeChannel subscribedYoutubeChannel = subscribedChannelEntity.toSubscribedYoutubeChannelDomain();
					String subscribedYoutubeChannelId = subscribedYoutubeChannel.channelId();
					Instant lastFetchedAt = subscribedChannelEntity.getLastFetchedAt();
					if (lastFetchedAt == null) {
						lastFetchedAt = now.minus(2, ChronoUnit.DAYS);
					}

					List<Layout> layoutsAfterLastFetchedAt = layoutRepository
							.findAllByYoutubeVideoEntity_YoutubeChannel_ChannelIdAndYoutubeVideoEntity_PublishedAtAfterAndErrorCountIsOrderByYoutubeVideoEntity_PublishedAtAscTimestampAscImgPartAsc
									(subscribedYoutubeChannelId, lastFetchedAt, 0)
							.stream()
							.map(LayoutEntity::toDomain)
							.toList();

					List<MessageContent.EmbedContent> embedContents = layoutsAfterLastFetchedAt
							.stream()
							.map(layout -> MessageContent.EmbedContent.builder()
									.title("Click here to see the layout!")
									.description(createMessage(layout))
									.imageUrl(layout.detail().layoutImgUrl())
									.url(layout.detail().layoutUrl())
									.build())
							.toList();
					MessageCreation<MessageContent.EmbedContent> messageCreation = new MessageCreation<>(subscriber.channelId(), embedContents);

					discordContext
							.textChannel()
							.embedMessage()
							.send(messageCreation);

					subscribedChannelEntity.touchLastFetchedAt(now);
					subscribedChannelRepository.saveAndFlush(subscribedChannelEntity);
				});
	}

}
