package io.github.shk95.coclayoutbot.domain.user.message;

import lombok.Builder;
import lombok.Getter;

import java.awt.*;

public interface MessageContent {

	@Getter
	class TextContent implements MessageContent {

		private final String textContent;

		@Builder
		protected TextContent(String textContent) {
			this.textContent = textContent;
		}

	}

	@Getter
	class EmbedContent implements MessageContent {

		// main content
		private final String title;
		private final String description;
		private final String url;
		private final String imageUrl;

		private final Color color;

		private final String footerText;
		private final String footerIconUrl;

		private final String authorName;
		private final String authorUrl;
		private final String authorIconUrl;

		@Builder
		protected EmbedContent(String title,
		                       String description,
		                       String url,
		                       String imageUrl,
		                       Color color,
		                       String footerText,
		                       String footerIconUrl,
		                       String authorName,
		                       String authorUrl,
		                       String authorIconUrl
		) {
			this.title = title;
			this.description = description;
			this.url = url;
			this.imageUrl = imageUrl;
			this.color = color;
			this.footerText = footerText;
			this.footerIconUrl = footerIconUrl;
			this.authorName = authorName;
			this.authorUrl = authorUrl;
			this.authorIconUrl = authorIconUrl;
		}

	}

}
