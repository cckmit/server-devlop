package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApiModel(description = "属性集")
public class PropertySetBean {

    public PropertySetBean() {
    }

    public PropertySetBean(String id, String name, Integer isBase, String propertySetTypeId) {
        super();
        this.id = id;
        this.name = name;
        this.isBase = isBase;
        this.propertySetTypeId = propertySetTypeId;
    }

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "名称", required = true)
    private String name;

    @ApiModelProperty(value = "类型ID", required = false)
    private String propertySetTypeId;

    @ApiModelProperty(value = "是否为基本属性集，0=否，1=是", example = "1", required = true, allowableValues = "0,1")
    private Integer isBase;

    @ApiModelProperty(value = "属性集合")
    private List<PropertyBean> properties = new ArrayList<>();

    @ApiModelProperty(value = "创建时间", required = false)
    private Date createDateTime;

    @ApiModelProperty(value = "更新时间", required = false)
    private Date updateDateTime;

    /**
     * 是否被子类继承
     */
    private boolean hasDescendant = true;

    /**
     * 是否继承自父类
     */
    private boolean inheritDataset = false;

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Date getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Date updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPropertySetTypeId() {
        return propertySetTypeId;
    }

    public Integer getIsBase() {
        return isBase;
    }

    public List<PropertyBean> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyBean> properties) {
        this.properties = properties;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPropertySetTypeId(String propertySetTypeId) {
        this.propertySetTypeId = propertySetTypeId;
    }

    public void setIsBase(Integer isBase) {
        this.isBase = isBase;
    }

    public boolean isHasDescendant() {
        return hasDescendant;
    }

    public void setHasDescendant(boolean hasDescendant) {
        this.hasDescendant = hasDescendant;
    }

    public boolean isInheritDataset() {
        return inheritDataset;
    }

    public void setInheritDataset(boolean inheritDataset) {
        this.inheritDataset = inheritDataset;
    }
}
