package io.github.shk95.coclayoutbot.service;

import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import io.github.shk95.coclayoutbot.domain.user.SubscriberAction;
import io.github.shk95.coclayoutbot.repository.jpa.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriberActionImpl implements SubscriberAction {

	private final SubscriberDiscordChannelRepository subscriberDiscordChannelRepository;
	private final SubscribedChannelRepository subscribedChannelRepository;
	private final YoutubeChannelRepository youtubeChannelRepository;

	@Transactional(propagation = Propagation.MANDATORY)
	@Override
	public SubscriberDiscordChannelEntity register(Subscriber subscriber) {
		SubscriberDiscordChannelEntity newSubscriber =
				SubscriberDiscordChannelEntity.builder()
						.channelId(subscriber.channelId())
						.guildId(subscriber.guildId())
						.build();
		return subscriberDiscordChannelRepository.save(newSubscriber);
	}

	@Transactional
	@Override
	public boolean subscribe(Subscriber subscriber, YoutubeChannel youtubeChannel) {
		SubscriberDiscordChannelEntity subscriberFound = subscriberDiscordChannelRepository
				.findById(subscriber.channelId())
				.orElseGet(() -> register(subscriber));

		Optional<YoutubeChannelEntity> youtubeChannelToSubscribe = youtubeChannelRepository
				.findByChannelId(youtubeChannel.channelId());

		if (youtubeChannelToSubscribe.isEmpty()) { // if the channel is not registered
			return false;
		}

		Optional<SubscribedChannelEntity> subscribedYoutubeChannel = subscribedChannelRepository
				.findById(new SubscribedChannelEntity.SubscribedChannelId(
						youtubeChannelToSubscribe.get(),
						subscriberFound)
				);

		if (subscribedYoutubeChannel.isPresent()) { // if the channel is already subscribed
			subscribedYoutubeChannel.get().setupTrue(); // if the channel is not setup, set it to setup
			subscribedYoutubeChannel.get().touchLastFetchedAt(Instant.now());
			return true;
		}

		SubscribedChannelEntity newYoutubeChannelToSubscribe = SubscribedChannelEntity.builder()
				.subscriber(subscriberFound)
				.youtubeChannel(youtubeChannelToSubscribe.get())
				.setup(true)
				.lastFetchedAt(Instant.now().minus(1, java.time.temporal.ChronoUnit.DAYS))
				.build();

		subscribedChannelRepository.save(newYoutubeChannelToSubscribe);
		return true;
	}

	@Transactional
	@Override
	public boolean unsubscribe(Subscriber subscriber, YoutubeChannel youtubeChannel) {
		Optional<SubscriberDiscordChannelEntity> subscriberFound = subscriberDiscordChannelRepository
				.findById(subscriber.channelId());

		if (subscriberFound.isEmpty()) { // if the subscriber is not registered
			return false;
		}

		Optional<SubscribedChannelEntity> youtubeChannelToUnsubscribe = subscribedChannelRepository
				.findBySubscriber_ChannelIdAndYoutubeChannel_ChannelId(
						subscriberFound.get().getChannelId(),
						youtubeChannel.channelId());

		if (youtubeChannelToUnsubscribe.isEmpty()) {
			return false;
		}

		youtubeChannelToUnsubscribe.get().setupFalse();
		subscribedChannelRepository.save(youtubeChannelToUnsubscribe.get());
		return true;
	}

	// TODO: 예상치못한 예외에대한 처리 필요.
	@Transactional
	@Override
	public void unsubscribeAll(Subscriber subscriber) {
		subscribedChannelRepository.deleteAllBySubscriber_ChannelId(subscriber.channelId());
		subscriberDiscordChannelRepository.deleteById(subscriber.channelId());
	}

}
