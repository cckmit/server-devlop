package com.glodon.pcop.cim.common.model.relationship;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class AddInstanceRelationInputBean {
    @NotNull
    private String relationType;
    @NotNull
    private AddInstanceRelationSourceInputBean soruceInstance;
    @NotNull
    private RelationDirectionEnum relationDirectionEnum;
    @Size(min = 1)
    private List<AddInstanceRelationTargetInputBean> targetInstances;

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public RelationDirectionEnum getRelationDirectionEnum() {
        return relationDirectionEnum;
    }

    public void setRelationDirectionEnum(RelationDirectionEnum relationDirectionEnum) {
        this.relationDirectionEnum = relationDirectionEnum;
    }

    public AddInstanceRelationSourceInputBean getSoruceInstance() {
        return soruceInstance;
    }

    public void setSoruceInstance(AddInstanceRelationSourceInputBean soruceInstance) {
        this.soruceInstance = soruceInstance;
    }

    public List<AddInstanceRelationTargetInputBean> getTargetInstances() {
        return targetInstances;
    }

    public void setTargetInstances(List<AddInstanceRelationTargetInputBean> targetInstances) {
        this.targetInstances = targetInstances;
    }

    @Override
    public String toString() {
        return "AddInstanceRelationInputBean{" +
                "relationType='" + relationType + '\'' +
                ", soruceInstance=" + soruceInstance +
                ", relationDirectionEnum=" + relationDirectionEnum +
                ", targetInstances=" + targetInstances +
                '}';
    }

    public enum RelationDirectionEnum {
        FROM, TO
    }

}
