package com.intelizign.report.fmea.model;

public class LinkedWorkItems {

	private String wiToLink;
	private String linkrole;
	private boolean suspect;
	private String direction;

	public String getWiToLink() {
		return wiToLink;
	}

	public void setWiToLink(String wiToLink) {
		this.wiToLink = wiToLink;
	}

	public String getLinkrole() {
		return linkrole;
	}

	public void setLinkrole(String linkrole) {
		this.linkrole = linkrole;
	}

	public boolean isSuspect() {
		return suspect;
	}

	public void setSuspect(boolean suspect) {
		this.suspect = suspect;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

}
