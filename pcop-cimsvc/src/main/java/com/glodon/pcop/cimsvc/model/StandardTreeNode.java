package com.glodon.pcop.cimsvc.model;

import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * @author tangd-a
 * @date 2019/4/10 14:33
 */
@ApiModel(value = "树节点")
public class StandardTreeNode {
    private String key;
    private String title;
    private String parentId;
    private List<StandardTreeNode> children;

    private String rid;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<StandardTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<StandardTreeNode> children) {
        this.children = children;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }
}
