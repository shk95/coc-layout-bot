package io.github.shk95.coclayoutbot.repository.jpa.entity;

import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscriber_discord_channel")
public class SubscriberDiscordChannelEntity {

	@Id
	@Column(name = "channel_id", updatable = false)
	private long channelId;
	@Column(name = "guild_id", updatable = false)
	private Long guildId; // if null, it means it's a direct message channel

	@Builder
	public SubscriberDiscordChannelEntity(long channelId, Long guildId) {
		this.channelId = channelId;
		this.guildId = guildId;
	}

	public Subscriber toDomain() {
		return new Subscriber(this.channelId, this.guildId);
	}

	public boolean hasGuild() {
		return this.guildId != null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SubscriberDiscordChannelEntity that = (SubscriberDiscordChannelEntity) o;
		return channelId == that.channelId && Objects.equals(guildId, that.guildId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(channelId, guildId);
	}

	@Override
	public String toString() {
		return """

				SubscriberDiscordChannelEntity{
				        channelId=%s,
				        guildId=%s
				}""".formatted(channelId, guildId);
	}

}
