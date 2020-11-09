package com.glodon.pcop.cimsvc.feature;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;

import java.util.HashMap;
import java.util.Map;

public class DataSetTest {

	public static void main(String[] args) throws CimDataEngineRuntimeException {
		Map<String, String> map = new HashMap<String, String>();
		map.put(PropertyHandler.DISCOVER_ENGINE_SERVICE_LOCATION, "remote:10.129.27.104/");
		map.put(PropertyHandler.DISCOVER_ENGINE_ADMIN_ACCOUNT, "root");
		map.put(PropertyHandler.DISCOVER_ENGINE_ADMIN_PWD, "wyc");
		map.put(PropertyHandler.DISCOVER_SPACE_DATABASE_TYPE, "graph");
		map.put(PropertyHandler.DISCOVER_SPACE_STORAGE_MODE, "plocal");
//		map.put(PropertyHandler.DISCOVER_DEFAULT_PREFIX, "");
//		map.put(PropertyHandler.META_CONFIG_DISCOVERSPACE, "InfoDiscover_MetaConfigSpace");

		PropertyHandler.map = map;
		
		CimDataSpace ids = null;
		try {/*
				 * ids = DiscoverEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.
				 * defauleSpaceName);
				 * 
				 * DatasetVO ds = new DatasetVO(); ds.setDatasetClassify("#75:2");
				 * ds.setDatasetDesc("公共属性集");
				 * ds.setDatasetName(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME);
				 * 
				 * PropertyTypeVO pt = new PropertyTypeVO(); pt.setPropertyTypeId("#83:0");
				 * pt.setPropertyFieldDataClassify("#83:0"); pt.setPropertyTypeName("name");
				 * pt.setPropertyTypeDesc("名称"); List<PropertyTypeVO> lt = new
				 * ArrayList<PropertyTypeVO>(); lt.add(pt);
				 * 
				 * ds.setLinkedPropertyTypes(lt);
				 * 
				 * // pt = PropertyTypeFeatures.addPropertyType(CimConstants.defauleSpaceName,
				 * pt);
				 * 
				 * 
				 * // DatasetFeatures.addDataset(CimConstants.defauleSpaceName, ds);
				 * 
				 * // DatasetFeatures.addPropertyTypeLink(CimConstants.defauleSpaceName,
				 * "#130:0", pt.getPropertyTypeId());
				 * 
				 * 
				 * DatasetVO ds = DatasetFeatures.getDatasetVOById(ids, "#111:0");
				 * System.out.println(ds.getDatasetId());
				 * System.out.println(ds.getLinkedPropertyTypes().size());
				 * System.out.println(ds.getLinkedPropertyTypes().get(0).getPropertyTypeId());
				 * 
				 * 
				 * DatasetVO dsi = DatasetFeatures.getDatasetVOById(ids, "#130:0"); // DatasetVO
				 * dsi = DatasetFeatures.getDatasetVOById(ids, "#138:0");
				 * System.out.println("==" + dsi.getDatasetName() + "  " +
				 * dsi.getDatasetDesc());
				 * 
				 * dsi = DatasetFeatures.getDatasetVOByName(CimConstants.defauleSpaceName,
				 * BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME);
				 * 
				 * System.out.println("==" + dsi.getDatasetName() + "  " +
				 * dsi.getDatasetDesc());
				 * 
				 */

			DatasetVO datasetVO = new DatasetVO();
			datasetVO.setDatasetDesc("测试属性集");
			datasetVO.setDatasetName("");
			datasetVO.setDatasetClassify("shp");
			DatasetVO addResult = DatasetFeatures.addDataset(CimConstants.defauleSpaceName,
					datasetVO);
			
			InfoObjectFeatures.linkInfoObjectTypeWithDataset(CimConstants.defauleSpaceName, "roadTestObject",
					addResult.getDatasetId());

//			PropertyTypeVO propertyTypeVO1 = new PropertyTypeVO();
//			propertyTypeVO1.setPropertyTypeName("osm_id");
//			propertyTypeVO1.setPropertyTypeDesc("osm_id");
//			propertyTypeVO1.setPropertyFieldDataClassify("STRING");
//			Map<String, Object> addConfigItem = new HashMap<>();
////			Date additionalItemDateValue = new Date();
////			propertyTypeVO1.setAdditionalConfigItems(addConfigItem);
////			addConfigItem.put("config1", 10000);
////			addConfigItem.put("config2", additionalItemDateValue);
//			PropertyTypeVO result = PropertyTypeFeatures
//					.addPropertyType(CimConstants.defauleSpaceName, propertyTypeVO1);
//			String propertyTypr1Id = result.getPropertyTypeId();
//
//			boolean addLinkResult = DatasetFeatures.addPropertyTypeLink(CimConstants.defauleSpaceName,
//					addResult.getDatasetId(),
//					result.getPropertyTypeId());
////			List<PropertyTypeVO> propertyTypeVOList = DatasetFeatures.getLinkedPropertyTypes(
////					CimConstants.defauleSpaceName, addResult.getDatasetId());
//
//			propertyTypeVO1 = new PropertyTypeVO();
//			propertyTypeVO1.setPropertyTypeName("barrier");
//			propertyTypeVO1.setPropertyTypeDesc("barrier");
//			propertyTypeVO1.setPropertyFieldDataClassify("STRING");
//			result = PropertyTypeFeatures.addPropertyType(CimConstants.defauleSpaceName, propertyTypeVO1);
////			String propertyTypr2Id = result.getPropertyTypeId();
//
//			addLinkResult = DatasetFeatures.addPropertyTypeLink(CimConstants.defauleSpaceName, addResult.getDatasetId(),
//					result.getPropertyTypeId());
//
//			propertyTypeVO1 = new PropertyTypeVO();
//			propertyTypeVO1.setPropertyTypeName("highway");
//			propertyTypeVO1.setPropertyTypeDesc("highway");
//			propertyTypeVO1.setPropertyFieldDataClassify("STRING");
//			result = PropertyTypeFeatures.addPropertyType(CimConstants.defauleSpaceName, propertyTypeVO1);
////			propertyTypr2Id = result.getPropertyTypeId();
//
//			addLinkResult = DatasetFeatures.addPropertyTypeLink(CimConstants.defauleSpaceName, addResult.getDatasetId(),
//					result.getPropertyTypeId());

//			propertyTypeVOList = DatasetFeatures.getLinkedPropertyTypes(CimConstants.defauleSpaceName,
//					addResult.getDatasetId());

		} finally {
			if (ids != null) {
				ids.closeSpace();
			}
		}
		 


		
		
//		dataset


	}

}
