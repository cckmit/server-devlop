package com.glodon.pcop.cimsvc.feature;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.ConfigurationFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeConfigurationItemVO;

import java.util.HashMap;
import java.util.Map;

public class PropertyTest {
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(PropertyHandler.DISCOVER_ENGINE_SERVICE_LOCATION, "remote:10.129.27.104/");
		map.put(PropertyHandler.DISCOVER_ENGINE_ADMIN_ACCOUNT, "root");
		map.put(PropertyHandler.DISCOVER_ENGINE_ADMIN_PWD, "wyc");
		map.put(PropertyHandler.DISCOVER_SPACE_DATABASE_TYPE, "graph");
		map.put(PropertyHandler.DISCOVER_SPACE_STORAGE_MODE, "plocal");
//		map.put(PropertyHandler.DISCOVER_DEFAULT_PREFIX, "");
//		map.put(PropertyHandler.META_CONFIG_DISCOVERSPACE, "InfoDiscover_MetaConfigSpace");

		PropertyHandler.map = map;

		PropertyTypeConfigurationItemVO ptc = ConfigurationFeatures
				.getPropertyTypeConfigurationItemByItemId(CimConstants.defauleSpaceName, "#83:0");

		System.out.println(ptc.getTypeId() + " " + ptc.getTypeName());
	}
}
