package com.glodon.pcop.cim.common.model;

public class PropertyMappingInputBean {
    private String srcPropertyName;
    private String desPropertyName;
    private String propertyType;

    public String getSrcPropertyName() {
        return srcPropertyName;
    }

    public void setSrcPropertyName(String srcPropertyName) {
        this.srcPropertyName = srcPropertyName;
    }

    public String getDesPropertyName() {
        return desPropertyName;
    }

    public void setDesPropertyName(String desPropertyName) {
        this.desPropertyName = desPropertyName;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    public String toString() {
        return "PropertyMappingInputBean{" +
                "srcPropertyName='" + srcPropertyName + '\'' +
                ", desPropertyName='" + desPropertyName + '\'' +
                ", propertyType='" + propertyType + '\'' +
                '}';
    }
}
