package com.glodon.pcop.cimsvc.model.stat;

import com.glodon.pcop.cim.engine.dataServiceFeature.vo.CommonTagVO;

import java.util.List;
import java.util.Map;

public class CommonTagOutput {
    CommonTagVO    commonTag;
    List<Map<String, Object>>  stat;
    List<CommonTagOutput> children;

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

    public List<CommonTagOutput> getChildren() {
        return children;
    }

    public void setChildren(List<CommonTagOutput> children) {
        this.children = children;
    }
}

