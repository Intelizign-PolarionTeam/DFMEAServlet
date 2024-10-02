package com.intelizign.report.fmea.pojo;

import java.util.List;

public class WorkItem {
    private String wiType;
    private String projectId;
    private String docPath;
    private String addAsChildNode;
    private List<FieldData> fieldData;

    // Getters and setters

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

    public static class FieldData {
        private String field;
        private List<String> value;

        // Getters and setters

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }
    }
}
