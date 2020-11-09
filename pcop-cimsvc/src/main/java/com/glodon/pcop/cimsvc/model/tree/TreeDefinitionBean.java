package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "树定义")
public class TreeDefinitionBean {
    private String ID;
    private String NAME;
    private String comment;
    private String treeNodeObj;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTreeNodeObj() {
        return treeNodeObj;
    }

    public void setTreeNodeObj(String treeNodeObj) {
        this.treeNodeObj = treeNodeObj;
    }
}
