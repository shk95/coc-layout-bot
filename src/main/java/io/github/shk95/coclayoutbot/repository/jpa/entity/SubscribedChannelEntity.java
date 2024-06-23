package io.github.shk95.coclayoutbot.repository.jpa.entity;

import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import io.github.shk95.coclayoutbot.domain.user.Subscriber;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@ToString(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscribed_channel")
public class SubscribedChannelEntity {

	@ToString.Include
	@EmbeddedId
	private SubscribedChannelId subscribedChannelId;
	@MapsId("subscriberChannelId")
	@ManyToOne(optional = false)
	@JoinColumn(referencedColumnName = "channel_id", name = "subscriber_channel_id", updatable = false)
	private SubscriberDiscordChannelEntity subscriber;
	@MapsId("youtubeChannelId")
	@ManyToOne(optional = false)
	@JoinColumn(referencedColumnName = "channel_id", name = "youtube_channel_id", updatable = false)
	private YoutubeChannelEntity youtubeChannel;
	@ToString.Include
	@Convert(converter = BooleanConverter.class)
	@Column(name = "setup", columnDefinition = "NUMBER(1) DEFAULT 0", length = 1)
	private boolean setup;
	@ToString.Include
	@Nullable
	@Column(name = "last_fetched_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private Instant lastFetchedAt; // 마지막으로 가져온 날짜

	@Builder
	public SubscribedChannelEntity(SubscriberDiscordChannelEntity subscriber, YoutubeChannelEntity youtubeChannel, boolean setup, @Nullable Instant lastFetchedAt) {
		this.subscribedChannelId = new SubscribedChannelId(youtubeChannel, subscriber);
		this.subscriber = subscriber;
		this.youtubeChannel = youtubeChannel;
		this.setup = setup;
		this.lastFetchedAt = lastFetchedAt;
	}

	public Subscriber toSubscriberDomain() {
		return subscriber.toDomain();
	}

	public YoutubeChannel toSubscribedYoutubeChannelDomain() {
		return youtubeChannel.toDomain();
	}

	public void setupTrue() {
		this.setup = true;
	}

	public void setupFalse() {
		this.setup = false;
	}

	public void touchLastFetchedAt(Instant lastFetchedAt) {
		this.lastFetchedAt = lastFetchedAt;
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
		SubscribedChannelEntity entity = (SubscribedChannelEntity) o;
		return getSubscribedChannelId() != null
				&& Objects.equals(getSubscribedChannelId(), entity.getSubscribedChannelId());
	}

	@Override
	public final int hashCode() {
		return Objects.hash(subscribedChannelId);
	}

	@ToString
	@Getter
	@NoArgsConstructor
	@Embeddable
	public static class SubscribedChannelId implements Serializable {

		private String youtubeChannelId;
		private long subscriberChannelId;

		public SubscribedChannelId(YoutubeChannelEntity youtubeChannelEntity, SubscriberDiscordChannelEntity subscriberDiscordChannelEntity) {
			this.youtubeChannelId = youtubeChannelEntity.getChannelId();
			this.subscriberChannelId = subscriberDiscordChannelEntity.getChannelId();
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
			SubscribedChannelId that = (SubscribedChannelId) o;
			return Objects.equals(getYoutubeChannelId(), that.getYoutubeChannelId())
					&& Objects.equals(getSubscriberChannelId(), that.getSubscriberChannelId());
		}

		@Override
		public final int hashCode() {
			return Objects.hash(youtubeChannelId, subscriberChannelId);
		}

	}

}
