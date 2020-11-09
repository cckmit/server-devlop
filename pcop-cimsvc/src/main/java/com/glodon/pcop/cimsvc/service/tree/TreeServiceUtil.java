package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil;
import com.glodon.pcop.cimsvc.model.tree.NodeCopyInputBean;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TreeServiceUtil {
    private static final Logger log = LoggerFactory.getLogger(TreeServiceUtil.class);

    @Autowired
    private DataPermissionService dataPermissionService;

    /**
     * 单个节点基本信息
     *
     * @param rlab
     * @return
     */
    public static void relationableToNodeInfo(Relationable rlab, NodeInfoBean infoBean) {
        infoBean.setRid(rlab.getId());

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.ID) && rlab.getProperty(CimConstants.TreeNodeBaseInfo.ID) != null) {
            infoBean.setID(rlab.getProperty(CimConstants.TreeNodeBaseInfo.ID).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.NAME) && rlab.getProperty(CimConstants.TreeNodeBaseInfo.NAME) != null) {
            infoBean.setNAME(rlab.getProperty(CimConstants.TreeNodeBaseInfo.NAME).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.NODE_TYPE) && rlab.getProperty(CimConstants.TreeNodeBaseInfo.NODE_TYPE) != null) {
            infoBean.setNodeType(NodeInfoBean.NodeType.valueOf(rlab.getProperty(CimConstants.TreeNodeBaseInfo.NODE_TYPE).getPropertyValue().toString()));
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.REF_OBJECT_TYPE) && rlab.getProperty(CimConstants.TreeNodeBaseInfo.REF_OBJECT_TYPE) != null) {
            infoBean.setRefObjType(rlab.getProperty(CimConstants.TreeNodeBaseInfo.REF_OBJECT_TYPE).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.REF_CIM_ID) && rlab.getProperty(CimConstants.TreeNodeBaseInfo.REF_CIM_ID) != null) {
            infoBean.setRefCimId(rlab.getProperty(CimConstants.TreeNodeBaseInfo.REF_CIM_ID).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.RELATION_TYPE) && rlab.getProperty(CimConstants.TreeNodeBaseInfo.RELATION_TYPE) != null) {
            infoBean.setRelationType(rlab.getProperty(CimConstants.TreeNodeBaseInfo.RELATION_TYPE).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.FILTER) && rlab.getProperty(CimConstants.TreeNodeBaseInfo.FILTER) != null) {
            infoBean.setFilter(rlab.getProperty(CimConstants.TreeNodeBaseInfo.FILTER).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.CREATE_TIME) && rlab.getProperty(CimConstants.TreeNodeBaseInfo.CREATE_TIME) != null) {
            infoBean.setCreateTime((Date) rlab.getProperty(CimConstants.TreeNodeBaseInfo.CREATE_TIME).getPropertyValue());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.UPDATE_TIME) && rlab.getProperty(CimConstants.TreeNodeBaseInfo.UPDATE_TIME) != null) {
            infoBean.setUpdateTime((Date) rlab.getProperty(CimConstants.TreeNodeBaseInfo.UPDATE_TIME).getPropertyValue());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.LEVEL) && rlab.getProperty(CimConstants.TreeNodeBaseInfo.LEVEL) != null) {
            infoBean.setLevel((Integer) rlab.getProperty(CimConstants.TreeNodeBaseInfo.LEVEL).getPropertyValue());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.IDX) && rlab.getProperty(CimConstants.TreeNodeBaseInfo.IDX) != null) {
            infoBean.setIdx((Double) rlab.getProperty(CimConstants.TreeNodeBaseInfo.IDX).getPropertyValue());
        }

        if (rlab.hasProperty(CimConstants.CREATOR) && rlab.getProperty(CimConstants.CREATOR) != null) {
            infoBean.setCreator(rlab.getProperty(CimConstants.CREATOR).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.UPDATOR) && rlab.getProperty(CimConstants.UPDATOR) != null) {
            infoBean.setUpdator(rlab.getProperty(CimConstants.UPDATOR).getPropertyValue().toString());
        }
        if(rlab.hasProperty(CimConstants.TreeNodeBaseInfo.INDUSTRY_RID )&& rlab.getProperty(CimConstants.TreeNodeBaseInfo.INDUSTRY_RID)!= null){
            infoBean.setRefIndustryRid(rlab.getProperty(CimConstants.TreeNodeBaseInfo.INDUSTRY_RID).getPropertyValue().toString());
        }
        if(rlab.hasProperty(CimConstants.TreeNodeBaseInfo.OBJECT_TYPE_RID )&& rlab.getProperty(CimConstants.TreeNodeBaseInfo.OBJECT_TYPE_RID)!= null){
            infoBean.setRefIndustryRid(rlab.getProperty(CimConstants.TreeNodeBaseInfo.OBJECT_TYPE_RID).getPropertyValue().toString());
        }
        if(rlab.hasProperty(CimConstants.TreeNodeBaseInfo.DATASET_RID )&& rlab.getProperty(CimConstants.TreeNodeBaseInfo.DATASET_RID)!= null){
            infoBean.setRefDatasetRid(rlab.getProperty(CimConstants.TreeNodeBaseInfo.DATASET_RID).getPropertyValue().toString());
        }
        if(rlab.hasProperty(CimConstants.TreeNodeBaseInfo.RELATIONSHIP_RID )&& rlab.getProperty(CimConstants.TreeNodeBaseInfo.RELATIONSHIP_RID)!= null){
            infoBean.setRefRelationShipRid(rlab.getProperty(CimConstants.TreeNodeBaseInfo.RELATIONSHIP_RID).getPropertyValue().toString());
        }
    }

    /**
     * 递归复制指定树节点及其子节点
     *
     * @param treeDefId
     * @param tenantId
     * @param targetNode
     * @param node
     * @param cds
     * @param nodeFactList
     */
    public static void nodeCopyRecursive(String treeDefId, String tenantId, NodeInfoBean targetNode, NodeInfoBean node,
                                         CimDataSpace cds, List<Fact> nodeFactList) {
        try {
            log.info("start to copy tree node: [{}]", node.getRid());
            //add node info
            Fact fact = CimDataEngineComponentFactory.createFact(treeDefId);
            fact.setInitProperty(CimConstants.TreeNodeBaseInfo.ID, treeDefId + "_" + UUID.randomUUID());
            fact.setInitProperty(CimConstants.TreeNodeBaseInfo.IDX, node.getIdx());
            fact.setInitProperty(CimConstants.TreeNodeBaseInfo.NODE_TYPE, node.getNodeType().toString());
            if (StringUtils.isNotBlank(node.getNAME())) {
                fact.setInitProperty(CimConstants.TreeNodeBaseInfo.NAME, node.getNAME());
            } else {
                fact.setInitProperty(CimConstants.TreeNodeBaseInfo.NAME, node.getID());
            }
            if (StringUtils.isNotBlank(node.getRefObjType())) {
                fact.setInitProperty(CimConstants.TreeNodeBaseInfo.REF_OBJECT_TYPE, node.getRefObjType());
            }
            if (StringUtils.isNotBlank(node.getRefCimId())) {
                fact.setInitProperty(CimConstants.TreeNodeBaseInfo.REF_CIM_ID, node.getRefCimId());
            }
            if (StringUtils.isNotBlank(node.getRelationType())) {
                fact.setInitProperty(CimConstants.TreeNodeBaseInfo.RELATION_TYPE, node.getRelationType());
            }
            if (StringUtils.isNotBlank(node.getFilter())) {
                fact.setInitProperty(CimConstants.TreeNodeBaseInfo.FILTER, node.getFilter());
            }
            if (StringUtils.isNotBlank(node.getCreator())) {
                fact.setInitProperty(CimConstants.CREATOR, node.getCreator());
            }
            if (StringUtils.isNotBlank(node.getUpdator())) {
                fact.setInitProperty(CimConstants.UPDATOR, node.getUpdator());
            }

            fact.setInitProperty(CimConstants.TreeNodeBaseInfo.CREATE_TIME, new Date());
            fact.setInitProperty(CimConstants.TreeNodeBaseInfo.UPDATE_TIME, new Date());
            int level = 0;
            if (targetNode != null) {
                level = targetNode.getLevel() + 1;
            }
            fact.setInitProperty(CimConstants.TreeNodeBaseInfo.LEVEL, level);
            fact = cds.addFact(fact);
            nodeFactList.add(fact);
            CommonOperationUtil.addToBelongingTenant(cds, tenantId, fact);
            //add node parent child relation
            if (targetNode != null && StringUtils.isNotBlank(targetNode.getRid())) {
                Fact parentFact = cds.getFactById(targetNode.getRid());
                parentFact.addToRelation(fact, BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE);
            }
            //add ref instance relation
            Fact nodeFact = cds.getFactById(node.getRid());
            List<Relation> refInstanceRelation = nodeFact.getAllSpecifiedRelations(BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE, RelationDirection.FROM);
            if (CollectionUtils.isNotEmpty(refInstanceRelation)) {
                for (Relation relation : refInstanceRelation) {
                    Relationable refInst = relation.getToRelationable();
                    fact.addToRelation(refInst, BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
                }
            }

            //copy child nodes recursively
            List<Relation> childNodesRelation = nodeFact.getAllSpecifiedRelations(BusinessLogicConstant.TREE_PARENT_CHILD_RELATION_TYPE, RelationDirection.FROM);
            if (CollectionUtils.isNotEmpty(childNodesRelation)) {
                NodeInfoBean nodeInfoBean = new NodeInfoBean();
                relationableToNodeInfo(fact, nodeInfoBean);
                for (Relation relation : childNodesRelation) {
                    Relationable rlab = relation.getToRelationable();
                    NodeInfoBean childNodeInfoBean = new NodeInfoBean();
                    relationableToNodeInfo(rlab, childNodeInfoBean);
                    nodeCopyRecursive(treeDefId, tenantId, nodeInfoBean, childNodeInfoBean, cds, nodeFactList);
                }
            }
        } catch (CimDataEngineRuntimeException e) {
            log.error("tree node copy failed", e);
        } catch (CimDataEngineInfoExploreException e) {
            log.error("add to tenant failed", e);
        }

    }

    /**
     * 递归复制指定树节点及其子节点，并新增节点数据权限到用户的某一方案
     *
     * @param treeDefId
     * @param tenantId
     * @param userId
     * @param nodeCopyInput
     */
    public boolean nodeCopyRecursiveWithPermission(String treeDefId, String tenantId, String userId,
                                                   NodeCopyInputBean nodeCopyInput) {
        boolean flag = false;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            List<Fact> nodeFactList = new ArrayList<>();
            nodeCopyRecursive(treeDefId, tenantId, nodeCopyInput.getTargetNodeInfo(), nodeCopyInput.getNodeInfo(), cds, nodeFactList);
            if (CollectionUtils.isNotEmpty(nodeFactList)) {
                List<String> schemaIds = dataPermissionService.getPermissionSchemaByUser(cds, userId);
                String schemaId = schemaIds.get(0);
                dataPermissionService.addDefaultDataPermission(nodeFactList, schemaId, cds);
            }
            flag = true;
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return flag;
    }

}
