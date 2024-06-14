package io.github.shk95.coclayoutbot.adapter;

import io.github.shk95.coclayoutbot.repository.jpa.entity.LayoutEntity;
import io.github.shk95.coclayoutbot.repository.jpa.entity.LayoutRepository;
import io.github.shk95.coclayoutbot.repository.jpa.entity.YoutubeChannelEntity;
import io.github.shk95.coclayoutbot.repository.jpa.entity.YoutubeVideoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SimpleRestController {

	private final LayoutRepository layoutRepository;

	@Transactional(readOnly = true)
	@GetMapping("/layouts")
	public ResponseEntity<?> getLayouts(@RequestParam(defaultValue = "0") Integer page) {
		if (page < 0) {
			page = 0;
		}
		int pageSize = 50;
		log.info(page.toString());
		PageRequest pageRequest = PageRequest.of(
				page,
				pageSize,
				Sort.by(
						Sort.Order.asc("youtubeVideoEntity.publishedAt"),
						Sort.Order.asc("timestamp"),
						Sort.Order.asc("imgPart")
				));
		Page<LayoutEntity> layoutPages = layoutRepository.findAll(pageRequest);
		int totalPages = layoutPages.getTotalPages();
		int currentPage = layoutPages.getNumber();
		long totalElements = layoutPages.getTotalElements();
		List<LayoutEntityDto> layouts = layoutPages.getContent()
				.stream()
				.map(LayoutEntityDto::fromEntity)
				.toList();
		log.info("Layout entities has been fetched.  : page : [{}], size : [{}], total pages : [{}], total elements : [{}]",
				currentPage, layoutPages.getSize(), totalPages, totalElements);
		return ResponseEntity.ok(new ResponseBody(currentPage, pageSize, totalPages, totalElements, layouts));
	}

	private record ResponseBody(
			int currentPage,
			int pageSize,
			int totalPages,
			long totalElements,
			List<LayoutEntityDto> layouts
	) {

	}

	/**
	 * DTO for {@link io.github.shk95.coclayoutbot.repository.jpa.entity.LayoutEntity}
	 */
	private record LayoutEntityDto(
			String layoutUrl,
			String timestamp,
			int imgPart,
			String layoutImgUrl,
			YoutubeVideoEntityDto youtubeVideo
	) implements Serializable, Comparable<LayoutEntityDto> {

		private static LayoutEntityDto fromEntity(LayoutEntity entity) {
			return new LayoutEntityDto(
					entity.getLayoutUrl(),
					entity.getTimestamp().toString(),
					entity.getImgPart(),
					entity.getLayoutImgUrl(),
					YoutubeVideoEntityDto.fromEntity(entity.getYoutubeVideoEntity()));
		}

		@Override
		public int compareTo(@NotNull LayoutEntityDto o) {
			return Comparator.comparing(
							LayoutEntityDto::youtubeVideo,
							Comparator.comparing(youtubeVideo::compareTo)
					)
					.thenComparing(LayoutEntityDto::timestamp)
					.thenComparing(LayoutEntityDto::imgPart)
					.compare(this, o);
		}

		/**
		 * DTO for {@link io.github.shk95.coclayoutbot.repository.jpa.entity.YoutubeVideoEntity}
		 */
		private record YoutubeVideoEntityDto(
				String videoId,
				String title,
				String publishedAt,
				YoutubeChannelEntityDto youtubeChannel
		) implements Serializable, Comparable<YoutubeVideoEntityDto> {

			private static YoutubeVideoEntityDto fromEntity(YoutubeVideoEntity entity) {
				return new YoutubeVideoEntityDto(
						entity.getVideoId(),
						entity.getTitle(),
						entity.getPublishedAt() == null ? null : entity.getPublishedAt().toString(),
						YoutubeVideoEntityDto.YoutubeChannelEntityDto.fromEntity(entity.getYoutubeChannel()));
			}

			@Override
			public int compareTo(@NotNull YoutubeVideoEntityDto o) {
				return Comparator.comparing(YoutubeVideoEntityDto::publishedAt)
						.compare(this, o);
			}

			/**
			 * DTO for {@link io.github.shk95.coclayoutbot.repository.jpa.entity.YoutubeChannelEntity}
			 */
			private record YoutubeChannelEntityDto(
					String channelId,
					String channelTitle,
					String fetchStartAt,
					String lastUpdateAt
			) implements Serializable {

				private static YoutubeChannelEntityDto fromEntity(YoutubeChannelEntity entity) {
					return new YoutubeChannelEntityDto(
							entity.getChannelId(),
							entity.getChannelTitle(),
							entity.getFetchStartAt() == null ? null : entity.getFetchStartAt().toString(),
							entity.getLastUpdateAt() == null ? null : entity.getLastUpdateAt().toString());
				}

			}

		}

	}

}
