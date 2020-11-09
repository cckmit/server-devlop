package com.glodon.pcop.cimstatsvc.model;

import com.glodon.pcop.cim.engine.dataServiceFeature.vo.CommonTagVO;

import java.util.List;
import java.util.Map;

public class CommonTagData {
    CommonTagVO    commonTag;
    List<Map<String, Object>>  stat;
    List<CommonTagData> children;

    public CommonTagVO getCommonTag() {
        return commonTag;
    }


    public void setCommonTag(CommonTagVO commonTag) {
        this.commonTag = commonTag;
    }

    public List<Map<String, Object>> getStat() {
        return stat;
    }

    public void setStat(List<Map<String, Object>> stat) {
        this.stat = stat;
    }

    public List<CommonTagData> getChildren() {
        return children;
    }

    public void setChildren(List<CommonTagData> children) {
        this.children = children;
    }
}

