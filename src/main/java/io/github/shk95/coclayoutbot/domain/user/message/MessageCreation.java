package io.github.shk95.coclayoutbot.domain.user.message;

import lombok.Getter;

import java.util.Collection;

@Getter
public class MessageCreation<T extends MessageContent> {

	private final long channelId;
	private final Collection<T> contents;

	public MessageCreation(long channelId, Collection<T> contents) {
		this.channelId = channelId;
		this.contents = contents;
	}

}
