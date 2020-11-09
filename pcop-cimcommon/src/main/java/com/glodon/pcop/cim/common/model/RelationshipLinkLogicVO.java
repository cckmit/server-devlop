package com.glodon.pcop.cim.common.model;

import java.io.Serializable;

public class RelationshipLinkLogicVO implements Serializable {

    private static final long serialVersionUID = 8642790252227327434L;
    private String relationshipLinkLogicId;
    private String sourceProperty;
    private String targetProperty;
    private String linkLogic;
    private String compositeLogic;

    public String getRelationshipLinkLogicId() {
        return relationshipLinkLogicId;
    }

    public void setRelationshipLinkLogicId(String relationshipLinkLogicId) {
        this.relationshipLinkLogicId = relationshipLinkLogicId;
    }

    public String getSourceProperty() {
        return sourceProperty;
    }

    public void setSourceProperty(String sourceProperty) {
        this.sourceProperty = sourceProperty;
    }

    public String getTargetProperty() {
        return targetProperty;
    }

    public void setTargetProperty(String targetProperty) {
        this.targetProperty = targetProperty;
    }

    public String getLinkLogic() {
        return linkLogic;
    }

    public void setLinkLogic(String linkLogic) {
        this.linkLogic = linkLogic;
    }

    public String getCompositeLogic() {
        return compositeLogic;
    }

    public void setCompositeLogic(String compositeLogic) {
        this.compositeLogic = compositeLogic;
    }
}