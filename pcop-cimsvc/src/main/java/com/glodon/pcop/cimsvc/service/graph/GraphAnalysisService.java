package com.glodon.pcop.cimsvc.service.graph;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.graph.OutputInstance;
import com.glodon.pcop.cim.common.model.graph.TubulationAnalysisInputBean;
import com.glodon.pcop.cim.common.model.graph.TubulationAnalysisOutputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.*;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureauImpl.OrientDBCimDataSpaceImpl;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.CimDataEngineConstant;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.path.OrientDBShortestPath;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.input.ConnectedInputBean;
import com.glodon.pcop.cimsvc.model.input.InstanceBaseBean;
import com.glodon.pcop.cimsvc.model.output.ConnectedOutputBean;
import com.glodon.pcop.cimsvc.model.output.InstanceOutputBean;
import com.glodon.pcop.cimsvc.util.ObjectTypeUtil;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GraphAnalysisService {
    private static final Logger log = LoggerFactory.getLogger(GraphAnalysisService.class);

    private static final String REGEX_RID = "#\\d+:\\d+";
    public static Pattern patternRid = Pattern.compile(REGEX_RID);

    public static final String PH = "ph";

    public static final String COMMON_VISUAL_REPRESENTATION_OF = "COMMON_VisualRepresentationOf";

    @Value("${cim.graph-analysis.additional-object-types}")
    private String additionalTypes;

    private static Set<String> additionalTypeSet = new HashSet<>();

    @PostConstruct
    public void init() {
        if (StringUtils.isNotBlank(additionalTypes)) {
            String[] typeTokens = additionalTypes.split(",");
            for (String token : typeTokens) {
                log.info("graph analysis additional types: [{}]", token.trim());
                additionalTypeSet.add(token.trim());
            }
        }
    }

    public ConnectedOutputBean instanceConnected(ConnectedInputBean inputBean) {
        ConnectedOutputBean outputBean = new ConnectedOutputBean();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            InstanceBaseBean fromBean = inputBean.getSourceInstance();
            Assert.notNull(fromBean, "source instance is null");
            Assert.hasText(fromBean.getObjectTypeId(), "source instance object type is empty");
            Assert.hasText(fromBean.getId(), "source instance id is empty");

            Fact fromFact = ObjectTypeUtil.queryFactById(cds, fromBean.getObjectTypeId(), fromBean.getId());
            Assert.notNull(fromBean, "source insatnce not found");
            outputBean.setSourceInstance(fromBean);

            List<InstanceBaseBean> desBeans = inputBean.getDestinationInstances();
            if (desBeans != null && desBeans.size() > 0) {
                List<InstanceOutputBean> destinationOutputBeanList = new ArrayList<>();
                outputBean.setDestinationInstances(destinationOutputBeanList);
                for (InstanceBaseBean baseBean : desBeans) {
                    InstanceOutputBean tmpOutputBean = new InstanceOutputBean(baseBean);
                    destinationOutputBeanList.add(tmpOutputBean);
                    List<OrientVertex> vertexList = shortestPathVertexById(cds, fromFact.getId(), baseBean,
                            inputBean.getDirectionEnum(), inputBean.getRelationType());
                    if (vertexList.size() > 0) {
                        if (inputBean.getDirectConnect() != null && inputBean.getDirectConnect()) {//if direct connected
                            String srcObjectTypeId = fromBean.getObjectTypeId();
                            boolean flag = true;
                            for (OrientVertex ov : vertexList) {
                                String vertexType = ov.getType().getName();
                                String relationableType = null;
                                if (vertexType.startsWith(CimDataEngineConstant.CLASSPERFIX_FACT)) {
                                    relationableType = vertexType.replaceFirst(CimDataEngineConstant.CLASSPERFIX_FACT
                                            , "");
                                } else if (vertexType.startsWith(CimDataEngineConstant.CLASSPERFIX_IHFACT)) {
                                    relationableType =
                                            vertexType.replaceFirst(CimDataEngineConstant.CLASSPERFIX_IHFACT, "");
                                } else if (vertexType.startsWith(CimDataEngineConstant.CLASSPERFIX_DIMENSION)) {
                                    relationableType =
                                            vertexType.replaceFirst(CimDataEngineConstant.CLASSPERFIX_DIMENSION, "");
                                }
                                if (!srcObjectTypeId.equalsIgnoreCase(relationableType)) {
                                    tmpOutputBean.setConnected(false);
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                tmpOutputBean.setConnected(true);
                            }
                        } else {
                            tmpOutputBean.setConnected(true);
                        }
                    } else {
                        tmpOutputBean.setConnected(false);
                    }
                }
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return outputBean;
    }

    public List<OrientVertex> shortestPathVertexById(CimDataSpace cds, String fromRid, InstanceBaseBean toBean,
                                                     OrientDBShortestPath.SearchDirectionEnum directionEnum,
                                                     String relationType) {
        List<OrientVertex> vertexList = new ArrayList<>();
        if (toBean == null || StringUtils.isBlank(toBean.getId()) || StringUtils.isBlank(toBean.getObjectTypeId())) {
            log.error("error input: [{}]", toBean);
            return vertexList;
        }

        Fact firstFact = ObjectTypeUtil.queryFactById(cds, toBean.getObjectTypeId().trim(), toBean.getId().trim());
        if (firstFact != null) {
            log.error("destination vertex rid: {}", firstFact.getId());
            vertexList =
                    OrientDBShortestPath.getVerticesFromShortestPath(((OrientDBCimDataSpaceImpl) cds).getGraph(),
                            fromRid, firstFact.getId(), directionEnum, relationType);
        }

        return vertexList;
    }


    public TubulationAnalysisOutputBean tubulationAnalysis(String tenantId, TubulationAnalysisInputBean conditions)
            throws EntityNotFoundException {
        TubulationAnalysisOutputBean outputBean = new TubulationAnalysisOutputBean();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            OutputInstance srcInstance = conditions.getSourceInstance();
            InfoObjectDef objectDef = modelCore.getInfoObjectDef(srcInstance.getObjectTypeId());
            if (objectDef == null) {
                log.error("object type of [{}] not found", srcInstance.getObjectTypeId());
                throw new EntityNotFoundException("info object type not found");
            }

            ExploreParameters ep = new ExploreParameters();
            FilteringItem equalFilteringItem = new EqualFilteringItem(CimConstants.ID_PROPERTY_TYPE_NAME,
                    srcInstance.getInstanceId());
            ep.setDefaultFilteringItem(equalFilteringItem);

            InfoObjectRetrieveResult queryResult = objectDef.getObjects(ep);
            List<InfoObject> infoObjectList = queryResult.getInfoObjects();
            if (CollectionUtils.isEmpty(infoObjectList)) {
                log.error("instance not found: object type = [{}], ID = [{}]", srcInstance.getObjectTypeId(),
                        srcInstance.getInstanceId());
                throw new EntityNotFoundException("instance not found");
            }

            InfoObject srcVertex = infoObjectList.get(0);
            List<InfoObject> tubulationObjectList = srcVertex.getAllRelatedInfoObjects(COMMON_VISUAL_REPRESENTATION_OF,
                    conditions.getRelationDirection());

            if (CollectionUtils.isEmpty(tubulationObjectList)) {
                log.error("bim item related tubulation not found");
                throw new EntityNotFoundException("bim item related tubulation not found");
            }

            String traverseSql = buildTraverseSql(conditions.getRelationDirection(), conditions.getRelationTypeName(),
                    tubulationObjectList.get(0).getObjectInstanceRID());
            log.info("traverse sql: [{}]", traverseSql);
            OrientGraph graph = ((OrientDBCimDataSpaceImpl) cds).getGraph();
            List<List<String>> pathsRids = getTraversePathWithRids(graph, traverseSql);
            Set<String> outputObjectTypeIds = new HashSet<>(conditions.getTargetObjectTypeIds());

            if (CollectionUtils.isNotEmpty(pathsRids)) {
                if (CollectionUtils.isNotEmpty(additionalTypeSet)) {
                    outputObjectTypeIds.addAll(additionalTypeSet);
                }
                outputBean = filterPathRidsByObjectTypes(cds, pathsRids, outputObjectTypeIds);
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return outputBean;
    }

    public static String buildTraverseSql(RelationDirection dirc, String relationName, String srcRid) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT $path AS ").append(PH).append(" FROM ( TRAVERSE ");
        switch (dirc) {
            case FROM:
                sb.append("out");
                break;
            case TO:
                sb.append("in");
                break;
            case TWO_WAY:
                sb.append("both");
                break;
            default:
                log.error("relation direction error input: {}", dirc);
        }
        sb.append("('").append(CimDataEngineConstant.CLASSPERFIX_RELATION).append(relationName).append("')");
        sb.append(" FROM ").append(srcRid).append(" MAXDEPTH 100 STRATEGY BREADTH_FIRST)");
        return sb.toString();
    }

    public static List<List<String>> getTraversePathWithRids(OrientGraph graph, String sql) {
        List<List<String>> pathsWithRids = new ArrayList<>();

        for (Vertex v : (Iterable<Vertex>) graph.command(
                new OCommandSQL(sql)).execute()) {
            String pathStr = v.getProperty(PH);
            if (pathStr == null) {
                log.error("traverse graph no ph property return");
                continue;
            }

            String[] pathToken = pathStr.split("\\[\\d+\\]");
            List<String> tmpRids = new ArrayList<>();
            for (String token : pathToken) {
                log.debug("path token: [{}]", token);
                Matcher matcher = patternRid.matcher(token);
                if (matcher.find()) {
                    tmpRids.add(matcher.group());
                } else {
                    log.info("no rid is found");
                }
            }
            if (CollectionUtils.isNotEmpty(tmpRids)) {
                pathsWithRids.add(tmpRids);
            }
        }

        return pathsWithRids;
    }

    public static TubulationAnalysisOutputBean filterPathRidsByObjectTypes(CimDataSpace cds,
                                                                           List<List<String>> pathsRids,
                                                                           Set<String> outputObjectTypeIds) {
        TubulationAnalysisOutputBean outputBean = new TubulationAnalysisOutputBean();

        List<OutputInstance> firstLevelInstance = new ArrayList<>();
        List<OutputInstance> secondLevelInstance = new ArrayList<>();
        Set<String> selectedRids = new HashSet<>();
        Set<String> firstLevelSelectedRids = new HashSet<>();
        Set<String> secondLevelSelectedRids = new HashSet<>();
        Map<String, OutputInstance> ridInstanceMap = new HashMap<>();
        Map<String, String> ridMapping = new HashMap<>();
        for (List<String> onePathRids : pathsRids) {
            if (CollectionUtils.isEmpty(onePathRids)) {
                log.info("no rid with this path");
                continue;
            }
            String firstRid = null;
            String secondRid = null;
            for (String rid : onePathRids) {
                try {
                    if (!selectedRids.contains(rid)) {
                        Fact fact = cds.getFactById(rid);
                        String objectType = fact.getType();
                        if (!outputObjectTypeIds.contains(objectType)) {
                            log.debug("filter out by object type: [{}], fact type [{}]", rid, objectType);
                            continue;
                        } else if (additionalTypeSet.contains(fact.getType())) {
                            log.debug("filter out by additional object type: [{}], fact type [{}]", rid,
                                    objectType);
                            continue;
                        } else {
                            if (fact.hasProperty(CimConstants.ID_PROPERTY_TYPE_NAME) && fact.getProperty(
                                    CimConstants.ID_PROPERTY_TYPE_NAME) != null) {
                                log.debug("get one available fact: [{}]", rid);
                                if (!ridInstanceMap.containsKey(fact.getId())) {
                                    List<OutputInstance> outputInstanceList = getOutputInstanceByFact(fact,
                                            COMMON_VISUAL_REPRESENTATION_OF, RelationDirection.TWO_WAY);
                                    if (CollectionUtils.isNotEmpty(outputInstanceList)) {
                                        ridInstanceMap.put(fact.getId(), outputInstanceList.get(0));
                                        ridMapping.put(fact.getId(), outputInstanceList.get(0).getRid());
                                    } else {
                                        log.error("filter out: no related visual representation found");
                                    }
                                }
                            } else {
                                log.error("this fact does not contain ID");
                            }
                        }
                    }
                    if (firstRid == null) {
                        firstRid = rid;
                    } else {
                        secondRid = rid;
                        break;
                    }
                } catch (Exception e) {
                    log.error("check is available rid failed", e);
                }
            }

            if (firstRid != null) {
                if (!firstLevelSelectedRids.contains(firstRid)) {
                    firstLevelSelectedRids.add(firstRid);
                    selectedRids.add(firstRid);
                    firstLevelInstance.add(ridInstanceMap.get(firstRid));
                }
                if (secondRid != null) {
                    if (!secondLevelSelectedRids.contains(secondRid)) {
                        secondLevelSelectedRids.add(secondRid);
                        selectedRids.add(secondRid);
                        secondLevelInstance.add(ridInstanceMap.get(secondRid));
                    }
                }
            }
        }

        log.debug("first level instances: [{}]", JSON.toJSONString(firstLevelInstance));
        outputBean.setFirstLevel(firstLevelInstance);
        log.debug("second level instances: [{}]", JSON.toJSONString(secondLevelInstance));
        outputBean.setSecondLevel(secondLevelInstance);
        log.info("visual rid mapping: [{}]", ridMapping);
        return outputBean;
    }


    private static List<OutputInstance> getOutputInstanceByFact(Fact fact, String relationName,
                                                                RelationDirection direction)
            throws CimDataEngineRuntimeException {
        List<OutputInstance> outputInstanceList = new ArrayList<>();
        List<Relation> relationList = fact.getAllSpecifiedRelations(relationName, direction);
        if (CollectionUtils.isNotEmpty(relationList)) {
            for (Relation relation : relationList) {
                Relationable targetRelationable = relation.getFromRelationable();
                OutputInstance tmpInstance = new OutputInstance();
                if (targetRelationable.getId().equals(fact.getId())) {
                    targetRelationable = relation.getToRelationable();
                }

                if (targetRelationable.hasProperty(
                        CimConstants.ID_PROPERTY_TYPE_NAME) && targetRelationable.getProperty(
                        CimConstants.ID_PROPERTY_TYPE_NAME) != null) {
                    if (targetRelationable instanceof Fact) {
                        tmpInstance.setObjectTypeId(((Fact) targetRelationable).getType());
                    } else if (targetRelationable instanceof Dimension) {
                        tmpInstance.setObjectTypeId(((Dimension) targetRelationable).getType());
                    } else {
                        log.error("cannot get relationable type name of this class: [{}]",
                                targetRelationable.getClass());
                        continue;
                    }
                    tmpInstance.setRid(targetRelationable.getId());
                    tmpInstance.setInstanceId(targetRelationable.getProperty(
                            CimConstants.ID_PROPERTY_TYPE_NAME).getPropertyValue().toString());

                    outputInstanceList.add(tmpInstance);
                }
            }
        }
        return outputInstanceList;
    }

}
