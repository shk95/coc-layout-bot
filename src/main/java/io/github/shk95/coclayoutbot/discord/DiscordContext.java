package io.github.shk95.coclayoutbot.discord;

public interface DiscordContext {

	SendMessageConsumer message();

	@FunctionalInterface
	interface SendMessageConsumer {

		void send(long channelId, String message);

	}

}
