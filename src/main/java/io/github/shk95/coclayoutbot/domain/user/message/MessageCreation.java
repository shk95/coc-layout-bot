package io.github.shk95.coclayoutbot.domain.user.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class MessageCreation<T extends MessageContent> {

	private final long channelId;
	private final Collection<T> contents;

	@Override
	public String toString() {
		return """
				MessageCreation{ channelId = %s , contents = %s }
				""".formatted(channelId, contents);
	}

}
