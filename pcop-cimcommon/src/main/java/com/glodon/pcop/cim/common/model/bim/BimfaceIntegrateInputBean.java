package com.glodon.pcop.cim.common.model.bim;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@ApiModel(value = "bimface 模型集成输入")
public class BimfaceIntegrateInputBean {
    @ApiModelProperty(value = "集成后显示名称")
    @Size(min = 3, max = 256)
    private String name;
    @ApiModelProperty(value = "待集成的文件")
    @NotEmpty
    private List<BimfaceIntegrateSourceBean> sources;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BimfaceIntegrateSourceBean> getSources() {
        return sources;
    }

    public void setSources(List<BimfaceIntegrateSourceBean> sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "BimfaceIntegrateInputBean{" +
                "name='" + name + '\'' +
                ", sources=" + sources +
                '}';
    }
}
