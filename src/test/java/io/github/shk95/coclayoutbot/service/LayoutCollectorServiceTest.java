package io.github.shk95.coclayoutbot.service;

import io.github.shk95.coclayoutbot.adapter.YtMediaExtractorApiAdapter;
import io.github.shk95.coclayoutbot.domain.LayoutCollector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

//@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
@DisplayName("DefaultCollectorService 테스트")
@SpringBootTest(properties = "spring.profiles.active=test")
class LayoutCollectorServiceTest {

	@MockBean
	YtMediaExtractorApiAdapter ytMediaExtractorApi;

	@Autowired
	LayoutCollector layoutCollector;

	//	@Sql("youtube_channel.sql")
	@Test
	void test() throws YtMediaExtractorException {
		given(ytMediaExtractorApi.makeImgUrl(anyString(), any())).willReturn("test ~~~~ img ~~~~ url ~~~~");
		layoutCollector.collectVideosFromChannel()
				.next(layoutCollector.collectLayoutLink())
				.next(layoutCollector.collectLayoutImg())
				.run();
	}

}
