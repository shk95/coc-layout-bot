package io.github.shk95.coclayoutbot.domain.user.message;

import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Comparator;

@Getter
public abstract class MessageContent implements Comparable<MessageContent> {

	protected final int order;

	protected MessageContent(int order) {
		this.order = order;
	}

	@Override
	public int compareTo(@NotNull MessageContent o) {
		return Comparator.comparingInt(MessageContent::getOrder)
				.compare(this, o);
	}

	@Getter
	public static class TextContent extends MessageContent {

		private final String textContent;

		@Builder
		protected TextContent(int order, String textContent) {
			super(order);
			this.textContent = textContent;
		}

		@Override
		public String toString() {
			return """
					TextContent{ textContent = %s }
					""".formatted(textContent);
		}

	}

	@Getter
	public static class EmbedContent extends MessageContent {

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
		protected EmbedContent(int order,
		                       String title,
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
			super(order);
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

		@Override
		public String toString() {
			return """
					EmbedContent{ title = %s , description = %s , url = %s , imageUrl = %s , color = %s }
					""".formatted(title, description, url, imageUrl, color);
		}

	}

}
