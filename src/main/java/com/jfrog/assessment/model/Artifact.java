package com.jfrog.assessment.model;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class Artifact {
	private String repo;
	private String path;
	private String name;
	private String type;
	private int size;
	private Instant created;
	private String created_by;
	private Instant modified;
	private String modified_by;
	private Instant updated;
	
	public Artifact(String repo, String path, String name, String type, int size, Instant created, String created_by,
			Instant modified, String modified_by, Instant updated) {
		super();
		this.repo = repo;
		this.path = path;
		this.name = name;
		this.type = type;
		this.size = size;
		this.created = created;
		this.created_by = created_by;
		this.modified = modified;
		this.modified_by = modified_by;
		this.updated = updated;
	}
	
	public Artifact() {}

	public String getRepo() {
		return repo;
	}

	public void setRepo(String repo) {
		this.repo = repo;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Instant getCreated() {
		return created;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public Instant getModified() {
		return modified;
	}

	public void setModified(Instant modified) {
		this.modified = modified;
	}

	public String getModified_by() {
		return modified_by;
	}

	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}

	public Instant getUpdated() {
		return updated;
	}

	public void setUpdated(Instant updated) {
		this.updated = updated;
	}
	
}
