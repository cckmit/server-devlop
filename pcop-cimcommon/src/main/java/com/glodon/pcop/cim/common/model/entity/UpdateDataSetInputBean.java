package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.glodon.pcop.cim.common.model.entity.AddDataSetInputBean.DataSetType;
import com.glodon.pcop.cim.common.model.entity.AddDataSetInputBean.DataStructure;

import java.util.List;

@JsonPropertyOrder({"name", "desc", "dataStructure", "dataSource", "dataSetType", "dataSetClassify",
        "linkedProperties"})
public class UpdateDataSetInputBean {
    private String name;
    private String desc;
    private DataStructure dataStructure = DataStructure.SINGLE;
    private DataSetType dataSetType = DataSetType.INSTANCE;
//    private DataSetClassify dataSetClassify = DataSetClassify.通用属性集;
    private String dataSetClassify;
    private List<UpdatePropertyInputBean> linkedProperties;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DataStructure getDataStructure() {
        return dataStructure;
    }

    public void setDataStructure(DataStructure dataStructure) {
        this.dataStructure = dataStructure;
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

    @JsonProperty("properties")
    public List<UpdatePropertyInputBean> getLinkedProperties() {
        return linkedProperties;
    }

    @JsonProperty("properties")
    public void setLinkedProperties(List<UpdatePropertyInputBean> linkedProperties) {
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

    @Override
    public String toString() {
        return "UpdateDataSetInputBean{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", dataStructure=" + dataStructure +
                ", dataSetType=" + dataSetType +
                ", dataSetClassify=" + dataSetClassify +
                ", linkedProperties=" + linkedProperties +
                ", collectionDataset=" + collectionDataset +
                ", referenceDataset=" + referenceDataset +
                ", linkDataset=" + linkDataset +
                ", externalDataset=" + externalDataset +
                ", tenantId='" + tenantId + '\'' +
                ", primaryKey='" + primaryKey + '\'' +
                '}';
    }

}


