package com.intelizign.report.fmea.model;

import java.util.List;

public class CreateLinkPojo {
	private String child;
	private String project;
	private List<ParentWorkItem> parents;

	public String getChild() {
		return child;
	}

	public void setChild(String child) {
		this.child = child;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public List<ParentWorkItem> getParents() {
		return parents;
	}

	public void setParents(List<ParentWorkItem> parents) {
		this.parents = parents;
	}

	

}
