package io.github.shk95.coclayoutbot.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.shk95.coclayoutbot.config.ApplicationProperties;
import io.github.shk95.coclayoutbot.service.YtMediaExtractorApi;
import io.github.shk95.coclayoutbot.service.YtMediaExtractorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
public class YtMediaExtractorApiAdapter implements YtMediaExtractorApi {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final String API_URL;

	public YtMediaExtractorApiAdapter(RestTemplate restTemplate, ObjectMapper objectMapper, ApplicationProperties properties) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
		this.API_URL = properties.getExternalApi().getYtMediaExtractorApi().getUrl();
	}

	public String makeImgUrl(String videoId, LocalTime timestamp) throws YtMediaExtractorException {
		String apiPath = "/link/image";
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(API_URL);
		uriBuilder.path(apiPath);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RequestBody requestBody = new RequestBody(
				videoId,
				timestamp.toString(), null, null);
		HttpEntity<List<RequestBody>> requestEntity = new HttpEntity<>(List.of(requestBody), headers);
		ResponseEntity<String> responseEntity;
		try {
			responseEntity = this.restTemplate.exchange(
					uriBuilder.toUriString(),
					HttpMethod.POST,
					requestEntity,
					String.class
			);
		} catch (Exception e) {
			log.error("Error occurred while making request to Image Extractor API: {}", e.getMessage(), e);
			throw new YtMediaExtractorException();
		}

		HttpStatusCode statusCode = responseEntity.getStatusCode();
		if (statusCode != HttpStatus.OK) {
			log.error("Request to Image Extractor API failed with status code: {} and response body: {}",
					statusCode, responseEntity.getBody());
			throw new YtMediaExtractorException();
		}

		String responseBody = responseEntity.getBody();
		String imgUrl;
		JsonNode rootNode;
		try {
			rootNode = objectMapper.readTree(responseBody);
			JsonNode item = rootNode.get(0); // 비디오 항목 1개만 요청하기 때문에.
			boolean success = item.get("success").asBoolean();
			if (!success) {
				int code = item.get("error").get("code").asInt();
				String errorMsg = item.get("error").get("description").asText();
				throw new YtMediaExtractorException("Image Extractor API failed to process the response. code : [" + code + "] description : [" + errorMsg + "]");
			}
			imgUrl = item.path("result").path("imgLink").asText();
			log.info("Image Extractor Api | videoId : [{} | result : [{}]", videoId, imgUrl);
		} catch (Exception e) {
			log.error("Error occurred while processing YouTube API response: {}", e.getMessage());
			throw new YtMediaExtractorException();
		}
		return imgUrl;
	}

	private record RequestBody(
			String videoId,
			String timestamp,
			String title,
			String description
	) {

	}

}
