package io.github.shk95.coclayoutbot.repository.jpa.entity;

import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@ToString(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscriber_discord_channel")
public class SubscriberDiscordChannelEntity {

	@ToString.Include
	@Id
	@Column(name = "channel_id", updatable = false)
	private long channelId;
	@ToString.Include
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
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
				: o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
				: this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		SubscriberDiscordChannelEntity that = (SubscriberDiscordChannelEntity) o;
		return Objects.equals(getChannelId(), that.getChannelId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}
