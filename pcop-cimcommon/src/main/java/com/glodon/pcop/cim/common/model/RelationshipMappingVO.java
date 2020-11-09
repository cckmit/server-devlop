package com.glodon.pcop.cim.common.model;

import java.io.Serializable;
import java.util.List;

public class RelationshipMappingVO implements Serializable {

    private static final long serialVersionUID = -371831425372833229L;
    private String sourceInfoObjectType;
    private String targetInfoObjectType;
    private String relationTypeName;
    private List<RelationshipLinkLogicVO> linkLogic;
    private String relationTypeDesc;
    private String relationshipId;
    private String tenantId;

    public String getSourceInfoObjectType() {
        return sourceInfoObjectType;
    }

    public void setSourceInfoObjectType(String sourceInfoObjectType) {
        this.sourceInfoObjectType = sourceInfoObjectType;
    }

    public String getTargetInfoObjectType() {
        return targetInfoObjectType;
    }

    public void setTargetInfoObjectType(String targetInfoObjectType) {
        this.targetInfoObjectType = targetInfoObjectType;
    }

    public String getRelationTypeName() {
        return relationTypeName;
    }

    public void setRelationTypeName(String relationTypeName) {
        this.relationTypeName = relationTypeName;
    }

    public List<RelationshipLinkLogicVO> getLinkLogic() {
        return linkLogic;
    }

    public void setLinkLogic(List<RelationshipLinkLogicVO> linkLogic) {
        this.linkLogic = linkLogic;
    }

    public String getRelationTypeDesc() {
        return relationTypeDesc;
    }

    public void setRelationTypeDesc(String relationTypeDesc) {
        this.relationTypeDesc = relationTypeDesc;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
