package io.github.shk95.coclayoutbot.service;

import io.github.shk95.coclayoutbot.discord.domain.ApplicationContext;
import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import io.github.shk95.coclayoutbot.domain.user.SubscriberAction;
import io.github.shk95.coclayoutbot.repository.jpa.entity.SubscribedChannelEntity;
import io.github.shk95.coclayoutbot.repository.jpa.entity.SubscribedChannelRepository;
import io.github.shk95.coclayoutbot.repository.jpa.entity.YoutubeChannelEntity;
import io.github.shk95.coclayoutbot.repository.jpa.entity.YoutubeChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DiscordServiceAdapter implements ApplicationContext {

	private final SubscriberAction subscriberAction;
	private final SubscribedChannelRepository subscribedChannelRepository;
	private final YoutubeChannelRepository youtubeChannelRepository;

	@Override
	public SubscriberAction subscriberAction() {
		return subscriberAction;
	}

	@Transactional
	@Override
	public void deleteSubscriber(Subscriber subscriber) {
		long channelId = subscriber.channelId();
		Long guildId = subscriber.guildId();
		if (!subscribedChannelRepository.existsBySubscribedChannelId_SubscriberChannelId(channelId)) {
			log.info("Catch the channel delete event but the channel is not subscribed. Channel Id : [{}], Guild Id : [{}]",
					channelId, guildId);
			return;
		}
		log.info("Catch the channel delete event and unsubscribed all the channels. Channel Id : [{}], Guild Id : [{}]",
				channelId, guildId);
		subscriberAction.unsubscribeAll(subscriber);
	}

	@Transactional(readOnly = true)
	@Override
	public List<YoutubeChannel> getYoutubeChannels() {
		return youtubeChannelRepository.findAll()
				.stream()
				.map(YoutubeChannelEntity::toDomain)
				.toList();
	}

	@Transactional(readOnly = true)
	@Override
	public List<YoutubeChannel> getSubscribedYoutubeChannels(Subscriber subscriber) {
		long channelId = subscriber.channelId();
		return subscribedChannelRepository
				.findAllBySubscriber_ChannelIdAndSetupTrue(channelId)
				.stream()
				.map(SubscribedChannelEntity::getYoutubeChannel)
				.map(YoutubeChannelEntity::toDomain)
				.toList();
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<YoutubeChannel> findYoutubeChannel(String youtubeChannelId) {
		return youtubeChannelRepository
				.findByChannelId(youtubeChannelId)
				.map(YoutubeChannelEntity::toDomain);
	}

}
