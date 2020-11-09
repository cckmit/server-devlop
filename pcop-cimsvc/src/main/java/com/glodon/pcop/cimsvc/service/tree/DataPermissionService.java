package com.glodon.pcop.cimsvc.service.tree;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.DataPermissionSchemaProperties;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.InValueFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cimsvc.model.tree.DataPermissionAddInputBean;
import com.glodon.pcop.cimsvc.model.tree.DataPermissionBean;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataPermissionService {
    private static final Logger log = LoggerFactory.getLogger(DataPermissionService.class);

    @Autowired
    private DataPermissionCache permissionCache;

    private static final int DEFAULT_PERMISSION = 0;
    private static final int ALL_PERMISSION = 2;

    /**
     * 新增节点的数据权限到指定的权限方案，节点ID
     *
     * @param treeDefId
     * @param permissionSchemaId
     * @param dataPermissions
     * @return
     */
    public Map<String, Boolean> addDataPermission(String treeDefId, String permissionSchemaId,
            List<DataPermissionAddInputBean> dataPermissions) {
        Map<String, Boolean> rs = new HashMap<>();

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            ExploreParameters ep = new ExploreParameters();
            InformationExplorer ie = cds.getInformationExplorer();

            Fact schemaFact = getPermissionSchemaById(cds, permissionSchemaId);
            if (schemaFact != null) {
                //权限数据处理
                List<Object> nodeIds = new ArrayList<>();
                Map<String, DataPermissionAddInputBean> nodeIdMap = new HashMap<>();
                for (DataPermissionAddInputBean permissionInput : dataPermissions) {
                    rs.put(permissionInput.getNodeId(), false);

                    Map<String, Object> perms = new HashMap<>();
                    if (permissionInput.getReadPermission() != null) {
                        perms.put(DataPermissionSchemaProperties.READ_PERMISSION, permissionInput.getReadPermission());
                    } else {
                        perms.put(DataPermissionSchemaProperties.READ_PERMISSION, DEFAULT_PERMISSION);
                    }
                    if (permissionInput.getWritePermission() != null) {
                        perms.put(DataPermissionSchemaProperties.WRITE_PERMISSION,
                                permissionInput.getWritePermission());
                    } else {
                        perms.put(DataPermissionSchemaProperties.WRITE_PERMISSION, DEFAULT_PERMISSION);
                    }
                    if (permissionInput.getDeletePermission() != null) {
                        perms.put(DataPermissionSchemaProperties.DELETE_PERMISSION,
                                permissionInput.getDeletePermission());
                    } else {
                        perms.put(DataPermissionSchemaProperties.DELETE_PERMISSION, DEFAULT_PERMISSION);
                    }

                    if (perms.size() > 0) {
                        nodeIds.add(permissionInput.getNodeId().trim());
                        permissionInput.setRelationProperties(perms);
                        nodeIdMap.put(permissionInput.getNodeId(), permissionInput);
                    }
                }
                log.info("node permission input: {}", JSON.toJSONString(nodeIdMap));

                FilteringItem inValueFilteringItem = new InValueFilteringItem(CimConstants.GeneralProperties.ID,
                        nodeIds);
                ep.setType(treeDefId);
                ep.setDefaultFilteringItem(inValueFilteringItem);

                List<Fact> nodeList = ie.discoverInheritFacts(ep);
                if (nodeList != null) {
                    for (Fact fact : nodeList) {
                        try {
                            String nodeId =
                                    fact.getProperty(CimConstants.GeneralProperties.ID).getPropertyValue().toString();
                            log.info("facte rid={}, nodeId={}", fact.getId(), nodeId);
                            DataPermissionAddInputBean addInputBean = nodeIdMap.get(nodeId);
                            Relation relation = schemaFact.addFromRelation(fact,
                                    BusinessLogicConstant.TREE_NODE_AND_DATA_PERMISSION_SCHEMA
                                    , addInputBean.getRelationProperties());
                            if (relation != null) {
                                rs.put(nodeId, true);
                                DataPermissionBean permissionBean = new DataPermissionBean(nodeId, relation.getId(),
                                        addInputBean.getReadPermission(), addInputBean.getWritePermission(),
                                        addInputBean.getDeletePermission());

                                permissionCache.addOrUpdateDataPermission(permissionSchemaId, permissionBean,
                                        fact.getId());
                            }
                        } catch (Exception e) {
                            log.error("data permission of node {} failed", fact.getId());
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                log.error("permisssion of {} not found", permissionSchemaId);
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
        return rs;
    }

    /**
     * 根据权限方案ID和节点RID批量添加权限
     *
     * @param cds
     * @param permissionSchemaId
     * @param dataPermissions    节点RID
     * @return
     */
    public Map<String, Boolean> addDataPermissionBySchema(CimDataSpace cds, String permissionSchemaId,
            List<DataPermissionAddInputBean> dataPermissions) {
        Map<String, Boolean> rs = new HashMap<>();
        Assert.notEmpty(dataPermissions, "data permission input is empty");
        Assert.hasText(permissionSchemaId, "permissionSchemaId is mandatory");
        Fact schemaFact = getPermissionSchemaById(cds, permissionSchemaId);
        if (schemaFact != null) {
            //权限数据处理
            Map<String, DataPermissionAddInputBean> nodeRidMap = new HashMap<>();
            for (DataPermissionAddInputBean permissionInput : dataPermissions) {
                rs.put(permissionInput.getNodeId(), false);

                Map<String, Object> perms = new HashMap<>();
                if (permissionInput.getReadPermission() != null) {
                    perms.put(DataPermissionSchemaProperties.READ_PERMISSION, permissionInput.getReadPermission());
                } else {
                    perms.put(DataPermissionSchemaProperties.READ_PERMISSION, DEFAULT_PERMISSION);
                }
                if (permissionInput.getWritePermission() != null) {
                    perms.put(DataPermissionSchemaProperties.WRITE_PERMISSION,
                            permissionInput.getWritePermission());
                } else {
                    perms.put(DataPermissionSchemaProperties.WRITE_PERMISSION, DEFAULT_PERMISSION);
                }
                if (permissionInput.getDeletePermission() != null) {
                    perms.put(DataPermissionSchemaProperties.DELETE_PERMISSION,
                            permissionInput.getDeletePermission());
                } else {
                    perms.put(DataPermissionSchemaProperties.DELETE_PERMISSION, DEFAULT_PERMISSION);
                }
                if (perms.size() > 0) {
                    permissionInput.setRelationProperties(perms);
                    nodeRidMap.put(permissionInput.getNodeRid(), permissionInput);
                }
            }
            log.info("node permission input: {}", new Gson().toJson(nodeRidMap));
            for (DataPermissionAddInputBean inputBean : dataPermissions) {
                try {
                    Fact fact = cds.getFactById(inputBean.getNodeRid());
                    if (fact == null) {
                        log.error("fact of [{}] not found", inputBean.getNodeRid());
                        continue;
                    }
                    String nodeId = fact.getProperty(CimConstants.GeneralProperties.ID).getPropertyValue().toString();
                    log.debug("facte rid={}, nodeId={}", fact.getId(), nodeId);
                    DataPermissionAddInputBean addInputBean = nodeRidMap.get(fact.getId());
                    Relation relation = schemaFact.addFromRelation(fact,
                            BusinessLogicConstant.TREE_NODE_AND_DATA_PERMISSION_SCHEMA
                            , addInputBean.getRelationProperties());
                    if (relation != null) {
                        rs.put(nodeId, true);
                        DataPermissionBean permissionBean = new DataPermissionBean(nodeId, relation.getId(),
                                addInputBean.getReadPermission(), addInputBean.getWritePermission(),
                                addInputBean.getDeletePermission());

                        permissionCache.addOrUpdateDataPermission(permissionSchemaId, permissionBean, fact.getId());
                    }
                } catch (Exception e) {
                    log.error("data permission of node {} failed", inputBean.getNodeRid());
                    e.printStackTrace();
                }
            }
        }
        return rs;
    }

    /**
     * 将指定节点添加到用户具有的所有方案中，默认权限为：222
     *
     * @param cds
     * @param userId
     * @param nodeRids 节点RID
     * @return
     */
    public Map<String, Boolean> addDataPermissionByUser(CimDataSpace cds, String userId, List<String> nodeRids) {
        Assert.notEmpty(nodeRids, "node rids is empty");
        Assert.hasText(userId, "user id is mandatory");
        List<String> schemaIds = getPermissionSchemaByUser(cds, userId);
        Assert.notEmpty(schemaIds, "no schema is assigned to this user");
        List<DataPermissionAddInputBean> permissionAddInputBeans = new ArrayList<>();
        for (String nodeRid : nodeRids) {
            permissionAddInputBeans.add(new DataPermissionAddInputBean(nodeRid));
        }

        Map<String, Boolean> rsMap = new HashMap<>();
        for (String schemaId : schemaIds) {
            rsMap.putAll(addDataPermissionBySchema(cds, schemaId, permissionAddInputBeans));
        }
        return rsMap;
    }

    /**
     * 更新节点在指定方案下的权限信息
     *
     * @param treeDefId
     * @param permissionSchemaId
     * @param dataPermissions
     * @return
     */
    public Map<String, Boolean> updateDataPermission(String treeDefId, String permissionSchemaId,
            List<DataPermissionBean> dataPermissions) {
        Map<String, Boolean> rs = new HashMap<>();
        Map<String, DataPermissionBean> updateInputBeanMap = new HashMap<>();

        for (DataPermissionBean inputBean : dataPermissions) {
            if (StringUtils.isNotBlank(inputBean.getRid())) {
                updateInputBeanMap.put(inputBean.getRid(), inputBean);
            }
            rs.put(inputBean.getRid(), false);
        }

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            Fact schemaFact = getPermissionSchemaById(cds, permissionSchemaId);
            Assert.notNull(schemaFact, "data permission schame not found");

            List<Relation> relationList =
                    schemaFact.getAllSpecifiedRelations(BusinessLogicConstant.TREE_NODE_AND_DATA_PERMISSION_SCHEMA,
                            RelationDirection.TO);
            log.info("relation seize: {}", relationList.size());
            for (Relation relation : relationList) {
                if (updateInputBeanMap.containsKey(relation.getId())) {
                    DataPermissionBean bean = updateInputBeanMap.get(relation.getId());
                    if (updateRelationProperties(relation, bean)) {
                        rs.put(relation.getId(), true);
                        permissionCache.addOrUpdateDataPermission(permissionSchemaId, bean,
                                relation.getFromRelationable().getId());
                    } else {
                        rs.put(relation.getId(), false);
                    }
                } else {
                    log.info("data permission should not be updated, {}", relation.getId());
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

    public List<DataPermissionBean> queryDataPermission(String userId, String treeDefId, String permissionSchemaId,
            List<String> nodeIds) {
        if (StringUtils.isBlank(permissionSchemaId)) {
            return queryDataPermissionByUser(treeDefId, userId, nodeIds);
        } else {
            return queryDataPermissionBySchema(treeDefId, permissionSchemaId, nodeIds);
        }
    }

    /**
     * 查询节点在指定方案下的权限信息
     *
     * @param treeDefId
     * @param permissionSchemaId
     * @param nodeIds
     * @return
     */
    public List<DataPermissionBean> queryDataPermissionBySchema(String treeDefId, String permissionSchemaId,
            List<String> nodeIds) {
        List<DataPermissionBean> rs = new ArrayList<>();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            Fact permissionSchema = getPermissionSchemaById(cds, permissionSchemaId);
            Assert.notNull(permissionSchema, "data permission schame not found");
            List<Relation> relationList =
                    permissionSchema.getAllSpecifiedRelations(
                            BusinessLogicConstant.TREE_NODE_AND_DATA_PERMISSION_SCHEMA, RelationDirection.TO);
            for (Relation relation : relationList) {
                Relationable fromRelationable = relation.getFromRelationable();
                if (fromRelationable.hasProperty(CimConstants.GeneralProperties.ID)) {
                    String nodeId =
                            fromRelationable.getProperty(
                                    CimConstants.GeneralProperties.ID).getPropertyValue().toString();
                    if (nodeIds.contains(nodeId)) {
                        rs.add(relationableToOutputBean(nodeId, relation));
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
        return rs;
    }

    public List<DataPermissionBean> queryDataPermissionByUser(String treeDefId, String userId,
            List<String> nodeIds) {
        List<DataPermissionBean> rs = new ArrayList<>();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            ExploreParameters ep = new ExploreParameters();
            ep.setType(treeDefId);
            List<Object> nodeIdList = new ArrayList<>(nodeIds);
            FilteringItem filteringItem = new InValueFilteringItem(CimConstants.GeneralProperties.ID, nodeIdList);
            ep.setDefaultFilteringItem(filteringItem);

            List<Fact> nodeFactList = cds.getInformationExplorer().discoverInheritFacts(ep);
            List<String> schemaIds = getPermissionSchemaByUser(cds, userId);

            Assert.notEmpty(nodeFactList, "tree nodes not found");
            Assert.notEmpty(schemaIds, "schema of this user not found");
            for (Fact fact : nodeFactList) {
                rs.add(getDataPermissionByUser(fact.getId(), schemaIds));
            }
        } catch (CimDataEngineRuntimeException | CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return rs;
    }

    /**
     * 删除节点在指定方案下的权限信息
     *
     * @param treeDefId
     * @param permissionSchemaId
     * @param dataPermissionRids
     * @return
     */
    public Map<String, Boolean> deleteDataPermission(String treeDefId, String permissionSchemaId,
            List<String> dataPermissionRids) {
        Map<String, Boolean> rs = new HashMap<>();
        for (String permissionRid : dataPermissionRids) {
            rs.put(permissionRid, false);
        }
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            Fact schemaFact = getPermissionSchemaById(cds, permissionSchemaId);
            Assert.notNull(schemaFact, "data permission schame not found");

            List<Relation> relationList =
                    schemaFact.getAllSpecifiedRelations(BusinessLogicConstant.TREE_NODE_AND_DATA_PERMISSION_SCHEMA,
                            RelationDirection.TO);
            for (Relation relation : relationList) {
                if (dataPermissionRids.contains(relation.getId())) {
                    String nodeRid = relation.getFromRelationable().getId();
                    String relationRid = relation.getId();
                    if (cds.removeRelation(relationRid)) {
                        rs.put(relationRid, true);
                        DataPermissionBean permissionBean =
                                permissionCache.getDataPermissionByRelationRid(permissionSchemaId, relationRid);
                        permissionCache.removeDataPermission(permissionSchemaId, permissionBean, nodeRid);
                    } else {
                        rs.put(relation.getId(), false);
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
        return rs;
    }

    public DataPermissionBean getDataPermissionByUser(String nodeRid, List<String> schemaIds) {
        Assert.notEmpty(schemaIds, "no schema is assigned to this user");
        DataPermissionBean permission = null;
        for (String schema : schemaIds) {
            DataPermissionBean tmpPermission = permissionCache.getDataPermissionByNodeRid(schema, nodeRid);
            if (tmpPermission != null) {
                if (permission == null) {
                    permission = tmpPermission;
                    permission.setRid(null);
                } else {
                    permission.mergeDataPermission(tmpPermission);
                }
            } else {
                log.debug("data permission not found, nodeRid=[{}], schema=[{}]", nodeRid, schema);
            }
        }
        return permission;
    }

    /**
     * 查询用户关联的权限方案
     *
     * @param cds
     * @param userId
     * @return
     */
    public List<String> getPermissionSchemaByUser(CimDataSpace cds, String userId) {
        FilteringItem filteringItem = new EqualFilteringItem(CimConstants.UserAndDataPermissionSchemaMapping.USER_ID,
                userId.trim());
        ExploreParameters ep = new ExploreParameters();
        ep.setDefaultFilteringItem(filteringItem);
        ep.setType(CimConstants.UserAndDataPermissionSchemaMapping.USERID_AND_DATA_PERMISSION_MAPPING);
        InformationExplorer ie = cds.getInformationExplorer();

        try {
            List<Fact> factList = ie.discoverInheritFacts(ep);
            if (factList != null && factList.size() > 0) {
                List<String> schemaIds = new ArrayList<>();
                for (Fact fact : factList) {
                    if (fact.hasProperty(CimConstants.UserAndDataPermissionSchemaMapping.DATA_PERMISSION_ID)) {
                        schemaIds.add(fact.getProperty(
                                CimConstants.UserAndDataPermissionSchemaMapping.DATA_PERMISSION_ID).getPropertyValue().toString());
                    } else {
                        log.error("dataPermissionId not found [{}]", fact.getId());
                    }
                }
                return schemaIds;
            } else {
                log.error("no data permission schema is assign to this user [{}]", userId);
            }
        } catch (CimDataEngineRuntimeException | CimDataEngineInfoExploreException e) {
            log.error("permission schema not found", e);
        }
        return null;
    }

    /**
     * 通过方案ID查询对应的方案实例
     *
     * @param cds
     * @param id
     * @return
     */
    private Fact getPermissionSchemaById(CimDataSpace cds, String id) {
        FilteringItem filteringItem = new EqualFilteringItem(CimConstants.GeneralProperties.ID, id.trim());
        ExploreParameters ep = new ExploreParameters();
        ep.setDefaultFilteringItem(filteringItem);
        ep.setType(DataPermissionSchemaProperties.DATA_PERMISSION_SCHEMA_TREE);
        InformationExplorer ie = cds.getInformationExplorer();

        try {
            List<Fact> factList = ie.discoverInheritFacts(ep);
            if (factList != null && factList.size() > 0) {
                return factList.get(0);
            }
        } catch (CimDataEngineRuntimeException | CimDataEngineInfoExploreException e) {
            log.error("permission schema not found", e);
        }
        return null;
    }

    public void addDefaultDataPermission(List<Fact> nodeList, String permissionSchemaId, CimDataSpace cds) {
        Fact schemaFact = getPermissionSchemaById(cds, permissionSchemaId);
        for (Fact fact : nodeList) {
            try {
                String nodeId = fact.getProperty(CimConstants.GeneralProperties.ID).getPropertyValue().toString();
                log.info("fact rid={}, nodeId={}", fact.getId(), nodeId);
                Map<String, Object> perms = new HashMap<>();
                perms.put(DataPermissionSchemaProperties.WRITE_PERMISSION, ALL_PERMISSION);
                perms.put(DataPermissionSchemaProperties.DELETE_PERMISSION, ALL_PERMISSION);
                perms.put(DataPermissionSchemaProperties.READ_PERMISSION, ALL_PERMISSION);
                Relation relation = schemaFact.addFromRelation(fact,
                        BusinessLogicConstant.TREE_NODE_AND_DATA_PERMISSION_SCHEMA, perms);
                if (relation != null) {
                    DataPermissionBean permissionBean = new DataPermissionBean(nodeId, relation.getId(),
                            ALL_PERMISSION, ALL_PERMISSION, ALL_PERMISSION);
                    permissionCache.addOrUpdateDataPermission(permissionSchemaId, permissionBean, fact.getId());
                }
            } catch (Exception e) {
                log.error("data permission of node {} failed", fact.getId());
                e.printStackTrace();
            }
        }
    }


    public DataPermissionBean relationableToOutputBean(String nodeId, Relation relation) {
        DataPermissionBean outputBean = new DataPermissionBean();
        outputBean.setRid(relation.getId());
        outputBean.setNodeId(nodeId);
        if (relation.hasProperty(DataPermissionSchemaProperties.READ_PERMISSION)) {
            outputBean.setReadPermission(
                    (Integer) relation.getProperty(DataPermissionSchemaProperties.READ_PERMISSION).getPropertyValue());
        } else {
            outputBean.setReadPermission(DEFAULT_PERMISSION);
        }
        if (relation.hasProperty(DataPermissionSchemaProperties.WRITE_PERMISSION)) {
            outputBean.setWritePermission(
                    (Integer) relation.getProperty(DataPermissionSchemaProperties.WRITE_PERMISSION).getPropertyValue());
        } else {
            outputBean.setWritePermission(DEFAULT_PERMISSION);
        }
        if (relation.hasProperty(DataPermissionSchemaProperties.DELETE_PERMISSION)) {
            outputBean.setDeletePermission((Integer) relation.getProperty(
                    DataPermissionSchemaProperties.DELETE_PERMISSION).getPropertyValue());
        } else {
            outputBean.setDeletePermission(DEFAULT_PERMISSION);
        }
        return outputBean;
    }

    private boolean updateRelationProperties(Relation relation, DataPermissionBean bean) {
        boolean flag = false;
        try {
            if (relation.hasProperty(DataPermissionSchemaProperties.READ_PERMISSION)) {
                int oldVal =
                        (int) relation.getProperty(DataPermissionSchemaProperties.READ_PERMISSION).getPropertyValue();
                if (oldVal != bean.getReadPermission()) {
                    relation.updateProperty(DataPermissionSchemaProperties.READ_PERMISSION,
                            bean.getReadPermission());
                }
            } else {
                relation.addProperty(DataPermissionSchemaProperties.READ_PERMISSION,
                        bean.getReadPermission());
            }

            if (relation.hasProperty(DataPermissionSchemaProperties.WRITE_PERMISSION)) {
                int oldVal =
                        (int) relation.getProperty(DataPermissionSchemaProperties.WRITE_PERMISSION).getPropertyValue();
                if (oldVal != bean.getWritePermission()) {
                    relation.updateProperty(DataPermissionSchemaProperties.WRITE_PERMISSION,
                            bean.getWritePermission());
                }
            } else {
                relation.addProperty(DataPermissionSchemaProperties.WRITE_PERMISSION,
                        bean.getWritePermission());
            }

            if (relation.hasProperty(DataPermissionSchemaProperties.DELETE_PERMISSION)) {
                int oldVal =
                        (int) relation.getProperty(DataPermissionSchemaProperties.DELETE_PERMISSION).getPropertyValue();
                if (oldVal != bean.getDeletePermission()) {
                    relation.updateProperty(DataPermissionSchemaProperties.DELETE_PERMISSION,
                            bean.getDeletePermission());
                }
            } else {
                relation.addProperty(DataPermissionSchemaProperties.DELETE_PERMISSION,
                        bean.getDeletePermission());
            }

            flag = true;
        } catch (Exception e) {
            log.error("update relation propeties failed");
        }
        return flag;
    }

    public void setPermissionCache(DataPermissionCache permissionCache) {
        this.permissionCache = permissionCache;
    }
}
