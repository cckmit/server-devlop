package com.glodon.pcop.cimsvc.model.tree;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "子节点统计输出")
public class ChildNodeCountBean {
    @ApiModelProperty(value = "树节点ID")
    private String ID;
    @ApiModelProperty(value = "树节点名称")
    private String NAME;
    @ApiModelProperty(value = "文件节点总量")
    private Integer filesCount = 0;
    @ApiModelProperty(value = "非文件节点总量")
    private Integer othersCount = 0;

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

    public ChildNodeCountBean(String ID, String NAME, Integer filesCount, Integer othersCount) {
        this.ID = ID;
        this.NAME = NAME;
        this.filesCount = filesCount;
        this.othersCount = othersCount;
    }

    public ChildNodeCountBean(String ID, String NAME) {
        this.ID = ID;
        this.NAME = NAME;
    }

    public ChildNodeCountBean() {
    }

    public Integer getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(Integer filesCount) {
        this.filesCount = filesCount;
    }

    public Integer getOthersCount() {
        return othersCount;
    }

    public void setOthersCount(Integer othersCount) {
        this.othersCount = othersCount;
    }

    public synchronized void addFiles(int cnt) {
        filesCount = filesCount + cnt;
    }

    public synchronized void addOthers(int cnt) {
        othersCount = othersCount + cnt;
    }

}
