package com.glodon.pcop.cimsvc.model;

public class RelationTypeBean {
    private String relationTypeName;
    private String relationTypeDesc;
    private boolean isDisabled;

    public RelationTypeBean(String relationTypeName, String relationTypeDesc, boolean isDisabled) {
        this.relationTypeName = relationTypeName;
        this.relationTypeDesc = relationTypeDesc;
        this.isDisabled = isDisabled;
    }

    public String getRelationTypeName() {
        return relationTypeName;
    }

    public void setRelationTypeName(String relationTypeName) {
        this.relationTypeName = relationTypeName;
    }

    public String getRelationTypeDesc() {
        return relationTypeDesc;
    }

    public void setRelationTypeDesc(String relationTypeDesc) {
        this.relationTypeDesc = relationTypeDesc;
    }

    public boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(boolean disabled) {
        isDisabled = disabled;
    }
}
