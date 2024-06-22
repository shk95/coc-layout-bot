package io.github.shk95.coclayoutbot.discord.domain;

import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public record YoutubeChannelAvailable(
		String channelTitle,
		String channelId
) implements Comparable<YoutubeChannelAvailable> {

	public static YoutubeChannelAvailable from(YoutubeChannel youtubeChannel) {
		return new YoutubeChannelAvailable(youtubeChannel.channelTitle(), youtubeChannel.channelId());
	}

	@Override
	public String toString() {
		return """
				-  Channel Title : %s  |  Channel Id : %s
				""".formatted(channelTitle, channelId);
	}

	@Override
	public int compareTo(@NotNull YoutubeChannelAvailable o) {
		return Comparator.comparing(YoutubeChannelAvailable::channelTitle)
				.thenComparing(YoutubeChannelAvailable::channelId)
				.compare(this, o);
	}

}
