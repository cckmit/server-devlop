package com.glodon.pcop.cimsvc.feature;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineDataMartException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.ConfigurationFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetTypeConfigurationItemVO;
import com.glodon.pcop.cimsvc.model.PropertySetTypeBean;
import com.glodon.pcop.cimsvc.model.adapter.PropertySetAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationTest {

	public static void main(String[] args) {
		List<DatasetTypeConfigurationItemVO> lt = ConfigurationFeatures
				.getDatasetTypeConfigurationItems(CimConstants.defauleSpaceName,
				"tenantIdA");

		System.out.print(lt.size());
		List<PropertySetTypeBean> pst = PropertySetAdapter.propertyTypeAdapter(lt);

		System.out.print(pst.size());
	}

	public static void addConfigurationTypesSet(String[] args) {
		String cimSpaceName = "test";
		String tenantId = "1";

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
		try {
			ids = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
			if (!ids.hasFlatFactType(BusinessLogicConstant.CONFIGURATION_DATASETTYPE_FACT_TYPE_NAME)) {
				ids.addFlatFactType(BusinessLogicConstant.CONFIGURATION_DATASETTYPE_FACT_TYPE_NAME);
			}
			if (!ids.hasFlatFactType(BusinessLogicConstant.CONFIGURATION_PROPERTYTYPE_FACT_TYPE_NAME)) {
				ids.addFlatFactType(BusinessLogicConstant.CONFIGURATION_PROPERTYTYPE_FACT_TYPE_NAME);
			}

			Fact propertyTypeConfigItem01 = CimDataEngineComponentFactory
					.createFact(BusinessLogicConstant.CONFIGURATION_PROPERTYTYPE_FACT_TYPE_NAME);
			propertyTypeConfigItem01.setInitProperty(BusinessLogicConstant.GLOBAL_SYSTEM_PROPERTY_TENANTID, tenantId);
			propertyTypeConfigItem01.setInitProperty("typeName", "字符型");
			propertyTypeConfigItem01.setInitProperty("typeValue", "varchar");
			ids.addFact(propertyTypeConfigItem01);

			Fact propertyTypeConfigItem02 = CimDataEngineComponentFactory
					.createFact(BusinessLogicConstant.CONFIGURATION_PROPERTYTYPE_FACT_TYPE_NAME);
			propertyTypeConfigItem02.setInitProperty(BusinessLogicConstant.GLOBAL_SYSTEM_PROPERTY_TENANTID, tenantId);
			propertyTypeConfigItem02.setInitProperty("typeName", "短整型");
			propertyTypeConfigItem02.setInitProperty("typeValue", "int");
			ids.addFact(propertyTypeConfigItem02);

			Fact propertyTypeConfigItem03 = CimDataEngineComponentFactory
					.createFact(BusinessLogicConstant.CONFIGURATION_PROPERTYTYPE_FACT_TYPE_NAME);
			propertyTypeConfigItem03.setInitProperty(BusinessLogicConstant.GLOBAL_SYSTEM_PROPERTY_TENANTID, tenantId);
			propertyTypeConfigItem03.setInitProperty("typeName", "时间戳");
			propertyTypeConfigItem03.setInitProperty("typeValue", "timestamp");
			ids.addFact(propertyTypeConfigItem03);

			Fact datasetTypeConfigItem01 = CimDataEngineComponentFactory
					.createFact(BusinessLogicConstant.CONFIGURATION_DATASETTYPE_FACT_TYPE_NAME);
			datasetTypeConfigItem01.setInitProperty(BusinessLogicConstant.GLOBAL_SYSTEM_PROPERTY_TENANTID, tenantId);
			datasetTypeConfigItem01.setInitProperty("typeName", "jpg");
			ids.addFact(datasetTypeConfigItem01);

			Fact datasetTypeConfigItem02 = CimDataEngineComponentFactory
					.createFact(BusinessLogicConstant.CONFIGURATION_DATASETTYPE_FACT_TYPE_NAME);
			datasetTypeConfigItem02.setInitProperty(BusinessLogicConstant.GLOBAL_SYSTEM_PROPERTY_TENANTID, tenantId);
			datasetTypeConfigItem02.setInitProperty("typeName", "png");
			ids.addFact(datasetTypeConfigItem02);

			Fact datasetTypeConfigItem03 = CimDataEngineComponentFactory
					.createFact(BusinessLogicConstant.CONFIGURATION_DATASETTYPE_FACT_TYPE_NAME);
			datasetTypeConfigItem03.setInitProperty(BusinessLogicConstant.GLOBAL_SYSTEM_PROPERTY_TENANTID, tenantId);
			datasetTypeConfigItem03.setInitProperty("typeName", "tif");
			ids.addFact(datasetTypeConfigItem03);

		} catch (CimDataEngineDataMartException e) {
			e.printStackTrace();
		} catch (CimDataEngineRuntimeException e) {
			e.printStackTrace();
		} finally {
			if (ids != null) {
				ids.closeSpace();
			}
		}
	}

}
