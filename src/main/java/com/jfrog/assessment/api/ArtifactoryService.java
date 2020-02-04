package com.jfrog.assessment.api;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jfrog.assessment.dto.ArtifactDto;
import com.jfrog.assessment.dto.ArtifactStatsDto;
import com.jfrog.assessment.model.ArtifactStatsResponse;
import com.jfrog.assessment.model.ArtifactStat;

/**
 * @author akashyellappa
 *
 *         This service helps getting the second most used artifact from a demo
 *         artifactory - Orbitera
 */
@Service
public class ArtifactoryService {

	public static final Logger logger = LoggerFactory.getLogger(ArtifactoryService.class);

	final String URL_PATH = "path";
	final String URL_NAME = "name";
	final String URL_REPO = "repo";
	final String URL_STATS = "stats";

	@Value("${jfrog.orbitera.repo.all_artifacts_url}")
	private String artifactsUrl;

	@Value("${jfrog.orbitera.repo.artifact_stats_url}")
	private String artifactStatsUrl;

	@Value("${jfrog.orbitera.repo.username}")
	private String userName;

	@Value("${jfrog.orbitera.repo.password}")
	private String password;

	private static ExecutorService executorService;

	@Autowired
	RestTemplate restTemplate;

	@Bean
	public RestTemplate rest() {
		return new RestTemplate();
	}

	public ArtifactDto getTwoMostUsedJfrogArtifact() throws JSONException {

		// 1. Get all artifacts
		Instant start = Instant.now();
		logger.info(" Fetching all artifacts... ");
		JSONArray artifacts = getAllArtifacts();
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();
		logger.info(" Fetched all artifacts in {} ms. ", timeElapsed);

		// 2. get URLs
		start = Instant.now();
		logger.info(" Fetching all URLs... ");
		List<String> urls = getArtifactUrls(artifacts);
		finish = Instant.now();
		timeElapsed = Duration.between(start, finish).toMillis();
		logger.info(" Fetched all {} URLs in {} ms. ", urls.size(), timeElapsed);

		// 3. Get all artifacts stats
		start = Instant.now();
		logger.info(" Fetching all artifacts stats... ");
		List<ArtifactStatsResponse> artifactsStats = getAllArtifactsStat(urls);
		finish = Instant.now();
		timeElapsed = Duration.between(start, finish).toMillis();
		logger.info(" Fetched all {} artifacts stats in {} ms. ", artifactsStats.size(), timeElapsed);

		// 4. Get the second most used artifact

		start = Instant.now();
		logger.info(" Fetch the second most used artifact... ");
		List<ArtifactStatsResponse> response = getTwoMostPopularArtifacts(artifactsStats);

		finish = Instant.now();
		timeElapsed = Duration.between(start, finish).toMillis();
		logger.info(" Fetched the artifact in {} ms. ", urls.size(), timeElapsed);

		if (response != null) {

			List<ArtifactStatsDto> dtos = getStatsDto(response);

			return new ArtifactDto() {
				@Override
				public List<ArtifactStatsDto> getArtifactsDto() {
					return dtos;
				}
			};
		}
		
		return null;
	}

	private List<ArtifactStatsDto> getStatsDto(List<ArtifactStatsResponse> response) {
		List<ArtifactStatsDto> dtos = new ArrayList<>();
		ArtifactStatsDto dto;
		int i = 0;

		for (ArtifactStatsResponse stat : response) {
			dto = new ArtifactStatsDto();
			dto.setCount(stat.getResponse().getDownloadCount());
			dto.setRank(i + 1);
			dto.setName(stat.getResponse().getUri());
			i++;
			dtos.add(dto);
		}
		return dtos;
	}

	private JSONArray getAllArtifacts() throws JSONException {
		JSONObject jsonObj = new JSONObject(getAllFromArtifactory());
		return jsonObj.getJSONArray("results");
	}

	private List<ArtifactStatsResponse> getAllArtifactsStat(List<String> urls) {

		List<CompletableFuture<ArtifactStatsResponse>> futures = urls.stream().map(url -> callApiFuture(url))
				.collect(Collectors.toList());

		List<ArtifactStatsResponse> result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList())
				.stream().filter(x -> x != null).collect(Collectors.toList());

		return result;

	}

	private List<String> getArtifactUrls(JSONArray arrayJson) throws JSONException {

		JSONObject obj;
		StringBuilder sb = new StringBuilder();

		List<String> urls = new ArrayList<>();
		for (int i = 0; i < arrayJson.length(); i++) {
			obj = arrayJson.getJSONObject(i);
			sb.delete(0, sb.length());
			String link = sb.append(artifactStatsUrl).append(obj.get(URL_REPO)).append("/").append(obj.get(URL_PATH))
					.append("/").append(obj.get(URL_NAME)).append("?").append(URL_STATS).toString();
			urls.add(link);
		}
		return urls;
	}

	private String getAllFromArtifactory() {

		String notEncoded = userName + ":" + password;
		String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", encodedAuth);
		headers.set("Content-Type", "*/*");

		String requestPayload = "items.find({\"repo\":{\"$eq\":\"jcenter-cache\"}})";

		return restTemplate.postForObject(artifactsUrl, new HttpEntity<>(requestPayload, headers), String.class);
	}

	private List<ArtifactStatsResponse> getTwoMostPopularArtifacts(List<ArtifactStatsResponse> allArtifacts) {

		if (allArtifacts.size() < 2) {
			return null;
		}

		int downloadCount = 0;

		Collections.sort(allArtifacts, Collections.reverseOrder());

		List<ArtifactStatsResponse> stats = new ArrayList<>();
		ArtifactStatsResponse	mostDownloadedArtifact  = allArtifacts.get(0);
		int mostUsedCount = mostDownloadedArtifact.getResponse().getDownloadCount();
		stats.add(mostDownloadedArtifact);

		ArtifactStatsResponse secondMostUsed = null;

		for (int i = 1; i < allArtifacts.size(); i++) {
			downloadCount = allArtifacts.get(i).getResponse().getDownloadCount();

			if (downloadCount < mostUsedCount) {
				secondMostUsed = allArtifacts.get(i);
				break;
			}
		}

		stats.add(secondMostUsed);

		return stats;
	}

	private CompletableFuture<ArtifactStatsResponse> callApiFuture(String link) {
		CompletableFuture<ArtifactStatsResponse> artifactFuture = CompletableFuture.supplyAsync(() -> {
			try {
				return getArtifactWithRetries(link, 3);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}, getExecutorService());
		return artifactFuture;
	}

	private ArtifactStatsResponse getArtifactWithRetries(String artifactUrl, int retriesCount) throws JSONException {

		int currentAttempt = 0;

		do {
			ArtifactStatsResponse response = getArtifactResponse(artifactUrl);

			if (response.isStatus()) {
				return response;
			}

			currentAttempt++;
		} while (currentAttempt < retriesCount);

		return null;
	}

	private ArtifactStatsResponse getArtifactResponse(String artifactUrl) throws JSONException {

		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		ArtifactStatsResponse response = new ArtifactStatsResponse();
		response.setResponse(restTemplate.getForObject(artifactUrl, ArtifactStat.class));

		response.setStatus(false);
		if (response.getResponse() != null) {
			response.setStatus(true);
		}

		return response;
	}

	private static ExecutorService getExecutorService() {
		if (executorService == null) {
			executorService = new ThreadPoolExecutor(5, 10, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
		}
		return executorService;
	}
}
