package io.github.shk95.coclayoutbot.domain;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Slf4j
public record Layout(
		@NotNull YoutubeVideo youtubeVideo,
		Detail detail
) implements Comparable<Layout> {

	@Override
	public String toString() {
		return """
				Layout{
				        youtubeVideo = "%s",
				        detail = "%s"
				}""".formatted(youtubeVideo, detail);
	}

	@Override
	public int compareTo(@NotNull Layout o) {
		return Comparator.comparing(
						Layout::youtubeVideo,
						Comparator.comparing(video ->
								video.youtubeVideoInfoPart().publishedAt()
						)
				)
				.thenComparing(layout -> layout.detail().timestamp())
				.thenComparing(layout -> layout.detail().imgPart())
				.compare(this, o);
	}

	@Builder
	public record Detail(
			LocalTime timestamp,
			String layoutUrl,
			int imgPart,
			@Nullable String layoutImgUrl
	) implements Comparable<Detail> {

		@Override
		public String toString() {
			return """
					Detail{
					        timestamp = "%s",
					        layoutUrl = "%s",
					        imgPart = "%s",
					        layoutImgUrl = "%s"
					}""".formatted(timestamp, layoutUrl, imgPart, layoutImgUrl);
		}

		@Override
		public int compareTo(@NotNull Detail o) {
			return Comparator.comparing(Detail::imgPart)
					.compare(this, o);
		}

	}

	public record Part(
			YoutubeVideo youtubeVideo,
			List<String> layoutUrls
	) {

		// 영상 길이를 넘지 않도록 처리
		private LocalTime checkTimestamp(LocalTime target) {
			LocalTime MIN = LocalTime.ofSecondOfDay(3);
			assert this.youtubeVideo.duration() != null;
			LocalTime MAX = LocalTime.ofSecondOfDay(this.youtubeVideo.duration().minusSeconds(3).getSeconds());
			if (target.isAfter(MAX)) {
				return MAX;
			}
			if (target.isBefore(MIN)) {
				return MIN;
			}
			return target;
		}

		public List<Detail> processTimestamp(int frameRadiusCount) {
			int count = layoutUrls.size();
			if (count == 0) return List.of(); // drop. no layout urls.

			log.debug("영상정보에서 레이아웃 url 들을 추출한 이후. [{}]", this);

			List<Detail> layoutPreProcessed = new ArrayList<>();

			Long intervalSecond = switch (this.youtubeVideo.youtubeVideoInfoPart().youtubeChannel().videoSplitStrategy().strategy()) {
				case NONE -> null;
				case EQUAL_INTERVAL -> {
					LocalTime offsetIntro = this.youtubeVideo.youtubeVideoInfoPart()
							.youtubeChannel().videoSplitStrategy().offsetIntro();
					LocalTime offsetSkip = this.youtubeVideo.youtubeVideoInfoPart()
							.youtubeChannel().videoSplitStrategy().offsetSkip();
					LocalTime offsetOutro = this.youtubeVideo.youtubeVideoInfoPart()
							.youtubeChannel().videoSplitStrategy().offsetOutro();
					assert this.youtubeVideo.duration() != null;
					long gap = (long) Math.floor((double) (
							this.youtubeVideo.duration().getSeconds()
									- offsetIntro.getSecond() - offsetOutro.getSecond())
							/ count);
					IntStream.range(0, count).forEachOrdered(i -> {
						LocalTime timestamp = LocalTime.ofSecondOfDay(
								offsetIntro.toSecondOfDay()
										+ offsetSkip.toSecondOfDay()
										+ gap * i);
						layoutPreProcessed.add(new Detail(checkTimestamp(timestamp), layoutUrls.get(i), 0, null));
					});
					yield gap;
				}
				case FIXED_INTERVAL -> {
					LocalTime offsetFixed = this.youtubeVideo.youtubeVideoInfoPart().youtubeChannel()
							.videoSplitStrategy().offsetFixed();
					IntStream.range(0, count).forEachOrdered(i -> {
						LocalTime timestamp = LocalTime.ofSecondOfDay((long) offsetFixed.toSecondOfDay() * i);
						layoutPreProcessed.add(new Detail(checkTimestamp(timestamp), layoutUrls.get(i), 0, null));
					});
					yield (long) offsetFixed.toSecondOfDay();
				}
			};
			Assert.notNull(intervalSecond, "intervalSecond must not be null");
			return addAdditionalFrames(layoutPreProcessed, frameRadiusCount, intervalSecond).get();
		}

		// TODO: 추출 알고리즘 개선 필요.
		private Supplier<List<Detail>> addAdditionalFrames(List<Detail> layoutPreProcessed, int frameRadiusCount, long intervalSecond) {
			List<Detail> processedResult = new ArrayList<>();
			layoutPreProcessed.forEach(preProcessed -> {
				// 현재 프레임
				processedResult.add(preProcessed);

				if (frameRadiusCount > 2) {
					// 이전 프레임
					processedResult.add(
							new Detail(
									checkTimestamp(preProcessed.timestamp().minusSeconds(4)),
									preProcessed.layoutUrl(), -1, null));
				}

				// 이후 프레임
				IntStream.range(1, frameRadiusCount - 1)
						.forEach(i ->
								processedResult.add(
										new Detail(
												checkTimestamp(preProcessed.timestamp().plusSeconds(5L * i)),
												preProcessed.layoutUrl(), i, null))
						);
			});
			return () -> processedResult;
		}

	}

}
