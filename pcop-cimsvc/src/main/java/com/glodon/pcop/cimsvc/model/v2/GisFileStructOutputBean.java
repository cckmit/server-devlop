package com.glodon.pcop.cimsvc.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class GisFileStructOutputBean {
    @JsonProperty(value = "featureCount")
    private String featureCount;
    @JsonProperty(value = "fileName")
    private String fileName;
    @JsonProperty(value = "fieldCollection")
    private List<Map<String, String>> fieldCollection;
    @JsonProperty(value = "geoType")
    private String geoType;

    public String getFeatureCount() {
        return featureCount;
    }

    public void setFeatureCount(String featureCount) {
        this.featureCount = featureCount;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Map<String, String>> getFieldCollection() {
        return fieldCollection;
    }

    public void setFieldCollection(List<Map<String, String>> fieldCollection) {
        this.fieldCollection = fieldCollection;
    }

    public String getGeoType() {
        return geoType;
    }

    public void setGeoType(String geoType) {
        this.geoType = geoType;
    }
}
