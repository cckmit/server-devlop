package com.glodon.pcop.cim.common.model.bim;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "待集成文件")
public class BimfaceIntegrateSourceBean {

    @NotNull
    private Long fileId;
    @ApiModelProperty(value = "专业")
    private String specialty;
    private Float specialtySort;
    @ApiModelProperty(value = "楼层")
    private String floor;
    private Float floorSort;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Float getSpecialtySort() {
        return specialtySort;
    }

    public void setSpecialtySort(Float specialtySort) {
        this.specialtySort = specialtySort;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Float getFloorSort() {
        return floorSort;
    }

    public void setFloorSort(Float floorSort) {
        this.floorSort = floorSort;
    }

    @Override
    public String toString() {
        return "BimfaceIntegrateSourceBean{" +
                "fileId=" + fileId +
                ", specialty='" + specialty + '\'' +
                ", specialtySort=" + specialtySort +
                ", floor='" + floor + '\'' +
                ", floorSort=" + floorSort +
                '}';
    }
}
