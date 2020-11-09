package com.glodon.pcop.cim.common.model;

public class PropertyMapping {
    private String propertyRid;
    private String sourcePropertyName;
    private String targetPropertyName;
    private String propertyValue;

    public String getPropertyRid() {
        return propertyRid;
    }

    public void setPropertyRid(String propertyRid) {
        this.propertyRid = propertyRid;
    }

    public String getSourcePropertyName() {
        return sourcePropertyName;
    }

    public void setSourcePropertyName(String sourcePropertyName) {
        this.sourcePropertyName = sourcePropertyName;
    }

    public String getTargetPropertyName() {
        return targetPropertyName;
    }

    public void setTargetPropertyName(String targetPropertyName) {
        this.targetPropertyName = targetPropertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
