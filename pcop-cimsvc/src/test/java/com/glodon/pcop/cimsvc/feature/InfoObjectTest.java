package com.glodon.pcop.cimsvc.feature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;

import javax.validation.constraints.AssertFalse;

public class InfoObjectTest {

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(PropertyHandler.DISCOVER_ENGINE_SERVICE_LOCATION, "remote:10.129.27.104/");
		map.put(PropertyHandler.DISCOVER_ENGINE_ADMIN_ACCOUNT, "root");
		map.put(PropertyHandler.DISCOVER_ENGINE_ADMIN_PWD, "wyc");
		map.put(PropertyHandler.DISCOVER_SPACE_DATABASE_TYPE, "graph");
		map.put(PropertyHandler.DISCOVER_SPACE_STORAGE_MODE, "plocal");
//		map.put(PropertyHandler.DISCOVER_DEFAULT_PREFIX, "");
//		map.put(PropertyHandler.META_CONFIG_DISCOVERSPACE, "InfoDiscover_MetaConfigSpace");

		PropertyHandler.map = OrientdbConfigUtil.getParameters();

		/*InfoObjectTypeVO iot = InfoObjectFeatures.getInfoObjectTypeVOByType(CimConstants.defauleSpaceName,
				"object_second");

		System.out.println(iot.getTenantId());
		System.out.println(iot.getObjectId());
		System.out.println(iot.getLinkedDatasets().size());
		System.out.println(iot.getLinkedDatasets().get(0).getDatasetName());*/
		
		
//		Map<String, String> kkMap = new HashMap<>();
//		kkMap.put("osm_id", "osm_id");
//		kkMap.put("barrier", "barrier");
//		kkMap.put("man_made", "man_made");
//		kkMap.put("highway", "highway");
		
//		InfoObjectFeatures.loadInfoObjectData(CimConstants.defauleSpaceName, "road-20180808211004.zip", kkMap, infoObjectDataList);

		List<DatasetVO> list = InfoObjectFeatures.getLinkedDatasets(CimConstants.defauleSpaceName, "ImportModel", true);
		System.out.println(list.size());
		for (DatasetVO ds : list) {
			System.out.println("data set: " + ds.getDatasetId() + " " + ds.getDatasetName());
			for (PropertyTypeVO pt : ds.getLinkedPropertyTypes()) {
				System.out.println("property type: " + pt.getPropertyTypeId() + ", " + pt.getPropertyTypeName() + ", "
						+ pt.getPropertyFieldDataClassify());
			}
		}
	}

}
