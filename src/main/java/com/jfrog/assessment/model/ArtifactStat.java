package com.jfrog.assessment.model;

public class ArtifactStat {
	private String uri;
	private Integer downloadCount;
	private Long lastDownloaded;
	private Long remoteDownloadCount;
	private Long remoteLastDownloaded;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Integer getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(Integer downloadCount) {
		this.downloadCount = downloadCount;
	}

	public Long getLastDownloaded() {
		return lastDownloaded;
	}

	public void setLastDownloaded(Long lastDownloaded) {
		this.lastDownloaded = lastDownloaded;
	}

	public Long getRemoteDownloadCount() {
		return remoteDownloadCount;
	}

	public void setRemoteDownloadCount(Long remoteDownloadCount) {
		this.remoteDownloadCount = remoteDownloadCount;
	}

	public Long getRemoteLastDownloaded() {
		return remoteLastDownloaded;
	}

	public void setRemoteLastDownloaded(Long remoteLastDownloaded) {
		this.remoteLastDownloaded = remoteLastDownloaded;
	}
	
	
	public String toString() {
		return "{\n" + 
				"  \"uri: " + getUri() + " \n" + 
				"  \"downloadCount: " + getDownloadCount() + "\n" + 
				"  \"lastDownloaded: " + getLastDownloaded() +"\n" + 
				"  \"remoteDownloadCount: " +  getRemoteLastDownloaded() + " \n }";
	}
}
