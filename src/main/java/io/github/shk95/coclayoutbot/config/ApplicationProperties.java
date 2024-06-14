package io.github.shk95.coclayoutbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "app")
public final class ApplicationProperties {

	private ExternalApi externalApi;
	private Secret secret;
	private int collectionStartMonthsAgo; // TODO: 사용되지 않음. 다른 방식으로 변경 필요

	@Setter
	@Getter
	public static class Secret {

		private String youtubeApiKey;
		private String discordToken;

	}

	@Setter
	@Getter
	public static class ExternalApi {

		private YtMediaExtractorApi ytMediaExtractorApi;

		@Setter
		@Getter
		public static class YtMediaExtractorApi {

			private String url;

		}

	}

}
