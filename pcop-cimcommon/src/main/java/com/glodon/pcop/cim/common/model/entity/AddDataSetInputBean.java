package com.glodon.pcop.cim.common.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;


public class AddDataSetInputBean {
    private String name;
    private String desc;
    @JsonIgnore
    private boolean isInherited = false;
    @JsonIgnore
    private boolean hasDescendant = true;
    private DataStructure dataStructure = DataStructure.SINGLE;
    @JsonIgnore
    private DataSource dataSource;
    private DataSetType dataSetType = DataSetType.INSTANCE;
    //    private DataSetClassify dataSetClassify = DataSetClassify.通用属性集;
    private String dataSetClassify;
    private List<AddPropertyInputBean> linkedProperties;

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

    @JsonIgnore
    public boolean isInherited() {
        return isInherited;
    }

    @JsonIgnore
    public void setInherited(boolean inherited) {
        isInherited = inherited;
    }

    @JsonIgnore
    @JsonProperty("has_descendant")
    public boolean isHasDescendant() {
        return hasDescendant;
    }

    @JsonIgnore
    @JsonProperty("has_descendant")
    public void setHasDescendant(boolean hasDescendant) {
        this.hasDescendant = hasDescendant;
    }

    @JsonProperty("properties")
    public List<AddPropertyInputBean> getLinkedProperties() {
        return linkedProperties;
    }

    @JsonProperty("properties")
    public void setLinkedProperties(List<AddPropertyInputBean> linkedProperties) {
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
        return "AddDataSetInputBean{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", isInherited=" + isInherited +
                ", hasDescendant=" + hasDescendant +
                ", dataStructure=" + dataStructure +
                ", dataSource=" + dataSource +
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

    public enum DataSource {EXTERNAL, REFERENCE, LINK}

    public enum DataStructure {SINGLE, COLLECTION, REFERENCE, LINK, EXTERNAL, STREAM, THREE_D, TWO_D}
    // public enum DataStructure {SINGLE, COLLECTION, REFERENCE, LINK, EXTERNAL, STREAM}

    public enum DataSetType {INSTANCE, OBJECT}

//    public enum DataSetClassify {通用属性集, SHP, OBJ, PSC_SPATIAL, CAD, THREE_DMAX, RVT, DGN, LAS}//NOSONAR
    // public enum DataSetClassify {通用属性集, SHP, OBJ, PSC_SPATIAL}//NOSONAR
}


