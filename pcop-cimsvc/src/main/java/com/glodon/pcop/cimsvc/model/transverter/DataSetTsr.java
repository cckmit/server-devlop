package com.glodon.pcop.cimsvc.model.transverter;

import com.glodon.pcop.cim.common.model.entity.*;
import com.glodon.pcop.cim.common.model.entity.AddDataSetInputBean.DataSetType;
import com.glodon.pcop.cim.common.model.entity.AddDataSetInputBean.DataSource;
import com.glodon.pcop.cim.common.model.entity.AddDataSetInputBean.DataStructure;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant.DatasetType;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef.DatasetStructure;

import java.util.ArrayList;
import java.util.List;

public class DataSetTsr {

    @Deprecated
    public static DatasetVO updateInputToVo(UpdateDataSetInputBean entity, boolean isIncludedProperty) {
        if (entity == null) {
            return null;
        }
        DatasetVO dataSetVO = new DatasetVO();
        dataSetVO.setDatasetName(entity.getName());
        dataSetVO.setDatasetDesc(entity.getDesc());

        dataSetVO.setDatasetClassify(entity.getDataSetClassify().toString());
        dataSetVO.setDataSetType(DatasetType.valueOf(entity.getDataSetType().toString()));
        if (entity.getDataStructure() != null) {
            dataSetVO.setDatasetStructure(DatasetStructure.valueOf(entity.getDataStructure().toString()));
            switch (entity.getDataStructure()) {
                case SINGLE:
                    dataSetVO.setCollectionDataset(false);
                    break;
                case COLLECTION:
                    dataSetVO.setCollectionDataset(true);
                    break;
                default:
                    dataSetVO.setCollectionDataset(false);
                    break;
            }
        }

        if (isIncludedProperty && entity.getLinkedProperties() != null) {
            List<PropertyTypeVO> propertyTypeVOS = new ArrayList<>();
            for (UpdatePropertyInputBean propertyEntity : entity.getLinkedProperties()) {
                propertyTypeVOS.add(PropertyTsr.updateInputToVo(propertyEntity, true));
            }
            dataSetVO.setLinkedPropertyTypes(propertyTypeVOS);
        }

        return dataSetVO;
    }

    public static DatasetVO updateInputToVoV2(UpdateDataSetInputBean entity, boolean isIncludedProperty) {
        if (entity == null) {
            return null;
        }
        DatasetVO dataSetVO = new DatasetVO();
        dataSetVO.setDatasetName(entity.getName());
        dataSetVO.setDatasetDesc(entity.getDesc());

        dataSetVO.setDatasetClassify(entity.getDataSetClassify().toString());
        dataSetVO.setDataSetType(DatasetType.valueOf(entity.getDataSetType().toString()));
        dataSetVO.setDatasetStructure(DatasetStructure.valueOf(entity.getDataStructure().toString()));

        if (isIncludedProperty && entity.getLinkedProperties() != null) {
            List<PropertyTypeVO> propertyTypeVOS = new ArrayList<>();
            for (UpdatePropertyInputBean propertyEntity : entity.getLinkedProperties()) {
                propertyTypeVOS.add(PropertyTsr.updateInputToVo(propertyEntity, true));
            }
            dataSetVO.setLinkedPropertyTypes(propertyTypeVOS);
        }

        return dataSetVO;
    }

    @Deprecated
    public static DatasetVO addInputToVo(AddDataSetInputBean entity, boolean isIncludedProperty) {
        if (entity == null) {
            return null;
        }
        DatasetVO dataSetVO = new DatasetVO();
        dataSetVO.setDatasetName(entity.getName());
        dataSetVO.setDatasetDesc(entity.getDesc());
        dataSetVO.setInheritDataset(entity.isInherited());
        dataSetVO.setHasDescendant(entity.isHasDescendant());

        dataSetVO.setDatasetClassify(entity.getDataSetClassify().toString());
        dataSetVO.setDataSetType(DatasetType.valueOf(entity.getDataSetType().toString()));
        if (entity.getDataStructure() != null) {
            dataSetVO.setDatasetStructure(DatasetStructure.valueOf(entity.getDataStructure().toString()));
            switch (entity.getDataStructure()) {
                case SINGLE:
                    dataSetVO.setCollectionDataset(false);
                    break;
                case COLLECTION:
                    dataSetVO.setCollectionDataset(true);
                    break;
                default:
                    dataSetVO.setCollectionDataset(false);
                    break;
            }
        }

        if (entity.getDataSource() != null && entity.getDataStructure().equals(AddDataSetInputBean.DataStructure.COLLECTION)) {
            dataSetVO.setDatasetStructure(DatasetStructure.valueOf(entity.getDataSource().toString()));
            switch (entity.getDataSource()) {
                case LINK:
                    dataSetVO.setLinkDataset(true);
                    break;
                case EXTERNAL:
                    dataSetVO.setExternalDataset(true);
                    break;
                case REFERENCE:
                    dataSetVO.setReferenceDataset(true);
                    break;
                default:
                    break;
            }
        }

        if (isIncludedProperty && entity.getLinkedProperties() != null) {
            List<PropertyTypeVO> propertyTypeVOS = new ArrayList<>();
            for (AddPropertyInputBean propertyEntity : entity.getLinkedProperties()) {
                propertyTypeVOS.add(PropertyTsr.addInputToVo(propertyEntity, true));
            }
            dataSetVO.setLinkedPropertyTypes(propertyTypeVOS);
        }

        return dataSetVO;
    }

    public static DatasetVO addInputToVoV2(AddDataSetInputBean entity, boolean isIncludedProperty) {
        if (entity == null) {
            return null;
        }
        DatasetVO dataSetVO = new DatasetVO();
        dataSetVO.setDatasetName(entity.getName());
        dataSetVO.setDatasetDesc(entity.getDesc());

        dataSetVO.setDatasetClassify(entity.getDataSetClassify().toString());
        dataSetVO.setDataSetType(DatasetType.valueOf(entity.getDataSetType().toString()));
        dataSetVO.setDatasetStructure(DatasetStructure.valueOf(entity.getDataStructure().toString()));

        if (isIncludedProperty && entity.getLinkedProperties() != null) {
            List<PropertyTypeVO> propertyTypeVOS = new ArrayList<>();
            for (AddPropertyInputBean propertyEntity : entity.getLinkedProperties()) {
                propertyTypeVOS.add(PropertyTsr.addInputToVo(propertyEntity, true));
            }
            dataSetVO.setLinkedPropertyTypes(propertyTypeVOS);
        }

        return dataSetVO;
    }

    @Deprecated
    public static DatasetVO entityToVo(DataSetEntity entity, boolean isIncludedProperty) {
        if (entity == null) {
            return null;
        }
        DatasetVO dataSetVO = new DatasetVO();
        dataSetVO.setDatasetId(entity.getId());
        dataSetVO.setDatasetName(entity.getName());
        dataSetVO.setDatasetDesc(entity.getDesc());
        dataSetVO.setInheritDataset(entity.isInherited());
        dataSetVO.setHasDescendant(entity.isHasDescendant());

        dataSetVO.setDatasetClassify(entity.getDataSetClassify());
        dataSetVO.setDataSetType(DatasetType.valueOf(entity.getDataSetType().toString()));
        if (entity.getDataStructure() != null) {
            dataSetVO.setDatasetStructure(DatasetStructure.valueOf(entity.getDataStructure().toString()));
            switch (entity.getDataStructure()) {
                case SINGLE:
                    dataSetVO.setCollectionDataset(false);
                    break;
                case COLLECTION:
                    dataSetVO.setCollectionDataset(true);
                    break;
                default:
                    dataSetVO.setCollectionDataset(false);
                    break;
            }
        }

        if (entity.getDataSource() != null) {
            dataSetVO.setDatasetStructure(DatasetStructure.valueOf(entity.getDataSource().toString()));
            switch (entity.getDataSource()) {
                case LINK:
                    dataSetVO.setLinkDataset(true);
                    break;
                case EXTERNAL:
                    dataSetVO.setExternalDataset(true);
                    break;
                case REFERENCE:
                    dataSetVO.setReferenceDataset(true);
                    break;
                default:
                    break;
            }
        }

        if (isIncludedProperty && entity.getLinkedProperties() != null) {
            List<PropertyTypeVO> propertyTypeVOS = new ArrayList<>();
            for (PropertyEntity propertyEntity : entity.getLinkedProperties()) {
                propertyTypeVOS.add(PropertyTsr.entityToVo(propertyEntity, true));
            }
            dataSetVO.setLinkedPropertyTypes(propertyTypeVOS);
        }

        dataSetVO.setCreateDateTime(entity.getCreateTime());
        dataSetVO.setUpdateDateTime(entity.getUpdateTime());

        return dataSetVO;
    }

    public static DatasetVO entityToVoV2(DataSetEntity entity, boolean isIncludedProperty) {
        if (entity == null) {
            return null;
        }
        DatasetVO dataSetVO = new DatasetVO();
        dataSetVO.setDatasetId(entity.getId());
        dataSetVO.setDatasetName(entity.getName());
        dataSetVO.setDatasetDesc(entity.getDesc());
        dataSetVO.setInheritDataset(entity.isInherited());
        dataSetVO.setHasDescendant(entity.isHasDescendant());

        dataSetVO.setDatasetClassify(entity.getDataSetClassify());
        dataSetVO.setDataSetType(DatasetType.valueOf(entity.getDataSetType().toString()));
        dataSetVO.setDatasetStructure(DatasetStructure.valueOf(entity.getDataStructure().toString()));

        if (isIncludedProperty && entity.getLinkedProperties() != null) {
            List<PropertyTypeVO> propertyTypeVOS = new ArrayList<>();
            for (PropertyEntity propertyEntity : entity.getLinkedProperties()) {
                propertyTypeVOS.add(PropertyTsr.entityToVo(propertyEntity, true));
            }
            dataSetVO.setLinkedPropertyTypes(propertyTypeVOS);
        }

        dataSetVO.setCreateDateTime(entity.getCreateTime());
        dataSetVO.setUpdateDateTime(entity.getUpdateTime());

        return dataSetVO;
    }

    @Deprecated
    public static DataSetEntity voToEntity(DatasetVO dataSetVO, boolean isIncludedProperty) {
        if (dataSetVO == null) {
            return null;
        }
        DataSetEntity entity = new DataSetEntity();

        entity.setId(dataSetVO.getDatasetId());
        entity.setName(dataSetVO.getDatasetName());
        entity.setDesc(dataSetVO.getDatasetDesc());
        entity.setInherited(dataSetVO.isInheritDataset());
        entity.setHasDescendant(dataSetVO.isHasDescendant());

        entity.setDataSetClassify(dataSetVO.getDatasetClassify());
        entity.setDataSetType(DataSetType.valueOf(dataSetVO.getDataSetType().toString()));

        switch (dataSetVO.getDatasetStructure()) {
            case REFERENCE:
                entity.setDataStructure(DataStructure.COLLECTION);
                entity.setDataSource(DataSource.REFERENCE);
                break;
            case EXTERNAL:
                entity.setDataStructure(DataStructure.COLLECTION);
                entity.setDataSource(DataSource.EXTERNAL);
                break;
            case LINK:
                entity.setDataStructure(DataStructure.COLLECTION);
                entity.setDataSource(DataSource.LINK);
                break;
            case COLLECTION:
                entity.setDataStructure(DataStructure.COLLECTION);
                break;
            case SINGLE:
                entity.setDataStructure(DataStructure.SINGLE);
                break;
            default:
                break;
        }

        if (isIncludedProperty && dataSetVO.getLinkedPropertyTypes() != null) {
            List<PropertyEntity> propertyEntities = new ArrayList<>();
            for (PropertyTypeVO propertyTypeVO : dataSetVO.getLinkedPropertyTypes()) {
                propertyEntities.add(PropertyTsr.voToEntity(propertyTypeVO, true));
            }
            entity.setLinkedProperties(propertyEntities);
        }

        entity.setCreateTime(dataSetVO.getCreateDateTime());
        entity.setUpdateTime(dataSetVO.getUpdateDateTime());

        return entity;
    }

    public static DataSetEntity voToEntityV2(DatasetVO dataSetVO, boolean isIncludedProperty) {
        if (dataSetVO == null) {
            return null;
        }
        DataSetEntity entity = new DataSetEntity();

        entity.setId(dataSetVO.getDatasetId());
        entity.setName(dataSetVO.getDatasetName());
        entity.setDesc(dataSetVO.getDatasetDesc());
        entity.setInherited(dataSetVO.isInheritDataset());
        entity.setHasDescendant(dataSetVO.isHasDescendant());

        entity.setDataSetClassify(dataSetVO.getDatasetClassify());
        entity.setDataSetType(DataSetType.valueOf(dataSetVO.getDataSetType().toString()));
        // entity.setDataStructure(DataStructure.COLLECTION);
        entity.setDataStructure(DataStructure.valueOf(dataSetVO.getDatasetStructure().toString()));

        if (isIncludedProperty && dataSetVO.getLinkedPropertyTypes() != null) {
            List<PropertyEntity> propertyEntities = new ArrayList<>();
            for (PropertyTypeVO propertyTypeVO : dataSetVO.getLinkedPropertyTypes()) {
                propertyEntities.add(PropertyTsr.voToEntity(propertyTypeVO, true));
            }
            entity.setLinkedProperties(propertyEntities);
        }

        entity.setCreateTime(dataSetVO.getCreateDateTime());
        entity.setUpdateTime(dataSetVO.getUpdateDateTime());

        return entity;
    }

}
