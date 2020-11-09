package com.glodon.pcop.cimsvc.model.transverter;

import com.glodon.pcop.cim.common.model.entity.AddIndustryTypeInputBean;
import com.glodon.pcop.cim.common.model.entity.AddIndustryTypeOutputBean;
import com.glodon.pcop.cim.common.model.entity.IndustryTypeEntity;
import com.glodon.pcop.cim.common.model.entity.ObjectTypeEntity;
import com.glodon.pcop.cim.common.model.entity.UpdateIndustryTypeInputBean;
import com.glodon.pcop.cim.common.model.entity.UpdateIndustryTypeOutputBean;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class IndustryTypeTsr {

    public static UpdateIndustryTypeOutputBean voToUpdateOutputBean(IndustryTypeVO typeVO) {
        Assert.notNull(typeVO, "industry type vo is null");
        UpdateIndustryTypeOutputBean entity = new UpdateIndustryTypeOutputBean();
        entity.setId(typeVO.getIndustryTypeId());
        entity.setName(typeVO.getIndustryTypeName());
        entity.setDesc(typeVO.getIndustryTypeDesc());
        entity.setCreator(typeVO.getCreatorId());
        entity.setParentId(typeVO.getParentIndustryTypeId());
        entity.setCreateTime(typeVO.getCreateDateTime());
        entity.setUpdateTime(typeVO.getUpdateDateTime());
        return entity;
    }

    public static IndustryTypeVO updateInputBeanToVo(String userId, UpdateIndustryTypeInputBean inputBean) {
        Assert.notNull(inputBean, "add industry type is null");
        IndustryTypeVO typeVO = new IndustryTypeVO();
        typeVO.setIndustryTypeDesc(inputBean.getDesc());
        typeVO.setCreatorId(userId);
        return typeVO;
    }

    public static AddIndustryTypeOutputBean voToAddOutputBean(IndustryTypeVO typeVO) {
        Assert.notNull(typeVO, "industry type vo is null");
        AddIndustryTypeOutputBean entity = new AddIndustryTypeOutputBean();
        entity.setId(typeVO.getIndustryTypeId());
        entity.setName(typeVO.getIndustryTypeName());
        entity.setDesc(typeVO.getIndustryTypeDesc());
        entity.setCreator(typeVO.getCreatorId());
        entity.setParentId(typeVO.getParentIndustryTypeId());
        entity.setCreateTime(typeVO.getCreateDateTime());
        entity.setUpdateTime(typeVO.getUpdateDateTime());
        return entity;
    }

    public static IndustryTypeVO addInputBeanToVo(String userId, AddIndustryTypeInputBean inputBean) {
        Assert.notNull(inputBean, "add industry type is null");
        IndustryTypeVO typeVO = new IndustryTypeVO();
        typeVO.setIndustryTypeName(inputBean.getName());
        typeVO.setIndustryTypeDesc(inputBean.getDesc());
        typeVO.setParentIndustryTypeId(inputBean.getParentId());
        typeVO.setCreatorId(userId);
        return typeVO;
    }

    public static IndustryTypeVO entityToVoWithoutChildren(IndustryTypeEntity entity) {
        if (entity == null) {
            return null;
        }
        IndustryTypeVO typeVO = new IndustryTypeVO();

        typeVO.setIndustryTypeId(entity.getId());
        typeVO.setIndustryTypeName(entity.getName());
        typeVO.setIndustryTypeDesc(entity.getDesc());
        typeVO.setCreatorId(entity.getCreator());
        typeVO.setTenantId(entity.getTenantId());
        typeVO.setParentIndustryTypeId(entity.getParentId());
        typeVO.setCreateDateTime(entity.getCreateTime());
        typeVO.setUpdateDateTime(entity.getUpdateTime());

        return typeVO;
    }


    public static IndustryTypeEntity voToEntityWithoutChildren(IndustryTypeVO typeVO) {
        if (typeVO == null) {
            return null;
        }

        IndustryTypeEntity entity = new IndustryTypeEntity();
        entity.setId(typeVO.getIndustryTypeId());
        entity.setName(typeVO.getIndustryTypeName());
        entity.setDesc(typeVO.getIndustryTypeDesc());
        entity.setCreator(typeVO.getCreatorId());
        entity.setTenantId(typeVO.getTenantId());
        entity.setParentId(typeVO.getParentIndustryTypeId());
        entity.setCreateTime(typeVO.getCreateDateTime());
        entity.setUpdateTime(typeVO.getUpdateDateTime());

        return entity;
    }

    public static IndustryTypeVO entityToVoWithChildren(IndustryTypeEntity entity, boolean isIncludedDataSet,
                                                        boolean isIncludedProperty) {
        if (entity == null) {
            return null;
        }
        IndustryTypeVO typeVO = entityToVoWithoutChildren(entity);

        if (entity.getChildIds() != null) {
            List<IndustryTypeVO> childTypeVOS = new ArrayList<>();
            for (IndustryTypeEntity childEntity : entity.getChildIds()) {
                childTypeVOS.add(entityToVoWithoutChildren(childEntity));
            }
            typeVO.setChildrenIndustryTypes(childTypeVOS);
        }

        if (entity.getLinkedObjectTypes() != null) {
            List<InfoObjectTypeVO> objectTypeVOS = new ArrayList<>();
            for (ObjectTypeEntity objectTypeEntity : entity.getLinkedObjectTypes()) {
                objectTypeVOS.add(ObjectTypeTsr.entityToVo(objectTypeEntity, isIncludedDataSet, isIncludedProperty));
            }
            typeVO.setLinkedInfoObjectTypes(objectTypeVOS);
        }

        return typeVO;
    }


    public static IndustryTypeEntity voToEntityWithChildren(IndustryTypeVO typeVO, boolean isIncludedDataSet,
                                                            boolean isIncludedProperty) {
        if (typeVO == null) {
            return null;
        }

        IndustryTypeEntity entity = voToEntityWithoutChildren(typeVO);

        if (typeVO.getChildrenIndustryTypes() != null) {
            List<IndustryTypeEntity> childEntities = new ArrayList<>();
            for (IndustryTypeVO childTypeVO : typeVO.getChildrenIndustryTypes()) {
                childEntities.add(voToEntityWithoutChildren(childTypeVO));
            }
            entity.setChildIds(childEntities);
        }

        if (typeVO.getLinkedInfoObjectTypes() != null) {
            List<ObjectTypeEntity> objectTypeEntities = new ArrayList<>();
            for (InfoObjectTypeVO objectTypeVO : typeVO.getLinkedInfoObjectTypes()) {
                objectTypeEntities.add(ObjectTypeTsr.voToEntity(objectTypeVO, isIncludedDataSet, isIncludedProperty));
            }
            entity.setLinkedObjectTypes(objectTypeEntities);
        }

        return entity;
    }

}
