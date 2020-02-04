package com.jfrog.assessment.api;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jfrog.assessment.model.ArtifactStat;

@RestController
@RequestMapping(value = "/api")
public class ArtifactoryController {

	@Autowired
	ArtifactoryService artifactoryService;

	@GetMapping(value = "/artifacts")
	public ResponseEntity<ArtifactStat> getArtifacts() throws JsonMappingException, JsonProcessingException, JSONException {
		
		ArtifactStat result = artifactoryService.getAllArtifacts();
		if(result == null) {
			return  new ResponseEntity<>(null, HttpStatus.NO_CONTENT);	
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping(value = "hw")
	public String hw() {
		return "hewwwlloooo wooollldds";

	}
}
