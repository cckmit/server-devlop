package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.InheritFactType;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryType;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimapi.exception.ApiRunTimeException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InfoObjectTypeDefsService {
    private static final Logger log = LoggerFactory.getLogger(InfoObjectTypeDefsService.class);

    private static final String DEFAULT_INDUSTRY_NAME = "未定义分类";

    /**
     * 新增行业分类模型定义，默认关联"未定义分类"
     *
     * @param tenantId
     * @param objectTypeVO
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public String addObjectTypeDef(String tenantId, InfoObjectTypeVO objectTypeVO) throws DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);

        InfoObjectDefs objectDefs = cimModelCore.getInfoObjectDefs();
        InfoObjectDef objectDef;
        if (StringUtils.isBlank(objectTypeVO.getParentObjectTypeName())) {
            objectDef = objectDefs.addRootInfoObjectDef(objectTypeVO);
        } else {
            objectDef = objectDefs.addChildInfoObjectDef(objectTypeVO, objectTypeVO.getParentObjectTypeName());
        }
        if (StringUtils.isNotBlank(objectTypeVO.getIndustryTypeId())) {
            objectDef.linkIndustryType(objectTypeVO.getIndustryTypeId());
        } else {
            IndustryTypeVO defaultIndustry = IndustryTypeFeatures.getIndustryTypeByName(CimConstants.defauleSpaceName, DEFAULT_INDUSTRY_NAME);
            if (defaultIndustry != null) {
                objectDef.linkIndustryType(defaultIndustry.getIndustryTypeId());
            } else {
                log.error("默认行业分类：{}，不存在", DEFAULT_INDUSTRY_NAME);
            }
        }
        return objectDef.getObjectTypeName();
    }

    /**
     * 获取指定的对象类型信息
     *
     * @param tenantId
     * @param objectTypeId
     * @return
     */
    public InfoObjectTypeVO getObjectTypeDef(String tenantId, String objectTypeId, boolean isIncludedDataSet, boolean isIncludedProperty) throws DataServiceModelRuntimeException {//NOSONAR
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        InfoObjectDef objectDef = cimModelCore.getInfoObjectDef(objectTypeId);

        InfoObjectTypeVO objectTypeVO = InfoObjectFeatures.getInfoObjectTypeVOByType(CimConstants.defauleSpaceName, objectTypeId, false, false);
        if (objectTypeVO == null) {
            log.error("Object type definition of objectTypeId={} not found", objectTypeId);
            return null;
        }

        List<IndustryType> industryTypeList = objectDef.getLinkedIndustryTypes();
        if (industryTypeList != null && industryTypeList.size() > 0) {
            objectTypeVO.setIndustryTypeId(industryTypeList.get(0).getIndustryTypeRID());
        }

        InheritFactType inheritFactType = InfoObjectFeatures.getAncestorObjectTypes(CimConstants.defauleSpaceName, objectTypeId);
        if (inheritFactType != null) {
            objectTypeVO.setParentObjectTypeName(inheritFactType.getTypeName());
        }

        List<DatasetVO> datasetVOList = new ArrayList<>();
        if (isIncludedDataSet) {
            try {
                List<DatasetDef> datasetDefList = objectDef.getDatasetDefs();
                if (datasetDefList != null && datasetDefList.size() > 0) {
                    for (DatasetDef datasetDef : datasetDefList) {
                        // DatasetVO datasetVO = datasetDef.getDataSetVO(isIncludedProperty);
                        DatasetVO datasetVO = DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName, datasetDef.getDatasetRID(), isIncludedProperty);
                        if (datasetVO != null) {
                            datasetVOList.add(DatasetFeatures.setInherientInfo(CimConstants.defauleSpaceName, datasetVO, objectTypeId));
                        }
                    }
                } else {
                    log.info("No data set is link with object type of objectTypeId={}", objectTypeId);
                }
                // datasetVOList.add(objectDef.getBaseDatasetDef().getDataSetVO(isIncludedProperty));
                datasetVOList.add(DatasetFeatures.getBaseDatasetVO(CimConstants.defauleSpaceName, isIncludedProperty));
                objectTypeVO.setLinkedDatasets(datasetVOList);
            } catch (CimDataEngineRuntimeException e) {
                throw new ApiRunTimeException(CodeAndMsg.E05010501, e);
            } catch (CimDataEngineInfoExploreException e) {
                throw new ApiRunTimeException(CodeAndMsg.E05010502, e);
            }
        }

        return objectTypeVO;
    }


    /**
     * 更新对象模型名称及其关联的行业分类
     *
     * @param tenantId
     * @param objectTypeId
     * @param objectTypeVO
     * @return
     */
    public InfoObjectTypeVO updateObjectTypeDef(String tenantId, String objectTypeId, InfoObjectTypeVO objectTypeVO) throws DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        InfoObjectDefs objectDefs = cimModelCore.getInfoObjectDefs();
        if (objectDefs.updateInfoObjectDef(objectTypeId, objectTypeVO)) {
            return getObjectTypeDef(tenantId, objectTypeId, false, false);
        } else {
            log.error("Object type update failed, objectTypeId={}", objectTypeId);
            return null;
        }
    }


    /**
     * 禁用对象模型
     *
     * @param tenantId
     * @param objectTypeId
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public boolean deleteObjectTypeDef(String tenantId, String objectTypeId) throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        InfoObjectDefs objectDefs = cimModelCore.getInfoObjectDefs();
        return objectDefs.disableInfoObjectDef(objectTypeId);
    }


}
