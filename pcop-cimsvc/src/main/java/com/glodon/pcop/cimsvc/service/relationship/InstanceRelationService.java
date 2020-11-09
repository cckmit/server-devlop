package com.glodon.pcop.cimsvc.service.relationship;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.relationship.AddInstanceRelationInputBean;
import com.glodon.pcop.cim.common.model.relationship.AddInstanceRelationOutputBean;
import com.glodon.pcop.cim.common.model.relationship.AddInstanceRelationSourceInputBean;
import com.glodon.pcop.cim.common.model.relationship.AddInstanceRelationSourceOutputBean;
import com.glodon.pcop.cim.common.model.relationship.AddInstanceRelationTargetInputBean;
import com.glodon.pcop.cim.common.model.relationship.AddInstanceRelationTargetOutputBean;
import com.glodon.pcop.cim.common.model.relationship.RelatedInstancesOutputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceCache.ObjectTypeCache;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.InValueFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeStatusCacheVO;
import com.glodon.pcop.cimsvc.model.RelationTypeBean;
import com.glodon.pcop.cimsvc.service.v2.RelationshipsService;
import com.glodon.pcop.cimsvc.util.ObjectTypeUtil;
import com.glodon.pcop.cimsvc.util.ServiceCacheUtil;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class InstanceRelationService {
    private static Logger log = LoggerFactory.getLogger(InstanceRelationService.class);

    @Autowired
    private RelationshipsService relationshipsService;

    @Autowired
    private ServiceCacheUtil serviceCacheUtil;

    private static final String RELATION_TYPE_NAMES_CACHE = "RELATION_TYPE_NAMES_CACHE";
    @Value("${cim.ehcache.relation-type-names: 600}")
    private long timeToLiveSeconds;

    public AddInstanceRelationOutputBean createRelations(String tenantId, AddInstanceRelationInputBean inputBean) {
        AddInstanceRelationSourceInputBean sourceInputBean = inputBean.getSoruceInstance();
        AddInstanceRelationSourceOutputBean sourceOutputBean = new AddInstanceRelationSourceOutputBean(sourceInputBean);
        AddInstanceRelationOutputBean outputBean = new AddInstanceRelationOutputBean(inputBean.getRelationType(),
                inputBean.getRelationDirectionEnum(), sourceOutputBean);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            FilteringItem equalItem = new EqualFilteringItem(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME,
                    sourceInputBean.getInstanceId());
            ExploreParameters ep = new ExploreParameters();
            ep.setDefaultFilteringItem(equalItem);
            ep.setType(sourceInputBean.getObjectTypeId());

            List<Fact> factList = cds.getInformationExplorer().discoverInheritFacts(ep);
            if (factList == null || factList.size() < 1) {
                sourceOutputBean.setSuccess(false);
                log.error("fact not found: objectTypeId=[{}], instanceId=[{}]", sourceInputBean.getObjectTypeId(),
                        sourceInputBean.getInstanceId());
            } else {
                Fact sourceFact = factList.get(0);
                Set<String> insatnceIds = directRelatedInstanceIdsByFact(sourceFact, inputBean.getRelationType(),
                        inputBean.getRelationDirectionEnum());
                List<AddInstanceRelationTargetOutputBean> targetOutputBeans = new ArrayList<>();
                outputBean.setTargetInstances(targetOutputBeans);
                for (AddInstanceRelationTargetInputBean targetInputBean : inputBean.getTargetInstances()) {
                    targetOutputBeans.add(createInstanceRelations(cds, sourceFact, inputBean.getRelationType(),
                            insatnceIds, inputBean.getRelationDirectionEnum(), targetInputBean));
                }
            }
        } catch (CimDataEngineInfoExploreException | CimDataEngineRuntimeException e) {
            log.error("fact query failed", e);
            sourceOutputBean.setSuccess(false);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return outputBean;
    }

    public AddInstanceRelationTargetOutputBean createInstanceRelations(CimDataSpace cds, Fact sourceFact,
                                                                       String relationType,
                                                                       Set<String> relatedInstanceIds,
                                                                       AddInstanceRelationInputBean.RelationDirectionEnum directionEnum,
                                                                       AddInstanceRelationTargetInputBean targetInputBean) {
        AddInstanceRelationTargetOutputBean targetOutputBean =
                new AddInstanceRelationTargetOutputBean(targetInputBean.getObjectTypeId());
        Map<String, Boolean> rsMap = new HashMap<>();
        List<String> instanceIds = targetInputBean.getInstanceIds();
        List<Object> queryInstanceIds = new ArrayList<>();
        for (String id : instanceIds) {
            rsMap.put(id, false);
            if (relatedInstanceIds.contains(id)) {
                log.debug("relation is existed: [{}]", id);
            } else {
                queryInstanceIds.add(id);
            }
        }
        targetOutputBean.setInstanceIds(rsMap);

        FilteringItem filteringItem = new InValueFilteringItem(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME,
                queryInstanceIds);
        ExploreParameters ep = new ExploreParameters();
        ep.setType(targetInputBean.getObjectTypeId());
        ep.setDefaultFilteringItem(filteringItem);
        try {
            List<Fact> factList = cds.getInformationExplorer().discoverInheritFacts(ep);
            switch (directionEnum) {
                case FROM:
                    for (Fact fact : factList) {
                        Relation relation = sourceFact.addToRelation(fact, relationType);
                        if (relation != null) {
                            String id =
                                    fact.getProperty(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME).getPropertyValue().toString();
                            rsMap.put(id, true);
                        } else {
                            log.error("add relation failed: [{}]", fact.getId());
                        }
                    }
                    break;
                case TO:
                    for (Fact fact : factList) {
                        Relation relation = sourceFact.addFromRelation(fact, relationType);
                        if (relation != null) {
                            String id =
                                    fact.getProperty(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME).getPropertyValue().toString();
                            rsMap.put(id, true);
                        } else {
                            log.error("add relation failed: [{}]", fact.getId());
                        }
                    }
                    break;
                default:
                    log.error("not support relation direction: [{}]", directionEnum);
            }
        } catch (CimDataEngineRuntimeException | CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        }

        return targetOutputBean;
    }


    public Map<String, Boolean> deleteRelationsByRid(String tennatId, List<String> relationRids) {
        Map<String, Boolean> rsMap = new HashMap<>();
        for (String rid : relationRids) {
            rsMap.put(rid, false);
        }

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            for (String rid : relationRids) {
                try {
                    rsMap.put(rid, cds.removeRelation(rid));
                } catch (CimDataEngineRuntimeException e) {
                    log.error("remove relation failed: [{}]", rid);
                }
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return rsMap;
    }

    public List<RelatedInstancesOutputBean> queryDirectRelatedInstances(String tennatId, String objectTypeId,
                                                                        String instanceId) {
        List<RelatedInstancesOutputBean> outputBeanList = new ArrayList<>();

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            Fact sourceFact = ObjectTypeUtil.queryFactById(cds, objectTypeId, instanceId);
            if (sourceFact != null) {
                List<Relation> relationList = sourceFact.getAllRelations();
                if (relationList != null && relationList.size() > 0) {
                    Set<String> relationTypesNames = getRelationTypesCache(tennatId, cds);
                    if (relationTypesNames.size() == 0) {
                        return outputBeanList;
                    }
                    for (Relation relation : relationList) {
                        if (!relationTypesNames.contains(relation.getType())) {
                            log.debug("relation filter out by type");
                            continue;
                        }
                        Relationable relationable = relation.getFromRelationable();
                        if (relationable.getId().equals(sourceFact.getId())) {
                            relationable = relation.getToRelationable();
                        }

                        RelatedInstancesOutputBean outputBean = new RelatedInstancesOutputBean(relation.getId(),
                                ((Fact) relationable).getType(), relation.getType());

                        InfoObjectTypeStatusCacheVO typeStatusCache =
                                ObjectTypeCache.getObjectStatusCacheItem(CimConstants.defauleSpaceName,
                                        ((Fact) relationable).getType());
                        if (typeStatusCache != null) {
                            outputBean.setObjectTypeName(typeStatusCache.getInfoObjectTypeDesc());
                        }
                        Map<String, Object> baseInfo = new HashMap<>();
                        baseInfo.put(CimConstants.ID_PROPERTY_TYPE_NAME,
                                relationable.getProperty(CimConstants.ID_PROPERTY_TYPE_NAME).getPropertyValue().toString());
                        if (relationable.hasProperty(CimConstants.NAME_PROPERTY_TYPE_NAME)) {
                            baseInfo.put(CimConstants.NAME_PROPERTY_TYPE_NAME,
                                    relationable.getProperty(CimConstants.NAME_PROPERTY_TYPE_NAME).getPropertyValue().toString());
                        }
                        outputBean.setBaseInfo(baseInfo);

                        outputBeanList.add(outputBean);
                    }
                }
            } else {
                log.error("fact not found: objectTypeId=[{}], instanceId=[{}]", objectTypeId, instanceId);
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return outputBeanList;
    }

    public Set<String> getRelationTypesCache(String tenantId, CimDataSpace cds) {
        HashSet<String> relationTypeNames = new HashSet<>();
        Cache<String, HashSet> relationTypeNameCache = serviceCacheUtil.getCache(RELATION_TYPE_NAMES_CACHE,
                String.class, HashSet.class);
        if (relationTypeNameCache == null || relationTypeNameCache.get(RELATION_TYPE_NAMES_CACHE) == null) {
            if (relationTypeNameCache == null) {
                log.debug("===relation type name cache time to live seconds: [{}]", timeToLiveSeconds);
                log.debug("new cache is created: [{}]", RELATION_TYPE_NAMES_CACHE);
                relationTypeNameCache = serviceCacheUtil.createCacheWithExpiry((RELATION_TYPE_NAMES_CACHE),
                        String.class, HashSet.class, timeToLiveSeconds);
            }
            List<RelationTypeBean> allRelationTypeBeans = relationshipsService.getAllRelationTypes(tenantId, cds);
            if (allRelationTypeBeans != null && allRelationTypeBeans.size() > 0) {
                for (RelationTypeBean typeBean : allRelationTypeBeans) {
                    relationTypeNames.add(typeBean.getRelationTypeName());
                }
                relationTypeNameCache.put(RELATION_TYPE_NAMES_CACHE, relationTypeNames);
                log.debug("relation type names: [{}]", JSON.toJSONString(relationTypeNames));
            } else {
                log.error("no relation types are found");
            }
        } else {
            relationTypeNames = relationTypeNameCache.get(RELATION_TYPE_NAMES_CACHE);
        }
        return relationTypeNames;
    }


    public Set<String> directRelatedInstanceIdsByFact(Fact sourceFact, String relationTypeName,
                                                      AddInstanceRelationInputBean.RelationDirectionEnum directionEnum) throws CimDataEngineRuntimeException {
        Set<String> instanceIds = new HashSet<>();

        List<Relation> relationList;
        switch (directionEnum) {
            case FROM:
                relationList = sourceFact.getAllSpecifiedRelations(relationTypeName, RelationDirection.FROM);
                break;
            case TO:
                relationList = sourceFact.getAllSpecifiedRelations(relationTypeName, RelationDirection.TO);
                break;
            default:
                relationList = sourceFact.getAllRelations();
                break;
        }
        if (relationList != null && relationList.size() > 0) {
            for (Relation relation : relationList) {
                Relationable targetRelationable = relation.getFromRelationable();
                if (targetRelationable.getId().equals(sourceFact.getId())) {
                    targetRelationable = relation.getToRelationable();
                }
                if (targetRelationable.hasProperty(CimConstants.ID_PROPERTY_TYPE_NAME)) {
                    instanceIds.add(targetRelationable.getProperty(CimConstants.ID_PROPERTY_TYPE_NAME).getPropertyValue().toString());
                } else {
                    log.error("fact not found ID property: [{}]", targetRelationable.getId());
                }
            }
        }

        return instanceIds;
    }
}
