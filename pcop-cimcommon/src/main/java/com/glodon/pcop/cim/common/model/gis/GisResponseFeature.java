package com.glodon.pcop.cim.common.model.gis;

public class GisResponseFeature {
    private String bondingbox;
    private String combineInfo;
    private String combineNodeName;
    private String featureClassName;
    private String objectId;
    private String objectName;
    private String showName;

    public String getBondingbox() {
        return bondingbox;
    }

    public void setBondingbox(String bondingbox) {
        this.bondingbox = bondingbox;
    }

    public String getCombineInfo() {
        return combineInfo;
    }

    public void setCombineInfo(String combineInfo) {
        this.combineInfo = combineInfo;
    }

    public String getCombineNodeName() {
        return combineNodeName;
    }

    public void setCombineNodeName(String combineNodeName) {
        this.combineNodeName = combineNodeName;
    }

    public String getFeatureClassName() {
        return featureClassName;
    }

    public void setFeatureClassName(String featureClassName) {
        this.featureClassName = featureClassName;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    @Override
    public String toString() {
        return "GisResponseFeature{" +
                "bondingbox='" + bondingbox + '\'' +
                ", combineInfo='" + combineInfo + '\'' +
                ", combineNodeName='" + combineNodeName + '\'' +
                ", featureClassName='" + featureClassName + '\'' +
                ", objectId='" + objectId + '\'' +
                ", objectName='" + objectName + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }
}
