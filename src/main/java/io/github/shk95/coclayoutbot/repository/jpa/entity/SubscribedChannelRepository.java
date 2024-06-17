package io.github.shk95.coclayoutbot.repository.jpa.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface SubscribedChannelRepository extends
		JpaRepository<SubscribedChannelEntity, SubscribedChannelEntity.SubscribedChannelId> {

	Set<SubscribedChannelEntity> findAllBySubscriber_ChannelIdAndSetupTrue(Long subscriberId);

	Optional<SubscribedChannelEntity> findBySubscriber_ChannelIdAndYoutubeChannel_ChannelId(Long subscriberId, String channelId);

	boolean existsBySubscribedChannelId_SubscriberChannelId(Long subscriberChannelId);

	void deleteAllBySubscriber_ChannelId(Long subscriberChannelId);

}
