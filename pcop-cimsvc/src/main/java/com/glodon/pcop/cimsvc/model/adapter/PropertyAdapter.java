package com.glodon.pcop.cimsvc.model.adapter;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.PropertyTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeRestrictVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cimsvc.model.PropertyBean;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyAdapter {
	private Logger log = LoggerFactory.getLogger(PropertyAdapter.class);

	public static PropertyTypeVO propertyAdapter(PropertyBean pb) {
		PropertyTypeVO pt = new PropertyTypeVO();
		if (StringUtils.isNotBlank(pb.getId())) {
			pt.setPropertyTypeId(pb.getId());
		}
		pt.setPropertyTypeName(pb.getName());
		pt.setPropertyTypeDesc(pb.getAlias());
		pt.setPropertyFieldDataClassify(PropertyTypeFeatures.getDataTypeById(CimConstants.defauleSpaceName, pb.getTypeId()));
		return pt;
	}

	public static PropertyBean propertyAdapter(PropertyTypeVO pt) {
		PropertyBean ptb = new PropertyBean();
		ptb.setId(pt.getPropertyTypeId());
		ptb.setName(pt.getPropertyTypeName());
		ptb.setAlias(pt.getPropertyTypeDesc());
		String tmp = PropertyTypeFeatures.getDataTypeByTypeName(CimConstants.defauleSpaceName, pt.getPropertyFieldDataClassify());
		String[] tmpSplit = tmp.split("=", 2);
		if (tmpSplit.length == 2) {
			ptb.setTypeId(tmpSplit[0]);
			ptb.setTypeName(tmpSplit[1]);
		}

		PropertyTypeRestrictVO restrictVO = pt.getRestrictVO();
		if (restrictVO != null) {
			ptb.setIsNull(restrictVO.getNull());
		}else {
			if (pt.getPropertyTypeName().equals(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME) || pt.getPropertyTypeName().equals(CimConstants.ID_PROPERTY_TYPE_NAME)) {
				ptb.setIsNull(false);
			} else {
				ptb.setIsNull(true);
			}
		}
		return ptb;
	}

}
