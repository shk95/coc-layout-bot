package io.github.shk95.coclayoutbot.repository.jpa.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YoutubeVideoRepository extends JpaRepository<YoutubeVideoEntity, String> {

	List<YoutubeVideoEntity> findAllByProcessedFalse();

}
