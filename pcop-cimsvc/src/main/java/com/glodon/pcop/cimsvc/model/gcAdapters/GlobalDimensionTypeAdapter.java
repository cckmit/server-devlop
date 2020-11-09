package com.glodon.pcop.cimsvc.model.gcAdapters;

import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.GlobalDimensionTypeVO;
import com.glodon.pcop.cimsvc.model.DatasetBean;
import com.glodon.pcop.cimsvc.model.dimension.DimensionTypeInputBean;

public class GlobalDimensionTypeAdapter {

    public static GlobalDimensionTypeVO typeCast(DimensionTypeInputBean globalDimensionTypeBean) {
        GlobalDimensionTypeVO globalDimensionTypeVO = new GlobalDimensionTypeVO();

        globalDimensionTypeVO.setTenantId(globalDimensionTypeBean.getTenantId());
        globalDimensionTypeVO.setDimensionTypeName(globalDimensionTypeBean.getDimensionTypeName());
        globalDimensionTypeVO.setDimensionTypeDesc(globalDimensionTypeBean.getDimensionTypeDesc());

        DatasetBean datasetBean = globalDimensionTypeBean.getLinkedDataset();
        if (datasetBean != null) {
            globalDimensionTypeVO.setLinkedDataset(DataSetAdapter.typeCast(globalDimensionTypeBean.getLinkedDataset()));
        }

        return globalDimensionTypeVO;
    }


    public static DimensionTypeInputBean typeCast(GlobalDimensionTypeVO globalDimensionTypeVO) {
        DimensionTypeInputBean globalDimensionTypeBean = new DimensionTypeInputBean();

        globalDimensionTypeBean.setTenantId(globalDimensionTypeVO.getTenantId());
        globalDimensionTypeBean.setDimensionTypeName(globalDimensionTypeVO.getDimensionTypeName());
        globalDimensionTypeBean.setDimensionTypeDesc(globalDimensionTypeVO.getDimensionTypeDesc());

        DatasetVO datasetVO = globalDimensionTypeVO.getLinkedDataset();
        if (datasetVO != null) {
            globalDimensionTypeBean.setLinkedDataset(DataSetAdapter.typeCast(globalDimensionTypeVO.getLinkedDataset()));
        }

        return globalDimensionTypeBean;
    }
}
