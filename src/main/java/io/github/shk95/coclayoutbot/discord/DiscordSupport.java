package io.github.shk95.coclayoutbot.discord;

public interface DiscordSupport {

	void start() throws InterruptedException;

	void stop();

	DiscordContext getContext();

}
