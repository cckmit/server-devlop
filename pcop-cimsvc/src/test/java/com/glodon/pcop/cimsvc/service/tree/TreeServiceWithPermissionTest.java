package com.glodon.pcop.cimsvc.service.tree;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore
public class TreeServiceWithPermissionTest {

    private static String dataManagerContentTree = "DATA_MANAGER_CONTENT_TREE";

    private static TreeServiceWithPermission serviceWithPermission;

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "gdc";

//        serviceWithPermission = new TreeServiceWithPermission();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void expandNodes() {
    }

    @Test
    public void searchNodeByName() {
    }

    @Test
    public void listChildNodesWithFilter() {
    }

    @Test
    public void listChildNodesWithFilter1() {
    }

    @Test
    public void listRootNodesWithFilter() {
    }

    @Test
    public void childNodeCount() {
    }

    @Test
    public void childNodeCount1() {
    }

    @Test
    public void getTreeNodeMetadata() {
        String tenantId = "3";
        // Boolean includePermission = true;
        Boolean includePermission = false;

        NodeInfoBean nodeInfoBean = new NodeInfoBean();
        nodeInfoBean.setID("DATA_MANAGER_CONTENT_TREE_1eea0be3-7846-4e17-acb3-ba15808417e0_1561012725915");

        try {
            System.out.println("metadata result: " + (new Gson().toJson(serviceWithPermission.getTreeNodeMetadata(tenantId, "1",
                    dataManagerContentTree, includePermission, "", nodeInfoBean))));
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listChildNodesRecursively() {
        String tenantId = "3";
        String userId = "1";
        // Boolean includePermission = true;
        Boolean includePermission = false;

        NodeInfoBean parentNode = new NodeInfoBean();
        parentNode.setID("DATA_MANAGER_CONTENT_TREE_f8571dc9-7b9a-4990-ada5-a0db7274da35_1562136887671");
        parentNode.setTreeDefId(dataManagerContentTree);

        System.out.println("list child nodes recursively: " + JSON.toJSONString(serviceWithPermission.listChildNodesRecursively(tenantId, userId, "", dataManagerContentTree, parentNode, 2, includePermission)));

    }

    @Test
    public void deleteNodesRecursively() {
        String tenantId = "3";
        String userId = "1";
        // Boolean includePermission = true;
        Boolean includePermission = false;

        NodeInfoBean parentNode = new NodeInfoBean();
        parentNode.setID("DATA_MANAGER_CONTENT_TREE_f8571dc9-7b9a-4990-ada5-a0db7274da35_1562136887671");
        parentNode.setTreeDefId(dataManagerContentTree);

        System.out.println("list child nodes recursively: " + JSON.toJSONString(serviceWithPermission.listChildNodesRecursively(tenantId, userId, "", dataManagerContentTree, parentNode, 2, includePermission)));

    }

}