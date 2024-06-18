package io.github.shk95.coclayoutbot.domain.layout;

import java.time.LocalTime;

public record VideoSplitStrategy(
		VideoSplitStrategy.Mode strategy,
		LocalTime offsetIntro,
		LocalTime offsetSkip,
		LocalTime offsetOutro,
		LocalTime offsetFixed,
		int frameRadiusCount
) {

	public VideoSplitStrategy(VideoSplitStrategy.Mode strategy,
	                          LocalTime offsetIntro,
	                          LocalTime offsetSkip,
	                          LocalTime offsetOutro,
	                          LocalTime offsetFixed,
	                          int frameRadiusCount) {
		this.frameRadiusCount = frameRadiusCount;
		if (strategy == VideoSplitStrategy.Mode.NONE) { // 기본값을 지정.
			this.strategy = VideoSplitStrategy.Mode.EQUAL_INTERVAL;
			this.offsetIntro = LocalTime.ofSecondOfDay(10);
			this.offsetSkip = LocalTime.ofSecondOfDay(8);
			this.offsetOutro = LocalTime.ofSecondOfDay(5);
			this.offsetFixed = null;
		} else {
			this.strategy = strategy;
			this.offsetIntro = offsetIntro;
			this.offsetSkip = offsetSkip;
			this.offsetOutro = offsetOutro;
			this.offsetFixed = offsetFixed;
		}
	}

	@Override
	public String toString() {
		return """
				VideoSplitStrategy{
				        strategy = "%s"
				}""".formatted(strategy);
	}

	public enum Mode {
		NONE,
		EQUAL_INTERVAL,
		FIXED_INTERVAL
	}

}
