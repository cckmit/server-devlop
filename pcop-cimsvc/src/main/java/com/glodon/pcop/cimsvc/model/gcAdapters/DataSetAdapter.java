package com.glodon.pcop.cimsvc.model.gcAdapters;

import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cimsvc.model.DatasetBean;
import com.glodon.pcop.cimsvc.model.PropertyTypeVOBean;

import java.util.ArrayList;
import java.util.List;

public class DataSetAdapter {

    public static DatasetVO typeCast(DatasetBean datasetBean) {
        DatasetVO datasetVO = new DatasetVO();

        datasetVO.setTenantId(datasetBean.getTenantId());
        datasetVO.setDatasetId(datasetBean.getDatasetId());
        datasetVO.setDatasetName(datasetBean.getDatasetName());
        datasetVO.setDatasetDesc(datasetBean.getDatasetDesc());
        datasetVO.setDatasetClassify(datasetBean.getDatasetClassify());
        datasetVO.setInheritDataset(datasetBean.isInheritDataset());

        List<PropertyTypeVOBean> propertyList = datasetBean.getLinkedPropertyTypes();
        List<PropertyTypeVO> list = new ArrayList<>();
        if (propertyList != null) {
            for (PropertyTypeVOBean ptb : propertyList) {
                list.add(PropertyTypeAdapter.typeCast(ptb));
            }
        }
        datasetVO.setLinkedPropertyTypes(list);

        return datasetVO;
    }

    public static DatasetBean typeCast(DatasetVO datasetVO) {
        DatasetBean datasetBean = new DatasetBean();

        datasetBean.setTenantId(datasetVO.getTenantId());
        datasetBean.setDatasetId(datasetVO.getDatasetId());
        datasetBean.setDatasetName(datasetVO.getDatasetName());
        datasetBean.setDatasetDesc(datasetVO.getDatasetDesc());
        datasetBean.setDatasetClassify(datasetVO.getDatasetClassify());
        datasetBean.setInheritDataset(datasetVO.isInheritDataset());

        List<PropertyTypeVO> propertyList = datasetVO.getLinkedPropertyTypes();
        List<PropertyTypeVOBean> list = new ArrayList<>();
        if (propertyList != null) {
            for (PropertyTypeVO pt : propertyList) {
                list.add(PropertyTypeAdapter.typeCast(pt));
            }
        }
        datasetBean.setLinkedPropertyTypes(list);

        return datasetBean;
    }


}
