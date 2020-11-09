package com.glodon.pcop.cimsvc.util.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.input.TreeNodeInputBean;
import com.glodon.pcop.cimsvc.model.output.TreeNodeOutputBean;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.glodon.pcop.cimsvc.service.tree.TreeNodeService;
import com.glodon.pcop.cimsvc.service.tree.TreeServiceWithPermission;
import com.glodon.pcop.cimsvc.service.v2.ContentService;
import com.google.gson.Gson;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeDataMigrationUtil {
    private static Logger log = LoggerFactory.getLogger(TreeDataMigrationUtil.class);

    private static TreeServiceWithPermission treeService = new TreeServiceWithPermission();
    private static TreeNodeService treeNodeService = new TreeNodeService();
    private static ContentService contentService = new ContentService();

    static {
        treeNodeService.setTreeService(treeService);
    }

    public static void dataMigration(String tenantId, String userId, String treeDefId, TreeNodeInputBean oldParentNode,
                                     NodeInfoBean newParentNode) throws EntityNotFoundException {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            List<TreeNodeOutputBean> treeNodeOutputBeans = contentService.listChildNode(tenantId, oldParentNode);
            log.info("root node size [{}]", treeNodeOutputBeans.size());
            for (TreeNodeOutputBean outputBean : treeNodeOutputBeans) {
                oneToOneRecursively(cds, tenantId, userId, treeDefId, outputBean, newParentNode);
                // break;
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }


    public static void oneToOneRecursively(CimDataSpace cds, String tenantId, String userId, String treeDefId,
                                           TreeNodeOutputBean treeNodeOutputBean,
                                           NodeInfoBean parentNode) throws EntityNotFoundException {
        switch (treeNodeOutputBean.getNodeType()) {
            case INSTANCE:
                log.info("add instance node rid [{}]", treeNodeOutputBean);
                Map<String, String> rmap1 = treeNodeService.addInstanceNodeByRid(cds, tenantId, userId, treeDefId,
                        parentNode, Arrays.asList(treeNodeOutputBean.getInstanceRid()));
                for (Map.Entry<String, String> entry : rmap1.entrySet()) {
                    if (StringUtils.isNotBlank(entry.getValue())) {
                        NodeInfoBean tmpParentNode = treeService.getNodeInfoByRid(cds, entry.getValue(), treeDefId);
                        TreeNodeInputBean inputBean = new TreeNodeInputBean(treeNodeOutputBean.getId(),
                                treeNodeOutputBean.getInstanceRid(), TreeNodeInputBean.TreeNodeTypeEnum.INSTANCE,
                                treeNodeOutputBean.getParentIndustryRid());
                        List<TreeNodeOutputBean> treeNodeOutputBeans = contentService.listChildNode(tenantId,
                                inputBean);
                        for (TreeNodeOutputBean nodeOutputBean : treeNodeOutputBeans) {
                            oneToOneRecursively(cds, tenantId, userId, treeDefId, nodeOutputBean, tmpParentNode);
                        }
                    } else {
                        log.error("add instance node of [{}] failed", treeNodeOutputBean.getInstanceRid());
                    }
                }
                break;
            case OBJECT:
                log.info("add object node rid [{}]", treeNodeOutputBean);
                Map<String, String> rmap2 = treeNodeService.addObjectTypeNodes(cds, tenantId, userId, treeDefId,
                        parentNode, Arrays.asList(treeNodeOutputBean.getId()));
                for (Map.Entry<String, String> entry : rmap2.entrySet()) {
                    NodeInfoBean tmpParentNode = treeService.getNodeInfoByRid(cds, entry.getValue(), treeDefId);
                    TreeNodeInputBean inputBean = new TreeNodeInputBean(treeNodeOutputBean.getId(),
                            treeNodeOutputBean.getInstanceRid(), TreeNodeInputBean.TreeNodeTypeEnum.OBJECT,
                            treeNodeOutputBean.getParentIndustryRid());
                    // List<TreeNodeOutputBean> treeNodeOutputBeans = contentService.listChildNode(tenantId,
                    // inputBean);
                    List<TreeNodeOutputBean> treeNodeOutputBeans = treeNodeOutputBean.getLinkedInstances();
                    if (treeNodeOutputBeans != null) {
                        for (TreeNodeOutputBean nodeOutputBean : treeNodeOutputBeans) {
                            oneToOneRecursively(cds, tenantId, userId, treeDefId, nodeOutputBean, tmpParentNode);
                        }
                    } else {
                        log.error("no instance related with this object [{}]", treeNodeOutputBean.getId());
                    }
                }
                break;
            case FILE:
                log.info("add file node rid [{}]", treeNodeOutputBean);
                Map<String, String> rmap3 = treeNodeService.addFileNodeByRidWithMetadata(cds, tenantId, userId,
                        treeDefId, parentNode, Arrays.asList(treeNodeOutputBean.getInstanceRid()));
                for (Map.Entry<String, String> entry : rmap3.entrySet()) {
                    NodeInfoBean tmpParentNode = treeService.getNodeInfoByRid(cds, entry.getValue(), treeDefId);
                    TreeNodeInputBean inputBean = new TreeNodeInputBean(treeNodeOutputBean.getId(),
                            treeNodeOutputBean.getInstanceRid(), TreeNodeInputBean.TreeNodeTypeEnum.FILE,
                            treeNodeOutputBean.getParentIndustryRid());
                    List<TreeNodeOutputBean> treeNodeOutputBeans = contentService.listChildNode(tenantId,
                            inputBean);
                    for (TreeNodeOutputBean nodeOutputBean : treeNodeOutputBeans) {
                        oneToOneRecursively(cds, tenantId, userId, treeDefId, nodeOutputBean, tmpParentNode);
                    }
                }
                break;
            case INDUSTRY:
                log.info("add industry node rid [{}]", treeNodeOutputBean);
                Map<String, String> rmap4 = treeNodeService.addIndustryNodeByRid(cds, tenantId, userId, treeDefId,
                        parentNode, Arrays.asList(treeNodeOutputBean.getInstanceRid()), new HashMap<>());
                log.info("industry node add result: {}", rmap4);
                for (Map.Entry<String, String> entry : rmap4.entrySet()) {
                    NodeInfoBean tmpParentNode = treeService.getNodeInfoByRid(cds, entry.getValue(), treeDefId);
                    TreeNodeInputBean inputBean = new TreeNodeInputBean(treeNodeOutputBean.getId(),
                            treeNodeOutputBean.getInstanceRid(), TreeNodeInputBean.TreeNodeTypeEnum.INDUSTRY,
                            treeNodeOutputBean.getParentIndustryRid());
                    List<TreeNodeOutputBean> treeNodeOutputBeans = contentService.listChildNode(tenantId,
                            inputBean);
                    log.info("industry child nodes : {}", ArrayUtils.toString(treeNodeOutputBeans));
                    for (TreeNodeOutputBean nodeOutputBean : treeNodeOutputBeans) {
                        oneToOneRecursively(cds, tenantId, userId, treeDefId, nodeOutputBean, tmpParentNode);
                    }
                }
                break;
            default:
                log.error("unsupport node type: {}", (new Gson()).toJson(treeNodeOutputBean));
                break;
        }
    }

    public static void main(String[] args) {

        String tenant = "3";
        String userID = "6508264434020594292";

        // String tenant = "2";
        // String userID = "6468343598790656434";


        // String tenant = "1";
        // String userID = "6435737162427609322";

        String treeDefId = "DATA_MANAGER_CONTENT_TREE";
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";
        TreeNodeInputBean treeNodeInputBean = new TreeNodeInputBean();
        try {
            dataMigration(tenant, userID, treeDefId, treeNodeInputBean, null);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

    }

}
