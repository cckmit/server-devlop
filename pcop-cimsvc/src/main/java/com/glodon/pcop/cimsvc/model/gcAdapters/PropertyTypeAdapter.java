package com.glodon.pcop.cimsvc.model.gcAdapters;

import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cimsvc.model.PropertyTypeVOBean;

public class PropertyTypeAdapter {

    public static PropertyTypeVO typeCast(PropertyTypeVOBean propertyTypeVOBean) {
        PropertyTypeVO propertyTypeVO = new PropertyTypeVO();

//        propertyTypeVO.setTenantId(propertyTypeVOBean.getTenantId());
        propertyTypeVO.setPropertyTypeId(propertyTypeVOBean.getPropertyTypeId());
        propertyTypeVO.setPropertyTypeName(propertyTypeVOBean.getPropertyTypeName());
        propertyTypeVO.setPropertyTypeDesc(propertyTypeVOBean.getPropertyTypeDesc());
        propertyTypeVO.setPropertyFieldDataClassify(propertyTypeVOBean.getPropertyFieldDataClassify());
        propertyTypeVO.setAdditionalConfigItems(propertyTypeVOBean.getAdditionalConfigItems());

        return propertyTypeVO;
    }

    public static PropertyTypeVOBean typeCast(PropertyTypeVO propertyTypeVO) {
        PropertyTypeVOBean propertyTypeVOBean = new PropertyTypeVOBean();

//        propertyTypeVOBean.setTenantId(propertyTypeVO.getTenantId());
        propertyTypeVOBean.setPropertyTypeId(propertyTypeVO.getPropertyTypeId());
        propertyTypeVOBean.setPropertyTypeName(propertyTypeVO.getPropertyTypeName());
        propertyTypeVOBean.setPropertyTypeDesc(propertyTypeVO.getPropertyTypeDesc());
        propertyTypeVOBean.setPropertyFieldDataClassify(propertyTypeVO.getPropertyFieldDataClassify());
        propertyTypeVOBean.setAdditionalConfigItems(propertyTypeVO.getAdditionalConfigItems());

        return propertyTypeVOBean;
    }


}
