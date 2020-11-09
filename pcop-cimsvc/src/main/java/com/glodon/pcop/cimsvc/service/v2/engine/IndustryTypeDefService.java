package com.glodon.pcop.cimsvc.service.v2.engine;

import com.glodon.pcop.cim.common.model.entity.IndustryTypeEntity;
import com.glodon.pcop.cim.common.model.entity.ObjectTypeEntity;
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
import com.glodon.pcop.cimsvc.model.transverter.IndustryTypeTsr;
import com.glodon.pcop.cimsvc.model.transverter.ObjectTypeTsr;
import com.glodon.pcop.cimsvc.model.v2.IndustryTypeTreeQueryOutput;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndustryTypeDefService {
    private static Logger log = LoggerFactory.getLogger(IndustryTypeDefService.class);

    @Autowired
    public InfoObjectTypeDefService objectTypeDefService;

    public static final String CHILDREN_INDUSTRY_TYPES = "children_industry_types";
    public static final String LINKED_OBJECT_TYPES = "linked_object_types";

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
        return IndustryTypeFeatures.getIndustryTypeVOById(CimConstants.defauleSpaceName,
                industryType.getIndustryTypeRID());
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
            String msg = String.format("industry type not found: [%s]", industryTypeRid);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }

        // return industryType.getIndustryTypeVO();
        return IndustryTypeFeatures.getIndustryTypeVOById(CimConstants.defauleSpaceName,
                industryType.getIndustryTypeRID());
    }

    /**
     * 根据rid查询行业分类定义详情
     *
     * @param tenantId
     * @param industryTypeRid
     * @return
     * @throws EntityNotFoundException
     */
    public IndustryTypeVO getIndustryType(String tenantId, String industryTypeRid) throws EntityNotFoundException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        IndustryTypes industryTypes = cimModelCore.getIndustryTypes();
        IndustryType industryType = industryTypes.getIndustryType(industryTypeRid);
        if (industryType == null) {
            String msg = String.format("industry type not found: [%s]", industryTypeRid);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }

        // return industryType.getIndustryTypeVO();
        return IndustryTypeFeatures.getIndustryTypeVOById(CimConstants.defauleSpaceName,
                industryType.getIndustryTypeRID());
    }

    public IndustryTypeTreeQueryOutput getAllChildIndustryTypesAndLinkedObjectTypes(String tenantId,
                                                                                    String industryTypeRid) throws DataServiceModelRuntimeException, EntityNotFoundException {//NOSONAR
        // Map<String, Object> industryTypeChildren = new HashMap<>();
        IndustryTypeTreeQueryOutput industryTypeChildren = new IndustryTypeTreeQueryOutput();
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        IndustryTypes industryTypes = cimModelCore.getIndustryTypes();

        if (StringUtils.isBlank(industryTypeRid)) {
            // List<IndustryTypeVO> childrenIndustryTypes = industryTypes.getAllRootIndustryTypes();
            List<IndustryTypeVO> childrenIndustryTypes =
                    IndustryTypeFeatures.getAllRootIndustryTypesByTenant(CimConstants.defauleSpaceName, tenantId);
            if (childrenIndustryTypes != null) {
                List<IndustryTypeEntity> industryTypeEntities = new ArrayList<>();
                for (IndustryTypeVO typeVO : childrenIndustryTypes) {
                    industryTypeEntities.add(IndustryTypeTsr.voToEntityWithChildren(typeVO, true, true));
                }
                industryTypeChildren.setIndustryTypes(industryTypeEntities);
            }
            // industryTypeChildren.put(ChildrenIndustryTypes, childrenIndustryTypes);
        } else {
            IndustryType industryType = industryTypes.getIndustryType(industryTypeRid);
            if (industryType != null) {
                List<IndustryType> industryTypeList = industryType.getChildrenIndustryTypes();
                List<IndustryTypeVO> industryTypeVOList = new ArrayList<>();
                if (industryTypeList != null) {
                    for (IndustryType type : industryTypeList) {
                        // industryTypeVOList.add(type.getIndustryTypeVO());
                        industryTypeVOList.add(IndustryTypeFeatures.getIndustryTypeVOById(CimConstants.defauleSpaceName, type.getIndustryTypeRID()));
                    }
                }
                // industryTypeChildren.put(ChildrenIndustryTypes, industryTypeVOList);

                if (industryTypeVOList != null) {
                    List<IndustryTypeEntity> industryTypeEntities = new ArrayList<>();
                    for (IndustryTypeVO typeVO : industryTypeVOList) {
                        industryTypeEntities.add(IndustryTypeTsr.voToEntityWithChildren(typeVO, true, true));
                    }
                    industryTypeChildren.setIndustryTypes(industryTypeEntities);
                }

                List<InfoObjectDef> infoObjectList = industryType.getLinkedInfoObjectDefs();
                List<InfoObjectTypeVO> objectTypeVOList = new ArrayList<>();
                if (industryTypeList != null) {
                    for (InfoObjectDef infoObjectDef : infoObjectList) {
                        InfoObjectTypeVO objectTypeVO = objectTypeDefService.getObjectTypeDef(tenantId,
                                infoObjectDef.getObjectTypeName(), true, false);
                        if (objectTypeVO != null) {
                            objectTypeVOList.add(objectTypeVO);
                        }
                    }
                }
                // industryTypeChildren.put(LinkedInfoObjectTypes, objectTypeVOList);

                if (objectTypeVOList != null) {
                    List<ObjectTypeEntity> objectTypeEntities = new ArrayList<>();
                    for (InfoObjectTypeVO objectTypeVO : objectTypeVOList) {
                        objectTypeEntities.add(ObjectTypeTsr.voToEntity(objectTypeVO, true, true));
                    }
                    industryTypeChildren.setObjectTypes(objectTypeEntities);
                }

            } else {
                String msg = String.format("indudtry type of %s not found", industryTypeRid);
                log.error(msg);
                throw new EntityNotFoundException(msg);
            }
        }

        return industryTypeChildren;
    }

}
