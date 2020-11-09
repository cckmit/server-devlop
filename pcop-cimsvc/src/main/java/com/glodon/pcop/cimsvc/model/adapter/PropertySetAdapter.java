package com.glodon.pcop.cimsvc.model.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetTypeConfigurationItemVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cimsvc.model.PropertyBean;
import com.glodon.pcop.cimsvc.model.PropertySetBean;
import com.glodon.pcop.cimsvc.model.PropertySetTypeBean;

public class PropertySetAdapter {

	public static DatasetVO propertySetAdapter(PropertySetBean psb) {
		DatasetVO ds = new DatasetVO();
		if (StringUtils.isNotBlank(psb.getId())) {
			ds.setDatasetId(psb.getId());
		}
		ds.setDatasetName(psb.getName());
		ds.setDatasetDesc(psb.getName());
		ds.setDatasetClassify(psb.getPropertySetTypeId());
		ds.setCreateDateTime(psb.getCreateDateTime());
		ds.setUpdateDateTime(psb.getUpdateDateTime());
		List<PropertyTypeVO> list = new ArrayList<>();
		for (PropertyBean pb : psb.getProperties()) {
			PropertyTypeVO ptv = PropertyAdapter.propertyAdapter(pb);
			list.add(ptv);
		}
		ds.setLinkedPropertyTypes(list);
		return ds;
	}

	public static PropertySetBean propertySetAdapter(DatasetVO datasetVO) {
		PropertySetBean propertySetBean = new PropertySetBean(datasetVO.getDatasetId(), datasetVO.getDatasetName(), 0, datasetVO.getDatasetClassify());
        propertySetBean.setCreateDateTime(datasetVO.getCreateDateTime());
        propertySetBean.setUpdateDateTime(datasetVO.getUpdateDateTime());
		propertySetBean.setHasDescendant(datasetVO.isHasDescendant());
		propertySetBean.setInheritDataset(datasetVO.isInheritDataset());
		List<PropertyBean> list = new ArrayList<>();
		for (PropertyTypeVO pt : datasetVO.getLinkedPropertyTypes()) {
			PropertyBean ptb = PropertyAdapter.propertyAdapter(pt);
			list.add(ptb);
		}
		propertySetBean.setProperties(list);
		return propertySetBean;
	}

	public static PropertySetBean propertySetAdapterWittOutProperty(DatasetVO datasetVO) {
		PropertySetBean propertySetBean = new PropertySetBean(datasetVO.getDatasetId(), datasetVO.getDatasetName(), 0, datasetVO.getDatasetClassify());
		propertySetBean.setInheritDataset(datasetVO.isInheritDataset());
		propertySetBean.setHasDescendant(datasetVO.isHasDescendant());
        propertySetBean.setCreateDateTime(datasetVO.getCreateDateTime());
        propertySetBean.setUpdateDateTime(datasetVO.getUpdateDateTime());
		return propertySetBean;
	}

	public static PropertySetTypeBean propertyTypeAdapter(DatasetTypeConfigurationItemVO dstc) {
		PropertySetTypeBean pstb = new PropertySetTypeBean(dstc.getTypeId(), dstc.getTypeName());
		return pstb;
	}

	public static List<PropertySetTypeBean> propertyTypeAdapter(List<DatasetTypeConfigurationItemVO> dstcs) {
		List<PropertySetTypeBean> psts = new ArrayList<>();
		for (DatasetTypeConfigurationItemVO entity : dstcs) {
			psts.add(new PropertySetTypeBean(entity.getTypeId(), entity.getTypeName()));
		}
		return psts;
	}

}
