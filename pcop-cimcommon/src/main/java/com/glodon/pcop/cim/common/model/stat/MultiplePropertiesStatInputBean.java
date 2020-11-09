package com.glodon.pcop.cim.common.model.stat;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@ApiModel(value = "多属性统计输入")
public class MultiplePropertiesStatInputBean {
    @Size(min = 1)
    private String objectTypeId;
    @NotEmpty
    private List<MultiplePropertiesStatPropertyFilterBean> properties;

    private DateTimeBetweenFilterBean condition;

    private Map<String, Object> equalConditions;

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public List<MultiplePropertiesStatPropertyFilterBean> getProperties() {
        return properties;
    }

    public void setProperties(List<MultiplePropertiesStatPropertyFilterBean> properties) {
        this.properties = properties;
    }

    public DateTimeBetweenFilterBean getCondition() {
        return condition;
    }

    public void setCondition(DateTimeBetweenFilterBean condition) {
        this.condition = condition;
    }

    public Map<String, Object> getEqualConditions() {
        return equalConditions;
    }

    public void setEqualConditions(Map<String, Object> equalConditions) {
        this.equalConditions = equalConditions;
    }

    @Override
    public String toString() {
        return "MultiplePropertiesStatInputBean{" +
                "objectTypeId='" + objectTypeId + '\'' +
                ", properties=" + properties +
                ", condition=" + condition +
                ", equalConditions=" + equalConditions +
                '}';
    }
}
