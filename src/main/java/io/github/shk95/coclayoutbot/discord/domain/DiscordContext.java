package io.github.shk95.coclayoutbot.discord.domain;

import io.github.shk95.coclayoutbot.domain.user.message.type.EmbedMessage;
import io.github.shk95.coclayoutbot.domain.user.message.type.TextMessage;

public interface DiscordContext {

	TextChannelContext textChannel();

	interface TextChannelContext {

		EmbedMessage embedMessage();

		TextMessage textMessage();

	}

}
