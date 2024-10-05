package com.intelizign.report.fmea.model;

public class ParentWorkItem {
	private String id;
	private String linkrole;
	private String revision;
	private boolean suspect;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLinkrole() {
		return linkrole;
	}

	public void setLinkrole(String linkrole) {
		this.linkrole = linkrole;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public boolean isSuspect() {
		return suspect;
	}

	public void setSuspect(boolean suspect) {
		this.suspect = suspect;
	}

	
}