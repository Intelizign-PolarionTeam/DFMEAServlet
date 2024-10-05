package com.intelizign.report.fmea.model;

import java.util.List;

public class CreateWorkItemPojo {
    private String wiType;
    private String projectId;
    private String docPath;
    private String addAsChildNode;
    private List<FieldData> fieldData;

    public String getWiType() {
        return wiType;
    }

    public void setWiType(String wiType) {
        this.wiType = wiType;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDocPath() {
        return docPath;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    public String getAddAsChildNode() {
        return addAsChildNode;
    }

    public void setAddAsChildNode(String addAsChildNode) {
        this.addAsChildNode = addAsChildNode;
    }

    public List<FieldData> getFieldData() {
        return fieldData;
    }

    public void setFieldData(List<FieldData> fieldData) {
        this.fieldData = fieldData;
    }
}