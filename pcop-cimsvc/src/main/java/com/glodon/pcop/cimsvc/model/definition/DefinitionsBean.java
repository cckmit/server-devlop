package com.glodon.pcop.cimsvc.model.definition;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DefinitionsBean {
    private List<IndustryBatchBean> industries;
    private List<ObjectBatchBean> objectTypes;
    private List<DataSetBatchBean> dataSets;
    private List<PropertyBatchBean> properties;

    @JsonProperty("industry_types")
    public List<IndustryBatchBean> getIndustries() {
        return industries;
    }

    @JsonProperty("industry_types")
    public void setIndustries(List<IndustryBatchBean> industries) {
        this.industries = industries;
    }

    public List<ObjectBatchBean> getObjectTypes() {
        return objectTypes;
    }

    public void setObjectTypes(List<ObjectBatchBean> objectTypes) {
        this.objectTypes = objectTypes;
    }

    public List<DataSetBatchBean> getDataSets() {
        return dataSets;
    }

    public void setDataSets(List<DataSetBatchBean> dataSets) {
        this.dataSets = dataSets;
    }

    public List<PropertyBatchBean> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyBatchBean> properties) {
        this.properties = properties;
    }
}
