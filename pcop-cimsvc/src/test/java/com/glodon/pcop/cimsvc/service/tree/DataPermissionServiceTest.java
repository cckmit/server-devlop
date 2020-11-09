package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cimsvc.config.properties.ServiceEhcacheConfig;
import com.glodon.pcop.cimsvc.model.tree.DataPermissionAddInputBean;
import com.glodon.pcop.cimsvc.model.tree.DataPermissionBean;
import com.glodon.pcop.cimsvc.util.ServiceCacheUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Ignore
public class DataPermissionServiceTest {

    private static ServiceEhcacheConfig ehcacheConfig;
    private static ServiceCacheUtil cacheUtil;
    private static DataPermissionCache dataPermissionCache;
    private static DataPermissionService dataPermissionService;

    private static String permissionSchemaId = "DATA_PERMISSION_SCHEMA_TREE_1791dc08-c8e3-4495-ae06" +
            "-cf97ad2a583f_1562642611914";

    // private static String permissionSchemaId = "DATA_PERMISSION_SCHEMA_TREE_7544297b-5e7f-4cdd-b75d" +
    //         "-1e25a2a1e968_1562643000105";

    private static String userId = "1";
    private static String tenantId = "3";

    // private static Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";

        ehcacheConfig = new ServiceEhcacheConfig();
        ehcacheConfig.setClusterEnable(true);
        ehcacheConfig.setTerracottaServer("terracotta://localhost:9410/clustered");
        ehcacheConfig.setDefaultResources("main");
        ehcacheConfig.setPrimaryResources("primary-server-resource");

        cacheUtil = new ServiceCacheUtil();
        cacheUtil.setEhcacheConfig(ehcacheConfig);

        dataPermissionCache = new DataPermissionCache();
        dataPermissionService = new DataPermissionService();

        dataPermissionService.setPermissionCache(dataPermissionCache);

        dataPermissionCache.setCacheUtil(cacheUtil);
        dataPermissionCache.setDataPermissionService(dataPermissionService);
        dataPermissionCache.setEhcacheConfig(ehcacheConfig);

        // dataPermissionCache.dataPermissionCacheInit();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void isAccessiable() {
    }

    @Test
    public void addDataPermission() {
        CimConstants.defauleSpaceName = "pcopcim";
        String tenantId = "3";
        String treeDefId = "DATA_MANAGER_CONTENT_TREE";
        String permissionSchemaId = "DATA_PERMISSION_SCHEMA_TREE_7544297b-5e7f-4cdd-b75d-1e25a2a1e968_1562643000105";

        DataPermissionAddInputBean inputBean1 = new DataPermissionAddInputBean();
        inputBean1.setNodeId("DATA_MANAGER_CONTENT_TREE_09847a89-af58-4617-bfac-165aecf6f3d1_1562136888955");
        inputBean1.setReadPermission(1);
        inputBean1.setWritePermission(1);
        inputBean1.setDeletePermission(2);

        DataPermissionAddInputBean inputBean2 = new DataPermissionAddInputBean();
        inputBean2.setNodeId("DATA_MANAGER_CONTENT_TREE_4258c25e-ab0e-4b00-8989-a0cc6134e49b_1562136889173");
        inputBean2.setReadPermission(1);
        inputBean2.setWritePermission(1);
        inputBean2.setDeletePermission(1);

        List<DataPermissionAddInputBean> inputBeans = new ArrayList<>();
        inputBeans.add(inputBean1);
        inputBeans.add(inputBean2);

        System.out.println("data permission: " + dataPermissionService.addDataPermission(treeDefId, permissionSchemaId,
                inputBeans));

    }

    @Test
    public void updateDataPermission() {
        CimConstants.defauleSpaceName = "pcopcim";
        String tenantId = "3";
        String treeDefId = "DATA_MANAGER_CONTENT_TREE";
        // String permissionSchemaId = "DATA_PERMISSION_SCHEMA_TREE_1791dc08-c8e3-4495-ae06-cf97ad2a583f_1562642611914";

        DataPermissionBean inputBean1 = new DataPermissionBean();
        inputBean1.setRid("#6364:5");
        inputBean1.setNodeId("DATA_MANAGER_CONTENT_TREE_09847a89-af58-4617-bfac-165aecf6f3d1_1562136888955");
        inputBean1.setReadPermission(1);
        inputBean1.setWritePermission(0);
        inputBean1.setDeletePermission(0);

        DataPermissionBean inputBean2 = new DataPermissionBean();
        inputBean2.setRid("#6357:10");
        inputBean2.setNodeId("DATA_MANAGER_CONTENT_TREE_4258c25e-ab0e-4b00-8989-a0cc6134e49b_1562136889173");
        inputBean2.setReadPermission(2);
        inputBean2.setWritePermission(2);
        inputBean2.setDeletePermission(2);

        List<DataPermissionBean> inputBeans = new ArrayList<>();
        inputBeans.add(inputBean1);
        inputBeans.add(inputBean2);

        System.out.println("update result: " + (new Gson().toJson(dataPermissionService.updateDataPermission(treeDefId,
                permissionSchemaId, inputBeans))));

    }

    @Test
    public void queryDataPermission() {
        CimConstants.defauleSpaceName = "pcopcim";
        String tenantId = "3";
        String userId = "1";
        String treeDefId = "DATA_MANAGER_CONTENT_TREE";
        // String permissionSchemaId = "DATA_PERMISSION_SCHEMA_TREE_1791dc08-c8e3-4495-ae06-cf97ad2a583f_1562642611914";

        List<String> nodeIds = Arrays.asList("DATA_MANAGER_CONTENT_TREE_09847a89-af58-4617-bfac" +
                "-165aecf6f3d1_1562136888955", "DATA_MANAGER_CONTENT_TREE_4258c25e-ab0e-4b00-8989" +
                "-a0cc6134e49b_1562136889173");

        System.out.println("permissions: " + (new Gson().toJson(dataPermissionService.queryDataPermission(userId,
                treeDefId, permissionSchemaId, nodeIds))));
    }

    @Test
    public void deleteDataPermission() {
        CimConstants.defauleSpaceName = "pcopcim";
        String tenantId = "3";
        String treeDefId = "DATA_MANAGER_CONTENT_TREE";
        // String permissionSchemaId = "DATA_PERMISSION_SCHEMA_TREE_02faaffd-ff44-4c5a-9f7b-62b0e80e058b_1561365414099";
        List<String> permissionsRids = Arrays.asList("#6362:5", "#6363:5", "#6364:4", "#6357:9", "#6358:9", "#6359:8"
                , "#6360:8", "#6361:7", "#6362:6", "#6363:6");

        System.out.println("delete result: " + (new Gson().toJson(dataPermissionService.deleteDataPermission(treeDefId,
                permissionSchemaId, permissionsRids))));

    }

    @Test
    public void queryDataPermissionByUser() {
        String nodeRid = "#5625:1043";
        List<String> schemaIds = Arrays.asList("DATA_PERMISSION_SCHEMA_TREE_1791dc08-c8e3-4495-ae06" +
                "-cf97ad2a583f_1562642611914", "DATA_PERMISSION_SCHEMA_TREE_7544297b-5e7f-4cdd-b75d" +
                "-1e25a2a1e968_1562643000105");
        System.out.println(String.format("user [%s] nodeId [%s] schemaIds [%s] data permissions [%s]", userId, nodeRid
                , StringUtils.join(schemaIds, ','), dataPermissionService.getDataPermissionByUser(nodeRid, schemaIds)));
    }

    @Test
    public void getPermissionSchemaByUser() {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            System.out.println("user schemas [" + dataPermissionService.getPermissionSchemaByUser(cds, userId) + "]");
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void addDataPermissionByUser() {
        String userId = "1";

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            System.out.println("add data permission by user:  [" + dataPermissionService.addDataPermissionByUser(cds,
                    userId, Arrays.asList("#5625:1045")) + "]");
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }

}