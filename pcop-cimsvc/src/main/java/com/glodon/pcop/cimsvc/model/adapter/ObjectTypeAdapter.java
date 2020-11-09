package com.glodon.pcop.cimsvc.model.adapter;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cimsvc.model.ObjectTypeBean;
import com.glodon.pcop.cimsvc.model.PropertySetBean;

import java.util.ArrayList;
import java.util.List;

public class ObjectTypeAdapter {

	public static ObjectTypeBean objectTypeAdapterWithoutProperty(InfoObjectTypeVO infoObjectTypeVO) {
		ObjectTypeBean objectTypeBean = new ObjectTypeBean();
		objectTypeBean.setTypeName(infoObjectTypeVO.getObjectName());
		objectTypeBean.setTypeId(infoObjectTypeVO.getObjectId());
		objectTypeBean.setCreateDateTime(infoObjectTypeVO.getCreateDateTime());
		objectTypeBean.setUpdateDateTime(infoObjectTypeVO.getUpdateDateTime());
		List<String> industryTypeIdList = InfoObjectFeatures.getCachedInfoObjectTypeAndIndustryTypeMapping(CimConstants.defauleSpaceName, infoObjectTypeVO.getObjectId());
		if (industryTypeIdList != null && industryTypeIdList.size() > 0) {
			objectTypeBean.setIndustryTypeId(industryTypeIdList.get(0));
		}
		objectTypeBean.setParentTypeId(infoObjectTypeVO.getParentObjectId());
		List<PropertySetBean> propertySetList = new ArrayList<>();
		if (infoObjectTypeVO.getLinkedDatasets() != null) {
			for (DatasetVO ds : infoObjectTypeVO.getLinkedDatasets()) {
				propertySetList.add(PropertySetAdapter.propertySetAdapterWittOutProperty(ds));
			}
		}
		objectTypeBean.setPropertySet(propertySetList);
		return objectTypeBean;
	}

	public static InfoObjectTypeVO objectTypeAdapter(ObjectTypeBean objectTypeBean) {
		InfoObjectTypeVO infoObjectTypeVO = new InfoObjectTypeVO();
		infoObjectTypeVO.setObjectName(objectTypeBean.getTypeName());
		infoObjectTypeVO.setObjectId(objectTypeBean.getTypeId());
		for (PropertySetBean propertySetBean : objectTypeBean.getPropertySet()) {
			DatasetVO ds = PropertySetAdapter.propertySetAdapter(propertySetBean);
			List<DatasetVO> list = infoObjectTypeVO.getLinkedDatasets();
			if (list == null) {
				list = new ArrayList<>();
				infoObjectTypeVO.setLinkedDatasets(list);
			}
			list.add(ds);
		}
		return infoObjectTypeVO;
	}

	public static ObjectTypeBean objectTypeAdapter(InfoObjectTypeVO iotv) {
		ObjectTypeBean otb = new ObjectTypeBean();
		otb.setTypeName(iotv.getObjectName());
		otb.setTypeId(iotv.getObjectId());
		otb.setCreateDateTime(iotv.getCreateDateTime());
		otb.setUpdateDateTime(iotv.getUpdateDateTime());
		List<IndustryTypeVO> list = InfoObjectFeatures.getLinkedIndustryTypes(CimConstants.defauleSpaceName,
				iotv.getObjectId());
		if (list.size() > 0) {
			otb.setIndustryTypeId(list.get(0).getIndustryTypeId());
		}
		otb.setParentTypeId(iotv.getParentObjectId());
		List<PropertySetBean> propertySetList = new ArrayList<>();
		if (iotv.getLinkedDatasets() != null) {
			for (DatasetVO ds : iotv.getLinkedDatasets()) {
				propertySetList.add(PropertySetAdapter.propertySetAdapter(ds));
			}
		}
		otb.setPropertySet(propertySetList);
		return otb;
	}

	public static ObjectTypeBean objectTypeAdapterWithoutProperty(InfoObjectTypeVO iotv, String industryTypeId) {

		ObjectTypeBean otb = new ObjectTypeBean();
		otb.setTypeName(iotv.getObjectName());
		otb.setTypeId(iotv.getObjectId());
		otb.setCreateDateTime(iotv.getCreateDateTime());
		otb.setUpdateDateTime(iotv.getUpdateDateTime());
		otb.setIndustryTypeId(industryTypeId);
		otb.setParentTypeId(iotv.getParentObjectId());
		List<PropertySetBean> propertySetList = new ArrayList<>();
		if (iotv.getLinkedDatasets() != null) {
			for (DatasetVO ds : iotv.getLinkedDatasets()) {
				propertySetList.add(PropertySetAdapter.propertySetAdapterWittOutProperty(ds));
			}
		}
		otb.setPropertySet(propertySetList);
		return otb;
	}

}
