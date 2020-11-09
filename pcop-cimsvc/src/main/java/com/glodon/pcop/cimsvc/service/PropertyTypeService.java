package com.glodon.pcop.cimsvc.service;

import java.util.ArrayList;
import java.util.List;

import com.glodon.pcop.cim.common.util.CimConstants;
import org.springframework.stereotype.Service;

import com.glodon.pcop.cim.engine.dataServiceFeature.feature.ConfigurationFeatures;
import com.glodon.pcop.cimsvc.model.PropertyTypeBean;
import com.glodon.pcop.cimsvc.model.adapter.PropertyTypeAdapter;

/**
 * @author yuanjk(yuanjk@glodon.com), 2018-07-17 16:43:12
 *
 */
@Service
public class PropertyTypeService {

	/**
	 * 返回所有属性类型
	 * 
	 * @param tenantId
	 * @return
	 */
	public List<PropertyTypeBean> getAllPropertieType(String tenantId) {
		List<PropertyTypeBean> propertyBeans = new ArrayList<>();

		propertyBeans = PropertyTypeAdapter.propertyTypeAdapter(ConfigurationFeatures.getPropertyTypeConfigurationItems(CimConstants.defauleSpaceName, tenantId));

		return propertyBeans;
	}

}
