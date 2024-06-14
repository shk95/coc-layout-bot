package io.github.shk95.coclayoutbot.domain;

import io.github.shk95.coclayoutbot.repository.jpa.entity.SubscriberDiscordChannelEntity;

public interface SubscriberAction {

	SubscriberDiscordChannelEntity register(Subscriber subscriber);

	boolean subscribe(Subscriber subscriber, YoutubeChannel youtubeChannel);

	boolean unsubscribe(Subscriber subscriber, YoutubeChannel youtubeChannel);

}
