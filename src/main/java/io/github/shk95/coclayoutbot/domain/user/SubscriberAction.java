package io.github.shk95.coclayoutbot.domain.user;

import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import io.github.shk95.coclayoutbot.repository.jpa.entity.SubscriberDiscordChannelEntity;

public interface SubscriberAction {

	SubscriberDiscordChannelEntity register(Subscriber subscriber);

	boolean subscribe(Subscriber subscriber, YoutubeChannel youtubeChannel);

	boolean unsubscribe(Subscriber subscriber, YoutubeChannel youtubeChannel);

	void unsubscribeAll(Subscriber subscriber);

}
