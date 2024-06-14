package io.github.shk95.coclayoutbot.repository.jpa.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface YoutubeChannelRepository extends JpaRepository<YoutubeChannelEntity, String> {

	List<YoutubeChannelEntity> findAllByFetchStartAtIsNotNull();

	Optional<YoutubeChannelEntity> findByChannelId(String channelId);

}
