package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.TreeNodeBaseInfo;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Property;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.InValueFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.SimilarFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CommonTag;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.tree.ChildNodeCountBean;
import com.glodon.pcop.cimsvc.model.tree.DataPermissionBean;
import com.glodon.pcop.cimsvc.model.tree.NodeDeleteOutputBean;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.glodon.pcop.cimsvc.model.tree.NodeMetadataOutputBean;
import com.glodon.pcop.cimsvc.model.tree.NodeMetadataWithPermissionOutputBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TreeServiceWithPermission extends TreeService {
    private static final Logger log = LoggerFactory.getLogger(TreeServiceWithPermission.class);
    public static final int DEFAULT_MAX_LOOP_COUNT = 10;

    @Autowired
    private DataPermissionService dataPermissionService;

    @Autowired
    private DataPermissionCache dataPermissionCache;

    public List<NodeInfoBean> expandNodes(String tenantId, String userId, String permissionName, String treeDefId,
            NodeInfoBean parentNode, Boolean isCountChild, Boolean filterByPermission) {
        if (!filterByPermission) {
            log.info("expand node without permission");
            return expandNodes(tenantId, treeDefId, parentNode, isCountChild);
        }

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
                childNodes = listRootNodesWithFilter(userId, permissionName, treeDefId, modelCore);
            } else {
                childNodes = listChildNodesWithFilter(userId, permissionName, treeDefId, parentNode,
                        modelCore);
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
    public List<NodeInfoBean> searchNodeByName(String tenantId, String treeDefId, String keyWord) {
        List<NodeInfoBean> nodeInfoBeanList = new ArrayList<>();
        Assert.isTrue(StringUtils.isNotBlank(tenantId), "tenantId is mandatory");
        Assert.isTrue(StringUtils.isNotBlank(treeDefId), "treeDefId is mandatory");
        Assert.isTrue(StringUtils.isNotBlank(keyWord), "tree search keyWord is blank");
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
                for (InfoObject object : infoObjectList) {
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
     * 已知树节点的子节点
     *
     * @param treeDefId
     * @param parentNode
     * @param modelCore
     * @return
     * @throws CimDataEngineRuntimeException
     */
    public List<NodeInfoBean> listChildNodesWithFilter(String userId, String permissionName, String treeDefId,
            NodeInfoBean parentNode, CIMModelCore modelCore) throws CimDataEngineRuntimeException {
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

        childNodes = listChildNodesWithFilter(objectDef, userId, permissionName, treeDefId, parentNode.getID(),
                infoObjectList.get(0), cds);
        return childNodes;
    }

    public List<NodeInfoBean> listChildNodesWithFilter(String userId, String permissionName, InfoObjectDef treeObjDef
            , String treeDefId, NodeInfoBean parentNode, CIMModelCore modelCore) throws CimDataEngineRuntimeException {
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

        childNodes = listChildNodesWithFilter(objectDef, userId, permissionName, treeDefId, parentNode.getID(),
                infoObjectList.get(0), cds);
        return childNodes;
    }

    /**
     * 树节点的子节点
     *
     * @param objectDef
     * @param treeDefId
     * @param parentNodeId
     * @return
     * @throws CimDataEngineRuntimeException
     */
    public List<NodeInfoBean> listChildNodesWithFilter(InfoObjectDef objectDef, String userId, String permissionName,
            String treeDefId,
            String parentNodeId, InfoObject infoObject, CimDataSpace cds) throws CimDataEngineRuntimeException {
        List<NodeInfoBean> childNodes = new ArrayList<>();
        List<InfoObject> infoObjectList =
                infoObject.getAllRelatedInfoObjects(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE,
                        RelationDirection.FROM);

        if (infoObjectList != null && infoObjectList.size() > 0) {
            List<String> schemaIds = dataPermissionService.getPermissionSchemaByUser(cds, userId);
            for (InfoObject object : infoObjectList) {
                DataPermissionBean dataPermissionBean =
                        dataPermissionService.getDataPermissionByUser(object.getObjectInstanceRID(), schemaIds);

                if (dataPermissionBean == null || dataPermissionBean.getReadPermission().equals(0)) {
                    log.debug("filter out by permission: userId={}, rid={}", userId, object.getObjectInstanceRID());
                    continue;
                }

                Relationable rlab = cds.getFactById(object.getObjectInstanceRID());
                NodeInfoBean infoBean = new NodeInfoBean();
                //添加附加信息
                addAttachedInfo(cds, objectDef, object, infoBean);

                TreeServiceUtil.relationableToNodeInfo(rlab, infoBean);
                if (StringUtils.isNotBlank(infoBean.getID()) && StringUtils.isNotBlank(infoBean.getNAME())) {
                    infoBean.setTreeDefId(treeDefId);
                    infoBean.setParentId(parentNodeId);
                    childNodes.add(infoBean);
                } else {
                    log.error("tree node info ID and NAME are mandatary");
                }
            }
        }

        return childNodes;
    }


    /**
     * root节点
     *
     * @param treeDefId
     * @param permissionName
     * @param userId
     * @param modelCore
     * @return
     * @throws CimDataEngineRuntimeException
     */
    public List<NodeInfoBean> listRootNodesWithFilter(String userId, String permissionName, String treeDefId,
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
            List<String> schemaIds = dataPermissionService.getPermissionSchemaByUser(cds, userId);
            for (InfoObject object : infoObjectList) {
                DataPermissionBean dataPermissionBean =
                        dataPermissionService.getDataPermissionByUser(object.getObjectInstanceRID(), schemaIds);
                if (dataPermissionBean == null || dataPermissionBean.getReadPermission().equals(0)) {
                    log.debug("filter out by permission: userId={}, rid={}", userId, object.getObjectInstanceRID());
                    continue;
                }


                NodeInfoBean infoBean = new NodeInfoBean();
                //添加附加信息
                addAttachedInfo(cds, objectDef, object, infoBean);
                Fact rlab = cds.getFactById(object.getObjectInstanceRID());
                TreeServiceUtil.relationableToNodeInfo(rlab, infoBean);
                if (StringUtils.isNotBlank(infoBean.getID()) && StringUtils.isNotBlank(infoBean.getNAME())) {
                    infoBean.setTreeDefId(treeDefId);
                    childNodes.add(infoBean);
                } else {
                    log.error("tree node info ID and NAME are mandatary");
                }
            }
        }

        return childNodes;
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
                            List<InfoObject> relatedInfoObjects = object.getAllRelatedInfoObjects(
                                    BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE,
                                    RelationDirection.FROM);
                            if (relatedInfoObjects.size() > 0) {
                                InfoObject projectV1InfoObject = relatedInfoObjects.get(0);
                                List<InfoObject> bimObjects = projectV1InfoObject.getAllRelatedInfoObjects(
                                        CimConstants.TreeAttachedInfo.COMMON_VISUAL_REPRESENTATION_OF,
                                        RelationDirection.TO);
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


    /**
     * 添加附加信息
     * @param objectDef
     */
//	private void addAttachedObj(InfoObjectDef objectDef) {
//		List<CommonTag> commonTags = objectDef.getAttachedCommonTags(null, null);
//		for (CommonTag commonTag : commonTags) {
//			if (commonTag != null) {
//				if (StringUtils.isNotBlank(commonTag.getTagName())) {
//					if ("via".equals(commonTag.getTagName())) {
//
//					}
//				}
//			} else {
//				log.error("tag is null");
//			}
//
//		}
//	}


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

    public NodeMetadataOutputBean getTreeNodeMetadata(String tennatId, String userId, String treeDefId,
            Boolean includePermission, String permissionName,
            NodeInfoBean nodeInfo) throws DataServiceModelRuntimeException {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tennatId);

            InfoObjectDef objectDef = modelCore.getInfoObjectDef(treeDefId);
            Assert.notNull(objectDef, "object type of " + treeDefId + " not found");

            FilteringItem filteringItem = new EqualFilteringItem(CimConstants.GeneralProperties.ID, nodeInfo.getID());
            ExploreParameters ep = new ExploreParameters();
            ep.setType(treeDefId);
            ep.setDefaultFilteringItem(filteringItem);

            InfoObjectRetrieveResult retrieveResult = objectDef.getObjects(ep);
            List<InfoObject> infoObjectList = retrieveResult.getInfoObjects();
            if (infoObjectList == null || infoObjectList.size() < 1) {
                log.error("tree node metdata not found: [{}]", nodeInfo);
                return new NodeMetadataOutputBean();
            }
            InfoObject infoObject = infoObjectList.get(0);
            Map<String, Map<String, Object>> valuesByDataset = infoObject.getObjectPropertiesByDatasets();
            List<String> schemaIds = dataPermissionService.getPermissionSchemaByUser(cds, userId);
            Map<String, Object> metadata = new HashMap<>();
            for (String st : valuesByDataset.keySet()) {
                if (!st.equals(TreeNodeBaseInfo.TREE_NODE_INFO_DATA_SET) && !st.equals(
                        BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME)) {
                    metadata.putAll(valuesByDataset.get(st));
                }
            }
            if (includePermission) {
                DataPermissionBean permission =
                        dataPermissionService.getDataPermissionByUser(infoObject.getObjectInstanceRID(), schemaIds);
                // permission.setRid();
                NodeMetadataWithPermissionOutputBean outputBean = new NodeMetadataWithPermissionOutputBean();
                outputBean.setMetadata(metadata);
                outputBean.setPermission(permission);
                return outputBean;
            } else {
                NodeMetadataOutputBean outputBean = new NodeMetadataOutputBean();
                outputBean.setMetadata(metadata);
                return outputBean;
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }

    /**
     * 查询子树，根据用户权限过滤
     *
     * @param userId
     * @param permissionName
     * @param treeObjDef
     * @param treeDefId
     * @param parentNode
     * @param modelCore
     * @param loopCount
     * @return
     */
    public List<NodeInfoBean> listChildNodesRecursivelyWithFilter(String userId, String permissionName,
            InfoObjectDef treeObjDef, String treeDefId,
            NodeInfoBean parentNode, CIMModelCore modelCore,
            Integer loopCount) {
        List<NodeInfoBean> nodeInfoBeanList = new ArrayList<>();
        if (loopCount > 0) {
            int tmpLoop = --loopCount;
            try {
                if (parentNode == null || StringUtils.isBlank(parentNode.getID())) {
                    nodeInfoBeanList = listRootNodesWithFilter(userId, permissionName, treeDefId, modelCore);
                } else {
                    nodeInfoBeanList = listChildNodesWithFilter(userId, permissionName, treeObjDef, treeDefId,
                            parentNode, modelCore);
                }
                for (NodeInfoBean infoBean : nodeInfoBeanList) {
                    infoBean.setChildNodes(listChildNodesRecursivelyWithFilter(userId, permissionName, treeObjDef,
                            treeDefId, infoBean, modelCore, tmpLoop));
                }
            } catch (CimDataEngineRuntimeException e) {
                e.printStackTrace();
            }
        }
        return nodeInfoBeanList;
    }

    /**
     * 子树查询
     *
     * @param tenantId
     * @param userId
     * @param permissionName
     * @param treeDefId
     * @param parentNode
     * @param loopCount          子树层数
     * @param filterByPermission
     * @return
     */
    public List<NodeInfoBean> listChildNodesRecursively(String tenantId, String userId, String permissionName,
            String treeDefId, NodeInfoBean parentNode, Integer loopCount,
            Boolean filterByPermission) {
        List<NodeInfoBean> nodeInfoBeanList = new ArrayList<>();
        // Assert.hasText(parentNode.getID(), "parent node is mandatory");
        if (loopCount > 0) {
            CimDataSpace cds = null;
            try {
                cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
                CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                        tenantId);
                modelCore.setCimDataSpace(cds);
                InfoObjectDef treeObjDef = modelCore.getInfoObjectDef(treeDefId.trim());
                if (treeObjDef != null) {
                    if (filterByPermission) {
                        nodeInfoBeanList = listChildNodesRecursivelyWithFilter(userId, permissionName, treeObjDef,
                                treeDefId, parentNode, modelCore, loopCount);
                    } else {
                        nodeInfoBeanList = listChildNodesRecursivelyWithoutPermission(treeObjDef, treeDefId,
                                parentNode, false, modelCore, loopCount);
                    }
                } else {
                    log.error("tree object type not found");
                }
            } finally {
                if (cds != null) {
                    cds.closeSpace();
                }
            }
        } else {
            log.error("loopCount must > 0");
        }
        return nodeInfoBeanList;
    }

    /**
     * 删除树节点
     *
     * @param tenantId
     * @param userId
     * @param treeDefId
     * @param resursively  是否递归删除子节点
     * @param nodeInfoList
     * @return
     */
    public List<NodeDeleteOutputBean> deleteNodes(String tenantId, String userId, String treeDefId, Boolean resursively,
            List<NodeInfoBean> nodeInfoList, Boolean filterByPermission) {
        if (resursively) {
            return deleteNodesRecursively(tenantId, userId, treeDefId, nodeInfoList, filterByPermission);
        } else {
            return deleteNodesBatch(tenantId, userId, treeDefId, nodeInfoList);
        }
    }

    /**
     * 递归删除当前节点及其子节点
     *
     * @param tenantId
     * @param userId
     * @param treeDefId
     * @param nodeInfoList
     * @return
     */
    public List<NodeDeleteOutputBean> deleteNodesRecursively(String tenantId, String userId, String treeDefId,
            List<NodeInfoBean> nodeInfoList,
            Boolean filterByPermission) {
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
            Set<String> nodeIdSet = new HashSet<>();
            // List<Object> nodeIds = new ArrayList<>();
            for (NodeInfoBean nodeInfoBean : nodeInfoList) {
                // nodeIds.add(nodeInfoBean.getID());
                nodeIdSet.add(nodeInfoBean.getID());
                NodeDeleteOutputBean deleteOutputBean = new NodeDeleteOutputBean(nodeInfoBean.getID(), false);
                outputBeanMap.put(nodeInfoBean.getID(), deleteOutputBean);
                nodeDeleteOutputBeans.add(deleteOutputBean);
            }

            for (NodeInfoBean nodeInfoBean : nodeInfoList) {
                List<NodeInfoBean> childNodeInfoBeans;
                if (filterByPermission) {
                    childNodeInfoBeans = listChildNodesRecursivelyWithFilter(userId, "", objectDef,
                            treeDefId, nodeInfoBean, modelCore, DEFAULT_MAX_LOOP_COUNT);
                } else {
                    childNodeInfoBeans = listChildNodesRecursively(tenantId, userId, "", treeDefId, nodeInfoBean,
                            DEFAULT_MAX_LOOP_COUNT, false);
                }
                if (childNodeInfoBeans != null && childNodeInfoBeans.size() > 0) {
                    for (NodeInfoBean childNode : childNodeInfoBeans) {
                        getNodeIds(nodeIdSet, childNode);
                    }
                }
            }

            log.debug("node ids {}", StringUtils.join(nodeIdSet, ','));
            List<Object> nodeIdList = new ArrayList<>(nodeIdSet);
            deleteNodesByIds(nodeIdList, objectDef, cds, userId, outputBeanMap, filterByPermission);

            if (treeDefId.equals(CimConstants.DataPermissionSchemaProperties.DATA_PERMISSION_SCHEMA_TREE)) {
                for (String schemaId : nodeIdSet) {
                    log.debug("try to clear data permission cache: [{}]", schemaId);
                    dataPermissionCache.clearCacheBySchemaId(schemaId);
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
     * @param nodeIds       节点ID
     * @param treeObjectDef
     * @param cds
     * @param userId
     * @param outputBeanMap
     */
    private void deleteNodesByIds(List<Object> nodeIds, InfoObjectDef treeObjectDef, CimDataSpace cds, String userId,
            Map<String, NodeDeleteOutputBean> outputBeanMap, Boolean filterByPermission) {
        FilteringItem filteringItem = new InValueFilteringItem(CimConstants.GeneralProperties.ID, nodeIds);
        ExploreParameters ep = new ExploreParameters();
        ep.setDefaultFilteringItem(filteringItem);

        InfoObjectRetrieveResult retrieveResult = treeObjectDef.getObjects(ep);
        List<InfoObject> objectList = retrieveResult.getInfoObjects();
        log.debug("retrival fact size [{}]", objectList.size());
        if (objectList != null && objectList.size() > 0) {
            if (filterByPermission) {
                List<String> permissionSchemaIds = dataPermissionService.getPermissionSchemaByUser(cds, userId);
                for (InfoObject infoObject : objectList) {
                    try {
                        DataPermissionBean dataPermissionBean =
                                dataPermissionService.getDataPermissionByUser(infoObject.getObjectInstanceRID(),
                                        permissionSchemaIds);
                        if (dataPermissionBean != null && dataPermissionBean.getDeletePermission().equals(2)) {
                            Map<String, Object> baseInfo = infoObject.getInfo();
                            NodeDeleteOutputBean deleteOutputBean =
                                    outputBeanMap.get(baseInfo.get(CimConstants.GeneralProperties.ID));
                            boolean flag = cds.removeFact(infoObject.getObjectInstanceRID());
                            if (deleteOutputBean != null) {
                                deleteOutputBean.setDelete(flag);
                            } else {
                                log.debug("delete child node");
                            }
                        } else {
                            log.debug("delete forbidden by permission");
                        }
                    } catch (CimDataEngineRuntimeException | DataServiceModelRuntimeException e) {
                        log.error("remove fact failed", e);
                    }
                }
            } else {
                for (InfoObject infoObject : objectList) {
                    try {
                        Map<String, Object> baseInfo = infoObject.getInfo();
                        NodeDeleteOutputBean deleteOutputBean =
                                outputBeanMap.get(baseInfo.get(CimConstants.GeneralProperties.ID));
                        boolean flag = cds.removeFact(infoObject.getObjectInstanceRID());
                        if (deleteOutputBean != null) {
                            deleteOutputBean.setDelete(flag);
                        } else {
                            log.debug("delete child node");
                        }
                    } catch (CimDataEngineRuntimeException | DataServiceModelRuntimeException e) {
                        log.error("remove fact failed", e);
                    }
                }
            }
        }
    }

    private void getNodeIds(Set<String> nodeIdSet, NodeInfoBean nodeInfoBean) {
        if (nodeInfoBean != null && StringUtils.isNotBlank(nodeInfoBean.getID())) {
            nodeIdSet.add(nodeInfoBean.getID());
            List<NodeInfoBean> childNodeInfoBeans = nodeInfoBean.getChildNodes();
            if (childNodeInfoBeans != null && childNodeInfoBeans.size() > 0) {
                for (NodeInfoBean childNodeInfoBean : childNodeInfoBeans) {
                    getNodeIds(nodeIdSet, childNodeInfoBean);
                }
            }
        }
    }

}
