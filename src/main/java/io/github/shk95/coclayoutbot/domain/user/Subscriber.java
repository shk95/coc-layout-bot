package io.github.shk95.coclayoutbot.domain.user;


import lombok.Builder;
import org.jetbrains.annotations.Nullable;

@Builder
public record Subscriber(
		long channelId,
		@Nullable Long guildId
) {

	@Override
	public String toString() {
		return """
				Subscriber{ channelId = %s , guildId = %s }
				""".formatted(channelId, guildId);
	}

}
