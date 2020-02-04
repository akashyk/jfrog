package com.jfrog.assessment.api;

import com.jfrog.assessment.model.ArtifactStat;

public class ArtifactResponse  implements Comparable<ArtifactResponse>{
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
	 public int compareTo(ArtifactResponse artifactResponse) {
	        return this.getResponse().getDownloadCount().
	        		compareTo(artifactResponse.getResponse().getDownloadCount());
	    }
}
