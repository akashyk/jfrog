package com.jfrog.assessment.api;

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

	public ArtifactDto getSecondMostUsedArtifact() throws JSONException {
		// 1. Get all artifacts
		JSONArray artifacts = getAllArtifacts();

		// 2. get URLs
		List<String> urls = getArtifactUrls(artifacts);

		// 3. Get all artifacts stats
		List<ArtifactResponse> artifactsStats = getAllArtifactsStat(urls);

		// 4. Get the second most used artifact
		ArtifactStat result = getSecondMostUsedArtifact(artifactsStats).getResponse();
		
		
		return new ArtifactDto() {

			@Override
			public String getUri() {
				// TODO Auto-generated method stub
				return result.getUri();
			}

			@Override
			public Integer getDownloadCount() {
				// TODO Auto-generated method stub
				return result.getDownloadCount();
			}

		};
	}

	private JSONArray getAllArtifacts() throws JSONException {
		JSONObject jsonObj = new JSONObject(getAllFromArtifactory());
		return jsonObj.getJSONArray("results");
	}

	private List<ArtifactResponse> getAllArtifactsStat(List<String> urls) {

		long timeMilli = new Date().getTime();

		logger.info(" Pre-processing all artifacts");

		List<CompletableFuture<ArtifactResponse>> futures = urls.stream().map(url -> callApiFuture(url))
				.collect(Collectors.toList());

		List<ArtifactResponse> result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

		logger.info(" Procured all artifacts in {}ms ", timeMilli);
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

	private ArtifactResponse getArtifactWithRetries(String artifactUrl, int retriesCount) throws JSONException {

		int currentAttempt = 0;

		do {
			ArtifactResponse response = getArtifactResponse(artifactUrl);

			if (response.isStatus()) {
				return response;
			}

			currentAttempt++;
		} while (currentAttempt < retriesCount);

		return null;
	}

	private ArtifactResponse getArtifactResponse(String artifactUrl) throws JSONException {

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

	private static ExecutorService getExecutorService() {
		if (executorService == null) {
			executorService = new ThreadPoolExecutor(5, 10, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
		}
		return executorService;
	}
}
