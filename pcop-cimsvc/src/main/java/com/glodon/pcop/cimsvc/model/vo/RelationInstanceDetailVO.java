package com.glodon.pcop.cimsvc.model.vo;

public class RelationInstanceDetailVO {

    private String relationInstanceRID;
    private String relationTypeName;
    private String relationTypeDesc;
    private String fromDataRID;
    private String toDataRID;
    private String fromDataTypeName;
    private String fromDataTypeDesc;
    private String toDataTypeName;
    private String toDataTypeDesc;
    private String fromDataClassify;
    private String toDataClassify;

    public String getRelationInstanceRID() {
        return relationInstanceRID;
    }

    public void setRelationInstanceRID(String relationInstanceRID) {
        this.relationInstanceRID = relationInstanceRID;
    }

    public String getRelationTypeName() {
        return relationTypeName;
    }

    public void setRelationTypeName(String relationTypeName) {
        this.relationTypeName = relationTypeName;
    }

    public String getFromDataRID() {
        return fromDataRID;
    }

    public void setFromDataRID(String fromDataRID) {
        this.fromDataRID = fromDataRID;
    }

    public String getToDataRID() {
        return toDataRID;
    }

    public void setToDataRID(String toDataRID) {
        this.toDataRID = toDataRID;
    }

    public String getFromDataTypeName() {
        return fromDataTypeName;
    }

    public void setFromDataTypeName(String fromDataTypeName) {
        this.fromDataTypeName = fromDataTypeName;
    }

    public String getToDataTypeName() {
        return toDataTypeName;
    }

    public void setToDataTypeName(String toDataTypeName) {
        this.toDataTypeName = toDataTypeName;
    }

    public String getFromDataClassify() {
        return fromDataClassify;
    }

    public void setFromDataClassify(String fromDataClassify) {
        this.fromDataClassify = fromDataClassify;
    }

    public String getToDataClassify() {
        return toDataClassify;
    }

    public void setToDataClassify(String toDataClassify) {
        this.toDataClassify = toDataClassify;
    }

    public String getRelationTypeDesc() {
        return relationTypeDesc;
    }

    public void setRelationTypeDesc(String relationTypeDesc) {
        this.relationTypeDesc = relationTypeDesc;
    }

    public String getFromDataTypeDesc() {
        return fromDataTypeDesc;
    }

    public void setFromDataTypeDesc(String fromDataTypeDesc) {
        this.fromDataTypeDesc = fromDataTypeDesc;
    }

    public String getToDataTypeDesc() {
        return toDataTypeDesc;
    }

    public void setToDataTypeDesc(String toDataTypeDesc) {
        this.toDataTypeDesc = toDataTypeDesc;
    }
}
