package com.glodon.pcop.cimsvc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@ApiModel(value = "属性")
public class PropertyTypeVOBean {
    @ApiModelProperty(value = "属性ID")
    private String propertyTypeId;
    @ApiModelProperty(value = "属性名称", required = true)
    private String propertyTypeName;
    @ApiModelProperty(value = "属性描述", example = "字符型")
    private String propertyTypeDesc;
    @ApiModelProperty(value = "属性类型", required = true, example = "STRING")
    private String propertyFieldDataClassify;
    @ApiModelProperty(value = "属性附加信息")
    @JsonIgnore
    private Map<String, Object> additionalConfigItems;
    @JsonIgnore
    private String tenantId;

    public String getPropertyTypeId() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(String propertyTypeId) {
        this.propertyTypeId = propertyTypeId;
    }

    public String getPropertyTypeName() {
        return propertyTypeName;
    }

    public void setPropertyTypeName(String propertyTypeName) {
        this.propertyTypeName = propertyTypeName;
    }

    public String getPropertyTypeDesc() {
        return propertyTypeDesc;
    }

    public void setPropertyTypeDesc(String propertyTypeDesc) {
        this.propertyTypeDesc = propertyTypeDesc;
    }

    public String getPropertyFieldDataClassify() {
        return propertyFieldDataClassify;
    }

    public void setPropertyFieldDataClassify(String propertyFieldDataClassify) {
        this.propertyFieldDataClassify = propertyFieldDataClassify;
    }

    public Map<String, Object> getAdditionalConfigItems() {
        return additionalConfigItems;
    }

    public void setAdditionalConfigItems(Map<String, Object> additionalConfigItems) {
        this.additionalConfigItems = additionalConfigItems;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
