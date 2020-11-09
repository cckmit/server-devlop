package com.glodon.pcop.spacialimportsvc.service;

import com.glodon.pcop.cim.engine.dataServiceFeature.vo.RelationshipMappingVO;


/**
 * 文件导入输入参数
 */
public class ImportMappingBean {
    private String fileType;
    private PropertyMappingBean propertyMappingBean;
    private RelationshipMappingVO relationshipMappingVO;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public PropertyMappingBean getPropertyMappingBean() {
        return propertyMappingBean;
    }

    public void setPropertyMappingBean(PropertyMappingBean propertyMappingBean) {
        this.propertyMappingBean = propertyMappingBean;
    }

    public RelationshipMappingVO getRelationshipMappingVO() {
        return relationshipMappingVO;
    }

    public void setRelationshipMappingVO(RelationshipMappingVO relationshipMappingVO) {
        this.relationshipMappingVO = relationshipMappingVO;
    }
}
