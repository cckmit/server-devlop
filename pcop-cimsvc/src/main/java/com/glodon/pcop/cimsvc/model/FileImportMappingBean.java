package com.glodon.pcop.cimsvc.model;

import com.glodon.pcop.cim.engine.dataServiceFeature.vo.RelationshipMappingVO;


/**
 * 文件导入输入参数
 */
public class FileImportMappingBean {
    private PropertyMappingBeanGis propertyMappingBean;
    private RelationshipMappingVO relationshipMappingVO;

    public PropertyMappingBeanGis getPropertyMappingBean() {
        return propertyMappingBean;
    }

    public void setPropertyMappingBean(PropertyMappingBeanGis propertyMappingBean) {
        this.propertyMappingBean = propertyMappingBean;
    }

    public RelationshipMappingVO getRelationshipMappingVO() {
        return relationshipMappingVO;
    }

    public void setRelationshipMappingVO(RelationshipMappingVO relationshipMappingVO) {
        this.relationshipMappingVO = relationshipMappingVO;
    }
}
