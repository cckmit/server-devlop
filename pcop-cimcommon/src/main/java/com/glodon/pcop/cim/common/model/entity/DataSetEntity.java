package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cim.common.model.entity.AddDataSetInputBean.DataSetType;
import com.glodon.pcop.cim.common.model.entity.AddDataSetInputBean.DataSource;
import com.glodon.pcop.cim.common.model.entity.AddDataSetInputBean.DataStructure;

import java.util.List;

public class DataSetEntity extends BaseEntity {
    private boolean isInherited = false;
    private boolean hasDescendant = true;
    private DataStructure dataStructure = AddDataSetInputBean.DataStructure.SINGLE;
    @JsonIgnore
    private DataSource dataSource;
    private DataSetType dataSetType = DataSetType.INSTANCE;
    //    private DataSetClassify dataSetClassify = DataSetClassify.通用属性集;
    private String dataSetClassify;
    private List<PropertyEntity> linkedProperties;

    @JsonIgnore
    private boolean collectionDataset = false;
    @JsonIgnore
    private boolean referenceDataset = false;
    @JsonIgnore
    private boolean linkDataset = false;
    @JsonIgnore
    private boolean externalDataset = false;
    @JsonIgnore
    private String tenantId;
    @JsonIgnore
    private String primaryKey;

    public DataStructure getDataStructure() {
        return dataStructure;
    }

    public void setDataStructure(DataStructure dataStructure) {
        this.dataStructure = dataStructure;
    }

    @JsonIgnore
    public DataSource getDataSource() {
        return dataSource;
    }

    @JsonIgnore
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSetType getDataSetType() {
        return dataSetType;
    }

    public void setDataSetType(DataSetType dataSetType) {
        this.dataSetType = dataSetType;
    }

    public String getDataSetClassify() {
        return dataSetClassify;
    }

    public void setDataSetClassify(String dataSetClassify) {
        this.dataSetClassify = dataSetClassify;
    }

    public boolean isInherited() {
        return isInherited;
    }

    public void setInherited(boolean inherited) {
        isInherited = inherited;
    }

    @JsonProperty("has_descendant")
    public boolean isHasDescendant() {
        return hasDescendant;
    }

    @JsonProperty("has_descendant")
    public void setHasDescendant(boolean hasDescendant) {
        this.hasDescendant = hasDescendant;
    }

    @JsonProperty("properties")
    public List<PropertyEntity> getLinkedProperties() {
        return linkedProperties;
    }

    @JsonProperty("properties")
    public void setLinkedProperties(List<PropertyEntity> linkedProperties) {
        this.linkedProperties = linkedProperties;
    }

    public boolean isCollectionDataset() {
        return collectionDataset;
    }

    public void setCollectionDataset(boolean collectionDataset) {
        this.collectionDataset = collectionDataset;
    }

    public boolean isReferenceDataset() {
        return referenceDataset;
    }

    public void setReferenceDataset(boolean referenceDataset) {
        this.referenceDataset = referenceDataset;
    }

    public boolean isLinkDataset() {
        return linkDataset;
    }

    public void setLinkDataset(boolean linkDataset) {
        this.linkDataset = linkDataset;
    }

    public boolean isExternalDataset() {
        return externalDataset;
    }

    public void setExternalDataset(boolean externalDataset) {
        this.externalDataset = externalDataset;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

}


