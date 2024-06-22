package io.github.shk95.coclayoutbot.discord.domain;

import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import io.github.shk95.coclayoutbot.domain.user.SubscriberAction;

import java.util.List;
import java.util.Optional;

public interface ApplicationContext {

	SubscriberAction subscriberAction();

	void deleteSubscriber(Subscriber subscriber);

	List<YoutubeChannel> getYoutubeChannels();

	List<YoutubeChannel> getSubscribedYoutubeChannels(Subscriber subscriber);

	Optional<YoutubeChannel> findYoutubeChannel(String youtubeChannelId);

}
