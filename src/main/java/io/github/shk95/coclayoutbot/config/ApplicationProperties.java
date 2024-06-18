package io.github.shk95.coclayoutbot.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "app")
public final class ApplicationProperties {

	@Valid
	private ExternalApi externalApi;
	@Valid
	private Secret secret;

	@Setter
	@Getter
	public static class Secret {

		@Valid
		@NotEmpty
		private String youtubeApiKey;
		@Valid
		@NotEmpty
		private String discordToken;

	}

	@Setter
	@Getter
	public static class ExternalApi {

		@Valid
		private YtMediaExtractorApi ytMediaExtractorApi;

		@Setter
		@Getter
		public static class YtMediaExtractorApi {

			@Valid
			@NotEmpty
			private String url;

		}

	}

}
