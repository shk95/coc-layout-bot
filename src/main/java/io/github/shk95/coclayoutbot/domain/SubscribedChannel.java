package io.github.shk95.coclayoutbot.domain;

import java.time.Instant;

public record SubscribedChannel(
		String channelId,
		String youtubeChannelId,
		boolean setup,
		Instant lastFetchedAd
) {

}
