package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryType;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryTypes;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndustryTypeService {
    private static Logger log = LoggerFactory.getLogger(IndustryTypeService.class);

    @Autowired
    public InfoObjectTypeDefsService objectTypeDefService;

    private static final String CHILDREN_INDUSTRY_TYPES = "children_industry_types";
    private static final String LINKED_INFO_OBJECT_TYPES = "linked_info_object_types";

    /**
     * 新增行业分类定义
     *
     * @param tenantId
     * @param industryTypeVO
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public IndustryTypeVO addIndustryType(String tenantId, IndustryTypeVO industryTypeVO) throws DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        IndustryTypes industryTypes = cimModelCore.getIndustryTypes();
        IndustryType industryType;
        String parentIndustryTypeId = industryTypeVO.getParentIndustryTypeId();
        if (StringUtils.isBlank(parentIndustryTypeId)) {
            industryType = industryTypes.addRootIndustryType(industryTypeVO);
        } else {
            industryType = industryTypes.addChildIndustryType(industryTypeVO, parentIndustryTypeId);
        }
        // return industryType.getIndustryTypeVO();
        return IndustryTypeFeatures.getIndustryTypeVOById(CimConstants.defauleSpaceName, industryType.getIndustryTypeRID());
    }

    /**
     * 删除行业分类定义及其子类
     *
     * @param tenantId
     * @param industryTypeRid
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public boolean removeIndustryType(String tenantId, String industryTypeRid) throws DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        IndustryTypes industryTypes = cimModelCore.getIndustryTypes();

        return industryTypes.removeIndustryType(industryTypeRid);
    }

    /**
     * 更新行业分类名称
     *
     * @param tenantId
     * @param industryTypeRid
     * @param industryTypeVO
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public IndustryTypeVO updateIndustryType(String tenantId, String industryTypeRid, IndustryTypeVO industryTypeVO) throws DataServiceModelRuntimeException, EntityNotFoundException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        IndustryTypes industryTypes = cimModelCore.getIndustryTypes();
        IndustryType industryType = industryTypes.updateIndustryType(industryTypeRid, industryTypeVO);
        if (industryType == null) {
            String msg = String.format("industry type of %s not found", industryTypeRid);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }

        // return industryType.getIndustryTypeVO();
        return IndustryTypeFeatures.getIndustryTypeVOById(CimConstants.defauleSpaceName, industryType.getIndustryTypeRID());
    }

    /**
     * 根据rid查询行业分类定义详情
     *
     * @param tenantId
     * @param industryTypeRid
     * @return
     * @throws EntityNotFoundException
     */
    public IndustryTypeVO getIndustryType(String tenantId, String industryTypeRid) throws EntityNotFoundException, DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        IndustryTypes industryTypes = cimModelCore.getIndustryTypes();
        IndustryType industryType = industryTypes.getIndustryType(industryTypeRid);
        if (industryType == null) {
            String msg = String.format("industry type of %s not found", industryTypeRid);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }

        // return industryType.getIndustryTypeVO();
        return IndustryTypeFeatures.getIndustryTypeVOById(CimConstants.defauleSpaceName, industryType.getIndustryTypeRID());
    }

    public Map<String, Object> getAllChildIndustryTypesAndLinkedObjectTypes(String tenantId, String industryTypeRid) throws DataServiceModelRuntimeException, EntityNotFoundException {
        Map<String, Object> industryTypeChildren = new HashMap<>();
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        IndustryTypes industryTypes = cimModelCore.getIndustryTypes();

        if (StringUtils.isBlank(industryTypeRid)) {
            // List<IndustryTypeVO> childrenIndustryTypes = industryTypes.getAllRootIndustryTypes();
            List<IndustryTypeVO> childrenIndustryTypes = IndustryTypeFeatures.getAllRootIndustryTypesByTenant(CimConstants.defauleSpaceName, tenantId);
            industryTypeChildren.put(CHILDREN_INDUSTRY_TYPES, childrenIndustryTypes);
            return industryTypeChildren;
        }

        IndustryType industryType = industryTypes.getIndustryType(industryTypeRid);
        if (industryType == null) {
            String msg = String.format("indudtry type of %s not found", industryTypeRid);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }

        List<IndustryType> industryTypeList = industryType.getChildrenIndustryTypes();
        List<IndustryTypeVO> industryTypeVOList = new ArrayList<>();
        if (industryTypeList != null) {
            for (IndustryType type : industryTypeList) {
                // industryTypeVOList.add(type.getIndustryTypeVO());
                industryTypeVOList.add(IndustryTypeFeatures.getIndustryTypeVOById(CimConstants.defauleSpaceName, type.getIndustryTypeRID()));
            }
        }
        industryTypeChildren.put(CHILDREN_INDUSTRY_TYPES, industryTypeVOList);

        List<InfoObjectDef> infoObjectList = industryType.getLinkedInfoObjectDefs();
        List<InfoObjectTypeVO> objectTypeVOList = new ArrayList<>();
        if (industryTypeList != null) {
            for (InfoObjectDef infoObjectDef : infoObjectList) {
                InfoObjectTypeVO objectTypeVO = objectTypeDefService.getObjectTypeDef(tenantId, infoObjectDef.getObjectTypeName(), true, false);
                if (objectTypeVO != null) {
                    objectTypeVOList.add(objectTypeVO);
                }
            }
        }
        industryTypeChildren.put(LINKED_INFO_OBJECT_TYPES, objectTypeVOList);

        return industryTypeChildren;
    }

}
