package io.github.shk95.coclayoutbot.service;

import io.github.shk95.coclayoutbot.domain.layout.YoutubeChannel;
import io.github.shk95.coclayoutbot.domain.layout.YoutubeVideo;

import java.util.List;

public interface YoutubeApi {

	List<YoutubeVideo.Part> getVideosFromChannel(YoutubeChannel channel) throws YoutubeApiException;

	YoutubeVideo getVideoDetail(YoutubeVideo.Part youtubeVideoInfoPart) throws YoutubeApiException;

}
