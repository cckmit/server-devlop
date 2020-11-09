package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.GeneralProperties;
import com.glodon.pcop.cim.common.util.CimConstants.TreeNodeBaseInfo;
import com.glodon.pcop.cim.common.util.TreeConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.*;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.InValueFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.SimilarFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineDataMartException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.*;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.InfoObjectDefDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.BatchDataOperationResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.optionalInit.TreeInitiationUtil;
import com.glodon.pcop.cimsvc.model.tree.*;
import com.glodon.pcop.cimsvc.service.v2.InstancesService;
import com.glodon.pcop.cimsvc.util.DateUtil;
import com.glodon.pcop.cimsvc.util.PinyinUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

@Service
public class TreeService {
    private static final Logger log = LoggerFactory.getLogger(TreeService.class);

    @Autowired
    private DataPermissionService dataPermissionService;
    @Autowired
    private InstancesService instancesService;

    /**
     * 展开子节点
     *
     * @param treeDefId
     * @param parentNode
     * @param isCountChild
     * @return
     */
    public List<NodeInfoBean> expandNodes(String tenantId, String treeDefId, NodeInfoBean parentNode,
                                          Boolean isCountChild) {
        List<NodeInfoBean> childNodes = new ArrayList<>();
        if (StringUtils.isBlank(treeDefId)) {
            log.error("treeDefId is mandatory");
            return childNodes;
        }
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            if (parentNode == null || StringUtils.isBlank(parentNode.getID())) {
                childNodes = listRootNodes(treeDefId, isCountChild, modelCore);
            } else {
                childNodes = listChildNodes(treeDefId, parentNode, isCountChild, modelCore);
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return childNodes;
    }

    /**
     * @param tenantId
     * @param treeDefId
     * @param keyWord
     * @return
     */
    public List<NodeInfoBean> searchNodeByName(String tenantId, String userId, String treeDefId, String keyWord,
                                               Boolean filterByPermission) {
        List<NodeInfoBean> nodeInfoBeanList = new ArrayList<>();
        Assert.isTrue(StringUtils.isNotBlank(tenantId), "tenantId is mandatory");
        Assert.isTrue(StringUtils.isNotBlank(treeDefId), "treeDefId is mandatory");
        if (StringUtils.isBlank(keyWord)) {
            log.error("search input is blank");
            return nodeInfoBeanList;
        }
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            FilteringItem filteringItem = new SimilarFilteringItem(TreeNodeBaseInfo.NAME, keyWord,
                    SimilarFilteringItem.MatchingType.Contain, false);
            ExploreParameters ep = new ExploreParameters();
            ep.setType(treeDefId);
            ep.setDefaultFilteringItem(filteringItem);

            InfoObjectDef objectDef = modelCore.getInfoObjectDef(treeDefId);
            if (objectDef == null) {
                log.error("object type of {} not found, tenant id is {}", treeDefId, tenantId);
                return nodeInfoBeanList;
            }
            InfoObjectRetrieveResult retrieveResult = objectDef.getObjects(ep);
            List<InfoObject> infoObjectList = retrieveResult.getInfoObjects();

            if (infoObjectList != null) {
                Map<String, NodeInfoBean> nodeInfoBeanMap = new HashMap<>();
                List<String> schemaRids = dataPermissionService.getPermissionSchemaByUser(cds, userId);
                for (InfoObject object : infoObjectList) {
                    if (filterByPermission) {
                        String rid = object.getObjectInstanceRID();
                        DataPermissionBean dataPermissionBean = dataPermissionService.getDataPermissionByUser(rid,
                                schemaRids);
                        if (dataPermissionBean == null || dataPermissionBean.getReadPermission() == 0) {
                            log.debug("filter out by data permission");
                            continue;
                        }
                    }
                    Fact fact = cds.getFactById(object.getObjectInstanceRID());
                    getAncestryNodes(treeDefId, fact, null, nodeInfoBeanMap);
                }

                for (Map.Entry<String, NodeInfoBean> entry : nodeInfoBeanMap.entrySet()) {
                    if (entry.getValue().getLevel() == TreeNodeBaseInfo.ROOT_LEVEL) {
                        nodeInfoBeanList.add(entry.getValue());
                    }
                }
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return nodeInfoBeanList;
    }

    /**
     * 已知树节点的子节点 by parnet node info
     *
     * @param treeDefId
     * @param parentNode
     * @param isCountChild
     * @param modelCore
     * @return
     * @throws CimDataEngineRuntimeException
     */
    public List<NodeInfoBean> listChildNodes(String treeDefId, NodeInfoBean parentNode,
                                             Boolean isCountChild, CIMModelCore modelCore)
            throws CimDataEngineRuntimeException {
        List<NodeInfoBean> childNodes = new ArrayList<>();

        FilteringItem filteringItem = new EqualFilteringItem(TreeNodeBaseInfo.ID, parentNode.getID());
        ExploreParameters ep = new ExploreParameters();
        ep.setType(treeDefId);
        ep.setDefaultFilteringItem(filteringItem);

        InfoObjectRetrieveResult retrieveResult = modelCore.getInfoObjectDef(treeDefId).getObjects(ep);
        List<InfoObject> infoObjectList = retrieveResult.getInfoObjects();
        // List<Fact> factList = cds.getInformationExplorer().discoverInheritFacts(ep);
        if (infoObjectList == null || infoObjectList.size() < 1) {
            log.error("parent node of ID={} not found", parentNode.getID());
            return childNodes;
        }
        CimDataSpace cds = modelCore.getCimDataSpace();
        InfoObjectDef objectDef = modelCore.getInfoObjectDef(treeDefId);

        childNodes = listChildNodes(treeDefId, objectDef, parentNode.getID(), isCountChild, infoObjectList.get(0), cds);
        return childNodes;
    }

    /**
     * 查询子节点 by tree object type and parnet node info
     *
     * @param treeObjDef
     * @param treeDefId
     * @param parentNode
     * @param isCountChild
     * @param modelCore
     * @return
     * @throws CimDataEngineRuntimeException
     */
    public List<NodeInfoBean> listChildNodes(InfoObjectDef treeObjDef, String treeDefId, NodeInfoBean parentNode,
                                             Boolean isCountChild, CIMModelCore modelCore)
            throws CimDataEngineRuntimeException {
        List<NodeInfoBean> childNodes = new ArrayList<>();

        FilteringItem filteringItem = new EqualFilteringItem(TreeNodeBaseInfo.ID, parentNode.getID());
        ExploreParameters ep = new ExploreParameters();
        ep.setDefaultFilteringItem(filteringItem);

        InfoObjectRetrieveResult retrieveResult = treeObjDef.getObjects(ep);
        List<InfoObject> infoObjectList = retrieveResult.getInfoObjects();
        if (infoObjectList == null || infoObjectList.size() < 1) {
            log.error("parent node of ID={} not found", parentNode.getID());
            return childNodes;
        }
        CimDataSpace cds = modelCore.getCimDataSpace();
        InfoObjectDef objectDef = modelCore.getInfoObjectDef(treeDefId);

        childNodes = listChildNodes(treeDefId, objectDef, parentNode.getID(), isCountChild, infoObjectList.get(0), cds);
        return childNodes;
    }

    /**
     * 树节点的子节点 by parent node fact
     *
     * @param treeDefId
     * @param objectDef
     * @param parentNodeId
     * @param isCountChild
     * @return
     * @throws CimDataEngineRuntimeException
     */
    public List<NodeInfoBean> listChildNodes(String treeDefId, InfoObjectDef objectDef, String parentNodeId, Boolean isCountChild,
                                             InfoObject infoObject, CimDataSpace cds)
            throws CimDataEngineRuntimeException {
        List<NodeInfoBean> childNodes = new ArrayList<>();
        List<InfoObject> infoObjectList =
                infoObject.getAllRelatedInfoObjects(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE,
                        RelationDirection.FROM);

        if (infoObjectList != null && infoObjectList.size() > 0) {
            for (InfoObject object : infoObjectList) {
                Relationable rlab = cds.getFactById(object.getObjectInstanceRID());
                NodeInfoBean infoBean = new NodeInfoBean();
                //添加附加信息
                addAttachedInfo(cds, objectDef, object, infoBean);

                TreeServiceUtil.relationableToNodeInfo(rlab, infoBean);
                if (StringUtils.isNotBlank(infoBean.getID()) && StringUtils.isNotBlank(infoBean.getNAME())) {
                    infoBean.setTreeDefId(treeDefId);
                    infoBean.setParentId(parentNodeId);
                    if (isCountChild) {
                        infoBean.setChildCount(
                                object.getAllRelatedInfoObjects(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE,
                                        RelationDirection.FROM).size());
                    }
                    childNodes.add(infoBean);
                } else {
                    log.error("tree node info ID and NAME are mandatary");
                }
            }
        }

        return childNodes;
    }

    /**
     * 查询指定节点的所有祖先节点，直到root节点
     *
     * @param treeDefId
     * @param relationable
     * @param nodeInfoBeanMap
     * @throws CimDataEngineRuntimeException
     */
    public void getAncestryNodes(String treeDefId, Relationable relationable, NodeInfoBean childNodeInfoBean,
                                 Map<String, NodeInfoBean> nodeInfoBeanMap) throws CimDataEngineRuntimeException {
        Assert.notNull(relationable, "PCOP child node relationable is null");
        NodeInfoBean nodeInfoBean;
        if (nodeInfoBeanMap.containsKey(relationable.getId())) {
            if (childNodeInfoBean != null) {
                nodeInfoBean = nodeInfoBeanMap.get(relationable.getId());
                nodeInfoBean.addChildNode(childNodeInfoBean);
                childNodeInfoBean.setParentId(nodeInfoBean.getID());
            }
            return;
        }
        //add child node to result
        nodeInfoBean = new NodeInfoBean(treeDefId);
        TreeServiceUtil.relationableToNodeInfo(relationable, nodeInfoBean);
        if (childNodeInfoBean != null) {
            nodeInfoBean.addChildNode(childNodeInfoBean);
            childNodeInfoBean.setParentId(nodeInfoBean.getID());
        }
        nodeInfoBeanMap.put(relationable.getId(), nodeInfoBean);
        //add parent node to result
        List<Relation> relationList =
                relationable.getAllSpecifiedRelations(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE,
                        RelationDirection.TO);
        if (relationList == null || relationList.size() == 0) {
            log.debug("this is a root node: {}", nodeInfoBean.getID());
            return;
        }
        Relationable parentRelationable = relationList.get(0).getFromRelationable();
        getAncestryNodes(treeDefId, parentRelationable, nodeInfoBean, nodeInfoBeanMap);

    }

    /**
     * root节点
     *
     * @param treeDefId
     * @param isCountChild
     * @param modelCore
     * @return
     * @throws CimDataEngineRuntimeException
     */
    public List<NodeInfoBean> listRootNodes(String treeDefId, Boolean isCountChild,
                                            CIMModelCore modelCore) throws CimDataEngineRuntimeException {
        List<NodeInfoBean> childNodes = new ArrayList<>();

        FilteringItem filteringItem = new EqualFilteringItem(TreeNodeBaseInfo.LEVEL, TreeNodeBaseInfo.ROOT_LEVEL);
        ExploreParameters ep = new ExploreParameters();
        ep.setDefaultFilteringItem(filteringItem);
        ep.setType(treeDefId);
        CimDataSpace cds = modelCore.getCimDataSpace();
        if (log.isDebugEnabled()) {
            List<Fact> factList = null;
            try {
                factList = cds.getInformationExplorer().discoverInheritFacts(ep);
            } catch (CimDataEngineInfoExploreException e) {
                e.printStackTrace();
            }
            log.debug("===infoObject size: {}", factList.size());
        }

        InfoObjectDef objectDef = modelCore.getInfoObjectDef(treeDefId);
        InfoObjectRetrieveResult retrieveResult = objectDef.getObjects(ep);
        List<InfoObject> infoObjectList = retrieveResult.getInfoObjects();
        log.debug("===infoObject size: {}", infoObjectList.size());
        if (infoObjectList != null && infoObjectList.size() > 0) {
            for (InfoObject object : infoObjectList) {
                NodeInfoBean infoBean = new NodeInfoBean();

                addAttachedInfo(cds, objectDef, object, infoBean);


                Fact rlab = cds.getFactById(object.getObjectInstanceRID());
                TreeServiceUtil.relationableToNodeInfo(rlab, infoBean);
                if (StringUtils.isNotBlank(infoBean.getID()) && StringUtils.isNotBlank(infoBean.getNAME())) {
                    infoBean.setTreeDefId(treeDefId);
                    if (isCountChild) {
                        infoBean.setChildCount(
                                rlab.getAllSpecifiedRelations(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE,
                                        RelationDirection.FROM).size());
                    }
                    childNodes.add(infoBean);
                } else {
                    log.error("tree node info ID and NAME are mandatary");
                }
            }
        }

        return childNodes;
    }


    /**
     * 新增节点信息
     *
     * @param cds
     * @param tenantId
     * @param parentNode
     * @param childNode
     * @param metadata
     * @return
     * @throws DataServiceUserException
     * @throws CimDataEngineRuntimeException
     */
    public static String addNodeInfo(CimDataSpace cds, String tenantId, NodeInfoBean parentNode,
                                     NodeInfoBean childNode, Map<String, Object> metadata)
            throws DataServiceUserException, CimDataEngineRuntimeException {

        int level = -1;
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        modelCore.setCimDataSpace(cds);

        InfoObjectDef objectDef = modelCore.getInfoObjectDef(childNode.getTreeDefId());
        if (objectDef == null) {
            log.error("object type of {} not found", childNode.getTreeDefId());
            return null;
        }
        // get parent fact and level
        Fact parentFact = null;
        if (parentNode == null || StringUtils.isBlank(parentNode.getID())) {
            log.debug("add root node");
            level = 0; //root level is 0
        } else {
            log.debug("add child node");
            if (!parentNode.getTreeDefId().equals(childNode.getTreeDefId())) {
                log.error("parent node and child node must be same object type");
                return null;
            } else {
                FilteringItem filteringItem = new EqualFilteringItem(TreeNodeBaseInfo.ID, parentNode.getID());
                ExploreParameters ep = new ExploreParameters();
                ep.setDefaultFilteringItem(filteringItem);

                InfoObjectRetrieveResult retrieveResult = objectDef.getObjects(ep);
                if (retrieveResult.getInfoObjects() == null || retrieveResult.getInfoObjects().size() < 1) {
                    log.error("parent node of id={} not found", parentNode.getID());
                    return null;
                } else {
                    List<InfoObject> infoObjectList = retrieveResult.getInfoObjects();
                    InfoObject parentInfoObject = infoObjectList.get(0);
                    parentFact = cds.getFactById(parentInfoObject.getObjectInstanceRID());
                    if (parentFact.hasProperty(TreeNodeBaseInfo.LEVEL)) {
                        level = (int) (parentFact.getProperty(TreeNodeBaseInfo.LEVEL).getPropertyValue()) + 1;
                    } else {
                        log.error("level property of {} not found", parentInfoObject.getObjectInstanceRID());
                    }
                }
            }
        }

        //add node info
        InfoObjectValue objectValue = new InfoObjectValue();
        Map<String, Object> baseValues = new HashMap<>();
        baseValues.put(TreeNodeBaseInfo.LEVEL, level);
        // baseValues.put(TreeNodeBaseInfo.ID, childNode.getID());
        baseValues.put(TreeNodeBaseInfo.NAME, childNode.getNAME());
        baseValues.put(TreeNodeBaseInfo.IDX, new Date().getTime());
        baseValues.put(TreeNodeBaseInfo.NODE_TYPE, childNode.getNodeType());
        baseValues.put(TreeNodeBaseInfo.RELATION_TYPE, childNode.getRelationType());
        baseValues.put(TreeNodeBaseInfo.REF_OBJECT_TYPE, childNode.getRefObjType());
        baseValues.put(TreeNodeBaseInfo.REF_CIM_ID, childNode.getRefCimId());
        baseValues.put(TreeNodeBaseInfo.INDUSTRY_RID, childNode.getRefIndustryRid());
        baseValues.put(TreeNodeBaseInfo.OBJECT_TYPE_RID, childNode.getRefObjectTypeRid());
        baseValues.put(TreeNodeBaseInfo.DATASET_RID, childNode.getRefDatasetRid());
        baseValues.put(TreeNodeBaseInfo.RELATIONSHIP_RID, childNode.getRefRelationShipRid());
        baseValues.put(TreeNodeBaseInfo.FILTER, childNode.getFilter());
        baseValues.put(TreeNodeBaseInfo.CREATE_TIME, new Date());
        baseValues.put(TreeNodeBaseInfo.UPDATE_TIME, new Date());

        objectValue.setBaseDatasetPropertiesValue(baseValues);
        objectValue.setGeneralDatasetsPropertiesValue(metadata);
        InfoObject childInfoObject = objectDef.newObject(objectValue, false);
        //add node relation
        if (parentFact != null) {
            Fact childFact = cds.getFactById(childInfoObject.getObjectInstanceRID());
            Relation relation = parentFact.addToRelation(childFact,
                    BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE);
            if (relation == null) {
                log.error("add node relation failed");
                return null;
            }
        }

        return childInfoObject.getObjectInstanceRID();
    }

    public String addNodeInfoWithDataPermission(CimDataSpace cds, String tenantId, String userId,
                                                NodeInfoBean parentNode, NodeInfoBean childNode,
                                                Map<String, Object> metadata) throws DataServiceUserException,
            CimDataEngineRuntimeException {
        int level = -1;
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        modelCore.setCimDataSpace(cds);

        InfoObjectDef objectDef = modelCore.getInfoObjectDef(childNode.getTreeDefId());
        if (objectDef == null) {
            log.error("object type of {} not found", childNode.getTreeDefId());
            return null;
        }
        // get parent fact and level
        Fact parentFact = null;
        if (parentNode == null || StringUtils.isBlank(parentNode.getID())) {
            log.debug("add root node");
            level = 0; //root level is 0
        } else {
            log.debug("add child node");
            if (!parentNode.getTreeDefId().equals(childNode.getTreeDefId())) {
                log.error("parent node and child node must be same object type");
                return null;
            } else {
                FilteringItem filteringItem = new EqualFilteringItem(TreeNodeBaseInfo.ID, parentNode.getID());
                ExploreParameters ep = new ExploreParameters();
                ep.setDefaultFilteringItem(filteringItem);

                InfoObjectRetrieveResult retrieveResult = objectDef.getObjects(ep);
                if (retrieveResult.getInfoObjects() == null || retrieveResult.getInfoObjects().size() < 1) {
                    log.error("parent node of id={} not found", parentNode.getID());
                    return null;
                } else {
                    List<InfoObject> infoObjectList = retrieveResult.getInfoObjects();
                    InfoObject parentInfoObject = infoObjectList.get(0);
                    parentFact = cds.getFactById(parentInfoObject.getObjectInstanceRID());
                    if (parentFact.hasProperty(TreeNodeBaseInfo.LEVEL)) {
                        level = (int) (parentFact.getProperty(TreeNodeBaseInfo.LEVEL).getPropertyValue()) + 1;
                    } else {
                        log.error("level property of {} not found", parentInfoObject.getObjectInstanceRID());
                    }
                }
            }
        }

        //add node info
        InfoObjectValue objectValue = new InfoObjectValue();
        Map<String, Object> baseValues = new HashMap<>();
        baseValues.put(TreeNodeBaseInfo.LEVEL, level);
        // baseValues.put(TreeNodeBaseInfo.ID, childNode.getID());
        baseValues.put(TreeNodeBaseInfo.NAME, childNode.getNAME());
        baseValues.put(TreeNodeBaseInfo.IDX, new Date().getTime());
        baseValues.put(TreeNodeBaseInfo.NODE_TYPE, childNode.getNodeType());
        baseValues.put(TreeNodeBaseInfo.RELATION_TYPE, childNode.getRelationType());
        baseValues.put(TreeNodeBaseInfo.REF_OBJECT_TYPE, childNode.getRefObjType());
        baseValues.put(TreeNodeBaseInfo.REF_CIM_ID, childNode.getRefCimId());
        baseValues.put(TreeNodeBaseInfo.OBJECT_TYPE_RID, childNode.getRefObjectTypeRid());
        baseValues.put(TreeNodeBaseInfo.DATASET_RID, childNode.getRefDatasetRid());
        baseValues.put(TreeNodeBaseInfo.RELATIONSHIP_RID, childNode.getRefRelationShipRid());
        baseValues.put(TreeNodeBaseInfo.FILTER, childNode.getFilter());
        baseValues.put(TreeNodeBaseInfo.CREATE_TIME, new Date());
        baseValues.put(TreeNodeBaseInfo.UPDATE_TIME, new Date());

        objectValue.setBaseDatasetPropertiesValue(baseValues);
        objectValue.setGeneralDatasetsPropertiesValue(metadata);
        InfoObject childInfoObject = objectDef.newObject(objectValue, false);
        dataPermissionService.addDataPermissionByUser(cds, userId,
                Arrays.asList(childInfoObject.getObjectInstanceRID()));
        //add node relation
        if (parentFact != null) {
            Fact childFact = cds.getFactById(childInfoObject.getObjectInstanceRID());
            Relation relation = parentFact.addToRelation(childFact,
                    BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE);
            if (relation == null) {
                log.error("add node relation failed");
                return null;
            }
        }

        return childInfoObject.getObjectInstanceRID();
    }

    public String addNodeInfoWithoutDataPermission(CimDataSpace cds, String tenantId,
                                                   Fact parentNodeFact, NodeInfoBean childNode,
                                                   Map<String, Object> metadata) throws DataServiceUserException,
            CimDataEngineRuntimeException {
        int level;
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        modelCore.setCimDataSpace(cds);

        InfoObjectDef objectDef = modelCore.getInfoObjectDef(childNode.getTreeDefId());
        if (objectDef == null) {
            log.error("object type of {} not found", childNode.getTreeDefId());
            return null;
        }
        if (parentNodeFact == null) {
            log.debug("add root node");
            level = 0; //root level is 0
        } else {
            log.debug("add child node");
            level = (int) (parentNodeFact.getProperty(TreeNodeBaseInfo.LEVEL).getPropertyValue()) + 1;
        }

        //add node info
        InfoObjectValue objectValue = new InfoObjectValue();
        Map<String, Object> baseValues = new HashMap<>();
        baseValues.put(TreeNodeBaseInfo.LEVEL, level);
        // baseValues.put(TreeNodeBaseInfo.ID, childNode.getID());
        baseValues.put(TreeNodeBaseInfo.NAME, childNode.getNAME());
        baseValues.put(TreeNodeBaseInfo.IDX, new Date().getTime());
        baseValues.put(TreeNodeBaseInfo.NODE_TYPE, childNode.getNodeType());
        baseValues.put(TreeNodeBaseInfo.RELATION_TYPE, childNode.getRelationType());
        baseValues.put(TreeNodeBaseInfo.REF_OBJECT_TYPE, childNode.getRefObjType());
        baseValues.put(TreeNodeBaseInfo.REF_CIM_ID, childNode.getRefCimId());
        baseValues.put(TreeNodeBaseInfo.OBJECT_TYPE_RID, childNode.getRefObjectTypeRid());
        baseValues.put(TreeNodeBaseInfo.DATASET_RID, childNode.getRefDatasetRid());
        baseValues.put(TreeNodeBaseInfo.RELATIONSHIP_RID, childNode.getRefRelationShipRid());
        baseValues.put(TreeNodeBaseInfo.FILTER, childNode.getFilter());
        baseValues.put(TreeNodeBaseInfo.CREATE_TIME, new Date());
        baseValues.put(TreeNodeBaseInfo.UPDATE_TIME, new Date());

        objectValue.setBaseDatasetPropertiesValue(baseValues);
        objectValue.setGeneralDatasetsPropertiesValue(metadata);
        InfoObject childInfoObject = objectDef.newObject(objectValue, false);
        // dataPermissionService.addDataPermissionByUser(cds, userId,
        //         Arrays.asList(childInfoObject.getObjectInstanceRID()));
        //add node relation
        if (parentNodeFact != null) {
            Fact childFact = cds.getFactById(childInfoObject.getObjectInstanceRID());
            Relation relation = parentNodeFact.addToRelation(childFact,
                    BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE);
            if (relation == null) {
                log.error("add node relation failed");
                return null;
            }
        }

        return childInfoObject.getObjectInstanceRID();
    }

    /**
     * 统计第一级节点或指定节点的子节点的数量
     *
     * @param tenantId
     * @param treeDefId
     * @param parentNode
     * @return
     */
    public List<ChildNodeCountBean> childNodeCount(String tenantId, String treeDefId, NodeInfoBean parentNode) {
        List<ChildNodeCountBean> countBeanList = new ArrayList<>();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            if (parentNode != null && StringUtils.isNotBlank(parentNode.getID())) {
                ChildNodeCountBean countBean = new ChildNodeCountBean(parentNode.getID(), parentNode.getNAME());
                countBeanList.add(countBean);
                childNodeCount(treeDefId, parentNode, countBean, modelCore);
            } else {
                log.info("first level nodes' child node count");
                List<NodeInfoBean> rootNodeInfo = listRootNodes(treeDefId, false, modelCore);
                if (rootNodeInfo != null) {
                    for (NodeInfoBean infoBean : rootNodeInfo) {
                        ChildNodeCountBean countBean = new ChildNodeCountBean(infoBean.getID(), infoBean.getNAME());
                        countBeanList.add(countBean);
                        childNodeCount(treeDefId, infoBean, countBean, modelCore);
                    }
                }
            }
        } catch (CimDataEngineRuntimeException e) {
            log.error("list root of " + treeDefId + " failed", e);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return countBeanList;
    }

    /**
     * 递归统计子标签数量
     *
     * @param treeDefId
     * @param parentNode
     * @param childNodeCountBean
     * @param modelCore
     * @return
     */
    public void childNodeCount(String treeDefId, NodeInfoBean parentNode, ChildNodeCountBean childNodeCountBean,
                               CIMModelCore modelCore) {
        try {
            List<NodeInfoBean> nodeInfoBeans = listChildNodes(treeDefId, parentNode, false, modelCore);
            if (nodeInfoBeans != null && nodeInfoBeans.size() > 0) {
                for (NodeInfoBean infoBean : nodeInfoBeans) {
                    if (infoBean.getNodeType().equals(NodeInfoBean.NodeType.FILE)) {
                        childNodeCountBean.addFiles(1);
                    } else {
                        childNodeCountBean.addOthers(1);
                    }
                    childNodeCount(treeDefId, infoBean, childNodeCountBean, modelCore);
                }
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增行业分类节点
     *
     * @param tenantId
     * @param userId
     * @param treeDefId
     * @param nodeInfo
     * @return 行业分类
     * @throws DataServiceModelRuntimeException
     * @throws DataServiceUserException
     * @throws CimDataEngineRuntimeException
     */
    public String addIndustryNode(String tenantId, String userId, String treeDefId, IndustryNodeAddInputBean nodeInfo,
                                  boolean addDefaultPermission) throws DataServiceModelRuntimeException,
            DataServiceUserException, CimDataEngineRuntimeException, CimDataEngineInfoExploreException {//NOSONAR
        Assert.notNull(nodeInfo, "node info is null");
        Map<String, Object> metadataValues = nodeInfo.getMetadata();
        Assert.notNull(metadataValues.get(CimConstants.IndustryTypeNodeKeys.INDUSTRY_ID), "industryId is mandatory");
        Assert.notNull(metadataValues.get(CimConstants.IndustryTypeNodeKeys.INDUSTRY_NAME), "industryName is " +
                "mandatory");

        IndustryTypeVO industryTypeVO = new IndustryTypeVO();
        industryTypeVO.setIndustryTypeName(
                metadataValues.get(CimConstants.IndustryTypeNodeKeys.INDUSTRY_ID).toString());
        industryTypeVO.setIndustryTypeDesc(
                metadataValues.get(CimConstants.IndustryTypeNodeKeys.INDUSTRY_NAME).toString());
        industryTypeVO.setCreatorId(userId);
        CimDataSpace cds = null;
        try {
            NodeInfoBean parentNodeInfo = nodeInfo.getParentNodeInfo();
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            if (parentNodeInfo != null && StringUtils.isNotBlank(
                    parentNodeInfo.getID()) && parentNodeInfo.getNodeType().equals(NodeInfoBean.NodeType.INDUSTRY)) {
                FilteringItem filteringItem = new EqualFilteringItem(TreeNodeBaseInfo.ID, parentNodeInfo.getID());
                ExploreParameters ep = new ExploreParameters();
                ep.setType(treeDefId);
                ep.setDefaultFilteringItem(filteringItem);

                List<Fact> parentNodeFactList = cds.getInformationExplorer().discoverInheritFacts(ep);
                Assert.notEmpty(parentNodeFactList, "parent node not found");
                Fact fact = parentNodeFactList.get(0);
                List<Relation> relationList =
                        fact.getAllSpecifiedRelations(BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE,
                                RelationDirection.FROM);
                if (!relationList.isEmpty()) {
//					Assert.notEmpty(relationList, "related industry not found");
                    industryTypeVO.setParentIndustryTypeId(relationList.get(0).getToRelationable().getId());
                }
            }
            IndustryType industryType;
            IndustryTypes industryTypes = modelCore.getIndustryTypes();
            metadataValues.put(GeneralProperties.CREATOR_ID, userId);
            metadataValues.put(GeneralProperties.UPDATOR_ID, userId);
            metadataValues.put(GeneralProperties.CREATE_TIME, new Date());
            metadataValues.put(GeneralProperties.UPDATE_TIME, new Date());
            String parentIndustryTypeId = industryTypeVO.getParentIndustryTypeId();
            //新增分类
            if (StringUtils.isBlank(parentIndustryTypeId)) {
                //重命名
                industryType = industryTypes.addRootIndustryType(industryTypeVO);
            } else {
                //重命名
                industryType = industryTypes.addChildIndustryType(industryTypeVO, parentIndustryTypeId);
            }

            if (industryType != null) {
                NodeInfoBean childInfoBean = new NodeInfoBean(treeDefId);
                childInfoBean.setNAME(metadataValues.get(CimConstants.IndustryTypeNodeKeys.INDUSTRY_NAME).toString());
                childInfoBean.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
                childInfoBean.setRefIndustryRid(industryType.getIndustryTypeRID());
                String rid;
                //only data manager tree should add data permission
                if (addDefaultPermission) {
                    rid = addNodeInfoWithDataPermission(cds, tenantId, userId, parentNodeInfo, childInfoBean,
                            metadataValues);
                } else {
                    rid = addNodeInfo(cds, tenantId, parentNodeInfo, childInfoBean, metadataValues);
                }
                Fact fact = cds.getFactById(rid);
                fact.addToRelation(cds.getDimensionById(industryType.getIndustryTypeRID()),
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
                return industryType.getIndustryTypeRID();
            } else {
                return null;
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }


    /**
     * 新增实例节点
     *
     * @param tenantId
     * @param userId
     * @param treeDefId
     * @param objectTypeId
     * @param nodeInfo
     * @return
     * @throws DataServiceUserException
     * @throws CimDataEngineRuntimeException
     */
    public String addInstanceNode(String tenantId, String userId, String treeDefId, String objectTypeId,
                                  IndustryNodeAddInputBean nodeInfo, boolean addDefaultPermission)
            throws DataServiceUserException, CimDataEngineRuntimeException {//NOSONAR
        Assert.notNull(nodeInfo, "node info is null");
        Map<String, Object> metadataValues = nodeInfo.getMetadata();
        Assert.notNull(metadataValues.get(GeneralProperties.NAME), "instance name is mandatory");
        CimDataSpace cds = null;
        try {
            NodeInfoBean parentNode = nodeInfo.getParentNodeInfo();
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef objectDef = modelCore.getInfoObjectDef(objectTypeId);
            Assert.notNull(objectDef, "object type of " + objectTypeId + " not found");

            Map<String, Object> baseValues = new HashMap<>();
            if (!metadataValues.containsKey(GeneralProperties.ID)) {
                String cimId = CommonOperationUtil.generateObjectInstanceUUID(objectTypeId);
                baseValues.put(GeneralProperties.ID, cimId);
                metadataValues.put(TreeNodeBaseInfo.REF_CIM_ID, cimId);
            } else {
                metadataValues.put(TreeNodeBaseInfo.REF_CIM_ID, metadataValues.get(GeneralProperties.ID));
            }
            metadataValues.put(TreeNodeBaseInfo.REF_OBJECT_TYPE, objectTypeId);
            metadataValues.put(GeneralProperties.CREATOR_ID, userId);
            metadataValues.put(GeneralProperties.UPDATOR_ID, userId);
            metadataValues.put(GeneralProperties.CREATE_TIME, new Date());
            metadataValues.put(GeneralProperties.UPDATE_TIME, new Date());
            InfoObjectValue objectValue = new InfoObjectValue();
            objectValue.setBaseDatasetPropertiesValue(baseValues);
            objectValue.setGeneralDatasetsPropertiesValue(metadataValues);

            //新增实例
            InfoObject infoObject = objectDef.newObject(objectValue, false);
            if (infoObject != null) {
                NodeInfoBean childInfoBean = new NodeInfoBean(treeDefId);
                childInfoBean.setNAME(metadataValues.get(GeneralProperties.NAME).toString());
                childInfoBean.setNodeType(NodeInfoBean.NodeType.INSTANCE);
                String rid;
                //only data manager tree should add data permission
                if (addDefaultPermission) {
                    rid = addNodeInfoWithDataPermission(cds, tenantId, userId, parentNode, childInfoBean,
                            metadataValues);
                } else {
                    rid = addNodeInfo(cds, tenantId, parentNode, childInfoBean, metadataValues);
                }
                Fact fact = cds.getFactById(rid);
                fact.addToRelation(cds.getFactById(infoObject.getObjectInstanceRID()),
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
                return infoObject.getObjectInstanceRID();
            } else {
                return null;
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }

    public Boolean moveNode(String tenantId, String userId, @NotNull String treeDefId, Boolean isOverride,
                            Boolean moveFlag,
                            NodeMoveInputBean moveInfo) {
        Boolean flag = false;
        NodeInfoBean nodeInfo = moveInfo.getNodeInfo();
        NodeInfoBean beforeNodeInfo = moveInfo.getBeforeNodeInfo();
        NodeInfoBean afterNodeInfo = moveInfo.getAfterNodeInfo();
        NodeInfoBean parentNodeInfo = moveInfo.getParentNodeInfo();

        Assert.notNull(nodeInfo, "node info is mandatory");
        String acrossTreeDefId = nodeInfo.getTreeDefId();

        String parentId = null;
        Integer level = 0;

        if (parentNodeInfo != null && StringUtils.isNotBlank(parentNodeInfo.getID())) {
            parentId = parentNodeInfo.getID();
            level = parentNodeInfo.getLevel() + 1;
        } else {
            log.info("move to root level");
        }

        Double idx = Double.valueOf(System.currentTimeMillis());
        if (beforeNodeInfo != null && StringUtils.isNotBlank(beforeNodeInfo.getID())) {
            if (afterNodeInfo != null && StringUtils.isNotBlank(afterNodeInfo.getID())) {
                idx = (beforeNodeInfo.getIdx() + afterNodeInfo.getIdx()) / 2D;
            } else {
                idx = beforeNodeInfo.getIdx() + 1;
            }
        } else if (afterNodeInfo != null && StringUtils.isNotBlank(afterNodeInfo.getID())) {
            idx = afterNodeInfo.getIdx() - 1;
        }


        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InformationExplorer ip = cds.getInformationExplorer();
            ExploreParameters ep = new ExploreParameters();
            ep.setType(treeDefId);

            FilteringItem filteringItem = new EqualFilteringItem(GeneralProperties.ID, nodeInfo.getID());
            ep.setDefaultFilteringItem(filteringItem);

            List<Fact> factList = ip.discoverInheritFacts(ep);


            if (factList != null && factList.size() > 0) {
                Fact fact = factList.get(0);

                //跨树的移动或复制
                if (nodeInfo != null && StringUtils.isNotBlank(acrossTreeDefId)) {
                    if (!acrossTreeDefId.equals(treeDefId)) {
                        InfoObjectDefs infoObjectDefs = modelCore.getInfoObjectDefs();
                        //目标对象
                        InfoObjectDef targetTreeDef = infoObjectDefs.getInfoObjectDef(acrossTreeDefId);
                        //源对象
                        InfoObjectDef sourceTreeDef = infoObjectDefs.getInfoObjectDef(fact.getType());
                        ExploreParameters sourceEp = new ExploreParameters();

                        FilteringItem sourceFilteringItem = new EqualFilteringItem(CimConstants.GeneralProperties.ID, nodeInfo.getID());
                        sourceEp.setDefaultFilteringItem(sourceFilteringItem);

                        InfoObjectRetrieveResult objects = sourceTreeDef.getObjects(sourceEp);
                        InfoObject infoObject = objects.getInfoObjects().get(0);

                        recursive(cds, userId, infoObject, targetTreeDef);

                        if (moveFlag) {
                            recursiveDelete(cds, infoObject);
                        }
                    }

                }


                fact.addNewOrUpdateProperties(Collections.singletonMap(TreeNodeBaseInfo.IDX, idx));
                if (!StringUtils.equals(nodeInfo.getParentId(), parentId)) {
                    log.info("update node level");
                    List<Relation> relationList =
                            fact.getAllSpecifiedRelations(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE,
                                    RelationDirection.TO);

                    if (relationList != null && relationList.size() > 0) {
                        Relation relation = relationList.get(0);
                        log.debug("remove relation of {}", relation.getId());
                        cds.removeRelation(relation.getId());
                    }
                    if (StringUtils.isNotBlank(parentId)) {
                        filteringItem = new EqualFilteringItem(TreeNodeBaseInfo.ID, parentId);
                        ep.setDefaultFilteringItem(filteringItem);
                        factList = ip.discoverInheritFacts(ep);
                        if (factList != null && factList.size() > 0) {
                            Fact parentFact = factList.get(0);
                            updateDisplayNameByMove(cds, parentFact, fact, isOverride);
                            fact.addFromRelation(parentFact,
                                    BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE);
                        } else {
                            log.error("fact type of {}, ID={} not found", treeDefId, parentId);
                        }
                    }
                    log.info("move to root level");
                    fact.updateProperty(TreeNodeBaseInfo.LEVEL, level);
                    flag = true;
                } else {
                    log.info("only update idx");
                    flag = true;
                }
            } else {
                log.error("fact type of {}, ID={} not found", treeDefId, nodeInfo.getID());
            }
        } catch (Exception e) {
            log.error("node move failed", e);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return flag;
    }

    public List<NodeDeleteOutputBean> deleteNodesBatch(String tenantId, String userId, String treeDefId,
                                                       List<NodeInfoBean> nodeInfoList) {
        boolean flag;
        Assert.hasText(tenantId, "tenant id is mandatory");
        Assert.hasText(userId, "user id is mandatory");
        Assert.hasText(treeDefId, "treeDefId is mandatory");
        Assert.notEmpty(nodeInfoList, "node is mandatory");

        List<NodeDeleteOutputBean> nodeDeleteOutputBeans = new ArrayList<>();

        Map<String, NodeDeleteOutputBean> outputBeanMap = new HashMap<>();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef objectDef = modelCore.getInfoObjectDef(treeDefId);
            Assert.notNull(objectDef, "object type of " + treeDefId + " not found");
            List<Object> nodeIds = new ArrayList<>();
            for (NodeInfoBean nodeInfoBean : nodeInfoList) {
                nodeIds.add(nodeInfoBean.getID());
                NodeDeleteOutputBean deleteOutputBean = new NodeDeleteOutputBean(nodeInfoBean.getID(), false);
                outputBeanMap.put(nodeInfoBean.getID(), deleteOutputBean);
                nodeDeleteOutputBeans.add(deleteOutputBean);
            }
            log.debug("node ids [{}]", nodeIds);
            // FilteringItem filteringItem = new EqualFilteringItem(GeneralProperties.ID, nodeInfo.getID());
            FilteringItem filteringItem = new InValueFilteringItem(GeneralProperties.ID, nodeIds);
            ExploreParameters ep = new ExploreParameters();
            ep.setType(treeDefId);
            ep.setDefaultFilteringItem(filteringItem);

            InfoObjectRetrieveResult retrieveResult = objectDef.getObjects(ep);
            List<InfoObject> objectList = retrieveResult.getInfoObjects();
            log.debug("retrival fact size [{}]", objectList.size());
            if (objectList != null && objectList.size() > 0) {
                for (InfoObject infoObject : objectList) {
                    try {
                        Map<String, Object> baseInfo = infoObject.getInfo();
                        NodeDeleteOutputBean deleteOutputBean = outputBeanMap.get(baseInfo.get(GeneralProperties.ID));
                        flag = cds.removeFact(infoObject.getObjectInstanceRID());
                        deleteOutputBean.setDelete(flag);
                    } catch (CimDataEngineRuntimeException | DataServiceModelRuntimeException e) {
                        log.error("remove fact failed", e);
                    }
                }
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return nodeDeleteOutputBeans;
    }

    /**
     * 更新节点显示名称.
     *
     * @param tenantId
     * @param userId
     * @param treeDefId
     * @param override
     * @param nodeName
     * @param nodeInfo
     * @return
     */
    public Boolean updateDisplayName(String tenantId, String userId, String treeDefId, Boolean override,
                                     String nodeName, NodeInfoBean nodeInfo) {
        boolean flag = false;
        Assert.hasText(tenantId, "tenant id is mandatory");
        Assert.hasText(userId, "user id is mandatory");
        Assert.hasText(treeDefId, "treeDefId is mandatory");
        Assert.hasText(nodeName, "node name is mandatory");
        Assert.hasText(nodeInfo.getID(), "node id is mandatory");

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef objectDef = modelCore.getInfoObjectDef(treeDefId);
            Assert.notNull(objectDef, "object type of " + treeDefId + " not found");
            FilteringItem filteringItem = new EqualFilteringItem(GeneralProperties.ID, nodeInfo.getID());
            ExploreParameters ep = new ExploreParameters();
            ep.setType(treeDefId);
            ep.setDefaultFilteringItem(filteringItem);

            InfoObjectRetrieveResult retrieveResult = objectDef.getObjects(ep);
            List<InfoObject> objectList = retrieveResult.getInfoObjects();
            if (objectList != null && objectList.size() > 0) {
                InfoObject infoObject = objectList.get(0);
                flag = infoObject.addOrUpdateObjectProperty(GeneralProperties.NAME, nodeName);
            }
        } catch (DataServiceModelRuntimeException e) {
            log.error("update node name failed", e);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return flag;
    }

    /**
     * 更新节点元数据，ID和NAME不可更新.
     *
     * @param tenantId
     * @param userId
     * @param treeDefId
     * @param nodeMetadataUpdateInput
     * @return
     */
    public Boolean updateMetadataInfo(String tenantId, String userId, String treeDefId,
                                      NodeMetadataUpdateInputBean nodeMetadataUpdateInput) {
        boolean flag = false;
        Assert.hasText(tenantId, "tenant id is mandatory");
        Assert.hasText(userId, "user id is mandatory");
        Assert.hasText(treeDefId, "treeDefId is mandatory");
        Assert.notNull(nodeMetadataUpdateInput, "node metadata info is null");

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef objectDef = modelCore.getInfoObjectDef(treeDefId);
            Assert.notNull(objectDef, "object type of " + treeDefId + " not found");

            NodeInfoBean nodeInfo = nodeMetadataUpdateInput.getNodeInfo();
            Assert.notNull(nodeInfo, "node info is null");

            Map<String, Object> values = new HashMap<>();
            Map<String, Object> metadata = nodeMetadataUpdateInput.getMetadata();
            for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                if (entry.getKey().equals(GeneralProperties.ID)) {
                    log.info("update ID is omited");
                    continue;
                }
                if (entry.getKey().equals(GeneralProperties.NAME)) {
                    log.info("update NAME is omited");
                    continue;
                }
                values.put(entry.getKey(), entry.getValue());
            }
            //更新人和更新时间
            values.put(CimConstants.UPDATE_TIME, new Date());
            values.put(CimConstants.UPDATOR, userId);

            FilteringItem filteringItem = new EqualFilteringItem(GeneralProperties.ID, nodeInfo.getID());
            ExploreParameters ep = new ExploreParameters();
            ep.setType(treeDefId);
            ep.setDefaultFilteringItem(filteringItem);

            Map<String, Map<String, Object>> updateInput = new HashMap<>();
            List<DatasetDef> datasetDefList = objectDef.getDatasetDefs();
            if (datasetDefList != null && datasetDefList.size() > 0) {
                for (DatasetDef def : datasetDefList) {
                    //只更新元数据属性集
                    if (def.getDatasetName() != BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME) {
                        updateInput.put(def.getDatasetName(), values);
                    }
                }
            }

            if (updateInput.size() > 0) {
                InfoObjectRetrieveResult retrieveResult = objectDef.getObjects(ep);
                List<InfoObject> objectList = retrieveResult.getInfoObjects();
                if (objectList != null && objectList.size() > 0) {
                    InfoObject infoObject = objectList.get(0);
                    flag = infoObject.updateObjectPropertiesByDatasets(updateInput);
                } else {
                    log.error("treeDefId={}, node of {} not found", treeDefId, nodeInfo.getID());
                }
            } else {
                log.info("no metadata should be updated");
            }
        } catch (DataServiceModelRuntimeException e) {
            log.error("update node name failed", e);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return flag;
    }

    public NodeInfoBean getNodeInfoByRid(CimDataSpace cds, String rid, String treeDefId) {
        NodeInfoBean nodeInfoBean = new NodeInfoBean();
        try {
            Fact fact = cds.getFactById(rid);
            TreeServiceUtil.relationableToNodeInfo(fact, nodeInfoBean);
            nodeInfoBean.setTreeDefId(treeDefId);
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }
        return nodeInfoBean;
    }


    /**
     * 查询子树，未根据权限过滤
     *
     * @param treeObjDef
     * @param treeDefId
     * @param parentNode
     * @param isCountChild
     * @param modelCore
     * @param loopCount
     * @return
     */
    public List<NodeInfoBean> listChildNodesRecursivelyWithoutPermission(InfoObjectDef treeObjDef, String treeDefId,
                                                                         NodeInfoBean parentNode, Boolean isCountChild,
                                                                         CIMModelCore modelCore, Integer loopCount) {
        List<NodeInfoBean> nodeInfoBeanList = new ArrayList<>();
        if (loopCount > 0) {
            int tmpLoop = --loopCount;
            try {
                if (parentNode == null || StringUtils.isBlank(parentNode.getID())) {
                    nodeInfoBeanList = listRootNodes(treeDefId, false, modelCore);
                } else {
                    nodeInfoBeanList = listChildNodes(treeObjDef, treeDefId, parentNode, isCountChild, modelCore);
                }
                for (NodeInfoBean infoBean : nodeInfoBeanList) {
                    infoBean.setChildNodes(listChildNodesRecursivelyWithoutPermission(treeObjDef, treeDefId, infoBean
                            , isCountChild, modelCore, tmpLoop));
                }
            } catch (CimDataEngineRuntimeException e) {
                e.printStackTrace();
            }
        }
        return nodeInfoBeanList;
    }

    public boolean deleteNodesResursivelyByRid(String tenantId, String userId, String treeDefId,
                                               String nodeRid) {
        boolean flag = false;
        Assert.hasText(tenantId, "tenant id is mandatory");
        Assert.hasText(userId, "user id is mandatory");
        Assert.hasText(treeDefId, "treeDefId is mandatory");
        Assert.hasText(nodeRid, "node rid is mandatory");
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            Fact nodeFact = cds.getFactById(nodeRid);
            Assert.notNull(nodeFact, "node fact not found");
            if (!nodeFact.getType().equals(treeDefId)) {
                log.error("node type and treeDefId not mapping");
                return false;
            }
            List<Relation> relationList =
                    nodeFact.getAllSpecifiedRelations(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE,
                            RelationDirection.FROM);

            if (relationList != null && relationList.size() > 0) {
                for (Relation relation : relationList) {
                    Fact childFact = (Fact) relation.getToRelationable();
                    cds.removeRelation(relation.getId());
                    deleteNodesResursivelyByRid(tenantId, userId, treeDefId, childFact.getId());
                }
            }
            flag = cds.removeFact(nodeRid);
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return flag;
    }

    public void updateDisplayNameByMove(CimDataSpace cds, Fact parentFact, Fact childFact, Boolean isOverride)
            throws CimDataEngineRuntimeException {
        String childName = childFact.getProperty(GeneralProperties.NAME).getPropertyValue().toString();
        log.debug("original child node name: [{}]", childName);
        List<Relation> treeRelations =
                parentFact.getAllSpecifiedRelations(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE,
                        RelationDirection.FROM);

        //nodeName:relation
        Map<String, Relation> nameRelationMap = new HashMap<>();
        for (Relation relation : treeRelations) {
            Relationable toRelationable = relation.getToRelationable();
            if (toRelationable.hasProperty(GeneralProperties.ID) && toRelationable.hasProperty(
                    GeneralProperties.NAME)) {
                String tmpName = toRelationable.getProperty(GeneralProperties.NAME).getPropertyValue().toString();
                nameRelationMap.put(tmpName, relation);
            }
        }
        log.debug("parentNode=[{}]: childNodes=[{}]", parentFact.getId(), nameRelationMap);

        log.debug("udpate display name: isOverride [{}]", isOverride);
        if (isOverride) {
            if (nameRelationMap.containsKey(childName)) {
                Relation relation = nameRelationMap.get(childName);
                log.debug("remove old child node: [{}]", relation.getId());
                cds.removeRelation(relation.getId());
            }
        } else {
            //get new node name
            String tmpName = childName;
            for (int i = 0; i < 10000; i++) {
                if (nameRelationMap.containsKey(tmpName)) {
                    tmpName = childName + "(" + (i + 1) + ")";
                } else {
                    break;
                }
            }
            log.debug("new display name [{}]", tmpName);
            if (!tmpName.equals(childName)) {
                Map<String, Object> valMap = new HashMap<>();
                valMap.put(GeneralProperties.NAME, tmpName);
                childFact.addNewOrUpdateProperties(valMap);
            }
        }
    }


    public void setDataPermissionService(DataPermissionService dataPermissionService) {
        this.dataPermissionService = dataPermissionService;
    }

    /**
     * 新建租户时新增默认场景树
     *
     * @param tenantId
     * @param userId
     * @return
     * @throws CimDataEngineRuntimeException
     * @throws DataServiceModelRuntimeException
     * @throws DataServiceUserException
     */
    public String addDefaultSceneTreeNode(String tenantId, String userId) throws CimDataEngineRuntimeException, DataServiceModelRuntimeException, DataServiceUserException {
        SceneTreeNodeAddInputBean node = new SceneTreeNodeAddInputBean();
        node.setTreeName(TreeConstants.DEFAULT_SCENE_TREE);
        String rid = addSceneTreeNode(tenantId, userId, node);
        return rid;
    }


    /**
     * 新增场景树
     *
     * @param tenantId
     * @param userId
     * @param nodeInfo
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws DataServiceUserException
     * @throws CimDataEngineRuntimeException
     */
    public String addSceneTreeNode(String tenantId, String userId, SceneTreeNodeAddInputBean nodeInfo)
            throws DataServiceModelRuntimeException,
            DataServiceUserException, CimDataEngineRuntimeException {//NOSONAR
        Assert.notNull(nodeInfo, "node info is null");
        Map<String, Object> metadataValues = nodeInfo.getMetadata();
        Assert.notNull(nodeInfo.getTreeName(), "tree name is mandatory");
        String treeName = nodeInfo.getTreeName();
        String treeDefId = PinyinUtils.getPinYinWithoutSpecialChar(nodeInfo.getTreeName());
        CimDataSpace cds = null;
        try {
            TreeInitiationUtil.tenantId = tenantId;
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            if (cds.hasInheritFactType(treeDefId)) {
                treeDefId = treeDefId + "_" + DateUtil.getCurrentDateMs();
            }
            TreeInitiationUtil.projectKnowledgeBaseTreeObjectTypeName = treeDefId;
            TreeInitiationUtil.projectKnowledgeBaseTreeObjectTypeDesc = treeName;
            TreeInitiationUtil.projectKnowledgeBaseTreeBaseDataSet = treeDefId + "DataSet";
            TreeInitiationUtil.projectKnowledgeBaseTreeBaseDataSetDesc = treeName + "属性集";

//			IndustryTypeVO industryTypeVO = new IndustryTypeVO();
//			industryTypeVO.setIndustryTypeName(treeDefId);
//			industryTypeVO.setIndustryTypeDesc(treeName);
//			industryTypeVO.setCreatorId(userId);
            // CimDataSpace cds = null;
            // try {
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            //add tree object type
            log.info("add scene tree: tree type id = [{}], tree type name = [{}]", treeDefId, treeName);
            TreeInitiationUtil.initiation(CimConstants.defauleSpaceName, cds);
            //add tree definition fact
            InfoObjectDef treeDefinitionObjectDef =
                    modelCore.getInfoObjectDef(CimConstants.TreeDefinitionProperties.TREE_DEFINITION_OBJECT_TYPE);
            Map<String, Object> baseValues = new HashMap<>();
            baseValues.put(CimConstants.TreeDefinitionProperties.ID, treeDefId);
            baseValues.put(CimConstants.TreeDefinitionProperties.NAME, treeName);
            baseValues.put(CimConstants.TreeDefinitionProperties.TREE_NODE_OBJECT, treeDefId);
            baseValues.put(CimConstants.TreeDefinitionProperties.CREATE_TIME, new Date());
            baseValues.put(CimConstants.TreeDefinitionProperties.UPDATE_TIME, new Date());

            InfoObjectValue objectValue = new InfoObjectValue();
            objectValue.setBaseDatasetPropertiesValue(baseValues);
            InfoObject treeDefinirionInfoObject = treeDefinitionObjectDef.newObject(objectValue, false);
            //add industry
//			IndustryType industryType;
//			IndustryTypes industryTypes = modelCore.getIndustryTypes();
            if (metadataValues == null) {
                metadataValues = new HashMap<>();
            }
            metadataValues.put(GeneralProperties.CREATOR_ID, userId);
            metadataValues.put(GeneralProperties.UPDATOR_ID, userId);
            metadataValues.put(GeneralProperties.CREATE_TIME, new Date());
            metadataValues.put(GeneralProperties.UPDATE_TIME, new Date());
//			industryType = industryTypes.addRootIndustryType(industryTypeVO);

//			if (industryType != null) {
            NodeInfoBean childInfoBean = new NodeInfoBean(treeDefId);
            childInfoBean.setNAME(treeName);
            childInfoBean.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
            String rid = addNodeInfoWithDataPermission(cds, tenantId, userId, null, childInfoBean,
                    metadataValues);
            Fact nodeFact = cds.getFactById(rid);
//				nodeFact.addToRelation(cds.getDimensionById(industryType.getIndustryTypeRID()),
//						BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
            nodeFact.updateProperty(TreeNodeBaseInfo.LEVEL, -1);
            Fact treeDefinitionFact = cds.getFactById(treeDefinirionInfoObject.getObjectInstanceRID());
            treeDefinitionFact.addToRelation(nodeFact,
                    BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
//				return industryType.getIndustryTypeRID();
            return rid;
//			} else {
//				return null;
//			}
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }


    /**
     * 新增场景树不包含权限
     *
     * @param tenantId
     * @param userId
     * @param nodeInfo
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws DataServiceUserException
     * @throws CimDataEngineRuntimeException
     */
    public String addSceneTreeNodeWithoutPermission(String tenantId, String userId, SceneTreeNodeAddInputBean nodeInfo)
            throws DataServiceModelRuntimeException,
            DataServiceUserException, CimDataEngineRuntimeException {//NOSONAR
        Assert.notNull(nodeInfo, "node info is null");
        Map<String, Object> metadataValues = nodeInfo.getMetadata();
        Assert.notNull(nodeInfo.getTreeName(), "tree name is mandatory");
        String treeName = nodeInfo.getTreeName();
        String treeDefId = PinyinUtils.getPinYinWithoutSpecialChar(nodeInfo.getTreeName());
        CimDataSpace cds = null;
        try {
            TreeInitiationUtil.tenantId = tenantId;
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            if (cds.hasInheritFactType(treeDefId)) {
                treeDefId = treeDefId + "_" + DateUtil.getCurrentDateMs();
            }
            TreeInitiationUtil.projectKnowledgeBaseTreeObjectTypeName = treeDefId;
            TreeInitiationUtil.projectKnowledgeBaseTreeObjectTypeDesc = treeName;
            TreeInitiationUtil.projectKnowledgeBaseTreeBaseDataSet = treeDefId + "DataSet";
            TreeInitiationUtil.projectKnowledgeBaseTreeBaseDataSetDesc = treeName + "属性集";

//			IndustryTypeVO industryTypeVO = new IndustryTypeVO();
//			industryTypeVO.setIndustryTypeName(treeDefId);
//			industryTypeVO.setIndustryTypeDesc(treeName);
//			industryTypeVO.setCreatorId(userId);
            // CimDataSpace cds = null;
            // try {
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            //add tree object type
            log.info("add scene tree: tree type id = [{}], tree type name = [{}]", treeDefId, treeName);
            TreeInitiationUtil.initiation(CimConstants.defauleSpaceName, cds);
            //add tree definition fact
            InfoObjectDef treeDefinitionObjectDef =
                    modelCore.getInfoObjectDef(CimConstants.TreeDefinitionProperties.TREE_DEFINITION_OBJECT_TYPE);
            Map<String, Object> baseValues = new HashMap<>();
            baseValues.put(CimConstants.TreeDefinitionProperties.ID, treeDefId);
            baseValues.put(CimConstants.TreeDefinitionProperties.NAME, treeName);
            baseValues.put(CimConstants.TreeDefinitionProperties.TREE_NODE_OBJECT, treeDefId);
            baseValues.put(CimConstants.TreeDefinitionProperties.CREATE_TIME, new Date());
            baseValues.put(CimConstants.TreeDefinitionProperties.UPDATE_TIME, new Date());

            InfoObjectValue objectValue = new InfoObjectValue();
            objectValue.setBaseDatasetPropertiesValue(baseValues);
            InfoObject treeDefinirionInfoObject = treeDefinitionObjectDef.newObject(objectValue, false);
            //add industry
//			IndustryType industryType;
//			IndustryTypes industryTypes = modelCore.getIndustryTypes();
            if (metadataValues == null) {
                metadataValues = new HashMap<>();
            }
            metadataValues.put(GeneralProperties.CREATOR_ID, userId);
            metadataValues.put(GeneralProperties.UPDATOR_ID, userId);
            metadataValues.put(GeneralProperties.CREATE_TIME, new Date());
            metadataValues.put(GeneralProperties.UPDATE_TIME, new Date());
//			industryType = industryTypes.addRootIndustryType(industryTypeVO);

//			if (industryType != null) {
            NodeInfoBean childInfoBean = new NodeInfoBean(treeDefId);
            childInfoBean.setNAME(treeName);
            childInfoBean.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
            String rid = addNodeInfo(cds, tenantId, null, childInfoBean,
                    metadataValues);
            Fact nodeFact = cds.getFactById(rid);
//				nodeFact.addToRelation(cds.getDimensionById(industryType.getIndustryTypeRID()),
//						BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
            nodeFact.updateProperty(TreeNodeBaseInfo.LEVEL, -1);
            Fact treeDefinitionFact = cds.getFactById(treeDefinirionInfoObject.getObjectInstanceRID());
            treeDefinitionFact.addToRelation(nodeFact,
                    BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
//				return industryType.getIndustryTypeRID();
            return rid;
//			} else {
//				return null;
//			}
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    public String addSceneTreeNodeWithoutObjectType(String tenantId, String userId, String treeDefId,
                                                    String treeName) throws DataServiceModelRuntimeException,
            DataServiceUserException, CimDataEngineRuntimeException {//NOSONAR
        Assert.hasText(treeDefId, "node info is null");
        Assert.notNull(treeName, "tree name is mandatory");
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            if (!cds.hasInheritFactType(treeDefId)) {
                log.error("tree object type not defined");
                return null;
            }

            IndustryTypeVO industryTypeVO = new IndustryTypeVO();
            industryTypeVO.setIndustryTypeName(treeDefId);
            industryTypeVO.setIndustryTypeDesc(treeName);
            industryTypeVO.setCreatorId(userId);

            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            //add tree definition fact
            InfoObjectDef treeDefinitionObjectDef =
                    modelCore.getInfoObjectDef(CimConstants.TreeDefinitionProperties.TREE_DEFINITION_OBJECT_TYPE);
            Map<String, Object> baseValues = new HashMap<>();
            baseValues.put(CimConstants.TreeDefinitionProperties.ID, treeDefId);
            baseValues.put(CimConstants.TreeDefinitionProperties.NAME, treeName);
            baseValues.put(CimConstants.TreeDefinitionProperties.TREE_NODE_OBJECT, treeDefId);
            baseValues.put(CimConstants.TreeDefinitionProperties.CREATE_TIME, new Date());
            baseValues.put(CimConstants.TreeDefinitionProperties.UPDATE_TIME, new Date());

            InfoObjectValue objectValue = new InfoObjectValue();
            objectValue.setBaseDatasetPropertiesValue(baseValues);
            InfoObject treeDefinirionInfoObject = treeDefinitionObjectDef.newObject(objectValue, false);
            //add industry
            IndustryType industryType;
            IndustryTypes industryTypes = modelCore.getIndustryTypes();
            Map<String, Object> metadataValues = new HashMap<>();
            metadataValues.put(GeneralProperties.CREATOR_ID, userId);
            metadataValues.put(GeneralProperties.UPDATOR_ID, userId);
            metadataValues.put(GeneralProperties.CREATE_TIME, new Date());
            metadataValues.put(GeneralProperties.UPDATE_TIME, new Date());
            industryType = industryTypes.addRootIndustryType(industryTypeVO);

            if (industryType != null) {
                NodeInfoBean childInfoBean = new NodeInfoBean(treeDefId);
                childInfoBean.setNAME(treeName);
                childInfoBean.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
                String rid;
                // rid = addNodeInfoWithDataPermission(cds, tenantId, userId, null, childInfoBean,
                // metadataValues);
                rid = addNodeInfo(cds, tenantId, null, childInfoBean, metadataValues);
                Fact nodeFact = cds.getFactById(rid);
                nodeFact.addToRelation(cds.getDimensionById(industryType.getIndustryTypeRID()),
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
                nodeFact.updateProperty(TreeNodeBaseInfo.LEVEL, -1);
                Fact treeDefinitionFact = cds.getFactById(treeDefinirionInfoObject.getObjectInstanceRID());
                treeDefinitionFact.addToRelation(nodeFact,
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
                return industryType.getIndustryTypeRID();
            } else {
                return null;
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }


    /**
     * 增加附加信息
     *
     * @param cds
     * @param objectDef
     * @param object
     * @param infoBean
     */
    private void addAttachedInfo(CimDataSpace cds, InfoObjectDef objectDef, InfoObject object, NodeInfoBean infoBean) {
        try {
            //增加附加信息
            List<CommonTag> commonTags = objectDef.getAttachedCommonTags(null, null);
            for (CommonTag commonTag : commonTags) {
                if (commonTag != null) {
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(commonTag.getTagName())) {
                        if (CimConstants.TreeAttachedInfo.CIM_BIM_VIS.equals(commonTag.getTagName())) {
                            List<InfoObject> relatedInfoObjects = object.getAllRelatedInfoObjects(BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE, RelationDirection.FROM);
                            if (relatedInfoObjects.size() > 0) {
                                InfoObject projectV1InfoObject = relatedInfoObjects.get(0);
                                List<InfoObject> bimObjects = projectV1InfoObject.getAllRelatedInfoObjects(CimConstants.TreeAttachedInfo.COMMON_VISUAL_REPRESENTATION_OF, RelationDirection.TO);
                                if (bimObjects.size() > 0) {
                                    InfoObject bimObject = bimObjects.get(0);
                                    Fact bimFact = cds.getFactById(bimObject.getObjectInstanceRID());
                                    Property bimId = bimFact.getProperty("ID");
                                    Object bimIdValue = bimId.getPropertyValue();
                                    Map<String, Object> attachedMap = new HashMap<>();
                                    attachedMap.put("bim_object_type", bimObject.getObjectTypeName());
                                    attachedMap.put("bim_id", bimIdValue);
                                    infoBean.setAttachedMap(attachedMap);
                                }

                            }
                        }
                    }
                } else {
                    log.error("tag is null");
                }

            }
        } catch (Exception e) {
            log.error("system is error , is [{}]", e.getMessage());
        }

    }

    //递归
    private InfoObject recursive(CimDataSpace cds, String userId, InfoObject infoObject, InfoObjectDef targetInfoObjectDef) throws DataServiceModelRuntimeException, DataServiceUserException, CimDataEngineRuntimeException {
        //更改目标树的tree def ID
        //infoObject.addOrUpdateObjectProperty(CimConstants.TreeNodeBaseInfo.TREE_DEF_ID, targetInfoObjectDef.getObjectTypeName());
        Map<String, Object> baseInfo = infoObject.getInfo();
        InfoObjectValue infoObjectValue = new InfoObjectValue();
        //保存base信息 并清除ID信息
        baseInfo.remove(CimConstants.TreeNodeBaseInfo.ID);
        infoObjectValue.setBaseDatasetPropertiesValue(baseInfo);
        //保存通用信息
        for (Map.Entry<String, Map<String, Object>> unionEntry : infoObject.getObjectPropertiesByDatasets().entrySet()) {
            Map<String, Object> generalInfo = unionEntry.getValue();
            generalInfo.put(CimConstants.CREATE_TIME, new Date());
            generalInfo.put(CimConstants.UPDATE_TIME, new Date());
            generalInfo.put(TreeNodeBaseInfo.IDX, System.currentTimeMillis());
            generalInfo.put(TreeNodeBaseInfo.LEVEL, 0);
            generalInfo.put(TreeNodeBaseInfo.TREE_DEF_ID, targetInfoObjectDef.getObjectTypeName());
            infoObjectValue.setGeneralDatasetsPropertiesValue(generalInfo);
        }
        //第一层
        InfoObject targetInfoObject = targetInfoObjectDef.newObject(infoObjectValue, false);
        dataPermissionService.addDataPermissionByUser(cds, userId, Collections.singletonList(targetInfoObject.getObjectInstanceRID()));

        Fact fact = cds.getFactById(targetInfoObject.getObjectInstanceRID());
        List<InfoObject> allRelatedInfoObjects = infoObject.getAllRelatedInfoObjects(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE, RelationDirection.FROM);
        if (!allRelatedInfoObjects.isEmpty()) {
            for (InfoObject relatedInfoObject : allRelatedInfoObjects) {
                InfoObject targetChildrenInfoObject = recursive(cds, userId, relatedInfoObject, targetInfoObjectDef);
                Fact relatedFact = cds.getFactById(targetChildrenInfoObject.getObjectInstanceRID());
                //递归关系
                relatedFact.addFromRelation(fact, BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE);
            }
        }
        return targetInfoObject;
    }


    private void recursiveDelete(CimDataSpace cds, InfoObject infoObject) throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        Fact fact = cds.getFactById(infoObject.getObjectInstanceRID());
        //todo 先移除所有关系
        fact.removeAllRelations();
        List<InfoObject> allRelatedInfoObjects = infoObject.getAllRelatedInfoObjects(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE, RelationDirection.FROM);
        if (!allRelatedInfoObjects.isEmpty()) {
            for (InfoObject allRelatedInfoObject : allRelatedInfoObjects) {
                recursiveDelete(cds, allRelatedInfoObject);
            }
        }
    }

    public String addDefaultDataContext(String tenantId, String userId) {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            InformationExplorer informationExplorer = cds.getInformationExplorer();

            ExploreParameters rootTreeEp = new ExploreParameters();
            rootTreeEp.setType(BusinessLogicConstant.INDUSTRY_TYPE_DIMENSION_TYPE_NAME);
            rootTreeEp.setDefaultFilteringItem(new EqualFilteringItem("industryTypeName", "shujuguanlimulushu"));
            List<Dimension> linkedMappingList = informationExplorer.discoverDimensions(rootTreeEp);
            String defaultRid;
            if (linkedMappingList != null && linkedMappingList.size() > 0) {
                defaultRid = linkedMappingList.get(0).getId();
            } else {
                return null;
            }

            Dimension dimensionById = cds.getDimensionById(defaultRid);

//			Relationable relationable = relation.getFromRelationable();
            String treeDefId = CimConstants.DataPermissionSchemaProperties.DATA_PERMISSION_SCHEMA_TREE;
//			/*2020/4/3 更改需求 不需要新建分类*/
//			//权限树添加默认分类和默认方案
//			IndustryNodeAddInputBean nodeInfo = new IndustryNodeAddInputBean();
//			Map<String, Object> mapInfo = new HashMap<>();
//			mapInfo.put(CimConstants.IndustryTypeNodeKeys.INDUSTRY_ID, UUID.randomUUID().toString());
//			mapInfo.put(CimConstants.IndustryTypeNodeKeys.INDUSTRY_NAME, TreeConstants.DEFAULT_CLASSIFICATION);
//			nodeInfo.setMetadata(mapInfo);
////			//添加分类
//			String permissionClassificationRid = addIndustryNode(tenantId, userId, treeDefId, nodeInfo, false);
////			//#33:548
//			Dimension classificationDimension = cds.getDimensionById(permissionClassificationRid);
//			List<Relation> allSpecifiedRelations = classificationDimension.getAllSpecifiedRelations(BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE, RelationDirection.TO);
//			Relationable relationable = allSpecifiedRelations.get(0).getFromRelationable();

            NodeInfoBean childInfoBean = new NodeInfoBean(treeDefId);
            childInfoBean.setNAME("数据管理目录树");
            childInfoBean.setNodeType(NodeInfoBean.NodeType.INDUSTRY);

            Map<String, Object> metadataValues = new HashMap<>();

            String rid = addNodeInfo(cds, tenantId, null, childInfoBean, metadataValues);

            Fact fact = cds.getFactById(rid);
            Relationable relationable = fact.addToRelation(dimensionById,
                    BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE).getFromRelationable();

            NodeInfoBean nodeInfoBean = new NodeInfoBean();
            TreeServiceUtil.relationableToNodeInfo(relationable, nodeInfoBean);
            String objectTypeId = CimConstants.DataPermissionSchemaProperties.DATA_PERMISSION_SCHEMA;
            //setTreeDefId
            nodeInfoBean.setTreeDefId(treeDefId);
            IndustryNodeAddInputBean planNodeInfo = new IndustryNodeAddInputBean();
            Map<String, Object> planMap = new HashMap<>();
            planMap.put(CimConstants.TreeNodeBaseInfo.NAME, TreeConstants.DATA_CATALOG_PERMISSIONS);
            planNodeInfo.setMetadata(planMap);
            planNodeInfo.setParentNodeInfo(nodeInfoBean);
            //添加默认方案
            String instanceRid = addInstanceNode(tenantId, userId, treeDefId, objectTypeId, planNodeInfo, false);


            List<DataPermissionAddInputBean> inputBeans = new ArrayList<>();

            Fact planFact = cds.getFactById(instanceRid);

            //从方案表中找出id 给权限树用
            String id = planFact.getProperty(CimConstants.TreeNodeBaseInfo.ID).getPropertyValue().toString();
            //权限树
            FilteringItem filteringItem = new EqualFilteringItem(CimConstants.TreeNodeBaseInfo.REF_CIM_ID, id);
            ExploreParameters ep = new ExploreParameters();
            ep.setType(treeDefId);
            ep.setDefaultFilteringItem(filteringItem);

            InfoObjectDef objectDef = modelCore.getInfoObjectDef(treeDefId);
            InfoObjectRetrieveResult objects = objectDef.getObjects(ep);
            if (!objects.getInfoObjects().isEmpty()) {
                InfoObject infoObject = objects.getInfoObjects().get(0);
                String objectInstanceRID = infoObject.getObjectInstanceRID();
                Fact pTreeFact = cds.getFactById(objectInstanceRID);
                String name = pTreeFact.getProperty(CimConstants.TreeNodeBaseInfo.NAME).getPropertyValue().toString();
                String pTreeId = pTreeFact.getProperty(CimConstants.TreeNodeBaseInfo.ID).getPropertyValue().toString();
                Map<String, Object> userIdAndDataPmapping = new HashMap<>();
                userIdAndDataPmapping.put(CimConstants.TreeNodeBaseInfo.NAME, name);
                userIdAndDataPmapping.put(CimConstants.UserIdAndDataPermissionProperties.DATA_PERMISSSION_ID, pTreeId);
                userIdAndDataPmapping.put(CimConstants.UserIdAndDataPermissionProperties.USER_ID, userId);
                userIdAndDataPmapping.put(CimConstants.CREATE_TIME, new Date());
                userIdAndDataPmapping.put(CimConstants.UPDATE_TIME, new Date());
                List<Map<String, Object>> singletonList = Collections.singletonList(userIdAndDataPmapping);
                String useridAndDataPermissionType = CimConstants.UserIdAndDataPermissionProperties.USERID_AND_DATA_PERMISSION_MAPPING;

                BatchDataOperationResult batchDataOperationResult = instancesService.addInstanceSingle(tenantId, useridAndDataPermissionType, instancesService.addSingleObjectValuesFormal(singletonList), false);


                List<String> successDataInstanceRIDs = batchDataOperationResult.getSuccessDataInstanceRIDs();
                String sceneTreeRid = addDefaultSceneTreeNode(tenantId, userId);
                return sceneTreeRid;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return null;
    }


    public Map<String, Boolean> addDefaultDataContextV2(String tenantId, String userId) {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            //权限树添加默认分类和默认方案
            IndustryNodeAddInputBean nodeInfo = new IndustryNodeAddInputBean();
            Map<String, Object> mapInfo = new HashMap<>();
            mapInfo.put(CimConstants.IndustryTypeNodeKeys.INDUSTRY_ID, UUID.randomUUID().toString());
            mapInfo.put(CimConstants.IndustryTypeNodeKeys.INDUSTRY_NAME, TreeConstants.DEFAULT_CLASSIFICATION);
            nodeInfo.setMetadata(mapInfo);
            String treeDefId = CimConstants.DataPermissionSchemaProperties.DATA_PERMISSION_SCHEMA_TREE;
            //添加分类

            String permissionClassificationRid = addIndustryNode(tenantId, userId, treeDefId, nodeInfo, false);


            Dimension classificationDimension = cds.getDimensionById(permissionClassificationRid);
            List<Relation> allSpecifiedRelations = classificationDimension.getAllSpecifiedRelations(BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE, RelationDirection.TO);
            Relationable relationable = allSpecifiedRelations.get(0).getFromRelationable();

            NodeInfoBean nodeInfoBean = new NodeInfoBean();
            TreeServiceUtil.relationableToNodeInfo(relationable, nodeInfoBean);
            String objectTypeId = CimConstants.DataPermissionSchemaProperties.DATA_PERMISSION_SCHEMA;
            //setTreeDefId
            nodeInfoBean.setTreeDefId(treeDefId);
            IndustryNodeAddInputBean planNodeInfo = new IndustryNodeAddInputBean();
            Map<String, Object> planMap = new HashMap<>();
            planMap.put(CimConstants.TreeNodeBaseInfo.NAME, TreeConstants.DATA_CATALOG_PERMISSIONS);
            planNodeInfo.setMetadata(planMap);
            planNodeInfo.setParentNodeInfo(nodeInfoBean);
            //添加默认方案
            String planRid = addInstanceNode(tenantId, userId, treeDefId, objectTypeId, planNodeInfo, false);
            Fact planFact = cds.getFactById(planRid);
            //从方案表中找出id 给权限树用
            String planId = planFact.getProperty(CimConstants.TreeNodeBaseInfo.ID).getPropertyValue().toString();
            String contentTreeObject = CimConstants.ContentTreeObjectType.CONTENT_TREE_OBJECT;
            InformationExplorer informationExplorer = cds.getInformationExplorer();
            ExploreParameters contentTreeEp = new ExploreParameters();
            contentTreeEp.setType(contentTreeObject);
            contentTreeEp.setDefaultFilteringItem(new EqualFilteringItem("level", -1));
            List<Fact> facts = informationExplorer.discoverInheritFacts(contentTreeEp);
            Fact fact = facts.get(0);
            String contentTreeObjectId = fact.getProperty(GeneralProperties.ID).getPropertyValue().toString();
            //权限树
            FilteringItem filteringItem = new EqualFilteringItem(CimConstants.TreeNodeBaseInfo.REF_CIM_ID, planId);
            ExploreParameters ep = new ExploreParameters();
            ep.setType(treeDefId);
            ep.setDefaultFilteringItem(filteringItem);


            InfoObjectDef objectDef = modelCore.getInfoObjectDef(treeDefId);
            InfoObjectRetrieveResult objects = objectDef.getObjects(ep);


            if (!objects.getInfoObjects().isEmpty()) {
                InfoObject infoObject = objects.getInfoObjects().get(0);

                String objectInstanceRID = infoObject.getObjectInstanceRID();
                Fact pTreeFact = cds.getFactById(objectInstanceRID);
                String name = pTreeFact.getProperty(CimConstants.TreeNodeBaseInfo.NAME).getPropertyValue().toString();
                String pTreeId = pTreeFact.getProperty(CimConstants.TreeNodeBaseInfo.ID).getPropertyValue().toString();
                Map<String, Object> userIdAndDataPmapping = new HashMap<>();
                userIdAndDataPmapping.put(CimConstants.TreeNodeBaseInfo.NAME, name);
                userIdAndDataPmapping.put(CimConstants.UserIdAndDataPermissionProperties.DATA_PERMISSSION_ID, pTreeId);
                userIdAndDataPmapping.put(CimConstants.UserIdAndDataPermissionProperties.USER_ID, userId);
                userIdAndDataPmapping.put(CimConstants.CREATE_TIME, new Date());
                userIdAndDataPmapping.put(CimConstants.UPDATE_TIME, new Date());
                List<Map<String, Object>> singletonList = Collections.singletonList(userIdAndDataPmapping);
                String useridAndDataPermissionType = CimConstants.UserIdAndDataPermissionProperties.USERID_AND_DATA_PERMISSION_MAPPING;

                instancesService.addInstanceSingle(tenantId, useridAndDataPermissionType, instancesService.addSingleObjectValuesFormal(singletonList), false);

//				String objectInstanceRID = infoObject.getObjectInstanceRID();
                String ID = (String) infoObject.getPropertyValue((GeneralProperties.ID));
                String treeSchema = "DATA_MANAGER_CONTENT_TREE";

                DataPermissionAddInputBean inputBean = new DataPermissionAddInputBean();
                inputBean.setNodeId(contentTreeObjectId);
                inputBean.setReadPermission(2);
                inputBean.setWritePermission(2);
                inputBean.setDeletePermission(2);
                List<DataPermissionAddInputBean> inputBeans = new ArrayList<>();
                inputBeans.add(inputBean);
                Map<String, Boolean> strBooleanMap = dataPermissionService.addDataPermission(treeSchema, ID, inputBeans);
                return strBooleanMap;
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return null;
    }


    /**
     * 新增对象类型节点
     * 对象类型只能在分类下创建
     *
     * @param tenantId
     * @param userId
     * @param treeDefId
     * @param nodeInfo
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws DataServiceUserException
     * @throws CimDataEngineRuntimeException
     */
    public String addObjectTypeNode(String tenantId, String userId, String treeDefId, ObjectTypeAddInputBean nodeInfo,
                                    boolean addDefaultPermission) {//NOSONAR
        Assert.notNull(nodeInfo, "node info is null");


        CimDataSpace cds = null;
        try {
            NodeInfoBean parentNodeInfo = nodeInfo.getParentNodeInfo();
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            if (parentNodeInfo != null && StringUtils.isNotBlank(
                    parentNodeInfo.getID()) && parentNodeInfo.getNodeType().equals(NodeInfoBean.NodeType.INDUSTRY)) {
                //创建对象类型
                InfoObjectTypeVO infoObjectTypeVO = new InfoObjectTypeVO();
                infoObjectTypeVO.setObjectName(nodeInfo.getObjectTypeName());
                infoObjectTypeVO.setObjectId(nodeInfo.getObjectTypeId());
                InfoObjectDefs infoObjectDefs = modelCore.getInfoObjectDefs();

                InfoObjectDefDSImpl infoObjectDefDSImpl = (InfoObjectDefDSImpl) infoObjectDefs.addRootInfoObjectDef(infoObjectTypeVO);
                if (infoObjectDefDSImpl == null) {
                    log.error("object type create failure");
                    return null;
                }
                String infoObjectTypeStatusFactRid = infoObjectDefDSImpl.getInfoObjectTypeStatusFactRid();
                String objectTypeName = infoObjectDefDSImpl.getObjectTypeName();
                NodeInfoBean childNodeInfoBean = new NodeInfoBean(treeDefId);
                childNodeInfoBean.setNAME(nodeInfo.getObjectTypeName());
                childNodeInfoBean.setNodeType(NodeInfoBean.NodeType.OBJECT);
                childNodeInfoBean.setRefObjectTypeRid(infoObjectTypeStatusFactRid);
                childNodeInfoBean.setRefObjType(objectTypeName);
                Map<String, Object> metadataValues = new HashMap<>();
                metadataValues.put(GeneralProperties.CREATOR_ID, userId);
                metadataValues.put(GeneralProperties.UPDATOR_ID, userId);
                metadataValues.put(GeneralProperties.CREATE_TIME, new Date());
                metadataValues.put(GeneralProperties.UPDATE_TIME, new Date());
                String rid = addNodeInfo(cds, tenantId, parentNodeInfo, childNodeInfoBean, metadataValues);

                Fact treeNodeFact = cds.getFactById(rid);
                treeNodeFact.addToRelation(cds.getFactById(infoObjectTypeStatusFactRid),
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);

                return infoObjectTypeStatusFactRid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return null;
    }


    /**
     * 新增属性集节点
     *
     * @param tenantId
     * @param userId
     * @param treeDefId
     * @param nodeInfo
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws DataServiceUserException
     * @throws CimDataEngineRuntimeException
     */
    public String addDatasetNode(String tenantId, String userId, String treeDefId, DatasetAddInputBean nodeInfo,
                                 boolean addDefaultPermission) {//NOSONAR
        Assert.notNull(nodeInfo, "node info is null");
        CimDataSpace cds = null;
        try {
            NodeInfoBean parentNodeInfo = nodeInfo.getParentNodeInfo();
            if (parentNodeInfo != null && StringUtils.isNotBlank(parentNodeInfo.getID())) {
                cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
                CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
                modelCore.setCimDataSpace(cds);

                DatasetVO datasetVO = new DatasetVO();
                //todo dataset 在engine 里无描述 暂不存desc
                datasetVO.setDatasetName(nodeInfo.getDatasetId().trim());
                datasetVO.setDatasetDesc(nodeInfo.getDatasetName().trim());
                datasetVO.setDatasetStructure(DatasetDef.DatasetStructure.EXTERNAL);
                datasetVO.setDatasetClassify("通用属性集");
                datasetVO.setInheritDataset(false);
                datasetVO.setHasDescendant(false);
                DatasetDefs datasetDefs = modelCore.getDatasetDefs();

                DatasetDef datasetDef = datasetDefs.addDatasetDef(datasetVO);
                if (datasetDef == null) {
                    log.error("create dataset failure");
                    return null;
                }
                String datasetRID = datasetDef.getDatasetRID();
                if (parentNodeInfo.getNodeType().equals(NodeInfoBean.NodeType.OBJECT)) {
                    String objectTypeTreeId = parentNodeInfo.getRid();
                    Fact objectTypeTreeFact = cds.getFactById(objectTypeTreeId);
                    List<Relation> objectTypeRelations = objectTypeTreeFact.getAllSpecifiedRelations(BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE, RelationDirection.FROM);
                    if (CollectionUtils.isNotEmpty(objectTypeRelations)) {
                        Relation relation = objectTypeRelations.get(0);
                        Relationable toRelationable = relation.getToRelationable();
                        String objectTypeId = toRelationable.getProperty(CimConstants.ObjectTypeNodeKeys.INFO_OBJECT_TYPE_NAME).getPropertyValue().toString();
                        InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
                        if (infoObjectDef != null) {
                            boolean b = infoObjectDef.linkDatasetDef(datasetRID);
                        }
                    }
                }
                NodeInfoBean childNodeInfoBean = new NodeInfoBean(treeDefId);
                childNodeInfoBean.setNAME(nodeInfo.getDatasetName());
                childNodeInfoBean.setNodeType(NodeInfoBean.NodeType.DATASET);
                childNodeInfoBean.setRefDatasetRid(datasetRID);
                Map<String, Object> metadataValues = new HashMap<>();
                metadataValues.put(GeneralProperties.CREATOR_ID, userId);
                metadataValues.put(GeneralProperties.UPDATOR_ID, userId);
                metadataValues.put(GeneralProperties.CREATE_TIME, new Date());
                metadataValues.put(GeneralProperties.UPDATE_TIME, new Date());
                String rid = addNodeInfo(cds, tenantId, parentNodeInfo, childNodeInfoBean, metadataValues);
                Fact treeFact = cds.getFactById(rid);
                treeFact.addToRelation(cds.getFactById(datasetRID),
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);

                return treeFact.getId();

            } else {
                log.error("create dataset node failure,parent node must not null");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return null;
    }

    /**
     * 新增关联关系节点
     *
     * @param tenantId
     * @param userId
     * @param treeDefId
     * @param nodeInfo
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws DataServiceUserException
     * @throws CimDataEngineRuntimeException
     */
    public String addRelationShipNode(String tenantId, String userId, String treeDefId, RelationShipAddInputBean nodeInfo,
                                 boolean addDefaultPermission) {//NOSONAR
        Assert.notNull(nodeInfo, "node info is null");
        CimDataSpace cds = null;
        try {
            NodeInfoBean parentNodeInfo = nodeInfo.getParentNodeInfo();
            if (parentNodeInfo != null && StringUtils.isNotBlank(parentNodeInfo.getID())) {
                cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
                CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
                modelCore.setCimDataSpace(cds);
                //todo 产品原型未完善
//                RelationshipDefs relationshipDefs = modelCore.getRelationshipDefs();
//
//                relationshipDefs.addBasicRelationshipDef()


                if (parentNodeInfo.getNodeType().equals(NodeInfoBean.NodeType.OBJECT)) {


                    NodeInfoBean childNodeInfoBean = new NodeInfoBean(treeDefId);
                    childNodeInfoBean.setNAME(nodeInfo.getRelationShipName());
                    childNodeInfoBean.setNodeType(NodeInfoBean.NodeType.RELATIONSHIP);
                    //todo relationship rid
                    Map<String, Object> metadataValues = new HashMap<>();
                    metadataValues.put(GeneralProperties.CREATOR_ID, userId);
                    metadataValues.put(GeneralProperties.UPDATOR_ID, userId);
                    metadataValues.put(GeneralProperties.CREATE_TIME, new Date());
                    metadataValues.put(GeneralProperties.UPDATE_TIME, new Date());
                    String rid = addNodeInfo(cds, tenantId, parentNodeInfo, childNodeInfoBean, metadataValues);
                    Fact treeFact = cds.getFactById(rid);
                    return treeFact.getId();
                }


            } else {
                log.error("create relationShip node failure,parent node must not null");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return null;
    }
}
