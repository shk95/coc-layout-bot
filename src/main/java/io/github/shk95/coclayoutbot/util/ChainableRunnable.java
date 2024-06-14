package io.github.shk95.coclayoutbot.util;

@FunctionalInterface
public interface ChainableRunnable {

	boolean run();

	default ChainableRunnable next(ChainableRunnable next) {
		return () -> this.run() && next.run();
	}

}
