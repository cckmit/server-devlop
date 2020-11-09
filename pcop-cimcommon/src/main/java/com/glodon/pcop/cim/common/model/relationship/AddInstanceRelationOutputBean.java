package com.glodon.pcop.cim.common.model.relationship;

import java.util.List;

public class AddInstanceRelationOutputBean {
    private String relationType;
    private AddInstanceRelationInputBean.RelationDirectionEnum relationDirectionEnum;
    private AddInstanceRelationSourceOutputBean soruceInstance;
    private List<AddInstanceRelationTargetOutputBean> targetInstances;

    public AddInstanceRelationOutputBean(String relationType,
                                         AddInstanceRelationInputBean.RelationDirectionEnum relationDirectionEnum,
                                         AddInstanceRelationSourceOutputBean soruceInstance) {
        this.relationType = relationType;
        this.relationDirectionEnum = relationDirectionEnum;
        this.soruceInstance = soruceInstance;
    }

    public AddInstanceRelationOutputBean() {
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public AddInstanceRelationInputBean.RelationDirectionEnum getRelationDirectionEnum() {
        return relationDirectionEnum;
    }

    public void setRelationDirectionEnum(AddInstanceRelationInputBean.RelationDirectionEnum relationDirectionEnum) {
        this.relationDirectionEnum = relationDirectionEnum;
    }

    public AddInstanceRelationSourceOutputBean getSoruceInstance() {
        return soruceInstance;
    }

    public void setSoruceInstance(AddInstanceRelationSourceOutputBean soruceInstance) {
        this.soruceInstance = soruceInstance;
    }

    public List<AddInstanceRelationTargetOutputBean> getTargetInstances() {
        return targetInstances;
    }

    public void setTargetInstances(List<AddInstanceRelationTargetOutputBean> targetInstances) {
        this.targetInstances = targetInstances;
    }

    @Override
    public String toString() {
        return "AddInstanceRelationOutputBean{" +
                "relationType='" + relationType + '\'' +
                ", relationDirectionEnum=" + relationDirectionEnum +
                ", soruceInstance=" + soruceInstance +
                ", targetInstances=" + targetInstances +
                '}';
    }
}
