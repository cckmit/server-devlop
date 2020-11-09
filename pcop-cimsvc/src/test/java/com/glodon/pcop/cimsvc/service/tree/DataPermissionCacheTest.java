package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cimsvc.config.properties.ServiceEhcacheConfig;
import com.glodon.pcop.cimsvc.util.ServiceCacheUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class DataPermissionCacheTest {

    private static ServiceEhcacheConfig ehcacheConfig;
    private static ServiceCacheUtil cacheUtil;
    private static DataPermissionCache dataPermissionCache;
    private static DataPermissionService dataPermissionService;

    @Before
    public void setUp() throws Exception {
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

        dataPermissionCache.dataPermissionCacheInit();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void init() {
    }

    @Test
    public void removeDataPermission() {
    }

    @Test
    public void addOrUpdateDataPermission() {
    }

    @Test
    public void addOrUpdateDataPermissionByRelationRid() {
    }

    @Test
    public void addOrUpdateDataPermissionByNodeId() {
    }

    @Test
    public void getDataPermissionByRelationRid() {
    }

    @Test
    public void getDataPermissionByNodeRid() {
        String nodeRid = "";
        String schemaId = "";
        System.out.println(String.format("nodeRid [%s], schemaId [%s], data permission [%s]", nodeRid, schemaId,
                dataPermissionCache.getDataPermissionByNodeRid(schemaId, nodeRid)));

    }

    @Test
    public void dataPermissionCacheInit() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";

        dataPermissionCache.dataPermissionCacheInit();
    }

    @Test
    public void getRelationCacheNameBySchemaId() {
    }

    @Test
    public void getNodeCacheNameBySchemaId() {
    }
}