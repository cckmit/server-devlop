package com.glodon.pcop.cimsvc.model.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "属性集批量")
public class DataSetBatchBean extends BaseBatchBean {
    private String dataSetType;
    private String dataStructure;
    private String dataType;
    private String objectTypeId;

    public String getDataSetType() {
        return dataSetType;
    }

    public void setDataSetType(String dataSetType) {
        this.dataSetType = dataSetType;
    }

    @JsonProperty("data_set_structure")
    public String getDataStructure() {
        return dataStructure;
    }

    @JsonProperty("data_set_structure")
    public void setDataStructure(String dataStructure) {
        this.dataStructure = dataStructure;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }
}
