package io.github.shk95.coclayoutbot.discord.domain;

import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;

import java.time.Instant;

public record SubscribedYoutubeChannel(
		String channelTitle,
		String channelId,
		Instant lastUpdateAt) {

	public static SubscribedYoutubeChannel from(YoutubeChannel youtubeChannel) {
		return new SubscribedYoutubeChannel(
				youtubeChannel.channelTitle(),
				youtubeChannel.channelId(),
				youtubeChannel.lastUpdateAt()
		);
	}

	@Override
	public String toString() {
		return """
				-  Channel Title : %s  |  Last Update : %s  |  Channel Id : %s
				""".formatted(channelTitle, lastUpdateAt, channelId);
	}

}
