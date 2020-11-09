package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.ConfigurationFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.PropertyTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.PropertyTypeRestrictFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeRestrictVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.PropertyBean;
import com.glodon.pcop.cimsvc.model.PropertySetBean;
import com.glodon.pcop.cimsvc.model.PropertySetTypeBean;
import com.glodon.pcop.cimsvc.model.adapter.PropertyAdapter;
import com.glodon.pcop.cimsvc.model.adapter.PropertySetAdapter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuanjk(yuanjk @ glodon.com), 2018-07-17 16:43:12
 */
@Service
public class PropertySetService {
    static Logger log = LoggerFactory.getLogger(PropertySetService.class);

    /**
     * 若指定对象则返回该对象的所有属性集，若未指定对象则返回默认base属性集
     *
     * @param objectTypeId
     * @return
     */
    public List<PropertySetBean> getAllPropertySets(String objectTypeId) {
        List<PropertySetBean> propertySets = new ArrayList<>();
        // 添加基本属性集
        DatasetVO dso = DatasetFeatures.getDatasetVOByName(CimConstants.defauleSpaceName, BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, true);
        PropertySetBean pst = PropertySetAdapter.propertySetAdapter(dso);
        pst.setIsBase(1);
        propertySets.add(pst);
        // 添加父类属性集
        if (StringUtils.isNotBlank(objectTypeId)) {
            List<DatasetVO> list = InfoObjectFeatures.getLinkedDatasets(CimConstants.defauleSpaceName, objectTypeId, true);
            if (list != null) {
                log.debug("data set size: {}", list.size());
                for (DatasetVO entity : list) {
                    try {
                        DatasetVO datasetVO = DatasetFeatures.setInherientInfo(CimConstants.defauleSpaceName, entity, objectTypeId);
                        propertySets.add(PropertySetAdapter.propertySetAdapter(datasetVO));
                    } catch (Exception e) {
                        log.error("set data set inherit info failed", e);
                    }
                }
            } else {
                log.debug("no data set is linked to {}", objectTypeId);
            }
        }

        return propertySets;
    }

    /**
     * 若指定对象则返回该对象的所有属性集，若未指定对象则返回默认base属性集
     *
     * @param objectTypeId
     * @return
     */
    @Deprecated
    public List<PropertySetBean> getPropertySets(String objectTypeId) {
        List<PropertySetBean> propertySets = new ArrayList<>();
        // 添加基本属性集
        DatasetVO dso = DatasetFeatures.getDatasetVOByName(CimConstants.defauleSpaceName, BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, true);
        PropertySetBean pst = PropertySetAdapter.propertySetAdapter(dso);
        pst.setIsBase(1);
        propertySets.add(pst);
        // 添加父类属性集
        if (StringUtils.isNotBlank(objectTypeId)) {
            List<DatasetVO> list = InfoObjectFeatures.getLinkedDatasets(CimConstants.defauleSpaceName, objectTypeId, true);
            log.info("data set size: {}", list.size());
            if (list != null) {
                for (DatasetVO entity : list) {
                    // 目前不返回继承的属性集
                    if (!entity.isInheritDataset()) {
                        propertySets.add(PropertySetAdapter.propertySetAdapter(entity));
                    }
                }
            }
        }

        return propertySets;
    }

    /**
     * 返回基本属性集
     *
     * @return
     */
    public PropertySetBean getDefaultBasePropertySet() {
        // 添加基本属性集
        DatasetVO dso = DatasetFeatures.getDatasetVOByName(CimConstants.defauleSpaceName, BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, true);
        log.info("基本属性集：id={}, name={}", dso.getDatasetId(), dso.getDatasetName());
        PropertySetBean pst = PropertySetAdapter.propertySetAdapter(dso);
        pst.setIsBase(1);

        return pst;
    }

    /**
     * 获取所有的属性集类型
     *
     * @return
     */
    public List<PropertySetTypeBean> getAllPropertySetTypes(String tenantId) {
        List<PropertySetTypeBean> propertySetTypeBeanList;
        propertySetTypeBeanList = PropertySetAdapter.propertyTypeAdapter(ConfigurationFeatures.getDatasetTypeConfigurationItems(CimConstants.defauleSpaceName, tenantId));
        log.info("property set type size: {}", propertySetTypeBeanList.size());
        return propertySetTypeBeanList;
    }

    public DatasetVO addPropertySet(PropertySetBean psb) {
        // 创建属性集
        DatasetVO ds = PropertySetAdapter.propertySetAdapter(psb);
        ds = DatasetFeatures.addDataset(CimConstants.defauleSpaceName, ds);
        // 创建与该属性集关联的属性，并与属性集关联起来
        for (PropertyBean pb : psb.getProperties()) {
            PropertyTypeVO pt = PropertyAdapter.propertyAdapter(pb);
            pt = PropertyTypeFeatures.addPropertyType(CimConstants.defauleSpaceName, pt);
            boolean flag = DatasetFeatures.addPropertyTypeLink(CimConstants.defauleSpaceName, ds.getDatasetId(),
                    pt.getPropertyTypeId());
            if (flag) {
                PropertyTypeRestrictVO restrictVO = new PropertyTypeRestrictVO();
                restrictVO.setPropertyTypeId(pt.getPropertyTypeId());
                restrictVO.setNull(pb.getIsNull());
                restrictVO = PropertyTypeRestrictFeatures.addPropertyTypeRestrict(CimConstants.defauleSpaceName, restrictVO);
                flag = PropertyTypeRestrictFeatures.linkToDataset(CimConstants.defauleSpaceName, ds.getDatasetId(), restrictVO.getPropertyTypeRestrictId());
                if (!flag) {
                    log.error("link property restrict with dataset failed, datasetId={}, propertyId={}", ds.getDatasetId(), pt.getPropertyTypeId());
                }
            } else {
                log.error("add new property type failed, datasetId={}, propertyName={}", ds.getDatasetId(), pb.getAlias());
            }
        }
        return ds;
    }

    /**
     * @param propertySetId
     * @return
     */
    public boolean deletePropertySet(String propertySetId) {
        return DatasetFeatures.removeDataset(CimConstants.defauleSpaceName, propertySetId);
    }

    /**
     * 根据属性集ID返回该属性集及其属性
     *
     * @param dataSetId 属性集ID
     * @return
     */
    public DatasetVO getDataSetAndProperty(String dataSetId) throws EntityNotFoundException {
        DatasetVO datasetVO = DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName, dataSetId, true);
        if (datasetVO != null) {
            return datasetVO;
        } else {
            throw new EntityNotFoundException(EnumWrapper.CodeAndMsg.E05040003, "Dataset not found, datasetId=" + dataSetId);
        }
    }

}
