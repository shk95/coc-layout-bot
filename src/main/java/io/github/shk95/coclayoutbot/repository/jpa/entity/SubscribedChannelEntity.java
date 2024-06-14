package io.github.shk95.coclayoutbot.repository.jpa.entity;

import io.github.shk95.coclayoutbot.domain.Subscriber;
import io.github.shk95.coclayoutbot.domain.YoutubeChannel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscribed_channel")
public class SubscribedChannelEntity {

	@EmbeddedId
	private SubscribedChannelId subscribedChannelId;
	@MapsId("subscriberChannelId")
	@ManyToOne(optional = false)
	@JoinColumn(referencedColumnName = "channel_id", name = "subscriber_channel_id", updatable = false)
	private SubscriberDiscordChannelEntity subscriber;
	@MapsId("youtubeChannelId")
	@ManyToOne(optional = false)
	@JoinColumn(referencedColumnName = "channel_id", name = "channel_id", updatable = false)
	private YoutubeChannelEntity youtubeChannel;
	@Convert(converter = BooleanConverter.class)
	@Column(name = "setup", columnDefinition = "NUMBER(1) DEFAULT 0", length = 1)
	private boolean setup;
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SubscribedChannelEntity that = (SubscribedChannelEntity) o;
		return setup == that.setup && Objects.equals(subscribedChannelId, that.subscribedChannelId) && Objects.equals(subscriber, that.subscriber) && Objects.equals(youtubeChannel, that.youtubeChannel) && Objects.equals(lastFetchedAt, that.lastFetchedAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subscribedChannelId, subscriber, youtubeChannel, setup, lastFetchedAt);
	}

	@Override
	public String toString() {
		return """
				SubscribedChannelEntity{
				        subscribedChannelId = "%s",
				        subscriber = "%s",
				        youtubeChannel = "%s",
				        setup = "%s",
				        lastFetchedAt = "%s"
				}""".formatted(subscribedChannelId, subscriber, youtubeChannel, setup, lastFetchedAt);
	}

	@Getter
	@NoArgsConstructor
	@Embeddable
	public static class SubscribedChannelId implements Serializable {

		private String youtubeChannelId; // youtube channel id
		private long subscriberChannelId;

		public SubscribedChannelId(YoutubeChannelEntity youtubeChannelEntity, SubscriberDiscordChannelEntity subscriberDiscordChannelEntity) {
			this.youtubeChannelId = youtubeChannelEntity.getChannelId();
			this.subscriberChannelId = subscriberDiscordChannelEntity.getChannelId();
		}

		@Override
		public String toString() {
			return "SubscribedChannelId{" +
					"youtubeChannelId='" + youtubeChannelId + '\'' +
					", subscriberId='" + subscriberChannelId + '\'' +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			SubscribedChannelId that = (SubscribedChannelId) o;
			return Objects.equals(youtubeChannelId, that.youtubeChannelId) && Objects.equals(subscriberChannelId, that.subscriberChannelId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(youtubeChannelId, subscriberChannelId);
		}

	}

}
