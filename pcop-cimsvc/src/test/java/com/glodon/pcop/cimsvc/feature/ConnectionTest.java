package com.glodon.pcop.cimsvc.feature;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;

public class ConnectionTest {


    public static void main(String[] args){
        PropertyHandler.map = OrientdbConfigUtil.getParameters();

        IndustryTypeVO industryTypeVO = IndustryTypeFeatures.getIndustryTypeVOById(CimConstants.defauleSpaceName, "");
//        System.out.println("industryTypeId="+industryTypeVO.getIndustryTypeId(),);

    }

}
