package com.glodon.pcop.cimsvc.model.stat;

import java.util.List;

public class TagStatInput {
    private  String tagName;
    private  String relationShipType;
    private  List<PropertyStatVO> propertyList;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getRelationShipType() {
        return relationShipType;
    }

    public void setRelationShipType(String relationShipType) {
        this.relationShipType = relationShipType;
    }

    public List<PropertyStatVO> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<PropertyStatVO> propertyList) {
        this.propertyList = propertyList;
    }

    @Override
    public String toString() {
        return "TagStatInput{" +
                "tagName='" + tagName + '\'' +
                ", relationShipType='" + relationShipType + '\'' +
                ", propertyList=" + propertyList +
                '}';
    }
}
