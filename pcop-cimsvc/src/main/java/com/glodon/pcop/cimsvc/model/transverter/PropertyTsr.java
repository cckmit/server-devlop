package com.glodon.pcop.cimsvc.model.transverter;

import com.glodon.pcop.cim.common.model.entity.AddPropertyInputBean;
import com.glodon.pcop.cim.common.model.entity.AddPropertyRestrictInputBean;
import com.glodon.pcop.cim.common.model.entity.PropertyEntity;
import com.glodon.pcop.cim.common.model.entity.PropertyRestrictBean;
import com.glodon.pcop.cim.common.model.entity.UpdatePropertyInputBean;
import com.glodon.pcop.cim.common.model.entity.UpdatePropertyRestrictInputBean;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeRestrictVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;

import java.util.Date;

public class PropertyTsr {

    public static PropertyTypeRestrictVO updateInputToVo(UpdatePropertyRestrictInputBean bean) {
        if (bean == null) {
            return null;
        }
        PropertyTypeRestrictVO restrictVO = new PropertyTypeRestrictVO();
        restrictVO.setPropertyTypeRestrictId(bean.getId());
        restrictVO.setNull(bean.isNull());
        restrictVO.setPrimaryKey(bean.isPrimaryKey());
        restrictVO.setDefaultValue(bean.getDefaultValue());
        restrictVO.setLength(bean.getLength());
        restrictVO.setUniqueKey(bean.isUniqueKey());
        return restrictVO;
    }

    public static PropertyTypeRestrictVO addInputToVo(AddPropertyRestrictInputBean bean) {
        if (bean == null) {
            return null;
        }
        PropertyTypeRestrictVO restrictVO = new PropertyTypeRestrictVO();

        restrictVO.setNull(bean.isNull());
        restrictVO.setPrimaryKey(bean.isPrimaryKey());
        restrictVO.setDefaultValue(bean.getDefaultValue());
        restrictVO.setLength(bean.getLength());
        restrictVO.setUniqueKey(bean.isUniqueKey());
        return restrictVO;
    }

    public static PropertyTypeRestrictVO beanToVo(PropertyRestrictBean bean) {
        if (bean == null) {
            return null;
        }
        PropertyTypeRestrictVO restrictVO = new PropertyTypeRestrictVO();

        restrictVO.setPropertyTypeRestrictId(bean.getId());
        restrictVO.setNull(bean.isNull());
        restrictVO.setPrimaryKey(bean.isPrimaryKey());
        restrictVO.setDefaultValue(bean.getDefaultValue());
        restrictVO.setLength(bean.getLength());
        restrictVO.setUniqueKey(bean.isUniqueKey());

        restrictVO.setDatasetId(bean.getDataSetId());
        restrictVO.setPropertyTypeId(bean.getPropertyId());

        return restrictVO;
    }

    public static PropertyRestrictBean voToBean(PropertyTypeRestrictVO restrictVO) {
        if (restrictVO == null) {
            return null;
        }
        PropertyRestrictBean bean = new PropertyRestrictBean();

        bean.setId(restrictVO.getPropertyTypeRestrictId());
        bean.setNull(restrictVO.getNull());
        bean.setPrimaryKey(restrictVO.getPrimaryKey());
        bean.setDefaultValue(restrictVO.getDefaultValue());
        bean.setLength(restrictVO.getLength());
        bean.setUniqueKey(restrictVO.getUniqueKey());

        bean.setDataSetId(restrictVO.getDatasetId());
        bean.setPropertyId(restrictVO.getPropertyTypeId());

        return bean;
    }

    public static PropertyTypeVO updateInputToVo(UpdatePropertyInputBean entity, boolean isIncludedRestrict) {
        if (entity == null) {
            return null;
        }
        PropertyTypeVO typeVO = new PropertyTypeVO();
        typeVO.setPropertyTypeId(entity.getId());
        typeVO.setPropertyTypeName(entity.getName());
        typeVO.setPropertyTypeDesc(entity.getDesc());
        typeVO.setPropertyFieldDataClassify(entity.getDataType().toString());
        typeVO.setAdditionalConfigItems(entity.getConfItems());

        if (isIncludedRestrict && entity.getRestrictInfo() != null) {
            typeVO.setRestrictVO(updateInputToVo(entity.getRestrictInfo()));
        }

        return typeVO;
    }

    public static PropertyTypeVO addInputToVo(AddPropertyInputBean entity, boolean isIncludedRestrict) {
        if (entity == null) {
            return null;
        }
        PropertyTypeVO typeVO = new PropertyTypeVO();

        typeVO.setPropertyTypeName(entity.getName());
        typeVO.setPropertyTypeDesc(entity.getDesc());
        typeVO.setPropertyFieldDataClassify(entity.getDataType().toString());
        typeVO.setAdditionalConfigItems(entity.getConfItems());

        if (isIncludedRestrict && entity.getRestrictInfo() != null) {
            typeVO.setRestrictVO(addInputToVo(entity.getRestrictInfo()));
        }

        return typeVO;
    }

    public static PropertyTypeVO entityToVo(PropertyEntity entity, boolean isIncludedRestrict) {
        if (entity == null) {
            return null;
        }
        PropertyTypeVO typeVO = new PropertyTypeVO();

        typeVO.setPropertyTypeId(entity.getId());
        typeVO.setPropertyTypeName(entity.getName());
        typeVO.setPropertyTypeDesc(entity.getDesc());
        typeVO.setPropertyFieldDataClassify(entity.getDataType().toString());
        typeVO.setAdditionalConfigItems(entity.getConfItems());

        if (isIncludedRestrict && entity.getRestrictInfo() != null) {
            typeVO.setRestrictVO(beanToVo(entity.getRestrictInfo()));
        }

        return typeVO;
    }


    public static PropertyEntity voToEntity(PropertyTypeVO typeVO, boolean isIncludedRestrict) {
        if (typeVO == null) {
            return null;
        }
        PropertyEntity entity = new PropertyEntity();

        entity.setId(typeVO.getPropertyTypeId());
        entity.setName(typeVO.getPropertyTypeName());
        entity.setDesc(typeVO.getPropertyTypeDesc());
        entity.setDataType(PropertyEntity.DataTypes.valueOf(typeVO.getPropertyFieldDataClassify()));
        entity.setConfItems(typeVO.getAdditionalConfigItems());

        if (isIncludedRestrict && typeVO.getRestrictVO() != null) {
            entity.setRestrictInfo(voToBean(typeVO.getRestrictVO()));
        }

        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());

        return entity;
    }

}
