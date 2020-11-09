package com.glodon.pcop.cimsvc.feature;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryType;
import com.glodon.pcop.cimsvc.model.adapter.IndustryTypeAdapter;
import com.glodon.pcop.cimsvc.model.adapter.ObjectTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Iterator;
import java.util.List;

public class InfoObjectTypeDataTimeTest {

    public static void main(String[] args) {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        Gson gson = new Gson();

        InfoObjectTypeVO infoObjectTypeVO = InfoObjectFeatures.getInfoObjectTypeVOByType(CimConstants.defauleSpaceName, "ImportModel", true, false);
//        System.out.println("objectId=" + infoObjectTypeVO.getObjectId() + ", createDateTiem=" + infoObjectTypeVO.getCreateDateTime() + ", updateDateTime=" + infoObjectTypeVO.getUpdateDateTime() + ", datasetSize=" + infoObjectTypeVO.getLinkedDatasets().size());
//        System.out.println(gson.toJson(infoObjectTypeVO));
//        System.out.println(gson.toJson(ObjectTypeAdapter.objectTypeAdapterWithoutProperty(infoObjectTypeVO)));

        List<IndustryTypeVO> industryTypeList = IndustryTypeFeatures.listIndustryTypesInherit(CimConstants.defauleSpaceName, 0);
        for (IndustryTypeVO industryTypeVO : industryTypeList) {
//            JsonElement jsonElement= gson.toJsonTree(industryTypeList);
            System.out.println("industryType=" + industryTypeVO.getIndustryTypeName() + ":\n" + gson.toJson(IndustryTypeAdapter.industryTreeAdapter(industryTypeVO)));
            /*if (industryTypeVO.getIndustryTypeName().equals("测试-一级行业分类")) {
                JsonElement jsonElement= gson.toJsonTree(industryTypeList);
                Iterator<JsonElement> iterator = jsonElement.getAsJsonArray().iterator();
                while (iterator.hasNext()) {
                    JsonElement element = iterator.next();
                    System.out.println(element.getAsJsonObject());
                }
            }*/
        }
//



    }

}
