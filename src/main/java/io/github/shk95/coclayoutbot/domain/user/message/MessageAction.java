package io.github.shk95.coclayoutbot.domain.user.message;

public interface MessageAction<T extends MessageContent> {

	void send(MessageCreation<T> messageCreation);

}
