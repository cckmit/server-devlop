package com.glodon.pcop.cimsvc.model.excel;

import com.glodon.pcop.cim.common.model.excel.PropertyInputBean;
import com.glodon.pcop.cim.common.model.excel.PropertyValueMappingInputBean;
import com.glodon.pcop.cimsvc.model.v2.InstancesQueryInput;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value = "Excel导出输入条件")
public class ExcelExportInputBean {
    @NotNull
    private String objectTypeId;
    private List<PropertyInputBean> properties;
    private List<PropertyValueMappingInputBean> valueMapping;
    private InstancesQueryInput queryInput;

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public List<PropertyInputBean> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyInputBean> properties) {
        this.properties = properties;
    }

    public List<PropertyValueMappingInputBean> getValueMapping() {
        return valueMapping;
    }

    public void setValueMapping(List<PropertyValueMappingInputBean> valueMapping) {
        this.valueMapping = valueMapping;
    }

    public InstancesQueryInput getQueryInput() {
        return queryInput;
    }

    public void setQueryInput(InstancesQueryInput queryInput) {
        this.queryInput = queryInput;
    }

    @Override
    public String toString() {
        return "ExcelExportInputBean{" +
                "objectTypeId='" + objectTypeId + '\'' +
                ", properties=" + properties +
                ", valueMapping=" + valueMapping +
                ", queryInput=" + queryInput +
                '}';
    }
}
