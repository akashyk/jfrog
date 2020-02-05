package com.jfrog.assessment.api;

import static org.mockito.Mockito.when;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.jfrog.assessment.api.ArtifactoryService;
import com.jfrog.assessment.dto.ArtifactDto;
import com.jfrog.assessment.model.ArtifactStat;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactoryServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private ArtifactoryService artifactoryService;

	private ArtifactStat artifactStat1;
	private ArtifactStat artifactStat2;
	private ArtifactStat artifactStat3;
	private ArtifactStat artifactStat4;

	@Before
	public void init()
	{
		ReflectionTestUtils.setField(artifactoryService, "artifactsUrl", "http://35.222.197.119/artifactory/api/search/aql");
		ReflectionTestUtils.setField(artifactoryService, "artifactStatsUrl", "http://35.222.197.119/artifactory/api/storage/");
		ReflectionTestUtils.setField(artifactoryService, "userName", "admin");
		ReflectionTestUtils.setField(artifactoryService, "password", "Oo45DYc6TP");

		artifactStat1 = new ArtifactStat();
		artifactStat2 = new ArtifactStat();
		artifactStat3 = new ArtifactStat();
		artifactStat4 = new ArtifactStat();

		artifactStat1.setUri("http:35.222.197.119.80/artifactory/jcenter-cache1");
		artifactStat1.setDownloadCount(1);
		artifactStat1.setLastDownloaded(0L);
		artifactStat1.setRemoteDownloadCount(1L);
		artifactStat1.setRemoteLastDownloaded(0L);

		artifactStat2.setUri("http:35.222.197.119.80/artifactory/jcenter-cache2");
		artifactStat2.setDownloadCount(2);
		artifactStat2.setLastDownloaded(0L);
		artifactStat2.setRemoteDownloadCount(1L);
		artifactStat2.setRemoteLastDownloaded(0L);

		artifactStat3.setUri("http:35.222.197.119.80/artifactory/jcenter-cache3");
		artifactStat3.setDownloadCount(3);
		artifactStat3.setLastDownloaded(0L);
		artifactStat3.setRemoteDownloadCount(1L);
		artifactStat3.setRemoteLastDownloaded(0L);

		artifactStat4.setUri("http:35.222.197.119.80/artifactory/jcenter-cache4");
		artifactStat4.setDownloadCount(4);
		artifactStat4.setLastDownloaded(0L);
		artifactStat4.setRemoteDownloadCount(1L);
		artifactStat4.setRemoteLastDownloaded(0L);
	}

	@Test
	public void testGetSecondMostUsedArtifact() throws Exception	{

		when(restTemplate.postForObject(Mockito.eq("http://35.222.197.119/artifactory/api/search/aql"), Mockito.any(HttpEntity.class), Mockito.eq(String.class)))
				.thenReturn(getResponse());

		when(restTemplate.getForObject(Mockito.eq("http://35.222.197.119/artifactory/api/storage/jcenter-cache/asm/asm-parent/3.3/asm-parent-3.3.pom?stats"), Mockito.eq(ArtifactStat.class)))
				.thenReturn(artifactStat1);

		when(restTemplate.getForObject(Mockito.eq("http://35.222.197.119/artifactory/api/storage/jcenter-cache/asm/asm-tree/3.3/asm-tree-3.3.jar?stats"), Mockito.eq(ArtifactStat.class)))
				.thenReturn(artifactStat2);

		when(restTemplate.getForObject(Mockito.eq("http://35.222.197.119/artifactory/api/storage/jcenter-cache/org/apache/commons/commons-lang3/3.1/commons-lang3-3.1.jar?stats"), Mockito.eq(ArtifactStat.class)))
				.thenReturn(artifactStat3);

		when(restTemplate.getForObject(Mockito.eq("http://35.222.197.119/artifactory/api/storage/jcenter-cache/org/apache/maven/plugins/maven-compiler-plugin/3.1/maven-compiler-plugin-3.1.pom?stats"), Mockito.eq(ArtifactStat.class)))
				.thenReturn(artifactStat4);

		ArtifactDto artifactDto = artifactoryService.getSecondMostUsedArtifact();

		Assert.assertNotNull(artifactDto);
		Assert.assertEquals("http:35.222.197.119.80/artifactory/jcenter-cache3", artifactDto.getUri());
		Assert.assertEquals(Integer.valueOf(3), artifactDto.getDownloadCount());
	}

	@Test(expected = JSONException.class)
	public void testShouldThrowJSONException() throws JSONException {
		when(restTemplate.postForObject(Mockito.eq("http://35.222.197.119/artifactory/api/search/aql"), Mockito.any(HttpEntity.class), Mockito.eq(String.class)))
				.thenReturn("abcd'\"");
		artifactoryService.getSecondMostUsedArtifact();
	}

	@Test(expected = NullPointerException.class)
	public void testShouldThrowNullPointerException() throws JSONException {
		when(restTemplate.postForObject(Mockito.eq("http://35.222.197.119/artifactory/api/search/aql"), Mockito.any(HttpEntity.class), Mockito.eq(String.class)))
				.thenReturn(null);
		artifactoryService.getSecondMostUsedArtifact();
	}

	@Test(expected = MockitoException.class)
	public void testShouldThrowExceptionOnGetArtifact() throws Exception	{

		when(restTemplate.getForObject(Mockito.eq("http://35.222.197.119/artifactory/api/storage/jcenter-cache/asm/asm-tree/3.3/asm-tree-3.3.jar?stats"), Mockito.eq(ArtifactStat.class)))
				.thenThrow(JSONException.class);

		artifactoryService.getSecondMostUsedArtifact();
	}

	@Test
	public void testGetNullAsOnlyOneArtifactReturned() throws Exception	{

		when(restTemplate.postForObject(Mockito.eq("http://35.222.197.119/artifactory/api/search/aql"), Mockito.any(HttpEntity.class), Mockito.eq(String.class)))
				.thenReturn(getResponse());

		when(restTemplate.getForObject(Mockito.eq("http://35.222.197.119/artifactory/api/storage/jcenter-cache/asm/asm-parent/3.3/asm-parent-3.3.pom?stats"), Mockito.eq(ArtifactStat.class)))
				.thenReturn(artifactStat1);

		when(restTemplate.getForObject(Mockito.eq("http://35.222.197.119/artifactory/api/storage/jcenter-cache/asm/asm-tree/3.3/asm-tree-3.3.jar?stats"), Mockito.eq(ArtifactStat.class)))
				.thenReturn(null);

		when(restTemplate.getForObject(Mockito.eq("http://35.222.197.119/artifactory/api/storage/jcenter-cache/org/apache/commons/commons-lang3/3.1/commons-lang3-3.1.jar?stats"), Mockito.eq(ArtifactStat.class)))
				.thenReturn(null);

		when(restTemplate.getForObject(Mockito.eq("http://35.222.197.119/artifactory/api/storage/jcenter-cache/org/apache/maven/plugins/maven-compiler-plugin/3.1/maven-compiler-plugin-3.1.pom?stats"), Mockito.eq(ArtifactStat.class)))
				.thenReturn(null);

		ArtifactDto artifactDto = artifactoryService.getSecondMostUsedArtifact();

		Assert.assertNull(artifactDto);
	}

	private String getResponse() {
		return "{\n" + "  \"results\": [\n" + "    {\n" + "      \"repo\": \"jcenter-cache\",\n"
				+ "      \"path\": \"asm/asm-parent/3.3\",\n" + "      \"name\": \"asm-parent-3.3.pom\",\n"
				+ "      \"type\": \"file\",\n" + "      \"size\": 4330,\n"
				+ "      \"created\": \"2019-04-22T22:25:38.975Z\",\n" + "      \"created_by\": \"anonymous\",\n"
				+ "      \"modified\": \"2010-10-06T13:06:48.000Z\",\n" + "      \"modified_by\": \"anonymous\",\n"
				+ "      \"updated\": \"2019-04-22T22:25:38.976Z\"\n" + "    },\n" + "    {\n"
				+ "      \"repo\": \"jcenter-cache\",\n" + "      \"path\": \"asm/asm-tree/3.3\",\n"
				+ "      \"name\": \"asm-tree-3.3.jar\",\n" + "      \"type\": \"file\",\n" + "      \"size\": 21503,\n"
				+ "      \"created\": \"2019-04-22T22:25:45.174Z\",\n" + "      \"created_by\": \"anonymous\",\n"
				+ "      \"modified\": \"2010-10-06T13:07:04.000Z\",\n" + "      \"modified_by\": \"anonymous\",\n"
				+ "      \"updated\": \"2019-04-22T22:25:45.175Z\"\n" + "    },\n" + "    {\n"
				+ "      \"repo\": \"jcenter-cache\",\n" + "      \"path\": \"org/apache/commons/commons-lang3/3.1\",\n"
				+ "      \"name\": \"commons-lang3-3.1.jar\",\n" + "      \"type\": \"file\",\n"
				+ "      \"size\": 315805,\n" + "      \"created\": \"2019-04-22T22:25:43.916Z\",\n"
				+ "      \"created_by\": \"anonymous\",\n" + "      \"modified\": \"2011-11-15T07:27:05.000Z\",\n"
				+ "      \"modified_by\": \"anonymous\",\n" + "      \"updated\": \"2019-04-22T22:25:43.917Z\"\n"
				+ "    },\n" + "    {\n" + "      \"repo\": \"jcenter-cache\",\n"
				+ "      \"path\": \"org/apache/maven/plugins/maven-compiler-plugin/3.1\",\n"
				+ "      \"name\": \"maven-compiler-plugin-3.1.pom\",\n" + "      \"type\": \"file\",\n"
				+ "      \"size\": 10210,\n" + "      \"created\": \"2019-04-22T23:05:32.153Z\",\n"
				+ "      \"created_by\": \"anonymous\",\n" + "      \"modified\": \"2013-04-03T12:04:15.000Z\",\n"
				+ "      \"modified_by\": \"anonymous\",\n" + "      \"updated\": \"2019-04-22T23:05:32.154Z\"\n"
				+ "    }\n" + "  ],\n" + "  \"range\": {\n" + "    \"start_pos\": 0,\n" + "    \"end_pos\": 123,\n"
				+ "    \"total\": 123\n" + "  }\n" + "}";
	}
}