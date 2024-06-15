package io.github.shk95.coclayoutbot.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.shk95.coclayoutbot.config.ApplicationProperties;
import io.github.shk95.coclayoutbot.domain.YoutubeChannel;
import io.github.shk95.coclayoutbot.domain.YoutubeVideo;
import io.github.shk95.coclayoutbot.service.YoutubeApi;
import io.github.shk95.coclayoutbot.service.YoutubeApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


@Slf4j
@RequiredArgsConstructor
@Component
public class YoutubeApiAdapter implements YoutubeApi {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final ApplicationProperties properties;

	private static ResponseEntity<String> request(RestTemplate restTemplate,
	                                              String baseUrl,
	                                              Map<String, String> queryParams,
	                                              Consumer<Exception> errorLog) throws YoutubeApiException {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl);
		queryParams.forEach(uriBuilder::queryParam);
		String url = uriBuilder.build().encode().toString();

		ResponseEntity<String> responseEntity;
		try {
			responseEntity = restTemplate.exchange(
					url,
					HttpMethod.GET,
					null,
					String.class
			);
		} catch (Exception e) {
			errorLog.accept(e);
			throw new YoutubeApiException();
		}
		return responseEntity;
	}

	public List<YoutubeVideo.Part> getVideosFromChannel(YoutubeChannel channel) throws YoutubeApiException {
		return getVideosFromChannel(channel, null);
	}

	private List<YoutubeVideo.Part> getVideosFromChannel(YoutubeChannel channel, String nextPageTokenFrom) throws YoutubeApiException {
		String baseUrl = "https://youtube.googleapis.com/youtube/v3/search";

		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("part", "snippet");
		queryParams.put("type", "video");
		queryParams.put("channelId", channel.channelId());
		queryParams.put("order", "date");
		queryParams.put("maxResults", "50");
		queryParams.put("key", this.getToken());
		if (nextPageTokenFrom != null) {
			queryParams.put("pageToken", nextPageTokenFrom);
		}

		ResponseEntity<String> responseEntity = request(
				restTemplate, baseUrl, queryParams,
				e -> log.error("Error occurred while making request to YouTube API: {}", e.getMessage(), e)
		);

		HttpStatusCode statusCode = responseEntity.getStatusCode();
		if (statusCode != HttpStatus.OK) {
			log.error("Request to YouTube API (Search:list) failed with status code: {} and response body: {}",
					statusCode, responseEntity.getBody());
			throw new YoutubeApiException();
		}

		String responseBody = responseEntity.getBody();
		List<YoutubeVideo.Part> result = new ArrayList<>();
		JsonNode rootNode;
		try {
			rootNode = objectMapper.readTree(responseBody);
			String nextPageTokenTo = rootNode.path("nextPageToken").asText();
			JsonNode node = rootNode.path("items");
			AtomicBoolean checkDate = new AtomicBoolean(false);

			node.forEach(item -> {
				JsonNode snippet = item.path("snippet");
				String videoId = item.path("id").path("videoId").asText();
				Instant publishedAt = Instant.parse(snippet.path("publishedAt").asText());
				String title = snippet.path("title").asText();

				if (publishedAt.isBefore(channel.lastUpdateAt())) {
					checkDate.set(true);
				} else {
					YoutubeVideo.Part videoSearchResult = YoutubeVideo.Part.builder()
							.youtubeChannel(channel)
							.videoId(videoId)
							.title(title)
							.publishedAt(publishedAt)
							.build();
					log.info("Use YouTube API | Search:list | result : [{}]", videoSearchResult);
					result.add(videoSearchResult);
				}
			});

			if (!checkDate.get() && !nextPageTokenTo.isEmpty()) {
				List<YoutubeVideo.Part> next = this.getVideosFromChannel(channel, nextPageTokenTo);
				result.addAll(next);
			}
		} catch (Exception e) {
			log.error("Error occurred while processing YouTube API response: {}", e.getMessage(), e);
			throw new YoutubeApiException();
		}
		return result;
	}

	private String getToken() {
		return this.properties.getSecret().getYoutubeApiKey();
	}

	public YoutubeVideo getVideoDetail(YoutubeVideo.Part youtubeVideoInfoPart) throws YoutubeApiException {
		String baseUrl = "https://www.googleapis.com/youtube/v3/videos";

		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("part", "snippet,contentDetails");
		queryParams.put("id", youtubeVideoInfoPart.videoId());
		queryParams.put("key", this.getToken());

		ResponseEntity<String> responseEntity = request(
				restTemplate, baseUrl, queryParams,
				e -> log.error("Error occurred while making request to YouTube API: {}", e.getMessage(), e)
		);

		HttpStatusCode statusCode = responseEntity.getStatusCode();
		if (statusCode != HttpStatus.OK) {
			log.error("Request to YouTube API (Videos:list) failed with status code: {} and response body: {}",
					statusCode, responseEntity.getBody());
			throw new YoutubeApiException();
		}

		String responseBody = responseEntity.getBody();
		YoutubeVideo youtubeVideo;
		JsonNode rootNode;
		try {
			rootNode = objectMapper.readTree(responseBody);
			JsonNode item = rootNode.path("items").get(0);
			String description = item.path("snippet").path("description").asText();
			Duration duration = Duration.parse(item.path("contentDetails").path("duration").asText());
			youtubeVideo = YoutubeVideo.builder()
					.youtubeVideoInfoPart(youtubeVideoInfoPart)
					.description(description)
					.duration(duration)
					.build();
		} catch (Exception e) {
			log.error("Error occurred while processing YouTube API response: {}", e.getMessage(), e);
			throw new YoutubeApiException();
		}
		log.info("Use Youtube Api | Videos:list | result : [{}]", youtubeVideoInfoPart.youtubeChannel());
		return youtubeVideo;
	}

}
