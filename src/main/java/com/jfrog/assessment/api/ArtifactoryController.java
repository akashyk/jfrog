package com.jfrog.assessment.api;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jfrog.assessment.dto.ArtifactDto;
import com.jfrog.assessment.model.ArtifactStatsResponse;
import com.jfrog.assessment.model.ArtifactStat;

@RestController
@RequestMapping(value = "/api")
public class ArtifactoryController {

	public static final Logger logger = LoggerFactory.getLogger(ArtifactoryController.class);
	
	@Autowired
	ArtifactoryService artifactoryService;

	@GetMapping(value = "/artifacts")
	public ResponseEntity<ArtifactDto> getArtifacts() throws JsonMappingException, JsonProcessingException, JSONException {

		logger.info(" ======= START ======== ");
		
		ArtifactDto result = artifactoryService.getTwoMostUsedJfrogArtifact();
		if(result == null) {
			return  new ResponseEntity<>(null, HttpStatus.NO_CONTENT);	
		}

		logger.info(" ======= END ======== ");
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
