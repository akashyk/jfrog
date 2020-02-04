package com.jfrog.assessment.model;

public class ArtifactStat {
	private String uri;
	private long downloadCount;
	private long lastDownloaded;
	private long remoteDownloadCount;
	private long remoteLastDownloaded;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public long getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(long downloadCount) {
		this.downloadCount = downloadCount;
	}

	public long getLastDownloaded() {
		return lastDownloaded;
	}

	public void setLastDownloaded(long lastDownloaded) {
		this.lastDownloaded = lastDownloaded;
	}

	public long getRemoteDownloadCount() {
		return remoteDownloadCount;
	}

	public void setRemoteDownloadCount(long remoteDownloadCount) {
		this.remoteDownloadCount = remoteDownloadCount;
	}

	public long getRemoteLastDownloaded() {
		return remoteLastDownloaded;
	}

	public void setRemoteLastDownloaded(long remoteLastDownloaded) {
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
