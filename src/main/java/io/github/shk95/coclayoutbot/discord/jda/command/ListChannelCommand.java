package io.github.shk95.coclayoutbot.discord.jda.command;

import io.github.shk95.coclayoutbot.discord.jda.SlashCommand;
import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import io.github.shk95.coclayoutbot.repository.jpa.entity.YoutubeChannelEntity;
import io.github.shk95.coclayoutbot.repository.jpa.entity.YoutubeChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static io.github.shk95.coclayoutbot.discord.jda.command.Utils.commandEventLogger;

@Slf4j
@RequiredArgsConstructor
@Component
public class ListChannelCommand implements SlashCommand {

	private final YoutubeChannelRepository youtubeChannelRepository;

	@Override
	public String getCommandName() {
		return "list_channel";
	}

	@Override
	public String getDescription() {
		return "List all channels available to subscribe";
	}

	@Override
	public Set<Option> getOptions() {
		return Set.of();
	}

	@Transactional(readOnly = true)
	@Override
	public void execute(SlashCommandInteractionEvent event) {
		commandEventLogger(event);

		event.deferReply(true).queue();

		List<YoutubeChannelAvailable> availableChannels = youtubeChannelRepository.findAll()
				.stream()
				.map(YoutubeChannelEntity::toDomain)
				.map(YoutubeChannelAvailable::from)
				.sorted()
				.toList();

		String message = """
				Available Youtube Channels

				""";

		event.getHook()
				.sendMessage(
						message.concat(availableChannels.stream()
								.map(YoutubeChannelAvailable::toString)
								.reduce("", String::concat))
				)
				.queue();
	}

	private record YoutubeChannelAvailable(
			String channelTitle,
			String channelId
	) implements Comparable<YoutubeChannelAvailable> {

		private static YoutubeChannelAvailable from(YoutubeChannel youtubeChannel) {
			return new YoutubeChannelAvailable(youtubeChannel.channelTitle(), youtubeChannel.channelId());
		}

		@Override
		public String toString() {
			return """
					-  Channel Title : %s  |  Channel Id : %s
					""".formatted(channelTitle, channelId);
		}

		@Override
		public int compareTo(@NotNull YoutubeChannelAvailable o) {
			return Comparator.comparing(YoutubeChannelAvailable::channelTitle)
					.thenComparing(YoutubeChannelAvailable::channelId)
					.compare(this, o);
		}

	}

}
