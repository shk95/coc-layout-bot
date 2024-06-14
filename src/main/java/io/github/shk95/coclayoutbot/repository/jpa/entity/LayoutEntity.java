package io.github.shk95.coclayoutbot.repository.jpa.entity;

import io.github.shk95.coclayoutbot.domain.Layout;
import io.github.shk95.coclayoutbot.domain.YoutubeVideo;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Comparator;

@ToString(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor
@Entity
@Table(name = "layout_detail")
public class LayoutEntity implements Comparable<LayoutEntity> {

	@ToString.Include
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "layout_detail_seq")
	@SequenceGenerator(name = "layout_detail_seq")
	@Column(name = "id_seq", nullable = false, updatable = false)
	private Long idSeq;
	@Column(name = "layout_url", nullable = false, updatable = false)
	private String layoutUrl;
	@ToString.Include
	@Convert(converter = LocalTimeConverter.class)
	@Column(name = "timestamp", columnDefinition = "VARCHAR(8)", nullable = false)
	private LocalTime timestamp;
	@ToString.Include
	@Column(name = "img_part", nullable = false, updatable = false)
	private int imgPart;
	@Column(name = "layout_img_url")
	private String layoutImgUrl;
	@Column(name = "error_count", columnDefinition = "NUMBER(1) DEFAULT 0")
	private int errorCount;
	@ManyToOne
	@JoinColumn(name = "video_id", referencedColumnName = "video_id")
	private YoutubeVideoEntity youtubeVideoEntity;

	@Builder
	public LayoutEntity(
			YoutubeVideoEntity youtubeVideoEntity,
			String layoutUrl,
			LocalTime timestamp,
			String layoutImgUrl,
			int imgPart,
			int errorCount
	) {
		this.layoutUrl = layoutUrl;
		this.timestamp = timestamp;
		this.imgPart = imgPart;
		this.layoutImgUrl = layoutImgUrl;
		this.errorCount = errorCount;
		this.youtubeVideoEntity = youtubeVideoEntity;
	}

	public Layout toDomain() {
		YoutubeVideo youtubeVideo = new YoutubeVideo(this.youtubeVideoEntity.toDomain(), null, null);
		Layout.Detail layoutDetail = Layout.Detail.builder()
				.layoutUrl(this.layoutUrl)
				.timestamp(this.timestamp)
				.imgPart(this.imgPart)
				.layoutImgUrl(this.layoutImgUrl)
				.build();
		return new Layout(youtubeVideo, layoutDetail);
	}

	public Instant getPublishedAt() {
		return this.youtubeVideoEntity.getPublishedAt();
	}

	public String getVideoId() {
		return this.youtubeVideoEntity.getVideoId();
	}

	public void setLayoutImgUrl(String layoutImgUrl) {
		this.layoutImgUrl = layoutImgUrl;
	}

	public void clearErrorCount() {
		this.errorCount = 0;
	}

	public void increaseErrorCount() {
		this.errorCount++;
	}

	@Override
	public int compareTo(@NotNull LayoutEntity o) {
		return Comparator.comparing(LayoutEntity::getPublishedAt)
				.thenComparing(LayoutEntity::getImgPart)
				.compare(this, o);
	}

}
