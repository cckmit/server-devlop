package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cimsvc.config.properties.ServiceEhcacheConfig;
import com.glodon.pcop.cimsvc.model.tree.DataPermissionBean;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.glodon.pcop.cimsvc.util.ServiceCacheUtil;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataPermissionCache {
    private static final Logger log = LoggerFactory.getLogger(DataPermissionCache.class);

    public static final String ID_AND_DATA_PERMISSION_CACHE = "IdAndDataPermissionCache";
    public static final String RID_AND_DATA_PERMISSION_CACHE = "RidAndDataPermissionCache";

    @Autowired
    private ServiceEhcacheConfig ehcacheConfig;

    @Autowired
    private DataPermissionService dataPermissionService;

    @Autowired
    private ServiceCacheUtil cacheUtil;

    @PostConstruct
    public void init() {
        cacheUtil.setEhcacheConfig(ehcacheConfig);
    }

    public void removeDataPermission(String schemaId, DataPermissionBean permissionBean, String nodeRid) {
        Cache<String, DataPermissionBean> beanCache = cacheUtil.getCache(getNodeCacheNameBySchemaId(schemaId),
                String.class, DataPermissionBean.class);
        // beanCache.remove(permissionBean.getNodeId());
        if (beanCache != null) {
            beanCache.remove(nodeRid);
        }

        beanCache = cacheUtil.getOrCreateCache(getRelationCacheNameBySchemaId(schemaId), String.class,
                DataPermissionBean.class);
        if (beanCache != null) {
            beanCache.remove(permissionBean.getRid());
        }
    }

    public void addOrUpdateDataPermission(String schemaId, DataPermissionBean permissionBean, String nodeRid) {
        // addOrUpdateDataPermissionByNodeRid(schemaId, permissionBean.getNodeId(), permissionBean);
        addOrUpdateDataPermissionByNodeRid(schemaId, nodeRid, permissionBean);
        addOrUpdateDataPermissionByRelationRid(schemaId, permissionBean.getRid(), permissionBean);
    }

    public void addOrUpdateDataPermissionByRelationRid(String schemaId, String relationRid,
            DataPermissionBean permissionBean) {
        Cache<String, DataPermissionBean> relationCache =
                cacheUtil.getCache(getRelationCacheNameBySchemaId(schemaId), String.class,
                        DataPermissionBean.class);
        if (relationCache == null) {
            cacheDataPermissionBySchemaId(schemaId);
        }
        relationCache = cacheUtil.getCache(getRelationCacheNameBySchemaId(schemaId), String.class,
                DataPermissionBean.class);
        DataPermissionBean tmpDataPermissionBean = relationCache.get(relationRid);
        if (tmpDataPermissionBean == null) {
            relationCache.put(relationRid, permissionBean);
        } else {
            relationCache.replace(relationRid, permissionBean);
        }
    }

    public void addOrUpdateDataPermissionByNodeRid(String schemaId, String nodeRid, DataPermissionBean permissionBean) {
        Cache<String, DataPermissionBean> nodeCache =
                cacheUtil.getCache(getNodeCacheNameBySchemaId(schemaId), String.class,
                        DataPermissionBean.class);
        if (nodeCache == null) {
            cacheDataPermissionBySchemaId(schemaId);
        }
        nodeCache = cacheUtil.getCache(getNodeCacheNameBySchemaId(schemaId), String.class,
                DataPermissionBean.class);
        DataPermissionBean tmpDataPermissionBean = nodeCache.get(nodeRid);
        if (tmpDataPermissionBean == null) {
            nodeCache.put(nodeRid, permissionBean);
        } else {
            nodeCache.replace(nodeRid, permissionBean);
        }
    }

    public DataPermissionBean getDataPermissionByRelationRid(String schemaId, String relationRid) {
        Cache<String, DataPermissionBean> relationCache =
                cacheUtil.getCache(getRelationCacheNameBySchemaId(schemaId), String.class,
                        DataPermissionBean.class);
        if (relationCache == null) {
            cacheDataPermissionBySchemaId(schemaId);
        }
        relationCache = cacheUtil.getCache(getRelationCacheNameBySchemaId(schemaId), String.class,
                DataPermissionBean.class);
        return relationCache.get(relationRid);
    }

    public DataPermissionBean getDataPermissionByNodeRid(String schemaId, String nodeRid) {
        Cache<String, DataPermissionBean> nodeCache =
                cacheUtil.getCache(getNodeCacheNameBySchemaId(schemaId), String.class,
                        DataPermissionBean.class);
        if (nodeCache == null) {
            cacheDataPermissionBySchemaId(schemaId);
        }
        nodeCache = cacheUtil.getCache(getNodeCacheNameBySchemaId(schemaId), String.class,
                DataPermissionBean.class);
        return nodeCache.get(nodeRid);
    }

    public void clearCacheBySchemaId(String schemaId) {
        Cache<String, DataPermissionBean> nodeCache =
                cacheUtil.getCache(getNodeCacheNameBySchemaId(schemaId), String.class,
                        DataPermissionBean.class);
        if (nodeCache != null) {
            nodeCache.clear();
            log.info("data permission cache of [{}] is cleared", schemaId);
        }
    }

    public void dataPermissionCacheInit() {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            ExploreParameters ep = new ExploreParameters();
            FilteringItem filteringItem = new EqualFilteringItem("nodeType", NodeInfoBean.NodeType.INSTANCE.toString());
            ep.setDefaultFilteringItem(filteringItem);
            ep.setType(CimConstants.DataPermissionSchemaProperties.DATA_PERMISSION_SCHEMA_TREE);
            InformationExplorer ie = cds.getInformationExplorer();
            List<Fact> schemaFactList = ie.discoverInheritFacts(ep);

            Assert.notEmpty(schemaFactList, "no data permission schema is found");
            for (Fact fact : schemaFactList) {
                String schemaId = fact.getProperty(CimConstants.GeneralProperties.ID).getPropertyValue().toString();

                Map<String, DataPermissionBean> relarionMap = new HashMap<>();
                Map<String, DataPermissionBean> nodeMap = new HashMap<>();
                List<Relation> relationList =
                        fact.getAllSpecifiedRelations(BusinessLogicConstant.TREE_NODE_AND_DATA_PERMISSION_SCHEMA,
                                RelationDirection.TO);
                for (Relation relation : relationList) {
                    Relationable nodeRelationable = relation.getFromRelationable();
                    String nodeId =
                            nodeRelationable.getProperty(
                                    CimConstants.GeneralProperties.ID).getPropertyValue().toString();
                    DataPermissionBean dataPermissionBean = dataPermissionService.relationableToOutputBean(nodeId,
                            relation);
                    relarionMap.put(relation.getId(), dataPermissionBean);
                    nodeMap.put(nodeRelationable.getId(), dataPermissionBean);
                    if (log.isDebugEnabled()) {
                        log.debug("relation cache: {}", relarionMap);
                        log.debug("node cache: {}", nodeMap);
                    }
                }
                // cacheUtil.destoryCache(getRelationCacheNameBySchemaId(schemaId), String.class,
                //         DataPermissionBean.class);
                // cacheUtil.destoryCache(getNodeCacheNameBySchemaId(schemaId), String.class,
                //         DataPermissionBean.class);

                // Cache<String, DataPermissionBean> relationCache =
                //         cacheUtil.createCache(getRelationCacheNameBySchemaId(schemaId), String.class,
                //                 DataPermissionBean.class);
                Cache<String, DataPermissionBean> relationCache =
                        cacheUtil.getOrCreateCache(getRelationCacheNameBySchemaId(schemaId), String.class,
                                DataPermissionBean.class);
                relationCache.putAll(relarionMap);
                log.info("permission schema [{}] relation cache size: {}", schemaId, relarionMap.size());
                // Cache<String, DataPermissionBean> nodeCache =
                //         cacheUtil.createCache(getNodeCacheNameBySchemaId(schemaId), String.class,
                //                 DataPermissionBean.class);
                Cache<String, DataPermissionBean> nodeCache =
                        cacheUtil.getOrCreateCache(getNodeCacheNameBySchemaId(schemaId), String.class,
                                DataPermissionBean.class);
                nodeCache.putAll(nodeMap);
                log.info("permission schema [{}] node cache size: {}", schemaId, nodeMap.size());
            }
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }

    private void cacheDataPermissionBySchemaId(String schemaId) {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            ExploreParameters ep = new ExploreParameters();
            FilteringItem filteringItem = new EqualFilteringItem("ID", schemaId);
            ep.setDefaultFilteringItem(filteringItem);
            ep.setType(CimConstants.DataPermissionSchemaProperties.DATA_PERMISSION_SCHEMA_TREE);
            InformationExplorer ie = cds.getInformationExplorer();
            List<Fact> schemaFactList = ie.discoverInheritFacts(ep);
            Assert.notEmpty(schemaFactList, "no data permission schema is found");

            cacheDataPermissionBySchemaFact(schemaFactList.get(0));
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    private void cacheDataPermissionBySchemaFact(Fact schemaFact) {
        String schemaId = schemaFact.getProperty(CimConstants.GeneralProperties.ID).getPropertyValue().toString();
        try {
            Map<String, DataPermissionBean> relationMap = new HashMap<>();
            Map<String, DataPermissionBean> nodeMap = new HashMap<>();
            List<Relation> relationList =
                    schemaFact.getAllSpecifiedRelations(BusinessLogicConstant.TREE_NODE_AND_DATA_PERMISSION_SCHEMA,
                            RelationDirection.TO);
            for (Relation relation : relationList) {
                Relationable nodeRelationable = relation.getFromRelationable();
                String nodeId =
                        nodeRelationable.getProperty(CimConstants.GeneralProperties.ID).getPropertyValue().toString();
                DataPermissionBean dataPermissionBean = dataPermissionService.relationableToOutputBean(nodeId,
                        relation);
                relationMap.put(relation.getId(), dataPermissionBean);
                nodeMap.put(nodeRelationable.getId(), dataPermissionBean);
                if (log.isDebugEnabled()) {
                    log.debug("relation cache: {}", relationMap);
                    log.debug("node cache: {}", nodeMap);
                }
            }

            Cache<String, DataPermissionBean> relationCache =
                    cacheUtil.getOrCreateCache(getRelationCacheNameBySchemaId(schemaId), String.class,
                            DataPermissionBean.class);
            relationCache.putAll(relationMap);
            log.info("permission schema [{}] relation cache size: {}", schemaId, relationMap.size());

            Cache<String, DataPermissionBean> nodeCache =
                    cacheUtil.getOrCreateCache(getNodeCacheNameBySchemaId(schemaId), String.class,
                            DataPermissionBean.class);
            nodeCache.putAll(nodeMap);
            log.info("permission schema [{}] node cache size: {}", schemaId, nodeMap.size());
        } catch (Exception e) {
            log.error("cache schema data permission failed", e);
        }
    }


    public String getRelationCacheNameBySchemaId(String schemaId) {
        StringBuilder sb = new StringBuilder(RID_AND_DATA_PERMISSION_CACHE);
        sb.append("__").append(schemaId);
        return sb.toString();
    }

    public String getNodeCacheNameBySchemaId(String schemaId) {
        StringBuilder sb = new StringBuilder(ID_AND_DATA_PERMISSION_CACHE);
        sb.append("__").append(schemaId);
        return sb.toString();
    }


    public void setEhcacheConfig(ServiceEhcacheConfig ehcacheConfig) {
        this.ehcacheConfig = ehcacheConfig;
    }

    public void setDataPermissionService(DataPermissionService dataPermissionService) {
        this.dataPermissionService = dataPermissionService;
    }

    public void setCacheUtil(ServiceCacheUtil cacheUtil) {
        this.cacheUtil = cacheUtil;
    }
}
