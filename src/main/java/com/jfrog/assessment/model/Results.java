package com.jfrog.assessment.model;

public class Results {
	
	private Artifact[] artifacts;
	
	private Range range;
	public Artifact[] getArtifacts() {
		return artifacts;
	}
	public void setArtifacts(Artifact[] artifacts) {
		this.artifacts = artifacts;
	}
	public Range getRange() {
		return range;
	}
	public void setRange(Range range) {
		this.range = range;
	}
}
