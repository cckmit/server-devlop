package com.glodon.pcop.cimsvc.service.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.BaseFileInfoKeys;
import com.glodon.pcop.cim.common.util.CimConstants.IndustryTypeNodeKeys;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Dimension;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineDataMartException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryType;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryTypes;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.DouplicateNameException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.exception.InputNotEnoughException;
import com.glodon.pcop.cimsvc.model.input.DeleteTreeNodeInputBean;
import com.glodon.pcop.cimsvc.model.input.MoveNodesInputBean;
import com.glodon.pcop.cimsvc.model.input.TreeNodeInputBean;
import com.glodon.pcop.cimsvc.model.input.TreeNodeWithNameInputBean;
import com.glodon.pcop.cimsvc.model.input.UpdateTreeNodeInputBean;
import com.glodon.pcop.cimsvc.model.output.DeleteTreeNodeOutputBean;
import com.glodon.pcop.cimsvc.model.output.MoveNodeOutputBean;
import com.glodon.pcop.cimsvc.model.output.TreeNodeMetadataOutputBean;
import com.glodon.pcop.cimsvc.model.output.TreeNodeOutputBean;
import com.glodon.pcop.cimsvc.service.OrientDBCommonUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;//NOSONAR


@Service
public class ContentService {
    private static Logger log = LoggerFactory.getLogger(ContentService.class);
    private static final String INDUSTRY_NOT_FOUND = "industry of {} not found";
    // @Autowired
    // private ObjectMapper objectMapper;

    public List<TreeNodeOutputBean> listChildNode(String tenantId, TreeNodeInputBean parentNode) throws EntityNotFoundException {//NOSONAR
        List<TreeNodeOutputBean> childNodes = new ArrayList<>();
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            modelCore.setCimDataSpace(cds);
            if (parentNode == null || StringUtils.isBlank(parentNode.getInstanceRid())) {
                try {
                    Dimension logicRootDimension = IndustryTypeFeatures.getIndustryTypeLogicRootNode(cds);
                    List<Relation> rootTypeRelationsList = logicRootDimension.getAllSpecifiedRelations(BusinessLogicConstant.IS_ROOT_INDUSTRY_TYPE_RELATION_TYPE_NAME, RelationDirection.FROM);
                    if (rootTypeRelationsList != null && rootTypeRelationsList.size() > 0) {
                        for (Relation currentRelation : rootTypeRelationsList) {
                            Relationable currentTypeDimension = currentRelation.getToRelationable();
                            boolean isInValidTenant = CommonOperationUtil.isTenantContainsData(tenantId, currentTypeDimension);
                            if (isInValidTenant) {
                                TreeNodeOutputBean nodeOutputBean = relationableToOutBean(parentNode.getInstanceRid(), currentTypeDimension);
                                childNodes.add(nodeOutputBean);
                            }
                        }
                    }
                } catch (CimDataEngineInfoExploreException e) {
                    e.printStackTrace();
                } catch (CimDataEngineDataMartException e) {
                    e.printStackTrace();
                } catch (CimDataEngineRuntimeException e) {
                    e.printStackTrace();
                }
            } else {
                switch (parentNode.getNodeType()) {
                    case INDUSTRY:
                        childNodes = getIndustryChildNodes(modelCore, parentNode, cds);
                        break;
                    case FILE:
                        log.info("get child nodes of file is not implemnet");
                        break;
                    case OBJECT:
                        childNodes = getObjectChildNodes(modelCore, parentNode, cds);
                        break;
                    case INSTANCE:
                        childNodes = getInstanceChildNodes(parentNode, cds);
                        break;
                }
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return childNodes;
    }

    public static TreeNodeOutputBean relationableToOutBean(String parentIndustryRid, Relationable industryTypeDimension) {//NOSONAR
        TreeNodeOutputBean nodeOutputBean = new TreeNodeOutputBean();
        nodeOutputBean.setNodeType(TreeNodeOutputBean.TreeNodeTypeEnum.INDUSTRY);
        // nodeOutputBean.setParentIndustryRid(parentNode.getInstanceRid());
        nodeOutputBean.setParentIndustryRid(parentIndustryRid);
        if (industryTypeDimension != null) {
            nodeOutputBean.setInstanceRid(industryTypeDimension.getId());
            if (industryTypeDimension.hasProperty(IndustryTypeNodeKeys.INDUSTRY_ID)) {
                nodeOutputBean.setId(industryTypeDimension.getProperty(IndustryTypeNodeKeys.INDUSTRY_ID).getPropertyValue().toString());
            } else {
                log.error("industry id is null");
            }
            if (industryTypeDimension.hasProperty(IndustryTypeNodeKeys.INDUSTRY_NAME)) {
                nodeOutputBean.setName(industryTypeDimension.getProperty(IndustryTypeNodeKeys.INDUSTRY_NAME).getPropertyValue().toString());
            } else {
                log.error("industry name is null");
            }
            if (industryTypeDimension.hasProperty(IndustryTypeNodeKeys.CREATOR_ID)) {
                nodeOutputBean.setCreator(industryTypeDimension.getProperty(IndustryTypeNodeKeys.CREATOR_ID).getPropertyValue().toString());
            } else {
                log.error("industry creator is null");
            }
            if (industryTypeDimension.hasProperty(IndustryTypeNodeKeys.UPDATOR_ID)) {
                nodeOutputBean.setUpdator(industryTypeDimension.getProperty(IndustryTypeNodeKeys.UPDATOR_ID).getPropertyValue().toString());
            } else {
                log.error("industry updator is null");
            }
            if (industryTypeDimension.hasProperty(IndustryTypeNodeKeys.CREATE_TIME)) {
                nodeOutputBean.setCreateTime((Date) industryTypeDimension.getProperty(IndustryTypeNodeKeys.CREATE_TIME).getPropertyValue());
            } else {
                log.error("industry create time is null");
            }
            if (industryTypeDimension.hasProperty(IndustryTypeNodeKeys.UPDATE_TIME)) {
                nodeOutputBean.setUpdateTime((Date) industryTypeDimension.getProperty(IndustryTypeNodeKeys.UPDATE_TIME).getPropertyValue());
            } else {
                log.error("industry update time is null");
            }
        } else {
            log.error("industry type dimension is null");
        }
        return nodeOutputBean;
    }

    private static TreeNodeOutputBean industryTypeToNode(CimDataSpace cds, IndustryType industryType, TreeNodeInputBean parentNode) {
        TreeNodeOutputBean nodeOutputBean = new TreeNodeOutputBean();
        if (industryType == null) {
            log.error("industryType is null");
        } else {
            try {
                Dimension industryDimension = cds.getDimensionById(industryType.getIndustryTypeRID());
                nodeOutputBean = relationableToOutBean(parentNode.getInstanceRid(), industryDimension);
            } catch (CimDataEngineRuntimeException e) {
                e.printStackTrace();
            }
        }
        return nodeOutputBean;
    }

    public static TreeNodeOutputBean infoObjectToFileNode(CimDataSpace cds, String fileInfoRid, String parentIndustryRid) {//NOSONAR
        TreeNodeOutputBean nodeOutputBean = new TreeNodeOutputBean();

        if (StringUtils.isBlank(fileInfoRid)) {
            log.error("base file rid is blank");
        } else {
            try {
                // Fact fact = cds.getFactById(infoObject.getObjectInstanceRID());
                Fact fact = cds.getFactById(fileInfoRid);
                nodeOutputBean.setNodeType(TreeNodeOutputBean.TreeNodeTypeEnum.FILE);
                // nodeOutputBean.setParentIndustryRid(parentNode.getInstanceRid());
                nodeOutputBean.setParentIndustryRid(parentIndustryRid);
                if (fact != null) {
                    nodeOutputBean.setInstanceRid(fact.getId());
                    if (fact.hasProperty(BaseFileInfoKeys.FILE_DATA_ID)) {
                        nodeOutputBean.setId(fact.getProperty(BaseFileInfoKeys.FILE_DATA_ID).getPropertyValue().toString());
                    } else {
                        log.error("file id of file type is null");
                    }
                    if (fact.hasProperty(BaseFileInfoKeys.FILE_DATA_NAME)) {
                        nodeOutputBean.setName(fact.getProperty(BaseFileInfoKeys.FILE_DATA_NAME).getPropertyValue().toString());
                    } else {
                        log.error("file name of file type is null");
                    }
                    if (fact.hasProperty(BaseFileInfoKeys.FILE_DATA_TYPE)) {
                        nodeOutputBean.setDataType(fact.getProperty(BaseFileInfoKeys.FILE_DATA_TYPE).getPropertyValue().toString());
                    } else {
                        log.error("file data type of file type is null");
                    }
                    if (fact.hasProperty(BaseFileInfoKeys.SRC_FILE_NAME)) {
                        nodeOutputBean.setSrcFileName(fact.getProperty(BaseFileInfoKeys.SRC_FILE_NAME).getPropertyValue().toString());
                    } else {
                        log.error("source file name of file type is null");
                    }
                    if (fact.hasProperty(BaseFileInfoKeys.CREATOR)) {
                        nodeOutputBean.setCreator(fact.getProperty(BaseFileInfoKeys.CREATOR).getPropertyValue().toString());
                    } else {
                        log.error("creator of file type is null");
                    }
                    if (fact.hasProperty(BaseFileInfoKeys.UPDATOR)) {
                        nodeOutputBean.setCreator(fact.getProperty(BaseFileInfoKeys.UPDATOR).getPropertyValue().toString());
                    } else {
                        log.error("updator of file type is null");
                    }
                    if (fact.hasProperty(BaseFileInfoKeys.CREATE_TIME)) {
                        nodeOutputBean.setCreateTime((Date) fact.getProperty(BaseFileInfoKeys.CREATE_TIME).getPropertyValue());
                    } else {
                        log.error("create time of file type is null");
                    }
                    if (fact.hasProperty(BaseFileInfoKeys.UPDATE_TIME)) {
                        nodeOutputBean.setCreateTime((Date) fact.getProperty(BaseFileInfoKeys.UPDATE_TIME).getPropertyValue());
                    } else {
                        log.error("update time of file type is null");
                    }
                }
            } catch (CimDataEngineRuntimeException e) {
                e.printStackTrace();
            }
        }

        return nodeOutputBean;
    }

    public static Boolean deleteContentNode(String tenantId, DeleteTreeNodeInputBean nodeInputBean) {
        Boolean flag = false;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            switch (nodeInputBean.getNodeType()) {
                case FILE:
                    flag = deleteFileNode(modelCore, nodeInputBean.getInstanceRid());
                    break;
                case INDUSTRY:
                    flag = deleteIndustryNode(modelCore, nodeInputBean.getInstanceRid());
                    break;
                case OBJECT:
                    flag = unlinkObjectNode(modelCore, cds, nodeInputBean);
                    break;
                case INSTANCE:
                    flag = unlinkInstanceNode(modelCore, nodeInputBean);
                    break;
            }
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (InputNotEnoughException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return flag;
    }

    private static Boolean deleteIndustryNode(CIMModelCore modelCore, String industryRid) {
        boolean flag = false;
        IndustryTypes industryTypes = modelCore.getIndustryTypes();
        if (industryTypes != null) {
            try {
                flag = industryTypes.removeIndustryType(industryRid);
            } catch (DataServiceModelRuntimeException e) {
                log.error("remove industry node failed", e);
            }
        }
        return flag;
    }

    /**
     * 删除目录树文件节点
     *
     * @param modelCore
     * @param instanceRid
     * @return
     */
    private static Boolean deleteFileNode(CIMModelCore modelCore, String instanceRid) {
        Boolean flag = false;
        InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(BaseFileInfoKeys.BaseFileObjectTypeName);
        if (infoObjectDef != null) {
            flag = infoObjectDef.deleteObject(instanceRid);
        } else {
            log.error("info object type of {} not found", BaseFileInfoKeys.BaseFileObjectTypeName);
        }
        return flag;
    }

    /**
     * 取消对象类型到行业分类的挂载
     *
     * @param cds
     * @param nodeInputBean
     * @return
     * @throws InputNotEnoughException
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     */
    private static Boolean unlinkObjectNode(CIMModelCore modelCore, CimDataSpace cds, DeleteTreeNodeInputBean nodeInputBean) throws InputNotEnoughException, CimDataEngineRuntimeException, CimDataEngineInfoExploreException, DataServiceModelRuntimeException {
        if (StringUtils.isBlank(nodeInputBean.getParentIndustryRid()) || StringUtils.isBlank(nodeInputBean.getId())) {
            String msg = "parent industry rid and object type id are mandatory";
            log.error(msg);
            throw new InputNotEnoughException(msg);
        }

        IndustryType industryType = modelCore.getIndustryType(nodeInputBean.getParentIndustryRid());
        if (industryType != null) {
            List<InfoObject> infoObjectList = industryType.getChildInfoObjects();
            if (infoObjectList != null && infoObjectList.size() > 0) {
                for (InfoObject infoObject : infoObjectList) {
                    if (infoObject.getObjectTypeName().equals(nodeInputBean.getId())) {
                        industryType.unlinkChildInfoObject(infoObject.getObjectInstanceRID());
                    }
                }
            }
        } else {
            log.error(INDUSTRY_NOT_FOUND, nodeInputBean.getParentIndustryRid());
        }
        return InfoObjectFeatures.unlinkInfoObjectTypeAttachedIndustryType(cds, nodeInputBean.getId(), nodeInputBean.getParentIndustryRid());
    }

    private static Boolean unlinkInstanceNode(CIMModelCore modelCore, DeleteTreeNodeInputBean nodeInputBean) throws InputNotEnoughException, DataServiceModelRuntimeException {
        Boolean flag = false;
        if (StringUtils.isBlank(nodeInputBean.getParentIndustryRid()) || StringUtils.isBlank(nodeInputBean.getInstanceRid())) {
            String msg = "parent industry rid and object type id are mandatory";
            log.error(msg);
            throw new InputNotEnoughException(msg);
        }
        IndustryType industryType = modelCore.getIndustryType(nodeInputBean.getParentIndustryRid());
        if (industryType != null) {
            flag = industryType.unlinkChildInfoObject(nodeInputBean.getInstanceRid());
        } else {
            log.error("industry type of {} is null", nodeInputBean.getParentIndustryRid());
        }
        return flag;
    }

    /**
     * 获取行业分类的子节点
     *
     * @param modelCore
     * @param parentNode
     * @param cds
     * @return
     * @throws EntityNotFoundException
     */
    private List<TreeNodeOutputBean> getIndustryChildNodes(CIMModelCore modelCore, TreeNodeInputBean parentNode, CimDataSpace cds) throws EntityNotFoundException {//NOSONAR
        List<TreeNodeOutputBean> childNodes = new ArrayList<>();
        IndustryTypes industryTypes = modelCore.getIndustryTypes();
        IndustryType industryType = industryTypes.getIndustryType(parentNode.getInstanceRid());
        if (industryType == null) {
            String msg = String.format("industry type of %s not found", parentNode.getInstanceRid());
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }

        //子类处理
        List<IndustryType> childIndustryTypes = industryType.getChildrenIndustryTypes();
        if (childIndustryTypes != null && childIndustryTypes.size() > 0) {
            for (IndustryType childIndustry : childIndustryTypes) {
                log.info("child industry rid  = {} of parent industry rid = {}", parentNode.getInstanceRid(), childIndustry.getIndustryTypeRID());
                childNodes.add(industryTypeToNode(cds, childIndustry, parentNode));
            }
        }
        //关联的对象类型定义处理
        List<InfoObjectDef> objectDefList = industryType.getLinkedInfoObjectDefs();
        Map<String, TreeNodeOutputBean> objectOutputBeanMap = new HashMap<>();
        if (objectDefList != null) {
            for (InfoObjectDef objectDef : objectDefList) {
                TreeNodeOutputBean nodeOutputBean = new TreeNodeOutputBean();
                nodeOutputBean.setNodeType(TreeNodeOutputBean.TreeNodeTypeEnum.OBJECT);
                nodeOutputBean.setId(objectDef.getObjectTypeName());
                nodeOutputBean.setName(objectDef.getObjectTypeDesc());
                try {
                    List<Fact> factList = getIndustryAndObjectMapping(cds, parentNode.getInstanceRid(), objectDef.getObjectTypeName());
                    log.info("object={} and industry={} mapping fact size: {}", objectDef.getObjectTypeName(), parentNode.getInstanceRid(), factList.size());
                    if (factList != null && factList.size() > 0) {
                        Fact fact = factList.get(0);
                        if (fact.hasProperty(CimConstants.ObjectTypeNodeKeys.DISPLAY_NAME)) {
                            nodeOutputBean.setName(fact.getProperty(CimConstants.ObjectTypeNodeKeys.DISPLAY_NAME).getPropertyValue().toString());
                        }
                    }
                } catch (CimDataEngineRuntimeException e) {
                    e.printStackTrace();
                } catch (CimDataEngineInfoExploreException e) {
                    e.printStackTrace();
                }
                nodeOutputBean.setParentIndustryRid(parentNode.getInstanceRid());
                childNodes.add(nodeOutputBean);

                objectOutputBeanMap.put(objectDef.getObjectTypeName(), nodeOutputBean);
            }
        }

        //关联的文件节点和实例节点处理
        List<InfoObject> infoObjectList = industryType.getChildInfoObjects();
        if (infoObjectList != null && infoObjectList.size() > 0) {
            Map<String, List<TreeNodeOutputBean>> instanceListMap = new HashMap<>();
            for (InfoObject infoObject : infoObjectList) {
                if (infoObject.getObjectTypeName().equals(BaseFileInfoKeys.BaseFileObjectTypeName)) {
                    childNodes.add(infoObjectToFileNode(cds, infoObject.getObjectInstanceRID(), parentNode.getInstanceRid()));
                } else {
                    List<TreeNodeOutputBean> instancesList = instanceListMap.get(infoObject.getObjectTypeName());
                    if (instancesList == null) {
                        instancesList = new ArrayList<>();
                        instanceListMap.put(infoObject.getObjectTypeName(), instancesList);
                    }
                    infoObjectToInstanceNode(instancesList, infoObject, cds, parentNode, parentNode.getInstanceRid());
                }
            }
            if (instanceListMap.size() > 0) {
                for (Map.Entry<String, List<TreeNodeOutputBean>> entry : instanceListMap.entrySet()) {
                    TreeNodeOutputBean bean = objectOutputBeanMap.get(entry.getKey());
                    if (bean != null) {
                        bean.setLinkedInstances(entry.getValue());
                    } else {
                        log.error("object of objectName={} is not linked with industry type of industryRid={}, but instances " +
                                "of instanceRids={} are linked with this industry", entry.getKey(), industryType.getIndustryTypeRID(), new Gson().toJson(entry.getValue()));
                    }
                }
            }

        }
        return childNodes;
    }

    /**
     * 行业分类关联的实例
     *
     * @param nodeOutputBeans
     * @param infoObject
     * @param cds
     */
    private void infoObjectToInstanceNode(List<TreeNodeOutputBean> nodeOutputBeans, InfoObject infoObject, CimDataSpace cds, TreeNodeInputBean parentNode, String industryRid) {
        if (infoObject == null) {
            log.error("industry linked infoObject is null");
        } else {
            try {
                TreeNodeOutputBean nodeOutputBean = new TreeNodeOutputBean();
                Fact fact = cds.getFactById(infoObject.getObjectInstanceRID());
                nodeOutputBean.setNodeType(TreeNodeOutputBean.TreeNodeTypeEnum.INSTANCE);
                nodeOutputBean.setParentIndustryRid(industryRid);
                if (fact != null) {
                    nodeOutputBean.setInstanceRid(fact.getId());
                    if (fact.hasProperty(CimConstants.BaseDataSetKeys.ID)) {
                        nodeOutputBean.setId(fact.getProperty(CimConstants.BaseDataSetKeys.ID).getPropertyValue().toString());
                    } else {
                        log.error("id property of instanceRid={} is null", infoObject.getObjectInstanceRID());
                    }
                    if (fact.hasProperty(CimConstants.BaseDataSetKeys.NAME)) {
                        nodeOutputBean.setName(fact.getProperty(CimConstants.BaseDataSetKeys.NAME).getPropertyValue().toString());
                    } else {
                        log.error("name property of instanceRid={} is null", infoObject.getObjectInstanceRID());
                    }
                    nodeOutputBeans.add(nodeOutputBean);
                } else {
                    log.error("instance of instanceRid={} not found", infoObject.getObjectInstanceRID());
                }
            } catch (CimDataEngineRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private List<TreeNodeOutputBean> getObjectChildNodes(CIMModelCore modelCore, TreeNodeInputBean parentNode, CimDataSpace cds) throws EntityNotFoundException {//NOSONAR
        List<TreeNodeOutputBean> childNodes = new ArrayList<>();
        IndustryTypes industryTypes = modelCore.getIndustryTypes();
        IndustryType industryType = industryTypes.getIndustryType(parentNode.getParentIndustryRid());
        if (industryType == null) {
            String msg = String.format("industry type of %s not found", parentNode.getParentIndustryRid());
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }

        List<InfoObjectDef> objectDefList = industryType.getLinkedInfoObjectDefs();
        Set<String> linkedObjects = new HashSet<>();
        if (objectDefList != null) {
            for (InfoObjectDef objectDef : objectDefList) {
                linkedObjects.add(objectDef.getObjectTypeName());
            }
        }

        List<InfoObject> infoObjectList = industryType.getChildInfoObjects();
        if (linkedObjects.contains(parentNode.getId())) {
            if (infoObjectList != null && infoObjectList.size() > 0) {
                for (InfoObject infoObject : infoObjectList) {
                    if (infoObject.getObjectTypeName().equals(parentNode.getId())) {
                        infoObjectToInstanceNode(childNodes, infoObject, cds, parentNode, parentNode.getParentIndustryRid());
                    }
                }
            }
        } else {
            log.error("object of objectName={} not belong to industryRid={}", parentNode.getId(), industryType.getIndustryTypeRID());
        }

        return childNodes;
    }

    private List<TreeNodeOutputBean> getInstanceChildNodes(TreeNodeInputBean parentNode, CimDataSpace cds) {//NOSONAR
        List<TreeNodeOutputBean> childNodes = new ArrayList<>();
        Fact fact = null;
        try {
            fact = cds.getFactById(parentNode.getInstanceRid());
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }
        if (fact != null) {
            //add related industry
            List<Relation> industryRelations = null;
            try {
                // industryRelations = fact.getAllSpecifiedRelations(BusinessLogicConstant.INDUSTRYTYPE_INFOOBJECT_LINK_RELATION_TYPE_NAME, RelationDirection.FROM);
                ExploreParameters ep = new ExploreParameters();
                ep.addRelatedRelationType(BusinessLogicConstant.INDUSTRYTYPE_INFOOBJECT_LINK_RELATION_TYPE_NAME);
                EqualFilteringItem filteringItem = new EqualFilteringItem(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_INDUSTRY_RID, parentNode.getParentIndustryRid());
                ep.setDefaultFilteringItem(filteringItem);
                industryRelations = fact.getSpecifiedRelations(ep, RelationDirection.FROM);
                log.debug("===related industry size: {}", industryRelations.size());
            } catch (CimDataEngineRuntimeException e) {
                e.printStackTrace();
            }
            if (industryRelations != null && industryRelations.size() > 0) {
                for (Relation relation : industryRelations) {
                    Dimension dimension = (Dimension) relation.getToRelationable();
                    if (dimension.getType().equals(BusinessLogicConstant.INDUSTRY_TYPE_DIMENSION_TYPE_NAME)) {
                        childNodes.add(relationableToOutBean(null, dimension));
                    }
                }

                Comparator<TreeNodeOutputBean> cmpByUpdateTime = Comparator.comparing(TreeNodeOutputBean::getCreateTime);
                Collections.sort(childNodes, cmpByUpdateTime);
            }
            //add related files
            ExploreParameters ep = new ExploreParameters();
            ep.addRelatedRelationType(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_TYPE_NAME);
            // EqualFilteringItem filteringItem = new EqualFilteringItem(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_INDUSTRY_RID, RIDUtil.ridToString(parentNode.getParentIndustryRid()));
            EqualFilteringItem filteringItem = new EqualFilteringItem(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_INDUSTRY_RID, parentNode.getParentIndustryRid());
            ep.setDefaultFilteringItem(filteringItem);
            List<Relation> filesRelations = null;
            try {
                filesRelations = fact.getSpecifiedRelations(ep, RelationDirection.TO);
                // filesRelations = fact.getAllSpecifiedRelations(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_TYPE_NAME, RelationDirection.TO);
                log.info("===related files size: {}", filesRelations.size());
            } catch (CimDataEngineRuntimeException e) {
                e.printStackTrace();
            }
            if (filesRelations != null && filesRelations.size() > 0) {
                for (Relation relation : filesRelations) {
                    Fact fileFact = (Fact) relation.getFromRelationable();
                    if (fileFact.getType().equals(BaseFileInfoKeys.BaseFileObjectTypeName)) {
                        childNodes.add(infoObjectToFileNode(cds, fileFact.getId(), null));
                    }
                }
            }
        } else {
            log.error("fact of {} not found", parentNode.getInstanceRid());
        }
        return childNodes;
    }

    public boolean updateNodeMetadata(String tenantId, String creator, UpdateTreeNodeInputBean node, Map<String, Object> metadata) throws DouplicateNameException {
        boolean flag = false;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            switch (node.getNodeType()) {
                case FILE:
                    flag = updateFileNodeMetadata(cds, tenantId, creator, node, metadata);
                    break;
                case INSTANCE:
                    flag = updateInstanceMetadata(cds, node, metadata);
                    break;
                case OBJECT:
                    flag = updateObjectMetdata(cds, node, metadata);
                    break;
                case INDUSTRY:
                    log.error("udpate instance metadata is not support currently");
                    break;
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return flag;
    }

    /**
     * 更新文件元数据
     *
     * @param cds
     * @param tenantId
     * @param creator
     * @param node
     * @param metadata
     * @return
     * @throws DouplicateNameException
     */
    private boolean updateFileNodeMetadata(CimDataSpace cds, String tenantId, String creator, TreeNodeInputBean node, Map<String, Object> metadata) throws DouplicateNameException {//NOSONAR
        Boolean flag = false;
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        try {
            modelCore.setCimDataSpace(cds);
            IndustryTypes industryTypes = modelCore.getIndustryTypes();
            Map<String, Object> formalValues = fileValueDataTypeCast(modelCore, cds, metadata);
            formalValues.put(IndustryTypeNodeKeys.UPDATOR_ID, creator);
            formalValues.put(IndustryTypeNodeKeys.UPDATE_TIME, new Date());
            //child nodes duplicate names check
            String oldParentIndustryRid = node.getParentIndustryRid();
            String nwParentIndustryRid;
            if (metadata.containsKey(BaseFileInfoKeys.INDUSTRY_RID)) {
                nwParentIndustryRid = (metadata.get(BaseFileInfoKeys.INDUSTRY_RID).toString());
            } else {
                nwParentIndustryRid = oldParentIndustryRid;
            }
            IndustryType oldParentIndustry = industryTypes.getIndustryType(oldParentIndustryRid);
            IndustryType nwParentIndustry = industryTypes.getIndustryType(nwParentIndustryRid);

            Map<String, String> nameMap = IndustryTypesService.getAllChildNodeNamesMapByType(nwParentIndustry, cds, tenantId, TreeNodeInputBean.TreeNodeTypeEnum.FILE);
            if (nameMap != null && nameMap.containsKey(formalValues.get(BaseFileInfoKeys.FILE_DATA_NAME).toString())) {
                if (!nameMap.get(formalValues.get(BaseFileInfoKeys.FILE_DATA_NAME).toString()).equals(node.getInstanceRid())) {
                    log.error("douplicate child industry names={} of parentIndustryRid={}", formalValues.get(BaseFileInfoKeys.FILE_DATA_NAME).toString(), nwParentIndustry);
                    throw new DouplicateNameException(EnumWrapper.CodeAndMsg.E05050002);
                }
            }
            //update parent industry
            if (metadata.containsKey(BaseFileInfoKeys.INDUSTRY_RID)) {
                if (!node.getParentIndustryRid().trim().toLowerCase().equals(metadata.get(BaseFileInfoKeys.INDUSTRY_RID).toString().trim().toLowerCase())) {
                    if (oldParentIndustry != null) {
                        oldParentIndustry.unlinkChildInfoObject(node.getInstanceRid());
                    }
                    if (nwParentIndustry != null) {
                        nwParentIndustry.linkChildInfoObject(node.getInstanceRid());
                    }
                    log.info("parent industry type is updated");
                } else {
                    log.info("parent industry should not be updated");
                }
            } else {
                log.info("parent industry should not be updated");
            }
            //update other metadata
            InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(BaseFileInfoKeys.BaseFileObjectTypeName);
            InfoObject infoObject = infoObjectDef.getObject(node.getInstanceRid());
            if (infoObject != null) {
                if (formalValues != null) {
                    for (Map.Entry<String, Object> entry : formalValues.entrySet()) {
                        if (entry.getKey().equals(BaseFileInfoKeys.CREATE_TIME) || entry.getKey().equals(BaseFileInfoKeys.FILE_DATA_ID)) {
                            log.error("create date time and file data id can not be updated");
                            continue;
                        }
                        infoObject.addOrUpdateObjectProperty(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                log.error("base file info fact of rid={} not found", node.getInstanceRid());
            }
            flag = true;
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 更新已挂载对象类型的“显示名称”和“备注”属性
     *
     * @param cds
     * @param node
     * @param metadata
     * @return
     */
    private boolean updateObjectMetdata(CimDataSpace cds, UpdateTreeNodeInputBean node, Map<String, Object> metadata) {//NOSONAR
        Boolean flag = false;
        try {
            Map<String, Object> formalMetadata = new HashMap<>();
            if (metadata.containsKey(CimConstants.ObjectTypeNodeKeys.COMMENT)) {
                formalMetadata.put(CimConstants.ObjectTypeNodeKeys.COMMENT, metadata.get(CimConstants.ObjectTypeNodeKeys.COMMENT));
            }
            if (metadata.containsKey(CimConstants.ObjectTypeNodeKeys.DISPLAY_NAME)) {
                formalMetadata.put(CimConstants.ObjectTypeNodeKeys.DISPLAY_NAME, metadata.get(CimConstants.ObjectTypeNodeKeys.DISPLAY_NAME));
            }
            if (formalMetadata.size() > 0) {
                if (StringUtils.isNotBlank(node.getParentIndustryRid()) && StringUtils.isNotBlank(node.getId())) {
                    List<Fact> factList = getIndustryAndObjectMapping(cds, node.getParentIndustryRid(), node.getId());
                    if (factList != null && factList.size() > 0) {
                        Fact fact = factList.get(0);
                        List<String> propertyNames = fact.addNewOrUpdateProperties(formalMetadata);
                        if (propertyNames != null) {
                            flag = true;
                        }
                    }
                } else {
                    log.error("parent industry and object type id are mandatory");
                }
            } else {
                log.error("no comment or display name is provided");
            }
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 更新已挂载实例的“备注”属性
     *
     * @param cds
     * @param node
     * @param metadata
     * @return
     */
    private boolean updateInstanceMetadata(CimDataSpace cds, UpdateTreeNodeInputBean node, Map<String, Object> metadata) {
        Boolean flag = false;
        if (metadata.containsKey(CimConstants.InstanceNodeKeys.COMMENT)) {
            Relation relation = industryAndInstanceRelation(cds, node);
            if (relation != null) {
                try {
                    if (relation.hasProperty(CimConstants.InstanceNodeKeys.COMMENT)) {
                        relation.updateProperty(CimConstants.InstanceNodeKeys.COMMENT, metadata.get(CimConstants.InstanceNodeKeys.COMMENT).toString());
                    } else {
                        relation.addProperty(CimConstants.InstanceNodeKeys.COMMENT, metadata.get(CimConstants.InstanceNodeKeys.COMMENT).toString());
                    }
                    flag = true;
                } catch (CimDataEngineRuntimeException e) {
                    e.printStackTrace();
                }
            } else {
                log.error("relatin of indusrty={} and instance={} not exists", node.getParentIndustryRid(), node.getInstanceRid());
            }
        } else {
            log.error("comment is mandatory");
        }
        return flag;
    }

    /**
     * 查询行业分类和实例的关系
     *
     * @param node
     * @return
     */
    public static Relation industryAndInstanceRelation(CimDataSpace cds, TreeNodeInputBean node) {
        try {
            Dimension currentIndustryType = cds.getDimensionById(node.getParentIndustryRid());
            List<Relation> relationList = currentIndustryType.getAllSpecifiedRelations(BusinessLogicConstant.INDUSTRYTYPE_INFOOBJECT_LINK_RELATION_TYPE_NAME, RelationDirection.FROM);
            if (relationList != null) {
                for (Relation currentRelation : relationList) {
                    Relationable targetRelationable = currentRelation.getToRelationable();
                    if (targetRelationable instanceof Fact && targetRelationable.getId().equals(node.getInstanceRid())) {
                        return currentRelation;
                    }
                }
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Object> fileValueDataTypeCast(CIMModelCore modelCore, CimDataSpace cds, Map<String, Object> originalValues) throws CimDataEngineRuntimeException {
        //数据类型转换
        InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(BaseFileInfoKeys.BaseFileObjectTypeName);
        List<DatasetDef> datasetDefList = infoObjectDef.getDatasetDefs();
        if (datasetDefList == null || datasetDefList.size() < 1) {
            log.error("no data set of industry type is found");
            return null;
        }
        List<DatasetVO> datasetVOList = new ArrayList<>();
        for (DatasetDef datasetDef : datasetDefList) {
            datasetVOList.add(DatasetFeatures.getDatasetVOById(cds, datasetDef.getDatasetRID(), true));
        }
        Map<String, Object> formalValues = OrientDBCommonUtil.valuesTypeCast(OrientDBCommonUtil.mergeDataTypeDef(datasetVOList), originalValues);

        return formalValues;
    }

    /**
     * 查询对象类型和实例树节点的元数据
     *
     * @param tenantId
     * @param creator
     * @param inputBean
     * @return
     */
    public TreeNodeMetadataOutputBean getNodeMetadata(String tenantId, String creator, TreeNodeInputBean inputBean) throws CimDataEngineRuntimeException, DataServiceModelRuntimeException, CimDataEngineInfoExploreException {
        TreeNodeMetadataOutputBean outputBean = null;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            switch (inputBean.getNodeType()) {
                case FILE:
                    log.error("file metadata is not support currently");
                    break;
                case INSTANCE:
                    outputBean = insatnceNodeMetadata(cds, tenantId, creator, inputBean);
                    break;
                case OBJECT:
                    outputBean = objectNodeMetadata(cds, tenantId, creator, inputBean);
                    break;
                case INDUSTRY:
                    log.error("industry metadata is not support currently");
                    break;
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return outputBean;
    }

    /**
     * 查询已挂载对象类型的元数据
     *
     * @param cds
     * @param tenantId
     * @param creator
     * @param inputBean
     * @return
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     */
    private TreeNodeMetadataOutputBean objectNodeMetadata(CimDataSpace cds, String tenantId, String creator, TreeNodeInputBean inputBean) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {//NOSONAR
        // TreeNodeMetadataOutputBean outputBean = new TreeNodeMetadataOutputBean(inputBean);
        TreeNodeMetadataOutputBean outputBean = TreeNodeMetadataOutputBean.transferFactory(inputBean);
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        modelCore.setCimDataSpace(cds);
        IndustryType industryType = modelCore.getIndustryType(inputBean.getParentIndustryRid());
        if (industryType != null) {
            List<InfoObjectDef> infoObjectDefList = industryType.getLinkedInfoObjectDefs();
            if (infoObjectDefList != null && infoObjectDefList.size() > 0) {
                for (InfoObjectDef objectDef : infoObjectDefList) {
                    if (objectDef.getObjectTypeName().equals(inputBean.getId())) {
                        Fact statusFact = InfoObjectFeatures.getInfoObjectTypeStatusRecord(cds, inputBean.getId());
                        if (statusFact != null) {
                            Map<String, Object> metadata = new HashMap<>();
                            if (statusFact.hasProperty(CimConstants.ObjectTypeNodeKeys.INFO_OBJECT_TYPE_DESC)) {
                                metadata.put(CimConstants.ObjectTypeNodeKeys.OBJECT_NAME, statusFact.getProperty(CimConstants.ObjectTypeNodeKeys.INFO_OBJECT_TYPE_DESC).getPropertyValue().toString());
                                metadata.put(CimConstants.ObjectTypeNodeKeys.DISPLAY_NAME, statusFact.getProperty(CimConstants.ObjectTypeNodeKeys.INFO_OBJECT_TYPE_DESC).getPropertyValue().toString());
                            }
                            if (statusFact.hasProperty(CimConstants.ObjectTypeNodeKeys.CREATE_DATE_TIME)) {
                                metadata.put(CimConstants.ObjectTypeNodeKeys.CREATE_TIME, statusFact.getProperty(CimConstants.ObjectTypeNodeKeys.CREATE_DATE_TIME).getPropertyValue());
                            }
                            if (statusFact.hasProperty(CimConstants.ObjectTypeNodeKeys.UPDATE_DATE_TIME)) {
                                metadata.put(CimConstants.ObjectTypeNodeKeys.UPDATE_TIME, statusFact.getProperty(CimConstants.ObjectTypeNodeKeys.UPDATE_DATE_TIME).getPropertyValue());
                            }
                            metadata.put(CimConstants.ObjectTypeNodeKeys.CREATOR_ID, creator);
                            metadata.put(CimConstants.ObjectTypeNodeKeys.UPDATOR_ID, creator);

                            List<Fact> factList = getIndustryAndObjectMapping(cds, inputBean.getParentIndustryRid(), inputBean.getId());
                            if (factList != null && factList.size() > 0) {
                                Fact fact = factList.get(0);
                                if (fact.hasProperty(CimConstants.ObjectTypeNodeKeys.DISPLAY_NAME)) {
                                    metadata.put(CimConstants.ObjectTypeNodeKeys.DISPLAY_NAME, fact.getProperty(CimConstants.ObjectTypeNodeKeys.DISPLAY_NAME).getPropertyValue().toString());
                                }
                                if (fact.hasProperty(CimConstants.ObjectTypeNodeKeys.COMMENT)) {
                                    metadata.put(CimConstants.ObjectTypeNodeKeys.COMMENT, fact.getProperty(CimConstants.ObjectTypeNodeKeys.COMMENT).getPropertyValue().toString());
                                }
                            }
                            outputBean.setMetadata(metadata);
                        } else {
                            log.error("object type status of {} not found", inputBean.getId());
                        }
                        break;
                    }
                }
            } else {
                log.error("no object type is linked with industry={}", inputBean.getParentIndustryRid());
            }
        } else {
            log.error("industry type of {} not found", inputBean.getParentIndustryRid());
        }
        return outputBean;
    }

    /**
     * 查询实例数据元数据
     *
     * @param cds
     * @param tenantId
     * @param creator
     * @param inputBean
     * @return
     * @throws CimDataEngineRuntimeException
     * @throws DataServiceModelRuntimeException
     */
    private TreeNodeMetadataOutputBean insatnceNodeMetadata(CimDataSpace cds, String tenantId, String creator, TreeNodeInputBean inputBean) throws CimDataEngineRuntimeException, DataServiceModelRuntimeException {//NOSONAR
        // TreeNodeMetadataOutputBean outputBean = new TreeNodeMetadataOutputBean(inputBean);
        TreeNodeMetadataOutputBean outputBean = TreeNodeMetadataOutputBean.transferFactory(inputBean);
        Relation relation = industryAndInstanceRelation(cds, inputBean);
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        modelCore.setCimDataSpace(cds);
        if (relation != null) {
            Map<String, Object> metadata = new HashMap<>();
            if (relation.hasProperty(CimConstants.InstanceNodeKeys.COMMENT)) {
                metadata.put(CimConstants.InstanceNodeKeys.COMMENT, relation.getProperty(CimConstants.InstanceNodeKeys.COMMENT).getPropertyValue().toString());
            }
            Fact fact = cds.getFactById(inputBean.getInstanceRid());
            String objectId = fact.getType();
            InfoObjectDef objectDef = modelCore.getInfoObjectDef(objectId);
            if (objectDef != null) {
                metadata.put(CimConstants.InstanceNodeKeys.OBJECT_NAME, objectDef.getObjectTypeDesc());
                InfoObject infoObject = objectDef.getObject(inputBean.getInstanceRid());
                Map<String, Object> baseInfo = infoObject.getInfo();
                if (baseInfo != null && baseInfo.size() > 0) {
                    if (baseInfo.containsKey(CimConstants.BaseDataSetKeys.ID)) {
                        metadata.put(CimConstants.InstanceNodeKeys.ID, baseInfo.get(CimConstants.BaseDataSetKeys.ID));
                    }
                    if (baseInfo.containsKey(CimConstants.BaseDataSetKeys.NAME)) {
                        metadata.put(CimConstants.InstanceNodeKeys.NAME, baseInfo.get(CimConstants.BaseDataSetKeys.NAME));
                    }
                    if (baseInfo.containsKey(CimConstants.BaseDataSetKeys.CREATOR) && baseInfo.get(CimConstants.BaseDataSetKeys.CREATOR) != null) {
                        metadata.put(CimConstants.InstanceNodeKeys.CREATOR_ID, baseInfo.get(CimConstants.BaseDataSetKeys.CREATOR));
                        metadata.put(CimConstants.InstanceNodeKeys.UPDATOR_ID, baseInfo.get(CimConstants.BaseDataSetKeys.CREATOR));
                    } else {
                        metadata.put(CimConstants.InstanceNodeKeys.CREATOR_ID, creator);
                        metadata.put(CimConstants.InstanceNodeKeys.UPDATOR_ID, creator);
                    }
                    if (baseInfo.containsKey(CimConstants.BaseDataSetKeys.CREATE_TIME)) {
                        metadata.put(CimConstants.InstanceNodeKeys.CREATE_TIME, baseInfo.get(CimConstants.BaseDataSetKeys.CREATE_TIME));
                    }
                    if (baseInfo.containsKey(CimConstants.BaseDataSetKeys.UPDATE_TIME)) {
                        metadata.put(CimConstants.InstanceNodeKeys.UPDATE_TIME, baseInfo.get(CimConstants.BaseDataSetKeys.UPDATE_TIME));
                    }
                }
                outputBean.setMetadata(metadata);
            } else {
                log.error("object type of instanceRid={} not found", inputBean.getInstanceRid());
            }
        } else {
            log.error("the relation of industry={} and instance={} not found", inputBean.getParentIndustryRid(), inputBean.getInstanceRid());
        }
        return outputBean;
    }

    public static List<Fact> getIndustryAndObjectMapping(CimDataSpace ids, String industryTypeId, String objectTypeId) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        InformationExplorer ie = ids.getInformationExplorer();
        ExploreParameters ep = new ExploreParameters();
        ep.setType(BusinessLogicConstant.INFOOBJECTTYPE_INDUSTRYTYPE_MAPPING_FACT_TYPE_NAME);
        ep.setDefaultFilteringItem(new EqualFilteringItem("industryTypeId", industryTypeId));
        FilteringItem objectTypeIdItem = new EqualFilteringItem("infoObjectTypeName", objectTypeId);
        ep.addFilteringItem(objectTypeIdItem, ExploreParameters.FilteringLogic.AND);
        List<Fact> linkedMappingList = ie.discoverFacts(ep);
        return linkedMappingList;
    }

    /**
     * 批量删除节点
     *
     * @param tenantId
     * @param nodeInputBeanList
     * @return
     */
    public Object deleteContentNodeList(String tenantId, List<DeleteTreeNodeInputBean> nodeInputBeanList) {//NOSONAR
        List<DeleteTreeNodeOutputBean> list = new ArrayList();
        Boolean flag = false;
        CimDataSpace cds = null;
        DeleteTreeNodeOutputBean deleteTreeNodeOutputBean;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            for (DeleteTreeNodeInputBean nodeInputBean : nodeInputBeanList) {
                switch (nodeInputBean.getNodeType()) {
                    case FILE:
                        flag = deleteFileNode(modelCore, nodeInputBean.getInstanceRid());
                        break;
                    case INDUSTRY:
                        flag = deleteIndustryNode(modelCore, nodeInputBean.getInstanceRid());
                        break;
                    case OBJECT:
                        flag = unlinkObjectNode(modelCore, cds, nodeInputBean);
                        break;
                    case INSTANCE:
                        flag = unlinkInstanceNode(modelCore, nodeInputBean);
                        break;
                }
                //批量删除输出对象
                deleteTreeNodeOutputBean = new DeleteTreeNodeOutputBean();
                if (StringUtils.isNotBlank(nodeInputBean.getIndustryRid())) {
                    deleteTreeNodeOutputBean.setIndustryRid(nodeInputBean.getIndustryRid());
                }
                if (StringUtils.isNotBlank(nodeInputBean.getId())) {
                    deleteTreeNodeOutputBean.setId(nodeInputBean.getId());
                }
                if (StringUtils.isNotBlank(nodeInputBean.getInstanceRid())) {
                    deleteTreeNodeOutputBean.setInstanceRid(nodeInputBean.getInstanceRid());
                }
                if (StringUtils.isNotBlank(nodeInputBean.getParentIndustryRid())) {
                    deleteTreeNodeOutputBean.setParentIndustryRid(nodeInputBean.getParentIndustryRid());
                }
                deleteTreeNodeOutputBean.setNodeType(nodeInputBean.getNodeType());
                deleteTreeNodeOutputBean.setSuccess(flag);
                list.add(deleteTreeNodeOutputBean);
            }
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (InputNotEnoughException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return list;
    }

    /**
     * 树节点移动
     *
     * @param tenantId
     * @param isOverride
     * @param moveNodes
     * @return
     */
    public MoveNodeOutputBean moveNodes(String tenantId, Boolean isOverride, MoveNodesInputBean moveNodes) {//NOSONAR
        MoveNodeOutputBean outputBean = new MoveNodeOutputBean();
        TreeNodeWithNameInputBean fromNode = moveNodes.getFromNode();

        TreeNodeWithNameInputBean toNode = moveNodes.getToNode();
        if (fromNode == null || toNode == null) {
            log.error("fromNode or toNode is null");
            outputBean.setSuccess(false);
            return outputBean;
        }

        TreeNodeInputBean.TreeNodeTypeEnum fromNodeType = fromNode.getNodeType();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            Boolean isRemoved = removeFromOldParentNode(cds, modelCore, fromNode);
            if (isRemoved) {
                switch (toNode.getNodeType()) {
                    case INDUSTRY:
                        log.debug("start to move node to industry");
                        IndustryType industryType = modelCore.getIndustryType(toNode.getInstanceRid());
                        if (industryType != null) {
                            Map<String, String> names = IndustryTypesService.getAllChildNodeNamesMapByType(industryType, cds, tenantId, fromNodeType);
                            log.debug("===child node names: {}", names);
                            if (names != null) {
                                if (names.containsKey(fromNode.getName())) {
                                    log.info("detect duplicate names");
                                    if (isOverride) {
                                        throw new DuplicateFormatFlagsException("child node name must unique");
                                    } else {
                                        updateDisplayName(cds, fromNode, names);
                                    }
                                }
                                switch (fromNodeType) {//NOSONAR
                                    case FILE:
                                        log.info("move file to industry");
                                        industryType.linkChildInfoObject(fromNode.getInstanceRid());
                                        outputBean.setSuccess(true);
                                        break;
                                    case INDUSTRY:
                                        log.info("child industry file to industry");
                                        IndustryTypeFeatures.linkWithParentIndustry(cds, fromNode.getInstanceRid(), toNode.getInstanceRid());
                                        outputBean.setSuccess(true);
                                        break;
                                    case OBJECT:
                                        log.info("move object to industry");
                                        InfoObjectFeatures.linkInfoObjectTypeWithIndustryType(cds, fromNode.getId(), toNode.getInstanceRid());
                                        outputBean.setSuccess(true);
                                        break;
                                    default:
                                        log.error("only file, industry or object node can under industry node");
                                        break;
                                }
                            }
                        } else {
                            log.error("industry type of {} not found", toNode.getInstanceRid());
                        }
                        break;
                    case INSTANCE:
                        log.info("start to move node to instance");
                        Map<String, String> names = ContentInstanceService.getAllChildNamesByType(toNode, cds, fromNodeType);
                        if (names != null) {
                            if (names.containsKey(fromNode.getName())) {
                                if (isOverride) {
                                    throw new DuplicateFormatFlagsException("child node name must unique");
                                } else {
                                    updateDisplayName(cds, fromNode, names);
                                }
                            }
                        }
                        switch (fromNodeType) {//NOSONAR
                            case FILE:
                                log.info("move file to instance");
                                Map<String, Object> parms = new HashMap<>();
                                parms.put(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_INDUSTRY_RID, toNode.getParentIndustryRid());
                                Fact instanceFact = cds.getFactById(toNode.getInstanceRid());
                                Fact fileFact = cds.getFactById(fromNode.getInstanceRid());
                                instanceFact.addFromRelation(fileFact, BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_TYPE_NAME, parms);
                                outputBean.setSuccess(true);
                                break;
                            case INDUSTRY:
                                log.info("move industry to indtsnce");
                                IndustryType parentIndustryType = modelCore.getIndustryType(fromNode.getInstanceRid());
                                if (parentIndustryType != null) {
                                    parentIndustryType.linkParentInfoObject(toNode.getInstanceRid());
                                    outputBean.setSuccess(true);
                                } else {
                                    log.error(INDUSTRY_NOT_FOUND, fromNode.getInstanceRid());
                                }
                                break;
                            default:
                                log.error("only file or industry node can under instance node");
                                break;
                        }
                        break;
                    default:
                        log.error("target node type must be industry or instance");
                        break;
                }
            } else {
                log.error("unlink with parent node failed");
            }
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return outputBean;
    }

    /**
     * 断开与父节点的连接
     *
     * @param cds
     * @param modelCore
     * @param fromNode
     * @throws CimDataEngineRuntimeException
     * @throws DataServiceModelRuntimeException
     * @throws CimDataEngineInfoExploreException
     */
    private static Boolean removeFromOldParentNode(CimDataSpace cds, CIMModelCore modelCore, TreeNodeWithNameInputBean fromNode) throws CimDataEngineRuntimeException, DataServiceModelRuntimeException, CimDataEngineInfoExploreException {//NOSONAR
        Boolean flag = false;
        switch (fromNode.getNodeType()) {
            case INDUSTRY:
                //unlink industry with parent industry
                if (StringUtils.isNotBlank(fromNode.getParentIndustryRid())) {
                    flag = IndustryTypeFeatures.unlinkWithParentIndustry(cds, fromNode.getInstanceRid(), fromNode.getParentIndustryRid());
                } else {
                    //unlink industry with instance
                    IndustryType industryType = modelCore.getIndustryType(fromNode.getInstanceRid());
                    if (industryType != null) {
                        List<InfoObject> infoObjectList = industryType.getParentInfoObjects();
                        if (infoObjectList != null) {
                            for (InfoObject infoObject : infoObjectList) {
                                flag = industryType.unlinkParentInfoObject(infoObject.getObjectInstanceRID());
                            }
                        }
                    }
                }
                // flag = true;
                break;
            case FILE:
                //unlink file with industry
                if (StringUtils.isNotBlank(fromNode.getParentIndustryRid())) {
                    IndustryType industryType = modelCore.getIndustryType(fromNode.getParentIndustryRid());
                    industryType.unlinkChildInfoObject(fromNode.getInstanceRid());
                } else {
                    //unlink file with instance
                    Fact fileFact = cds.getFactById(fromNode.getInstanceRid());
                    if (fileFact != null) {
                        List<Relation> relationList = fileFact.getAllSpecifiedRelations(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_TYPE_NAME, RelationDirection.FROM);
                        if (relationList != null) {
                            for (Relation relation : relationList) {
                                cds.removeRelation(relation.getId());
                            }
                        }
                    }
                }
                flag = true;
                break;
            case OBJECT:
                IndustryType industryType = modelCore.getIndustryType(fromNode.getParentIndustryRid());
                if (industryType != null) {
                    InfoObjectFeatures.unlinkInfoObjectTypeAttachedIndustryType(cds, fromNode.getId(), fromNode.getParentIndustryRid());
                } else {
                    log.error(INDUSTRY_NOT_FOUND, fromNode.getParentIndustryRid());
                }
                flag = true;
                break;
            case INSTANCE:
                log.error("instacne node cannot move");
                break;
            default:
                log.error("unsupport node type");
        }
        return flag;
    }

    /**
     * 更新行业分类显示名称.
     *
     * @param cds
     * @param industryRid
     * @param displayName
     * @return
     * @throws CimDataEngineRuntimeException
     */
    private static boolean updateIndustryDisplayName(CimDataSpace cds, String industryRid, String displayName) throws CimDataEngineRuntimeException {
        boolean flag = false;
        Dimension dimension = cds.getDimensionById(industryRid);
        if (dimension != null) {
            if (dimension.hasProperty(IndustryTypeNodeKeys.INDUSTRY_TYPE_DESC)) {
                dimension.updateProperty(IndustryTypeNodeKeys.INDUSTRY_TYPE_DESC, displayName);
            } else {
                dimension.addProperty(IndustryTypeNodeKeys.INDUSTRY_TYPE_DESC, displayName);
            }
            if (dimension.hasProperty(IndustryTypeNodeKeys.INDUSTRY_NAME)) {
                dimension.updateProperty(IndustryTypeNodeKeys.INDUSTRY_NAME, displayName);
            } else {
                dimension.addProperty(IndustryTypeNodeKeys.INDUSTRY_NAME, displayName);
            }
            flag = true;
        } else {
            log.error(INDUSTRY_NOT_FOUND, industryRid);
        }
        return flag;
    }

    /**
     * 更新对象类型显示名称.
     *
     * @param cds
     * @param industryRid
     * @param objectTypeId
     * @param displayName
     * @return
     * @throws CimDataEngineRuntimeException
     */
    private static boolean updateObjectTypeDisplayName(CimDataSpace cds, String industryRid, String objectTypeId, String displayName) throws CimDataEngineRuntimeException {
        boolean flag = false;
        List<Fact> factList;
        try {
            factList = getIndustryAndObjectMapping(cds, industryRid, objectTypeId);
            if (factList != null && factList.size() > 0) {
                Fact fact = factList.get(0);
                if (fact.hasProperty(CimConstants.ObjectTypeNodeKeys.DISPLAY_NAME)) {
                    fact.updateProperty(CimConstants.ObjectTypeNodeKeys.DISPLAY_NAME, displayName);
                } else {
                    fact.addProperty(CimConstants.ObjectTypeNodeKeys.DISPLAY_NAME, displayName);
                }
                flag = true;
            } else {
                log.error("mapping of object {} and industry {} not found", objectTypeId, industryRid);
            }
        } catch (CimDataEngineInfoExploreException e) {
            log.error("update object display name", e);
        }

        return flag;
    }

    /**
     * 更新文件显示名称.
     *
     * @param cds
     * @param instanceRid
     * @param displayName
     * @return
     */
    private static boolean updateFileDisplayName(CimDataSpace cds, String instanceRid, String displayName) throws CimDataEngineRuntimeException {
        boolean flag = false;
        Fact fact = cds.getFactById(instanceRid);
        try {
            if (fact != null && fact.getType().equals(BaseFileInfoKeys.BaseFileObjectTypeName)) {
                fact.updateProperty(BaseFileInfoKeys.FILE_DATA_NAME, displayName);
                flag = true;
            } else {
                log.error("fact of {} in {} not found", instanceRid, BaseFileInfoKeys.BaseFileObjectTypeName);
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 显示名称重名处理.
     *
     * @param cds
     * @param fromNode
     * @param names
     */
    private static void updateDisplayName(CimDataSpace cds, TreeNodeWithNameInputBean fromNode, Map<String, String> names) {
        TreeNodeInputBean.TreeNodeTypeEnum fromNodeType = fromNode.getNodeType();
        int idx = 1;
        String cpName = fromNode.getName() + '(' + idx + ')';
        while (names.containsKey(cpName)) {
            idx++;
            cpName = fromNode.getName() + '(' + idx + ')';
        }
        try {
            if (fromNodeType.equals(TreeNodeInputBean.TreeNodeTypeEnum.INDUSTRY)) {
                updateIndustryDisplayName(cds, fromNode.getInstanceRid(), cpName);
            } else if (fromNodeType.equals(TreeNodeInputBean.TreeNodeTypeEnum.OBJECT)) {
                updateObjectTypeDisplayName(cds, fromNode.getParentIndustryRid(), fromNode.getId(), cpName);
            } else {
                updateFileDisplayName(cds, fromNode.getInstanceRid(), cpName);
            }
        } catch (CimDataEngineRuntimeException e) {
            log.error("update display name failed", e);
        }
    }

}
