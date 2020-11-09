package com.glodon.pcop.cim.common.model;

import com.glodon.pcop.cim.common.util.EnumWrapper.DATA_SET_DATA_TYPE;
import com.glodon.pcop.cim.common.util.EnumWrapper.DATA_SET_STRUCTURE;
import com.glodon.pcop.cim.common.util.EnumWrapper.DATA_SET_TYPE;
import com.glodon.pcop.cim.common.util.EnumWrapper.IMPORT_FILE_TYPE;

import java.util.List;

public class FileImportTaskInputBean {

    private boolean isMapping;
    private String fileName;
    private IMPORT_FILE_TYPE fileType;
    private String objectTypeId;
    private DATA_SET_TYPE dataSetType;
    private DATA_SET_STRUCTURE dataSetStructure;
    private DATA_SET_DATA_TYPE dataType;
    private String dataSetName;
    private boolean isUpdated;
    private String instanceRid;
    private List<DataSetMapping> dataSetMappings;
    private List<RelationshipMappingVO> relationshipMappings;
    // @JsonIgnore
    private String tenantId;

    public boolean getIsMapping() {
        return isMapping;
    }

    public void setIsMapping(boolean mapping) {
        isMapping = mapping;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public IMPORT_FILE_TYPE getFileType() {
        return fileType;
    }

    public void setFileType(IMPORT_FILE_TYPE fileType) {
        this.fileType = fileType;
    }

    public String getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(String objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public DATA_SET_TYPE getDataSetType() {
        return dataSetType;
    }

    public void setDataSetType(DATA_SET_TYPE dataSetType) {
        this.dataSetType = dataSetType;
    }

    public DATA_SET_STRUCTURE getDataSetStructure() {
        return dataSetStructure;
    }

    public void setDataSetStructure(DATA_SET_STRUCTURE dataSetStructure) {
        this.dataSetStructure = dataSetStructure;
    }

    public DATA_SET_DATA_TYPE getDataType() {
        return dataType;
    }

    public void setDataType(DATA_SET_DATA_TYPE dataType) {
        this.dataType = dataType;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public boolean getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(boolean updated) {
        isUpdated = updated;
    }

    public String getInstanceRid() {
        return instanceRid;
    }

    public void setInstanceRid(String instanceRid) {
        this.instanceRid = instanceRid;
    }

    public List<DataSetMapping> getDataSetMappings() {
        return dataSetMappings;
    }

    public void setDataSetMappings(List<DataSetMapping> dataSetMappings) {
        this.dataSetMappings = dataSetMappings;
    }

    public List<RelationshipMappingVO> getRelationshipMappings() {
        return relationshipMappings;
    }

    public void setRelationshipMappings(List<RelationshipMappingVO> relationshipMappings) {
        this.relationshipMappings = relationshipMappings;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}

