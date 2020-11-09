package com.glodon.pcop.cim.common.model;

import java.util.List;

public class DataSetMapping {
    private String dataSetId;
    private String dataSetName;
    private List<PropertyMapping> propertyMappings;

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public List<PropertyMapping> getPropertyMappings() {
        return propertyMappings;
    }

    public void setPropertyMappings(List<PropertyMapping> propertyMappings) {
        this.propertyMappings = propertyMappings;
    }
}
