package com.glodon.pcop.cimsvc.service.tree;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.minlog.Log;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Dimension;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.tree.LinkObjectAndInstancesByQueryInputBean;
import com.glodon.pcop.cimsvc.model.tree.LinkObjectTypeAndInstanceInputBean;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.glodon.pcop.cimsvc.model.tree.ObjectAndInstanceBean;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import com.glodon.pcop.cimsvc.model.v2.InstancesQueryInput;
import com.glodon.pcop.cimsvc.service.StandFoldersService;
import com.glodon.pcop.cimsvc.util.condition.QueryConditionParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *
 */
@Service
public class TreeNodeService {
    private static final Logger log = LoggerFactory.getLogger(TreeNodeService.class);

    @Autowired
    private TreeServiceWithPermission treeService;

    @Autowired
    private DataPermissionService dataPermissionService;

    @Autowired
    private StandFoldersService standFoldersService;

    private static Map<String, String> fileNodeMetadatKeyMapping;

    @Value("${my.object-type.project: projectV1}")
    private String projectV1 = "projectV1";

    static {
        fileNodeMetadatKeyMapping = new HashMap<>();

        fileNodeMetadatKeyMapping.put("fileDataName", "fileDataName");
        fileNodeMetadatKeyMapping.put("fileType", "fileType");
        fileNodeMetadatKeyMapping.put("srcFileName", "srcFileName");
        fileNodeMetadatKeyMapping.put("fileSize", "fileSize");
        fileNodeMetadatKeyMapping.put(CimConstants.GeneralProperties.CREATOR_ID,
                CimConstants.GeneralProperties.CREATOR_ID);
        fileNodeMetadatKeyMapping.put(CimConstants.GeneralProperties.UPDATOR_ID,
                CimConstants.GeneralProperties.UPDATOR_ID);
    }

    /**
     * 新增对象类型节点
     *
     * @param cds
     * @param tenantId
     * @param treeDefId
     * @param parentNode
     * @param objectTypeIds
     * @return
     */
    public Map<String, String> addObjectTypeNodes(CimDataSpace cds, String tenantId, String userId, String treeDefId,
                                                  NodeInfoBean parentNode, List<String> objectTypeIds) {
        Map<String, String> rs = new HashMap<>();
        NodeInfoBean.NodeType nodeType = NodeInfoBean.NodeType.OBJECT;
        for (String objId : objectTypeIds) {
            log.debug("add object type node: {}", objId);
            NodeInfoBean infoBean = new NodeInfoBean();
            rs.put(objId, "");
            try {
                Fact fact = InfoObjectFeatures.getInfoObjectTypeStatusRecord(cds, objId);
                infoBean.setRefObjType(fact.getProperty("infoObjectTypeName").getPropertyValue().toString());
                infoBean.setNAME(fact.getProperty("infoObjectTypeDesc").getPropertyValue().toString());
                infoBean.setNodeType(nodeType);
                infoBean.setTreeDefId(treeDefId);
                Map<String, Object> metadata = new HashMap<>();
                metadata.put(CimConstants.GeneralProperties.CREATOR_ID, userId);
                metadata.put(CimConstants.GeneralProperties.UPDATOR_ID, userId);
                // String nodeRid = TreeService.addNodeInfo(cds, tenantId, parentNode, infoBean, metadata);
                String nodeRid = treeService.addNodeInfoWithDataPermission(cds, tenantId, userId, parentNode,
                        infoBean, metadata);
                log.debug("new node rid: {}", nodeRid);
                rs.put(objId, nodeRid);
            } catch (Exception e) {
                log.error("add object type node failed", e);
            }
        }
        return rs;
    }

    /**
     * 新增实例节点.
     *
     * @param cds
     * @param tenantId
     * @param treeDefId
     * @param parentNode
     * @param factRids
     * @return
     */
    public Map<String, String> addInstanceNodeByRid(CimDataSpace cds, String tenantId, String userId, String treeDefId,
                                                    NodeInfoBean parentNode, List<String> factRids) {
        Map<String, String> rs = new HashMap<>();
        NodeInfoBean.NodeType nodeType = NodeInfoBean.NodeType.INSTANCE;
        for (String rid : factRids) {
            rs.put(rid, "");
            try {
                NodeInfoBean infoBean = new NodeInfoBean();
                Fact fact = cds.getFactById(rid);
                if (fact.hasProperty(CimConstants.BaseDataSetKeys.ID)) {
                    infoBean.setRefCimId(fact.getProperty(CimConstants.BaseDataSetKeys.ID).getPropertyValue().toString());
                } else {
                    log.error("[{}] add child node failed, id property of fact [{}] not found", parentNode, rid);
                    continue;
                }
                if (fact.hasProperty(CimConstants.BaseDataSetKeys.NAME) && fact.getProperty(CimConstants.BaseDataSetKeys.NAME) != null) {
                    infoBean.setNAME(fact.getProperty(CimConstants.BaseDataSetKeys.NAME).getPropertyValue().toString());
                } else {
                    infoBean.setNAME(fact.getProperty(CimConstants.BaseDataSetKeys.ID).getPropertyValue().toString());
                }
                infoBean.setNodeType(nodeType);
                infoBean.setTreeDefId(treeDefId);
                infoBean.setRefObjType(fact.getType());
                Map<String, Object> metadata = new HashMap<>();
                metadata.put(CimConstants.GeneralProperties.CREATOR_ID, userId);
                metadata.put(CimConstants.GeneralProperties.UPDATOR_ID, userId);

                // String nodeRid = TreeService.addNodeInfo(cds, tenantId, parentNode, infoBean, metadata);
                String nodeRid = treeService.addNodeInfoWithDataPermission(cds, tenantId, userId, parentNode,
                        infoBean, metadata);
                log.debug("new node rid: {}", nodeRid);
                Fact nodeFact = cds.getFactById(nodeRid);
                Relation relation = nodeFact.addToRelation(cds.getFactById(rid),
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
                log.debug("new relation rid: {}", relation.getId());

                if (fact.getType().equals(projectV1)) {
                    log.info("add stand folders");
                    List<String> standChildNodeRids = standFoldersService.addStandFolders(cds, tenantId, userId,
                            treeDefId, nodeFact);
                    if (standChildNodeRids.size() > 0) {
                        dataPermissionService.addDataPermissionByUser(cds, userId, standChildNodeRids);
                    }
                }
                rs.put(rid, nodeRid);
            } catch (Exception e) {
                log.error("add instance node failed", e);
            }
        }
        return rs;
    }

    public Map<String, String> addInstanceNodeByRidWithMetadataKeyMapping(CimDataSpace cds, String tenantId,
                                                                          String userId, String treeDefId,
                                                                          NodeInfoBean parentNode,
                                                                          NodeInfoBean.NodeType nodeType,
                                                                          List<String> factRids,
                                                                          Map<String, String> metadataKeyMapping) {
        Map<String, String> rs = new HashMap<>();
        // NodeInfoBean.NodeType nodeType = NodeInfoBean.NodeType.INSTANCE;
        for (String rid : factRids) {
            rs.put(rid, "");
            try {
                NodeInfoBean infoBean = new NodeInfoBean();
                Fact fact = cds.getFactById(rid);
                if (fact.hasProperty(CimConstants.BaseDataSetKeys.ID)) {
                    infoBean.setRefCimId(fact.getProperty(CimConstants.BaseDataSetKeys.ID).getPropertyValue().toString());
                } else {
                    log.error("fact of {} not found", rid);
                    continue;
                }
                if (fact.hasProperty(CimConstants.BaseDataSetKeys.NAME)) {
                    infoBean.setNAME(fact.getProperty(CimConstants.BaseDataSetKeys.NAME).getPropertyValue().toString());
                }
                infoBean.setNodeType(nodeType);
                infoBean.setTreeDefId(treeDefId);
                infoBean.setRefObjType(fact.getType());
                Map<String, Object> metadata = new HashMap<>();

                for (Map.Entry<String, String> entry : metadataKeyMapping.entrySet()) {
                    if (fact.hasProperty(entry.getKey())) {
                        metadata.put(entry.getValue(), fact.getProperty(entry.getKey()).getPropertyValue());
                    }
                }

                metadata.put(CimConstants.GeneralProperties.CREATOR_ID, userId);
                metadata.put(CimConstants.GeneralProperties.UPDATOR_ID, userId);
                // String nodeRid = TreeService.addNodeInfo(cds, tenantId, parentNode, infoBean, metadata);
                String nodeRid = treeService.addNodeInfoWithDataPermission(cds, tenantId, userId, parentNode,
                        infoBean, metadata);
                log.debug("new node rid: {}", nodeRid);
                Fact nodeFact = cds.getFactById(nodeRid);
                Relation relation = nodeFact.addToRelation(cds.getFactById(rid),
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
                log.debug("new relation rid: {}", relation.getId());
                rs.put(rid, nodeRid);
            } catch (Exception e) {
                log.error("add instance node failed", e);
            }
        }
        return rs;
    }

    public Map<String, String> addFileNodeByRidWithMetadata(CimDataSpace cds, String tenantId, String userId,
                                                            String treeDefId, NodeInfoBean parentNode,
                                                            List<String> rids) {
        Map<String, String> rs = new HashMap<>();
        for (String rid : rids) {
            rs.put(rid, "");
            try {
                NodeInfoBean infoBean = new NodeInfoBean();
                Fact fact = cds.getFactById(rid);
                if (fact.hasProperty(CimConstants.BaseDataSetKeys.ID)) {
                    infoBean.setRefCimId(fact.getProperty(CimConstants.BaseDataSetKeys.ID).getPropertyValue().toString());
                } else {
                    log.error("file fact of {} not found", rid);
                    continue;
                }
                if (fact.hasProperty(CimConstants.BaseDataSetKeys.NAME)) {
                    infoBean.setNAME(fact.getProperty(CimConstants.BaseDataSetKeys.NAME).getPropertyValue().toString());
                }
                infoBean.setNodeType(NodeInfoBean.NodeType.FILE);
                infoBean.setTreeDefId(treeDefId);
                infoBean.setRefObjType(fact.getType());
                Map<String, Object> metadata = new HashMap<>();

                for (Map.Entry<String, String> entry : fileNodeMetadatKeyMapping.entrySet()) {
                    if (fact.hasProperty(entry.getKey())) {
                        metadata.put(entry.getValue(), fact.getProperty(entry.getKey()).getPropertyValue());
                    }
                }

                // String nodeRid = TreeService.addNodeInfo(cds, tenantId, parentNode, infoBean, metadata);
                String nodeRid = treeService.addNodeInfoWithDataPermission(cds, tenantId, userId, parentNode, infoBean,
                        metadata);
                log.info("new node rid: {}", nodeRid);
                Fact nodeFact = cds.getFactById(nodeRid);
                Relation relation;
                relation = nodeFact.addToRelation(cds.getFactById(rid),
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);

                log.info("new relation rid: {}", relation.getId());
                rs.put(rid, nodeRid);
            } catch (Exception e) {
                log.error("add instance node failed", e);
            }
        }
        return rs;
    }

    public Map<String, String> addIndustryNodeByRid(CimDataSpace cds, String tenantId, String userId, String treeDefId,
                                                    NodeInfoBean parentNode, List<String> rids,
                                                    Map<String, String> metadataKeyMapping) {
        Map<String, String> rs = new HashMap<>();
        for (String rid : rids) {
            rs.put(rid, "");
            try {
                NodeInfoBean infoBean = new NodeInfoBean();
                Dimension dimension = cds.getDimensionById(rid);
                if (dimension.hasProperty(CimConstants.IndustryTypeNodeKeys.INDUSTRY_TYPE_NAME)) {
                    infoBean.setRefCimId(dimension.getProperty(CimConstants.IndustryTypeNodeKeys.INDUSTRY_TYPE_NAME).getPropertyValue().toString());
                } else {
                    log.error("Dimension of {} not found", rid);
                    continue;
                }
                if (dimension.hasProperty(CimConstants.IndustryTypeNodeKeys.INDUSTRY_TYPE_DESC)) {
                    infoBean.setNAME(dimension.getProperty(CimConstants.IndustryTypeNodeKeys.INDUSTRY_TYPE_DESC).getPropertyValue().toString());
                }
                infoBean.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
                infoBean.setTreeDefId(treeDefId);

                Map<String, Object> metadata = new HashMap<>();
                for (Map.Entry<String, String> entry : metadataKeyMapping.entrySet()) {
                    if (dimension.hasProperty(entry.getKey())) {
                        metadata.put(entry.getValue(), dimension.getProperty(entry.getKey()).getPropertyValue());
                    }
                }
                metadata.put(CimConstants.GeneralProperties.CREATOR_ID, userId);
                metadata.put(CimConstants.GeneralProperties.UPDATOR_ID, userId);

                // String nodeRid = TreeService.addNodeInfo(cds, tenantId, parentNode, infoBean, metadata);
                String nodeRid = treeService.addNodeInfoWithDataPermission(cds, tenantId, userId, parentNode,
                        infoBean, metadata);
                log.info("new node rid: {}", nodeRid);
                Fact nodeFact = cds.getFactById(nodeRid);
                Relation relation = nodeFact.addToRelation(cds.getDimensionById(rid),
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);

                log.info("new relation rid: {}", relation.getId());
                rs.put(rid, nodeRid);
            } catch (Exception e) {
                log.error("add instance node failed", e);
            }
        }
        return rs;
    }

    public Map<String, Boolean> linkObjectAndInstance(String tenantId, String treeDefId, String userId,
                                                      LinkObjectTypeAndInstanceInputBean nodeInfo) {
        Map<String, Boolean> rs = new HashMap<>();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            NodeInfoBean parentNode = nodeInfo.getParentNodeInfo();

            List<String> objIds = new ArrayList<>();
            for (ObjectAndInstanceBean objectAndInstance : nodeInfo.getObjectAndInstances()) {
                if (StringUtils.isNotBlank(objectAndInstance.getObjectTypeId())) {
                    objIds.add(objectAndInstance.getObjectTypeId());
                }
            }
            Map<String, String> objNodeMap = new HashMap<>();
            List<String> needAddObjectTypeIds = filterOutExistsObjectType(treeDefId, parentNode, modelCore, objIds,
                    objNodeMap);
            if (needAddObjectTypeIds.size() > 0) {
                objNodeMap.putAll(addObjectTypeNodes(cds, tenantId, userId, treeDefId, parentNode,
                        needAddObjectTypeIds));
                log.info("object node add result: {}", objNodeMap);
            } else {
                Log.info("no object type node should be added");
            }

            for (ObjectAndInstanceBean objectAndInstance : nodeInfo.getObjectAndInstances()) {
                String objId = objectAndInstance.getObjectTypeId();
                if (StringUtils.isNotBlank(objId)) {
                    if (StringUtils.isNotBlank(objNodeMap.get(objId))) {
                        rs.put(objId, true);
                    } else {
                        rs.put(objId, false);
                    }
                    if (objectAndInstance.getInstanceRids() != null && objectAndInstance.getInstanceRids().size() > 0) {
                        if (objNodeMap.containsKey(objId) && StringUtils.isNotBlank(objNodeMap.get(objId))) {
                            NodeInfoBean tmpParentNode = treeService.getNodeInfoByRid(cds, objNodeMap.get(objId),
                                    treeDefId);
                            Map<String, String> instanceRs = addInstanceNodeByRid(cds, tenantId
                                    , userId, treeDefId, tmpParentNode, objectAndInstance.getInstanceRids());
                            log.info("add instances node result {}", instanceRs);
                        } else {
                            log.info("object type node add failed, no instance node should be added");
                        }
                    } else {
                        log.info("no instance node should be added");
                    }
                } else {
                    Map<String, String> instanceRs = addInstanceNodeByRid(cds, tenantId, userId, treeDefId, parentNode,
                            objectAndInstance.getInstanceRids());
                    log.info("direct add instances node result {}", instanceRs);
                }
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return rs;
    }

    private List<String> filterOutExistsObjectType(String treeDefId, NodeInfoBean parnetNode, CIMModelCore modelCore,
                                                   List<String> srcObjectTypeIds,
                                                   Map<String, String> objectTypeAndNodeMapRid) throws CimDataEngineRuntimeException {
        List<NodeInfoBean> childNodeInfos;
        if (parnetNode == null || StringUtils.isBlank(parnetNode.getID())) {
            childNodeInfos = treeService.listRootNodes(treeDefId, false, modelCore);
        } else {
            childNodeInfos = treeService.listChildNodes(treeDefId, parnetNode, false, modelCore);
        }

        List<String> outObjectTypeIds = new ArrayList<>();
        if (childNodeInfos != null && childNodeInfos.size() > 0) {
            for (NodeInfoBean infoBean : childNodeInfos) {
                if (infoBean.getNodeType().equals(NodeInfoBean.NodeType.OBJECT) && srcObjectTypeIds.contains(infoBean.getRefObjType())) {
                    objectTypeAndNodeMapRid.put(infoBean.getRefObjType(), infoBean.getRid());
                    outObjectTypeIds.add(infoBean.getRefObjType());
                }
            }
        }

        return ListUtils.removeAll(srcObjectTypeIds, outObjectTypeIds);
    }

    public void setTreeService(TreeServiceWithPermission treeService) {
        this.treeService = treeService;
    }

    public int linkObjectAndInstanceByQuery(String tenantId, String treeDefId, String userId,
                                            LinkObjectAndInstancesByQueryInputBean nodeInfo) throws InputErrorException, EntityNotFoundException {
        int rs = 0;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            NodeInfoBean parentNode = nodeInfo.getParentNodeInfo();

            String objectTypeId = nodeInfo.getObjectTypeId();
            Map<String, String> objNodeMap = new HashMap<>();
            if (nodeInfo.getLinkObject()) {
                List<String> objIds = Arrays.asList(objectTypeId);
                objNodeMap = new HashMap<>();
                List<String> needAddObjectTypeIds = filterOutExistsObjectType(treeDefId, parentNode, modelCore, objIds, objNodeMap);
                if (needAddObjectTypeIds.size() > 0) {
                    objNodeMap.putAll(addObjectTypeNodes(cds, tenantId, userId, treeDefId, parentNode, needAddObjectTypeIds));
                    log.info("object node add result: {}", objNodeMap);
                } else {
                    Log.info("no object type node should be added");
                }
            }

            List<String> factRids = queryRidsByCondition(tenantId, modelCore, nodeInfo.getObjectTypeId(), nodeInfo.getConditions(), nodeInfo.getSqlWhereCondition());
            if (CollectionUtils.isNotEmpty(factRids)) {
                if (nodeInfo.getLinkObject()) {
                    if (objNodeMap.containsKey(objectTypeId) && StringUtils.isNotBlank(objNodeMap.get(objectTypeId))) {
                        NodeInfoBean tmpParentNode = treeService.getNodeInfoByRid(cds, objNodeMap.get(objectTypeId), treeDefId);
                        Map<String, String> instanceRs = addInstanceNodeByRid(cds, tenantId, userId, treeDefId, tmpParentNode, factRids);
                        rs = instanceRs.size();
                        log.info("add instances node result {}", instanceRs);
                    } else {
                        log.info("object type node add failed, no instance node should be added");
                    }
                } else {
                    Map<String, String> instanceRs = addInstanceNodeByRid(cds, tenantId, userId, treeDefId, parentNode, factRids);
                    rs = instanceRs.size();
                    log.info("direct add instances node result {}", instanceRs);
                }
            } else {
                log.info("no instance node should be added");
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return rs;
    }

    public List<String> queryRidsByCondition(String tenantId, CIMModelCore modelCore, String objectTypeId, List<CommonQueryConditionsBean> conditions, String sqlWhereCondition) throws InputErrorException, EntityNotFoundException {
        CimDataSpace cds = modelCore.getCimDataSpace();
        InstancesQueryInput queryInput = new InstancesQueryInput();
        queryInput.setConditions(conditions);
        queryInput.setSqlWhereCondition(sqlWhereCondition);
        queryInput.setPageIndex(0);
        queryInput.setPageSize(Integer.MAX_VALUE);
        ExploreParameters exploreParameters = QueryConditionParser.parserQueryInput(cds, objectTypeId, queryInput, false);
        log.info("query condition input: {}", JSON.toJSONString(exploreParameters));

        InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
        if (infoObjectDef == null) {
            log.error("object type of {} not exists or not belong to tenant of {}", objectTypeId, tenantId);
            throw new EntityNotFoundException("object type not defined or not belong to this tennat");
        }
        InfoObjectRetrieveResult infoObjectRetrieveResult = infoObjectDef.getObjects(exploreParameters);

        List<String> rids = new ArrayList<>();
        List<InfoObject> infoObjectList = infoObjectRetrieveResult.getInfoObjects();
        if (CollectionUtils.isNotEmpty(infoObjectList)) {
            for (InfoObject infoObject : infoObjectList) {
                rids.add(infoObject.getObjectInstanceRID());
            }
        }
        return rids;
    }

}
