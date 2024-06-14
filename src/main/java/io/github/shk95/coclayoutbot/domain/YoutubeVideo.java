package io.github.shk95.coclayoutbot.domain;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Builder
public record YoutubeVideo(
		@NotNull Part youtubeVideoInfoPart,
		String description,
		Duration duration
) {

	@Override
	public String toString() {
		return """
				YoutubeVideo{
				        youtubeVideoInfoPart = "%s",
				        duration = "%s"
				}""".formatted(youtubeVideoInfoPart, duration);
	}

	public Layout.Part processLayoutLink() {
		final String fixedPart1 = "https://link.clashofclans.com/";
		final String fixedPart2 = "action=OpenLayout";
		final String fixedPart3 = "id=TH16";
		final String regex = "https?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*(),]|%[0-9a-fA-F][0-9a-fA-F])+"; // detect url
		Pattern pattern = Pattern.compile(regex);

		List<String> layoutUrls = pattern.matcher(this.description).results()
				.map(MatchResult::group)
				.filter(matchResult ->
						matchResult.startsWith(fixedPart1) &&
								matchResult.contains(fixedPart2) &&
								matchResult.contains(fixedPart3))
				.collect(Collectors.toCollection(LinkedList::new));
		return new Layout.Part(this, layoutUrls);
	}

	@Builder
	public record Part(
			YoutubeChannel youtubeChannel,
			String videoId,
			String title,
			Instant publishedAt
	) implements Comparable<Part> {

		@Override
		public String toString() {
			return """
					Part{
					        youtubeChannel = "%s",
					        videoId = "%s",
					        publishedAt = "%s"
					}""".formatted(youtubeChannel, videoId, publishedAt);
		}

		@Override
		public int compareTo(@NotNull Part o) {
			return Comparator.comparing(Part::publishedAt)
					.thenComparing(Part::videoId)
					.compare(this, o);
		}

	}

}
