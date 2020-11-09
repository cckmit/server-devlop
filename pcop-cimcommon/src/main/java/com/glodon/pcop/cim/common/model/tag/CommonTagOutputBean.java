package com.glodon.pcop.cim.common.model.tag;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "新增标签输出")
public class CommonTagOutputBean {
    private String id;
    private String tagName;
    private String tagDesc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagDesc() {
        return tagDesc;
    }

    public void setTagDesc(String tagDesc) {
        this.tagDesc = tagDesc;
    }


    @Override
    public String toString() {
        return "CommonTagAddOutputBean{" +
                "id='" + id + '\'' +
                ", tagName='" + tagName + '\'' +
                ", tagDesc='" + tagDesc + '\'' +
                '}';
    }
}
