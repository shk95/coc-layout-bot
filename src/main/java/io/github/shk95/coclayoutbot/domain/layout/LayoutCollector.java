package io.github.shk95.coclayoutbot.domain.layout;

import io.github.shk95.coclayoutbot.util.ChainableRunnable;

public interface LayoutCollector {

	int ERROR_COUNT_LIMIT = 3;

	ChainableRunnable collectVideosFromChannel();

	ChainableRunnable collectLayoutLink();

	ChainableRunnable collectLayoutImg();

}
