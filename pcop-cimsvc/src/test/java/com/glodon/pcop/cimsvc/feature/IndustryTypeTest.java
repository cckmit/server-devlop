package com.glodon.pcop.cimsvc.feature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;

public class IndustryTypeTest {

	public static void main(String[] args) {

		Map<String, String> map = new HashMap<String, String>();
		map.put(PropertyHandler.DISCOVER_ENGINE_SERVICE_LOCATION, "remote:10.129.27.104:3424/");
		map.put(PropertyHandler.DISCOVER_ENGINE_ADMIN_ACCOUNT, "root");
		map.put(PropertyHandler.DISCOVER_ENGINE_ADMIN_PWD, "wyc");
		map.put(PropertyHandler.DISCOVER_SPACE_DATABASE_TYPE, "graph");
		map.put(PropertyHandler.DISCOVER_SPACE_STORAGE_MODE, "plocal");
//		map.put(PropertyHandler.DISCOVER_DEFAULT_PREFIX, "");
//		map.put(PropertyHandler.META_CONFIG_DISCOVERSPACE, "InfoDiscover_MetaConfigSpace");

		PropertyHandler.map = map;
//		List<IndustryTypeVO> lt = IndustryTypeFeatures.listIndustryTypesInherit(CimConstants.defauleSpaceName, 3);
//		System.out.println(lt.size());

		IndustryTypeVO industryTypeVO = IndustryTypeFeatures.getIndustryTypeVOById(CimConstants.defauleSpaceName, "#103:0");
		System.out.println("industryTypeId=" + industryTypeVO.getIndustryTypeId());
		System.out.println("industryTypeName=" + industryTypeVO.getIndustryTypeName());


	}

}
