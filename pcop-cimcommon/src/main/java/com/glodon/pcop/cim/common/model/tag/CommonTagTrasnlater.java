package com.glodon.pcop.cim.common.model.tag;

import com.glodon.pcop.cim.engine.dataServiceFeature.vo.CommonTagVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CommonTag;

public class CommonTagTrasnlater {

    public static CommonTagOutputBean addOutputVoToBean(CommonTag commonTag) {
        if (commonTag == null) {
            return null;
        }
        CommonTagOutputBean tagAddOutput = new CommonTagOutputBean();
        tagAddOutput.setId(commonTag.getTagRID());
        tagAddOutput.setTagName(commonTag.getTagName());
        tagAddOutput.setTagDesc(commonTag.getTagDesc());
        return tagAddOutput;
    }

    public static CommonTagVO updateInputBeanToVo(CommonTagBaseInputBean tagAddInput) {
        CommonTagVO tagVO = new CommonTagVO();
        tagVO.setTagName(tagAddInput.getTagName());
        tagVO.setTagDesc(tagAddInput.getTagDesc());
        return tagVO;
    }
}
