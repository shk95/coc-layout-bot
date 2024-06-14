package io.github.shk95.coclayoutbot.service;

import java.time.LocalTime;

public interface YtMediaExtractorApi {

	String makeImgUrl(String videoId, LocalTime timestamp) throws YtMediaExtractorException;

}
