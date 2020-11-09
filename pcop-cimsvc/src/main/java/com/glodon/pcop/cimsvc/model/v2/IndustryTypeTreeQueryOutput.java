package com.glodon.pcop.cimsvc.model.v2;

import com.glodon.pcop.cim.common.model.entity.IndustryTypeEntity;
import com.glodon.pcop.cim.common.model.entity.ObjectTypeEntity;

import java.util.List;

public class IndustryTypeTreeQueryOutput {
    private List<IndustryTypeEntity> industryTypes;
    private List<ObjectTypeEntity> objectTypes;

    public List<IndustryTypeEntity> getIndustryTypes() {
        return industryTypes;
    }

    public void setIndustryTypes(List<IndustryTypeEntity> industryTypes) {
        this.industryTypes = industryTypes;
    }

    public List<ObjectTypeEntity> getObjectTypes() {
        return objectTypes;
    }

    public void setObjectTypes(List<ObjectTypeEntity> objectTypes) {
        this.objectTypes = objectTypes;
    }
}
