package io.github.shk95.coclayoutbot.repository.jpa.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface LayoutRepository extends JpaRepository<LayoutEntity, Long> {

	Page<LayoutEntity> findAllByOrderByYoutubeVideoEntity_PublishedAtAscTimestampAscImgPartAsc(Pageable pageable);

	List<LayoutEntity> findAllByErrorCountLessThanAndLayoutImgUrlIsNull(int errorCount);

	List<LayoutEntity> findAllByYoutubeVideoEntity_YoutubeChannel_ChannelIdAndYoutubeVideoEntity_PublishedAtAfterAndErrorCountIs
			(String channelId, Instant after, int errorCount);

}
