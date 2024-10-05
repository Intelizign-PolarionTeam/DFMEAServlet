package com.intelizign.report.fmea.model;

import java.util.List;

public class FieldData {
	private String field;
	private Object value;
	private List<LinkedWorkItems> linkedWorkItems;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public List<LinkedWorkItems> getLinkedWorkItems() {
		return linkedWorkItems;
	}

	public void setLinkedWorkItems(List<LinkedWorkItems> linkedWorkItems) {
		this.linkedWorkItems = linkedWorkItems;
	}

}