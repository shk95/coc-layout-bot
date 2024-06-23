package io.github.shk95.coclayoutbot.repository.jpa.entity;

import io.github.shk95.coclayoutbot.domain.layout.VideoSplitStrategy;
import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Objects;

@ToString(onlyExplicitlyIncluded = true)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(YoutubeChannelEntity.EntityListener.class)
@Entity
@Table(name = "youtube_channel")
public class YoutubeChannelEntity {

	@ToString.Include
	@Id
	@Column(name = "channel_id", nullable = false, updatable = false)
	private String channelId;
	@ToString.Include
	@Column(name = "channel_title", nullable = false)
	private String channelTitle;
	@ToString.Include
	@Column(name = "fetch_start_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private Instant fetchStartAt; // 유튜브 채널에서 데이터를 가져오기 시작한 날짜
	@ToString.Include
	@Column(name = "last_update_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private Instant lastUpdateAt; // 마지막으로 유튜브 채널에서 데이터를 가져온 날짜
	@ToString.Include
	@Enumerated(EnumType.STRING)
	@Column(name = "video_split_strategy")
	private VideoSplitStrategy.Mode videoSplitStrategy;
	@Convert(converter = LocalTimeConverter.class)
	@Column(name = "offset_intro", columnDefinition = "VARCHAR(8)")
	private LocalTime offsetIntro;
	@Convert(converter = LocalTimeConverter.class)
	@Column(name = "offset_skip", columnDefinition = "VARCHAR(8)")
	private LocalTime offsetSkip;
	@Convert(converter = LocalTimeConverter.class)
	@Column(name = "offset_outro", columnDefinition = "VARCHAR(8)")
	private LocalTime offsetOutro;
	@Convert(converter = LocalTimeConverter.class)
	@Column(name = "offset_fixed", columnDefinition = "VARCHAR(8)")
	private LocalTime offsetFixed;
	@Max(9)
	@Min(2)
	@Column(name = "frame_radius_count",
			columnDefinition = "NUMBER(1) DEFAULT 3 CHECK (frame_radius_count BETWEEN 2 AND 9)", nullable = false)
	private int frameRadiusCount;

	public YoutubeChannel toDomain() {
		VideoSplitStrategy strategy = new VideoSplitStrategy(
				this.videoSplitStrategy,
				this.offsetIntro,
				this.offsetSkip,
				this.offsetOutro,
				this.offsetFixed,
				this.frameRadiusCount
		);
		Instant last = Objects.isNull(this.lastUpdateAt)
				? this.fetchStartAt
				: this.lastUpdateAt;
		return YoutubeChannel.builder()
				.channelId(this.channelId)
				.channelTitle(this.channelTitle)
				.fetchStartAt(this.fetchStartAt)
				.lastUpdateAt(last)
				.videoSplitStrategy(strategy)
				.build();
	}

	public boolean isFetchStartSet() {
		return this.fetchStartAt != null;
	}

	public void setVideoSplitStrategy(VideoSplitStrategy.Mode videoSplitStrategy) {
		this.videoSplitStrategy = videoSplitStrategy;
	}

	public void setFetchStartAt(Instant fetchStartAt) {
		this.fetchStartAt = fetchStartAt;
	}

	public void touchLastUpdateAt(Instant instant) {
		this.lastUpdateAt = instant;
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
		YoutubeChannelEntity that = (YoutubeChannelEntity) o;
		return getChannelId() != null && Objects.equals(getChannelId(), that.getChannelId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}

	@NoArgsConstructor
	protected static class EntityListener {

		@PrePersist
		protected void prePersist(YoutubeChannelEntity entity) {
			if (entity.getVideoSplitStrategy() == null) {
				entity.setVideoSplitStrategy(VideoSplitStrategy.Mode.NONE);
			}
		}

	}

}
