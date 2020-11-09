package com.glodon.pcop.cimsvc.model.transverter;

import com.glodon.pcop.cim.common.model.check.object.CheckAndAddDataSetInputBean;
import com.glodon.pcop.cim.common.model.check.object.CheckAndAddObjectTypeInputBean;
import com.glodon.pcop.cim.common.model.check.object.CheckAndAddPropertyInputBean;
import com.glodon.pcop.cim.common.model.entity.*;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ObjectTypeTsr {

    public static InfoObjectTypeVO updateInputBeanToVo(UpdateObjectTypeInputBean addObjectTypeInputBean) {
        if (addObjectTypeInputBean == null) {
            return null;
        }
        InfoObjectTypeVO infoObjectTypeVO = new InfoObjectTypeVO();

        infoObjectTypeVO.setObjectTypeDesc(addObjectTypeInputBean.getDesc());
        infoObjectTypeVO.setIndustryTypeId(addObjectTypeInputBean.getIndustryTypeId());

        return infoObjectTypeVO;
    }

    public static UpdateObjectTypeOutputBean voToUpdateOutputBean(InfoObjectTypeVO infoObjectTypeVO) {
        if (infoObjectTypeVO == null) {
            return null;
        }
        UpdateObjectTypeOutputBean outputBean = new UpdateObjectTypeOutputBean();

        outputBean.setId(infoObjectTypeVO.getObjectTypeName());
        outputBean.setName(infoObjectTypeVO.getObjectTypeName());
        outputBean.setDesc(infoObjectTypeVO.getObjectTypeDesc());
        outputBean.setDisabled(infoObjectTypeVO.isDisabled());
        outputBean.setParentId(infoObjectTypeVO.getParentObjectTypeName());
        outputBean.setIndustryTypeId(infoObjectTypeVO.getIndustryTypeId());
        outputBean.setCreateTime(infoObjectTypeVO.getCreateDateTime());
        outputBean.setUpdateTime(infoObjectTypeVO.getUpdateDateTime());

        return outputBean;
    }

    public static InfoObjectTypeVO addInputBeanToVo(AddObjectTypeInputBean addObjectTypeInputBean) {
        if (addObjectTypeInputBean == null) {
            return null;
        }
        InfoObjectTypeVO infoObjectTypeVO = new InfoObjectTypeVO();

        infoObjectTypeVO.setObjectTypeName(addObjectTypeInputBean.getName());
        infoObjectTypeVO.setObjectTypeDesc(addObjectTypeInputBean.getDesc());
        infoObjectTypeVO.setParentObjectTypeName(addObjectTypeInputBean.getParentId());
        infoObjectTypeVO.setIndustryTypeId(addObjectTypeInputBean.getIndustryTypeId());

        return infoObjectTypeVO;
    }

    public static AddObjectTypeOutputBean voToAddOutputBean(InfoObjectTypeVO infoObjectTypeVO) {
        if (infoObjectTypeVO == null) {
            return null;
        }
        AddObjectTypeOutputBean outputBean = new AddObjectTypeOutputBean();

        outputBean.setId(infoObjectTypeVO.getObjectTypeName());
        outputBean.setName(infoObjectTypeVO.getObjectTypeName());
        outputBean.setDesc(infoObjectTypeVO.getObjectTypeDesc());
        outputBean.setDisabled(infoObjectTypeVO.isDisabled());
        outputBean.setParentId(infoObjectTypeVO.getParentObjectTypeName());
        outputBean.setIndustryTypeId(infoObjectTypeVO.getIndustryTypeId());
        outputBean.setCreateTime(infoObjectTypeVO.getCreateDateTime());
        outputBean.setUpdateTime(infoObjectTypeVO.getUpdateDateTime());

        return outputBean;
    }

    public static InfoObjectTypeVO entityToVo(ObjectTypeEntity objectTypeEntity, boolean isIncludedDataSet,
                                              boolean isIncludedProperty) {
        if (objectTypeEntity == null) {
            return null;
        }
        InfoObjectTypeVO infoObjectTypeVO = new InfoObjectTypeVO();

        infoObjectTypeVO.setObjectId(objectTypeEntity.getId());
        infoObjectTypeVO.setObjectTypeName(objectTypeEntity.getName());
        infoObjectTypeVO.setObjectTypeDesc(objectTypeEntity.getDesc());
        infoObjectTypeVO.setDisabled(objectTypeEntity.isDisabled());
        infoObjectTypeVO.setParentObjectTypeName(objectTypeEntity.getParentId());
        infoObjectTypeVO.setIndustryTypeId(objectTypeEntity.getIndustryTypeId());
        infoObjectTypeVO.setCreateDateTime(objectTypeEntity.getCreateTime());
        infoObjectTypeVO.setUpdateDateTime(objectTypeEntity.getUpdateTime());

        if (isIncludedDataSet && objectTypeEntity.getLinkedDataSets() != null) {
            List<DatasetVO> datasetVOS = new ArrayList<>();
            for (DataSetEntity dataSetEntity : objectTypeEntity.getLinkedDataSets()) {
                datasetVOS.add(DataSetTsr.entityToVoV2(dataSetEntity, isIncludedProperty));
            }
            infoObjectTypeVO.setLinkedDatasets(datasetVOS);
        }

        return infoObjectTypeVO;
    }

    public static ObjectTypeEntity voToEntity(InfoObjectTypeVO infoObjectTypeVO, boolean isIncludedDataSet,
                                              boolean isIncludedProperty) {
        if (infoObjectTypeVO == null) {
            return null;
        }
        ObjectTypeEntity objectTypeEntity = new ObjectTypeEntity();

        objectTypeEntity.setId(infoObjectTypeVO.getObjectId());
        objectTypeEntity.setName(infoObjectTypeVO.getObjectTypeName());
        objectTypeEntity.setDesc(infoObjectTypeVO.getObjectTypeDesc());
        objectTypeEntity.setDisabled(infoObjectTypeVO.isDisabled());
        objectTypeEntity.setParentId(infoObjectTypeVO.getParentObjectTypeName());
        objectTypeEntity.setIndustryTypeId(infoObjectTypeVO.getIndustryTypeId());
        objectTypeEntity.setCreateTime(infoObjectTypeVO.getCreateDateTime());
        objectTypeEntity.setUpdateTime(infoObjectTypeVO.getUpdateDateTime());

        if (isIncludedDataSet && infoObjectTypeVO.getLinkedDatasets() != null) {
            List<DataSetEntity> dataSetEntities = new ArrayList<>();
            for (DatasetVO datasetVO : infoObjectTypeVO.getLinkedDatasets()) {
                dataSetEntities.add(DataSetTsr.voToEntityV2(datasetVO, isIncludedProperty));
            }
            objectTypeEntity.setLinkedDataSets(dataSetEntities);
        }

        return objectTypeEntity;
    }

    public static ObjectTypeEntityWithoutDataSet voToEntity(InfoObjectTypeVO infoObjectTypeVO) {
        if (infoObjectTypeVO == null) {
            return null;
        }
        ObjectTypeEntityWithoutDataSet objectTypeEntity = new ObjectTypeEntityWithoutDataSet();

        objectTypeEntity.setId(infoObjectTypeVO.getObjectId());
        objectTypeEntity.setName(infoObjectTypeVO.getObjectTypeName());
        objectTypeEntity.setDesc(infoObjectTypeVO.getObjectTypeDesc());
        objectTypeEntity.setDisabled(infoObjectTypeVO.isDisabled());
        objectTypeEntity.setParentId(infoObjectTypeVO.getParentObjectTypeName());
        objectTypeEntity.setIndustryTypeId(infoObjectTypeVO.getIndustryTypeId());
        objectTypeEntity.setCreateTime(infoObjectTypeVO.getCreateDateTime());
        objectTypeEntity.setUpdateTime(infoObjectTypeVO.getUpdateDateTime());


        return objectTypeEntity;
    }

    public static InfoObjectTypeVO checkAndAddInputBeanToVo(CheckAndAddObjectTypeInputBean addObjectTypeInputBean) {
        if (addObjectTypeInputBean == null) {
            return null;
        }
        InfoObjectTypeVO infoObjectTypeVO = new InfoObjectTypeVO();

        infoObjectTypeVO.setObjectTypeName(addObjectTypeInputBean.getName());
        infoObjectTypeVO.setObjectTypeDesc(addObjectTypeInputBean.getDesc());

        List<DatasetVO> datasetVOList = new ArrayList<>();
        List<CheckAndAddDataSetInputBean> dataSetInputBeans = addObjectTypeInputBean.getDataSets();
        for (CheckAndAddDataSetInputBean dataSetInputBean : dataSetInputBeans) {
            DatasetVO datasetVO = new DatasetVO();
            datasetVO.setDatasetName(dataSetInputBean.getName());
            datasetVO.setDatasetDesc(dataSetInputBean.getDesc());
            if (StringUtils.isNotBlank(dataSetInputBean.getClassify())) {
                datasetVO.setDatasetClassify(dataSetInputBean.getClassify());
            } else {
                datasetVO.setDatasetClassify("通用属性集");
            }
            datasetVO.setDataSetType(BusinessLogicConstant.DatasetType.INSTANCE);
            datasetVO.setDatasetStructure(DatasetDef.DatasetStructure.SINGLE);

            List<PropertyTypeVO> propertyTypeVOList = new ArrayList<>();
            List<CheckAndAddPropertyInputBean> propertyInputBeans = dataSetInputBean.getLinkedProperties();
            for (CheckAndAddPropertyInputBean propertyInputBean : propertyInputBeans) {
                PropertyTypeVO typeVO = new PropertyTypeVO();
                typeVO.setPropertyTypeName(propertyInputBean.getName());
                typeVO.setPropertyTypeDesc(propertyInputBean.getDesc());
                typeVO.setPropertyFieldDataClassify(propertyInputBean.getDataType().toString());
                propertyTypeVOList.add(typeVO);
            }
            datasetVO.setLinkedPropertyTypes(propertyTypeVOList);

            datasetVOList.add(datasetVO);
        }

        infoObjectTypeVO.setLinkedDatasets(datasetVOList);

        return infoObjectTypeVO;
    }
}
