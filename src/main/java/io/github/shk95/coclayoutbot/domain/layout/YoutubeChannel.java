package io.github.shk95.coclayoutbot.domain.layout;

import lombok.Builder;

import java.time.Instant;

@Builder
public record YoutubeChannel(
		String channelId,
		String channelTitle,
		Instant fetchStartAt,
		Instant lastUpdateAt,
		VideoSplitStrategy videoSplitStrategy
) {

	@Override
	public String toString() {
		return """

				YoutubeChannel{
				        channelId=%s,
				        channelTitle=%s,
				        fetchStartAt=%s,
				        lastUpdateAt=%s,
				        videoSplitStrategy=%s
				}""".formatted(channelId, channelTitle, fetchStartAt, lastUpdateAt, videoSplitStrategy);
	}

}
