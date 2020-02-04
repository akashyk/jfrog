package com.jfrog.assessment.api;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.jfrog.assessment.model.ArtifactStat;

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

	public ArtifactStat getAllArtifacts() throws JSONException {
		JSONObject jsonObj = new JSONObject(getAllFromArtifactory());

		JSONArray arrayJson = jsonObj.getJSONArray("results");

		JSONObject obj;

		StringBuilder sb = new StringBuilder();

		long timeMilli = new Date().getTime();

		logger.info(" Pre-processing all artifacts");

		List<String> urls = new ArrayList<>();
		for (int i = 0; i < arrayJson.length(); i++) {
			obj = arrayJson.getJSONObject(i);
			sb.delete(0, sb.length());
			String link = sb.append(artifactStatsUrl).append(obj.get(URL_REPO)).append("/").append(obj.get(URL_PATH))
					.append("/").append(obj.get(URL_NAME)).append("?").append(URL_STATS).toString();
			urls.add(link);
		}

		List<CompletableFuture<ArtifactResponse>> futures = urls.stream().map(url -> callApiFuture(url))
				.collect(Collectors.toList());

		List<ArtifactResponse> result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

		logger.info(" Procured all artifacts in {}ms ", timeMilli);
		return getSecondMostUsedArtifact(result).getResponse();
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

	private ArtifactResponse getSecondMostUsedArtifact(List<ArtifactResponse> allArtifacts) {

		int downloadCount = 0;

		if (allArtifacts.size() < 2) {
			return null;
		}

		Collections.sort(allArtifacts, Collections.reverseOrder());

		int mostUsedCount = allArtifacts.get(0).getResponse().getDownloadCount();
		ArtifactResponse secondMostUsed = null;

		for (int i = 1; i < allArtifacts.size(); i++) {
			downloadCount = allArtifacts.get(i).getResponse().getDownloadCount();

			if (downloadCount < mostUsedCount) {
				secondMostUsed = allArtifacts.get(i);
				break;
			}
		}
		return secondMostUsed;
	}

	private CompletableFuture<ArtifactResponse> callApiFuture(String link) {
		CompletableFuture<ArtifactResponse> artifactFuture = CompletableFuture.supplyAsync(() -> {
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

	private static ExecutorService getExecutorService() {
		if (executorService == null) {
			executorService = new ThreadPoolExecutor(5, 10, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
		}
		return executorService;
	}

	public String getNthMostUsed() throws JSONException {
		String artifact = "artifact";
		return artifact;
	}

	private ArtifactResponse getArtifactWithRetries(String artifactUrl, int retriesCount) throws JSONException {

		int currentAttempt = 0;

		do {
			ArtifactResponse response = getArtifactStats(artifactUrl);

			if (response.isStatus()) {
				return response;
			}

			currentAttempt++;
		} while (currentAttempt < retriesCount);

		return null;
	}

	private ArtifactResponse getArtifactStats(String artifactUrl) throws JSONException {

		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/json");
		ArtifactResponse response = new ArtifactResponse();
		response.setResponse(restTemplate.getForObject(artifactUrl, ArtifactStat.class));

		response.setStatus(false);
		if (response.getResponse() != null) {
			response.setStatus(true);
		}

		return response;
	}
}
