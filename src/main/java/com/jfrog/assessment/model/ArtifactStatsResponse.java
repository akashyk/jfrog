package com.jfrog.assessment.model;

public class ArtifactStatsResponse implements Comparable<ArtifactStatsResponse> {
	private ArtifactStat response;
	private boolean status;

	public ArtifactStat getResponse() {
		return response;
	}

	public void setResponse(ArtifactStat response) {
		this.response = response;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public int compareTo(ArtifactStatsResponse artifactResponse) {
		return this.getResponse().getDownloadCount().compareTo(artifactResponse.getResponse().getDownloadCount());
	}
}