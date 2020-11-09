package com.glodon.pcop.cimsvc.model.shp;

import io.swagger.annotations.ApiModel;

import java.util.Map;

@ApiModel(value = "shp mapping导入")
public class ShpMappingImportBean {
    private String objectTypeId;
    private String objectTypeName;
    private String dataSetId;
    private String dataSetName;
    private Map<String,String> propertyMapping;

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getObjectTypeName() {
        return objectTypeName;
    }

    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

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

    public Map<String, String> getPropertyMapping() {
        return propertyMapping;
    }

    public void setPropertyMapping(Map<String, String> propertyMapping) {
        this.propertyMapping = propertyMapping;
    }
}

