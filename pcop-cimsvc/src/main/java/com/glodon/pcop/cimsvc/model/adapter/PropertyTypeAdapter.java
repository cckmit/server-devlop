package com.glodon.pcop.cimsvc.model.adapter;

import java.util.ArrayList;
import java.util.List;

import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeConfigurationItemVO;
import com.glodon.pcop.cimsvc.model.PropertyTypeBean;

public class PropertyTypeAdapter {

	public static PropertyTypeBean propertyTypeAdapter(PropertyTypeConfigurationItemVO ptci) {
		PropertyTypeBean ptb = new PropertyTypeBean(ptci.getTypeId(), ptci.getTypeName(), ptci.getTypeValue());
		return ptb;
	}

	public static List<PropertyTypeBean> propertyTypeAdapter(List<PropertyTypeConfigurationItemVO> ptcis) {
		List<PropertyTypeBean> ptbs = new ArrayList<PropertyTypeBean>();
		for (PropertyTypeConfigurationItemVO entitiy : ptcis) {
			ptbs.add(new PropertyTypeBean(entitiy.getTypeId(), entitiy.getTypeName(), entitiy.getTypeValue()));
		}
		return ptbs;
	}

}
