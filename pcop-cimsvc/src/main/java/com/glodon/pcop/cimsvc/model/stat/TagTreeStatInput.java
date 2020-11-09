package com.glodon.pcop.cimsvc.model.stat;

import java.util.List;

public class TagTreeStatInput {
    List<TagStatInput>   tagList;
    String instanceRid;

    public String getInstanceRid() {
        return instanceRid;
    }

    public void setInstanceRid(String instanceRid) {
        this.instanceRid = instanceRid;
    }

    public List<TagStatInput> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagStatInput> tagList) {
        this.tagList = tagList;
    }

    @Override
    public String toString() {
        return "TagTreeStatInput{" +
                "tagList=" + tagList +
                ", instanceRid='" + instanceRid + '\'' +
                '}';
    }
}
