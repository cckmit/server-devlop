package com.glodon.pcop.cimsvc.model.transverter;

import com.glodon.pcop.cim.common.model.entity.RelationshipConditionBean;
import com.glodon.pcop.cim.common.model.entity.RelationshipEntity;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.RelationshipLinkLogicVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.RelationshipMappingVO;

import java.util.ArrayList;
import java.util.List;

public class RelationshipTsr {

    public static RelationshipLinkLogicVO beanToVo(RelationshipConditionBean conditionBean) {
        if (conditionBean == null) {
            return null;
        }
        RelationshipLinkLogicVO linkLogicVO = new RelationshipLinkLogicVO();

        linkLogicVO.setRelationshipLinkLogicId(conditionBean.getId());
        linkLogicVO.setSourceProperty(conditionBean.getSourcePropertyName());
        linkLogicVO.setTargetProperty(conditionBean.getTargetPropertyName());
        linkLogicVO.setLinkLogic(conditionBean.getLinkLogic().toString());
        linkLogicVO.setCompositeLogic(conditionBean.getCompositeLogic().toString());

        return linkLogicVO;
    }


    public static RelationshipConditionBean voToBean(RelationshipLinkLogicVO linkLogicVO) {
        if (linkLogicVO == null) {
            return null;
        }
        RelationshipConditionBean conditionBean = new RelationshipConditionBean();

        conditionBean.setId(linkLogicVO.getRelationshipLinkLogicId());
        conditionBean.setSourcePropertyName(linkLogicVO.getSourceProperty());
        conditionBean.setTargetPropertyName(linkLogicVO.getTargetProperty());
        conditionBean.setLinkLogic(RelationshipConditionBean.LinkLogic.valueOf(linkLogicVO.getLinkLogic()));
        conditionBean.setCompositeLogic(RelationshipConditionBean.CompositeLogic.valueOf(linkLogicVO.getCompositeLogic()));

        return conditionBean;
    }


    public static RelationshipMappingVO entityToVo(RelationshipEntity entity) {
        if (entity == null) {
            return null;
        }
        RelationshipMappingVO mappingVO = new RelationshipMappingVO();

        mappingVO.setRelationshipId(entity.getId());
        mappingVO.setRelationshipDesc(entity.getDesc());
        mappingVO.setRelationTypeName(entity.getRelationType());
        mappingVO.setSourceInfoObjectType(entity.getSourceObjectTypeId());
        mappingVO.setTargetInfoObjectType(entity.getTargetObjectTypeId());
        mappingVO.setTenantId(entity.getTenantId());
        if (entity.getConditions() != null) {
            List<RelationshipLinkLogicVO> linkLogicVOS = new ArrayList<>();
            for (RelationshipConditionBean conditionBean : entity.getConditions()) {
                try {
                    linkLogicVOS.add(beanToVo(conditionBean));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mappingVO.setLinkLogic(linkLogicVOS);
        }

        return mappingVO;
    }


    public static RelationshipEntity voToEntity(RelationshipMappingVO mappingVO) {
        if (mappingVO == null) {
            return null;
        }

        RelationshipEntity entity = new RelationshipEntity();

        entity.setId(mappingVO.getRelationshipId());
        entity.setDesc(mappingVO.getRelationshipDesc());
        entity.setRelationType(mappingVO.getRelationTypeName());
        entity.setSourceObjectTypeId(mappingVO.getSourceInfoObjectType());
        entity.setTargetObjectTypeId(mappingVO.getTargetInfoObjectType());
        entity.setTenantId(mappingVO.getTenantId());
        if (mappingVO.getLinkLogic() != null) {
            List<RelationshipConditionBean> conditionBeans = new ArrayList<>();
            for (RelationshipLinkLogicVO logicVO : mappingVO.getLinkLogic()) {
                try {
                    conditionBeans.add(voToBean(logicVO));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            entity.setConditions(conditionBeans);
        }

        return entity;
    }

}
