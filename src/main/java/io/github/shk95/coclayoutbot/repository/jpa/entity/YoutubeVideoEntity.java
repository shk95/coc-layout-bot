package io.github.shk95.coclayoutbot.repository.jpa.entity;

import io.github.shk95.coclayoutbot.domain.layout.YoutubeVideo;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@ToString(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor
@Entity
@Table(name = "youtube_video")
public class YoutubeVideoEntity {

	@ToString.Include
	@Id
	@Column(name = "video_id")
	private String videoId;
	@ToString.Include
	@Convert(converter = BooleanConverter.class)
	@Column(name = "processed", columnDefinition = "NUMBER(1) DEFAULT 0", nullable = false, length = 1)
	private boolean processed;
	@Column(name = "title")
	private String title;
	@Column(name = "published_at", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
	private Instant publishedAt;
	@ManyToOne
	@JoinColumn(referencedColumnName = "channel_id", name = "channel_id", updatable = false)
	private YoutubeChannelEntity youtubeChannel;

	@Builder
	public YoutubeVideoEntity(
			String videoId,
			boolean processed,
			String title,
			Instant publishedAt,
			YoutubeChannelEntity youtubeChannel
	) {
		this.videoId = videoId;
		this.processed = processed;
		this.title = title;
		this.publishedAt = publishedAt;
		this.youtubeChannel = youtubeChannel;
	}

	public YoutubeVideo.Part toDomain() {
		return new YoutubeVideo.Part(this.youtubeChannel.toDomain(), this.videoId, this.title, this.publishedAt);
	}

	public void processed() {
		this.processed = true;
	}

}
