package com.jfrog.assessment.api;

import static org.mockito.Mockito.when;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import com.jfrog.assessment.api.ArtifactoryService;;

@RunWith(MockitoJUnitRunner.class)
class ArtifactoryServiceTest {

	@InjectMocks
	ArtifactoryService artifactoryService;

	@Mock
	RestTemplate restTemplate;

	@Test
	void testGetSecondMostUsedArtifact() {

		try {

			when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(HttpEntity.class), Mockito.any()))
					.thenReturn(getResponse());

			artifactoryService.getSecondMostUsedArtifact();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
