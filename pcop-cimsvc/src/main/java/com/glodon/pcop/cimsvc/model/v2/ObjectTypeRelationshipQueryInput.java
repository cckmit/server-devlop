package com.glodon.pcop.cimsvc.model.v2;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;

public class ObjectTypeRelationshipQueryInput extends BaseQueryInputBean{
    private String relationTypeName;
    private RelationDirection relationDirection;

    public String getRelationTypeName() {
        return relationTypeName;
    }

    public void setRelationTypeName(String relationTypeName) {
        this.relationTypeName = relationTypeName;
    }

    public RelationDirection getRelationDirection() {
        return relationDirection;
    }

    public void setRelationDirection(RelationDirection relationDirection) {
        this.relationDirection = relationDirection;
    }
}
