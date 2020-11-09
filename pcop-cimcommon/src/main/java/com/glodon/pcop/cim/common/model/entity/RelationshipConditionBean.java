package com.glodon.pcop.cim.common.model.entity;

public class RelationshipConditionBean {
    private String id;
    private String sourcePropertyName;
    private String targetPropertyName;
    private LinkLogic linkLogic = LinkLogic.EQUAL;
    private CompositeLogic compositeLogic = CompositeLogic.DEFAULT;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourcePropertyName() {
        return sourcePropertyName;
    }

    public void setSourcePropertyName(String sourcePropertyName) {
        this.sourcePropertyName = sourcePropertyName;
    }

    public String getTargetPropertyName() {
        return targetPropertyName;
    }

    public void setTargetPropertyName(String targetPropertyName) {
        this.targetPropertyName = targetPropertyName;
    }

    public LinkLogic getLinkLogic() {
        return linkLogic;
    }

    public void setLinkLogic(LinkLogic linkLogic) {
        this.linkLogic = linkLogic;
    }

    public CompositeLogic getCompositeLogic() {
        return compositeLogic;
    }

    public void setCompositeLogic(CompositeLogic compositeLogic) {
        this.compositeLogic = compositeLogic;
    }

    public enum LinkLogic {EQUAL}

    public enum CompositeLogic {DEFAULT, AND, OR}
}