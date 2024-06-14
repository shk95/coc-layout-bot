package io.github.shk95.coclayoutbot.service;

import io.github.shk95.coclayoutbot.domain.LayoutCollector;
import io.github.shk95.coclayoutbot.domain.LayoutPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduledTasks {

	private final LayoutCollector layoutCollector;
	private final LayoutPublisher layoutPublisher;

	/*
	 * TODO: 다른방식으로 구현 필요.
	 *  1. 반드시 하나의 작업 흐름으로, 순차적 실행필요.
	 *  2. layout img 를 추출하는과정에서 실패시 재시도 과정 필요. [각 추출단계는 독립적으로 작동가능.]
	 *  */
	@Scheduled(fixedDelay = 2 * 60 * 60 * 1000)
	public void collect() {
		layoutCollector.collectVideosFromChannel()
				.next(layoutCollector.collectLayoutLink())
				.next(layoutCollector.collectLayoutImg())
				.next(layoutCollector.collectLayoutImg())
				.next(layoutCollector.collectLayoutImg())
				.next(layoutCollector.collectLayoutImg())
				.next(() -> {
							layoutPublisher.publish();
							log.info("Layouts published.");
							return true;
						}
				)
				.run();
	}

}
