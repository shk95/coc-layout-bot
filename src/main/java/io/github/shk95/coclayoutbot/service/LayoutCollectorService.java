package io.github.shk95.coclayoutbot.service;

import io.github.shk95.coclayoutbot.domain.Layout;
import io.github.shk95.coclayoutbot.domain.LayoutCollector;
import io.github.shk95.coclayoutbot.domain.YoutubeChannel;
import io.github.shk95.coclayoutbot.domain.YoutubeVideo;
import io.github.shk95.coclayoutbot.repository.jpa.entity.*;
import io.github.shk95.coclayoutbot.util.ChainableRunnable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class LayoutCollectorService implements LayoutCollector {

	private final YoutubeApi youtubeApi;
	private final YtMediaExtractorApi ytMediaExtractorApi;
	private final YoutubeChannelRepository youtubeChannelRepository;
	private final YoutubeVideoRepository youtubeVideoRepository;
	private final LayoutRepository layoutRepository;

	@Transactional
	@Override
	public ChainableRunnable collectVideosFromChannel() {
		final Instant now = Instant.now();
		List<YoutubeChannelEntity> youtubeChannelEntities = youtubeChannelRepository.findAllByFetchStartAtIsNotNull();
		List<YoutubeVideoEntity> processedVideoEntities = youtubeChannelEntities
				.parallelStream()
				.map(youtubeChannelEntity -> {
					YoutubeChannel youtubeChannel = youtubeChannelEntity.toDomain();
					List<YoutubeVideo.Part> parts;
					try {
						parts = youtubeApi.getVideosFromChannel(youtubeChannel);
					} catch (YoutubeApiException e) {
						return null;
					}
					if (parts.isEmpty()) return null;

					List<YoutubeVideoEntity> youtubeVideoEntities = parts.stream()
							.map(part ->
									YoutubeVideoEntity.builder()
											.youtubeChannel(youtubeChannelEntity)
											.videoId(part.videoId())
											.title(part.title())
											.publishedAt(part.publishedAt())
											.processed(false).build()
							)
							.toList();
					youtubeChannelEntity.touchLastUpdateAt(now);
					log.info("Update videos from channel. [{}]", youtubeChannel);
					return youtubeVideoEntities;
				})
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.toList();
		youtubeChannelRepository.saveAllAndFlush(youtubeChannelEntities);
		if (youtubeVideoRepository.saveAllAndFlush(processedVideoEntities).isEmpty()) {
			return () -> false;
		}
		return () -> true;
	}

	@Transactional
	@Override
	public ChainableRunnable collectLayoutLink() {
		List<YoutubeVideoEntity> youtubeVideoEntities = youtubeVideoRepository.findAllByProcessedFalse();
		List<LayoutEntity> processedLayouts = youtubeVideoEntities
				.parallelStream()
				.map(youtubeVideoEntity -> {
					YoutubeVideo.Part videoPart = youtubeVideoEntity.toDomain();
					YoutubeVideo youtubeVideo;
					try {
						youtubeVideo = youtubeApi.getVideoDetail(videoPart);
					} catch (YoutubeApiException e) {
						return null;
					}

					int frameRadiusCount = youtubeVideoEntity.getYoutubeChannel().getFrameRadiusCount();
					Layout.Part layoutPart = youtubeVideo.processLayoutLink(); // 레이아웃 링크를 추출하여 추가.
					List<Layout.Detail> layoutDetail = layoutPart.processTimestamp(frameRadiusCount); // 레이아웃 링크별 타임스탬프를 추출하여 추가.
					if (layoutDetail.isEmpty()) return null; // 해당 영상에서 유효한 데이터가 없음.

					List<LayoutEntity> layoutEntities =
							layoutDetail.stream()
									.map(detail -> new Layout(youtubeVideo, detail))
									.map(layout -> LayoutEntity.builder()
											.youtubeVideoEntity(youtubeVideoEntity)
											.layoutUrl(layout.detail().layoutUrl())
											.timestamp(layout.detail().timestamp())
											.imgPart(layout.detail().imgPart())
											.errorCount(0)
											.layoutImgUrl(null).build()
									)
									.toList();
					youtubeVideoEntity.processed();
					return layoutEntities;
				})
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.toList();
		youtubeVideoRepository.saveAllAndFlush(youtubeVideoEntities);
		if (layoutRepository.saveAllAndFlush(processedLayouts).isEmpty()) {
			return () -> false;
		}
		return () -> true;
	}

	// TODO: 여러 저장작업에 대해서, 중간에 실패하더라도 이전 작업들은 안전하게 저장될수있도록.
	@Transactional
	@Override
	public ChainableRunnable collectLayoutImg() {
		List<LayoutEntity> layoutEntities = layoutRepository.findAllByErrorCountLessThanAndLayoutImgUrlIsNull(ERROR_COUNT_LIMIT);
		layoutEntities
				.forEach(layoutEntity -> {
					String layoutImgUrl;
					try {
						layoutImgUrl = ytMediaExtractorApi.makeImgUrl(
								layoutEntity.getVideoId(),
								layoutEntity.getTimestamp());
					} catch (YtMediaExtractorException e) {
						layoutEntity.increaseErrorCount();
						return;
					}
					layoutEntity.clearErrorCount();
					layoutEntity.setLayoutImgUrl(layoutImgUrl);
					layoutRepository.saveAndFlush(layoutEntity);
				});
		return () -> true;
	}

}
