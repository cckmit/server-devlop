package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cimsvc.model.tree.LinkObjectTypeAndInstanceInputBean;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.glodon.pcop.cimsvc.model.tree.ObjectAndInstanceBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

@Ignore
public class TreeNodeServiceTest {
    private static TreeNodeService treeNodeService = new TreeNodeService();

    private static TreeServiceWithPermission treeService = new TreeServiceWithPermission();

    private static String permissionSchemaTree = "DATA_PERMISSION_SCHEMA_TREE";
    private static String permissionSchema = "DATA_PERMISSION_SCHEMA";

    private static String dataManagerTree = "DATA_MANAGER_CONTENT_TREE";

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";
        treeNodeService.setTreeService(treeService);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addChildObjectTypeNodes() {
        String tenantId = "3";
        String userId = "1003";
        String objectTypeId = permissionSchema;
        String treeDefId = dataManagerTree;

        NodeInfoBean parentNode = new NodeInfoBean();
        parentNode.setID("DATA_MANAGER_CONTENT_TREE_5f06e00a-f880-4148-b320-0cdb919b5cbf_1560933020849");
        parentNode.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
        parentNode.setTreeDefId(treeDefId);

        List<String> objIdList = Arrays.asList("QDTrees", "QDRivers");
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            System.out.println("add object nodes: " + (treeNodeService.addObjectTypeNodes(cds, tenantId, userId,
                    treeDefId, parentNode, objIdList)));
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void addRootObjectTypeNodes() {
        String tenantId = "3";
        String userId = "1003";
        String objectTypeId = permissionSchema;
        String treeDefId = dataManagerTree;

        NodeInfoBean parentNode = null;

        List<String> objIdList = Arrays.asList("QDTrees", "QDRivers");
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            System.out.println("add object nodes: " + (treeNodeService.addObjectTypeNodes(cds, tenantId, userId,
                    treeDefId, parentNode, objIdList)));
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void addChildInstanceNodeByRid() {
        String tenantId = "3";
        String userId = "1003";
        String objectTypeId = permissionSchema;
        String treeDefId = dataManagerTree;

        NodeInfoBean parentNode = new NodeInfoBean();
        parentNode.setID("DATA_MANAGER_CONTENT_TREE_5f06e00a-f880-4148-b320-0cdb919b5cbf_1560933020849");
        parentNode.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
        parentNode.setTreeDefId(treeDefId);

        List<String> objIdList = Arrays.asList(" #457:0", "#458:0", "#459:0");
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            System.out.println("add object nodes: " + (treeNodeService.addInstanceNodeByRid(cds, tenantId, userId,
                    treeDefId, parentNode, objIdList)));
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void addRootInstanceNodeByRid() {
        String tenantId = "3";
        String userId = "1003";
        String objectTypeId = permissionSchema;
        String treeDefId = dataManagerTree;

        NodeInfoBean parentNode = null;

        List<String> objIdList = Arrays.asList(" #457:0", "#458:0", "#459:0");
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            System.out.println("add object nodes: " + (treeNodeService.addInstanceNodeByRid(cds, tenantId, userId,
                    treeDefId, parentNode, objIdList)));
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void linkObjectAndInstances() {
        String tenantId = "3";
        String userId = "1003";
        String objectTypeId = permissionSchema;
        String treeDefId = dataManagerTree;
        CimConstants.defauleSpaceName = "pcopcim";

        NodeInfoBean parentNode = new NodeInfoBean();
        parentNode.setID("DATA_MANAGER_CONTENT_TREE_53d2e6b1-e651-4f0d-bf30-b8bddfadd21b_1561600664283");
        parentNode.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
        parentNode.setTreeDefId(treeDefId);

        ObjectAndInstanceBean objectAndInstanceBean1 = new ObjectAndInstanceBean();
        objectAndInstanceBean1.setObjectTypeId("yuanjk0409bb");
        List<String> rids1 = Arrays.asList("#4960:0", "#4962:0");
        objectAndInstanceBean1.setInstanceRids(rids1);

        ObjectAndInstanceBean objectAndInstanceBean2 = new ObjectAndInstanceBean();
        objectAndInstanceBean2.setObjectTypeId("yuanjk0409cc");
        List<String> rids2 = Arrays.asList("#4968:2");
        objectAndInstanceBean2.setInstanceRids(rids2);

        LinkObjectTypeAndInstanceInputBean inputBean = new LinkObjectTypeAndInstanceInputBean();
        inputBean.setParentNodeInfo(parentNode);
        inputBean.setObjectAndInstances(Arrays.asList(objectAndInstanceBean1, objectAndInstanceBean2));

        treeNodeService.linkObjectAndInstance(tenantId, treeDefId, userId, inputBean);
    }

    @Test
    public void linkInstances() {
        String tenantId = "3";
        String userId = "1003";
        String objectTypeId = permissionSchema;
        String treeDefId = dataManagerTree;
        CimConstants.defauleSpaceName = "pcopcim";

        NodeInfoBean parentNode = new NodeInfoBean();
        parentNode.setID("DATA_MANAGER_CONTENT_TREE_53d2e6b1-e651-4f0d-bf30-b8bddfadd21b_1561600664283");
        parentNode.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
        parentNode.setTreeDefId(treeDefId);

        ObjectAndInstanceBean objectAndInstanceBean2 = new ObjectAndInstanceBean();
        // objectAndInstanceBean2.setObjectTypeId("yuanjk0409cc");
        List<String> rids2 = Arrays.asList("#4968:2");
        objectAndInstanceBean2.setInstanceRids(rids2);

        LinkObjectTypeAndInstanceInputBean inputBean = new LinkObjectTypeAndInstanceInputBean();
        inputBean.setParentNodeInfo(parentNode);
        inputBean.setObjectAndInstances(Arrays.asList(objectAndInstanceBean2));

        treeNodeService.linkObjectAndInstance(tenantId, treeDefId, userId, inputBean);
    }

    @Test
    public void linkObject() {
        String tenantId = "3";
        String userId = "1003";
        String objectTypeId = permissionSchema;
        String treeDefId = dataManagerTree;
        CimConstants.defauleSpaceName = "pcopcim";

        NodeInfoBean parentNode = new NodeInfoBean();
        parentNode.setID("DATA_MANAGER_CONTENT_TREE_415bfada-ca4b-45c0-91bf-01a98f5275b2_1561602807787");
        parentNode.setNodeType(NodeInfoBean.NodeType.INSTANCE);
        parentNode.setTreeDefId(treeDefId);

        ObjectAndInstanceBean objectAndInstanceBean1 = new ObjectAndInstanceBean();
        objectAndInstanceBean1.setObjectTypeId("yuanjk0409bb");
        List<String> rids1 = Arrays.asList("#4960:0", "#4962:0");
        objectAndInstanceBean1.setInstanceRids(rids1);

        ObjectAndInstanceBean objectAndInstanceBean2 = new ObjectAndInstanceBean();
        objectAndInstanceBean2.setObjectTypeId("yuanjk0409cc");
        // List<String> rids2 = Arrays.asList("#4968:2");
        // objectAndInstanceBean2.setInstanceRids(rids2);

        LinkObjectTypeAndInstanceInputBean inputBean = new LinkObjectTypeAndInstanceInputBean();
        inputBean.setParentNodeInfo(parentNode);
        inputBean.setObjectAndInstances(Arrays.asList(objectAndInstanceBean1, objectAndInstanceBean2));

        treeNodeService.linkObjectAndInstance(tenantId, treeDefId, userId, inputBean);
    }
}