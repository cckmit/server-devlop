package com.glodon.pcop.cimsvc.util.tree;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.glodon.pcop.cimsvc.service.tree.TreeService;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Ignore
public class TreeInitinationTest {

    private static String treeDefId = "QingDaoCityComponentTree";
    private static String cimSpaceName = "pcopcim";
    private static String tenantId = "2";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addTreeDefinitionAndObjectType() {
    }

    @Test
    public void addRootNodesFromObjectType() {
        String treeObjectTypeId = "DATA_MANAGER_CONTENT_TREE";
        TreeInitination.addRootNodesFromObjectType(treeObjectTypeId);
    }

    @Test
    public void addObjectTypeNodes() {
        String treeObjectTypeId = "QingDaoCityComponentTree";
        // String treeObjectTypeId = "DATA_MANAGER_CONTENT_TREE";
        String tenantId = "2";

        NodeInfoBean parentNode = new NodeInfoBean();
        parentNode.setID("QingDaoCityComponentTree_93867978-e665-4e3f-bc07-0585cf0e6d4b_1560334170698");
        parentNode.setNAME("规划25号线");
        parentNode.setTreeDefId("QingDaoCityComponentTree");

        List<String> objectTypeIds = Arrays.asList("LampPostDev", "ledDev", "guideDisplay", "publicBroadcastingDev",
                "wifiDev", "parking", "cameraDev", "envmonitordevice");

        List<String> rids = TreeInitination.addObjectTypeNodes(tenantId, treeObjectTypeId, parentNode, objectTypeIds);
        System.out.println("object type node rids: " + (new Gson().toJson(rids)));
    }

    // @Test
    public void addChildNodeByRid() {
        String treeObjectTypeId = "QingDaoCityComponentTree";
        String tenantId = "2";

        NodeInfoBean parentNode = new NodeInfoBean();
        parentNode.setID("QingDaoCityComponentTree_b043d69d-f8c0-4fe4-b404-8bfd84521a29_1560334261462");
        parentNode.setNAME("灯杆");
        parentNode.setTreeDefId("QingDaoCityComponentTree");

        List<String> factRids = Arrays.asList("#1728:10", "#1728:12");

        List<String> rids = TreeInitination.addChildNodeByRid(tenantId, treeObjectTypeId, parentNode, factRids);
        System.out.println("add child nodes by rids: " + (new Gson().toJson(rids)));
    }

    @Test
    public void addFuZhouCityComponentTreeSecondLevelNodes() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        String cimSpaceName = "pcopcim";
        String tenantId = "1";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            System.out.println("start...");
            String treeDefId = "FuZhouCityComponentTree";
            TreeService treeService = new TreeService();
            List<NodeInfoBean> nodeInfoBeans = treeService.listRootNodes(treeDefId, true, modelCore);
            System.out.println("node info beans: \n" + new Gson().toJson(nodeInfoBeans));
            List<NodeInfoBean> childNodeInfoBeans = treeService.listChildNodes(treeDefId, nodeInfoBeans.get(0), false
                    , modelCore);
            for (NodeInfoBean parentNode : childNodeInfoBeans) {
                List<String> objectTypeIds = Arrays.asList("LampPostDev", "ledDev", "guideDisplay",
                        "publicBroadcastingDev", "wifiDev", "parking", "cameraDev", "envmonitordevice");
                List<String> rids = TreeInitination.addObjectTypeNodes(tenantId, treeDefId, parentNode, objectTypeIds);
                System.out.println("Node name: " + parentNode.getNAME() + "\nobject type node rids: " + (new Gson().toJson(rids)));
            }
            System.out.println("complete!!!");
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void addCityComponentTreeThirdLevelNodes() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        String cimSpaceName = "pcopcim";
        String tenantId = "1";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            System.out.println("start...");
            String treeDefId = "FuZhouCityComponentTree";
            TreeService treeService = new TreeService();
            List<NodeInfoBean> nodeInfoBeans = treeService.listRootNodes(treeDefId, true, modelCore);
            System.out.println("node info beans: \n" + new Gson().toJson(nodeInfoBeans));
            for (NodeInfoBean parentNode : nodeInfoBeans) {
                List<NodeInfoBean> childNodes = treeService.listChildNodes(treeDefId, parentNode, false, modelCore);
                System.out.println("second node of " + parentNode.getNAME());
                System.out.println(new Gson().toJson(childNodes));
                Map<String, NodeInfoBean> nodeInfoBeanMap = new HashMap<>();
                for (NodeInfoBean infoBean : childNodes) {
                    nodeInfoBeanMap.put(infoBean.getRefObjType(), infoBean);
                }
                System.out.println("===node map: \n" + new Gson().toJson(nodeInfoBeanMap));

                String parentObjectTypeId = "Road";
                FilteringItem filteringItem = new EqualFilteringItem(CimConstants.TreeNodeBaseInfo.ID,
                        parentNode.getRefCimId());
                // FilteringItem filteringItem = new EqualFilteringItem(CimConstants.TreeNodeBaseInfo.ID,
                //         "37020307020000S0001000000000");
                ExploreParameters ep = new ExploreParameters();
                ep.setType(parentObjectTypeId);
                ep.setDefaultFilteringItem(filteringItem);
                List<Fact> factList = cds.getInformationExplorer().discoverInheritFacts(ep);

                List<Relation> relationList =
                        factList.get(0).getAllSpecifiedRelations(BusinessLogicConstant.RELATION_TYPE_CONFIG_NAME_SpaceContainedBy, RelationDirection.TO);
                // System.out.println("---road fact size " + relationList.size());
                Map<String, List<String>> relatedRids = new HashMap<>();
                for (Relation relation : relationList) {
                    Relationable relationable = relation.getFromRelationable();
                    String factType = ((Fact) relationable).getType();
                    System.out.println("relation rid=" + relation.getId() + " fact type=" + factType);
                    if (relatedRids.containsKey(factType)) {
                        relatedRids.get(factType).add(relationable.getId());
                    } else {
                        List<String> rids = new ArrayList<>();
                        rids.add(relationable.getId());
                        relatedRids.put(factType, rids);
                    }
                }
                System.out.println("===rids map: \n" + new Gson().toJson(relatedRids));

                if (relatedRids.size() > 0) {
                    for (Map.Entry<String, List<String>> entry : relatedRids.entrySet()) {
                        List<String> rids = TreeInitination.addChildNodeByRid(tenantId, treeDefId,
                                nodeInfoBeanMap.get(entry.getKey()), entry.getValue());
                        System.out.println("add child nodes by rids: " + (new Gson().toJson(rids)));
                    }
                }
            }
            System.out.println("complete!!!");
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


    @Test
    public void addNodeByObjectType() {
        // String parentNodeId = "QingDaoCityComponentTree_28121453-88cc-49a0-b2a9-9aac27c5bc49_1561355023461";
        String parentNodeId = "";
        List<String> objectTypeIds = Arrays.asList("LampPostDev", "ledDev", "guideDisplay",
                "publicBroadcastingDev", "wifiDev", "parking", "cameraDev", "envmonitordevice");
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            System.out.println("start...");
            NodeInfoBean parentNode = getNodeInfoById(parentNodeId);
            List<String> rids = TreeInitination.addObjectTypeNodes(tenantId, treeDefId, parentNode, objectTypeIds);
            System.out.println("Node name: " + parentNode.getNAME() + "\nobject type node rids: " + (new Gson().toJson(rids)));
            System.out.println("complete!!!");
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void addNodeByInstanceRids() {
        // String parentNodeId = "QingDaoCityComponentTree_9c57dc89-9e68-4953-b618-bc19804e0944_1560339175778";
        String parentNodeId = "";
        List<String> instanceRids = Arrays.asList("#2137:9");

        List<String> rids = new ArrayList<>();
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        NodeInfoBean.NodeType nodeType = NodeInfoBean.NodeType.INSTANCE;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
            for (String rid : instanceRids) {
                Fact fact = cds.getFactById(rid);
                NodeInfoBean infoBean = new NodeInfoBean();
                if (fact.hasProperty(CimConstants.BaseDataSetKeys.ID)) {
                    infoBean.setRefCimId(fact.getProperty(CimConstants.BaseDataSetKeys.ID).getPropertyValue().toString());
                } else {
                    System.out.println("add node filed, ID is mandatary");
                    continue;
                }
                if (fact.hasProperty(CimConstants.BaseDataSetKeys.NAME)) {
                    infoBean.setNAME(fact.getProperty(CimConstants.BaseDataSetKeys.NAME).getPropertyValue().toString());
                }
                infoBean.setNodeType(nodeType);
                infoBean.setRefObjType(fact.getType());
                infoBean.setTreeDefId(treeDefId);
                Map<String, Object> metadata = new HashMap<>();
                NodeInfoBean parentNode = getNodeInfoById(parentNodeId);
                String nodeRid = TreeService.addNodeInfo(cds, tenantId, parentNode, infoBean, metadata);
                Fact nodeFact = cds.getFactById(nodeRid);
                Relation relation = nodeFact.addToRelation(fact,
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
                rids.add(nodeRid);
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (DataServiceUserException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    public static NodeInfoBean getNodeInfoById(String id) {
        NodeInfoBean nodeInfo = new NodeInfoBean();
        nodeInfo.setID(id);
        nodeInfo.setTreeDefId(treeDefId);
        return nodeInfo;
    }

    @Test
    public void fuZhouCityComponentsThirdLevel() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();

        String tenantId = "1";
        String treeDefId = "FuZhouCityComponentTree";

        String cimSpaceName = "pcopcim";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            System.out.println("start...");
            TreeService treeService = new TreeService();
            List<NodeInfoBean> nodeInfoBeans = treeService.listRootNodes(treeDefId, true, modelCore);
            System.out.println("node info beans: \n" + new Gson().toJson(nodeInfoBeans));
            List<NodeInfoBean> childNodeInfoBeans = treeService.listChildNodes(treeDefId, nodeInfoBeans.get(0), false
                    , modelCore);
            for (NodeInfoBean parentNode : childNodeInfoBeans) {
                List<String> roadRids = Arrays.asList("#2065:9", "#2065:10", "#2066:9", "#2067:9");
                List<String> rids = TreeInitination.addChildNodeByRid(tenantId, treeDefId, parentNode, roadRids);
                System.out.println("Component name: " + parentNode.getNAME() + "\nroad node rids: " + JSON.toJSONString(rids));
            }
            System.out.println("complete!!!");
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }


    @Test
    public void fuZhouCityComponentsForthLevel() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        Map<String, String> roadNameIdMap = new HashMap<>();
        roadNameIdMap.put("漳江路", "FZBH_DL_9");
        roadNameIdMap.put("湖文路", "FZBH_DL_11");
        roadNameIdMap.put("壶江路", "FZBH_DL_6");
        roadNameIdMap.put("悦湖路", "FZBH_DL_10");

        Map<String, String> componentsMap = new HashMap<>();
        componentsMap.put("路灯", "LampPostDev");
        componentsMap.put("视频（监控电子眼）", "cameraDev");
        componentsMap.put("井盖", "pipePointWell");

        String tenantId = "1";
        String treeDefId = "FuZhouCityComponentTree";
        String cimSpaceName = "pcopcim";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            System.out.println("start...");
            TreeService treeService = new TreeService();
            List<NodeInfoBean> rootNodeInfoBeans = treeService.listRootNodes(treeDefId, true, modelCore);
            System.out.println("node info beans: \n" + JSON.toJSONString(rootNodeInfoBeans));
            List<NodeInfoBean> childNodeInfoBeans = treeService.listChildNodes(treeDefId, rootNodeInfoBeans.get(0),
                    false, modelCore);
            for (NodeInfoBean nodeInfoBean : childNodeInfoBeans) {
                String nodeName = nodeInfoBean.getNAME();
                String objType = componentsMap.get(nodeName);
                System.out.println("object type: " + nodeName);
                List<NodeInfoBean> thirdLevelChildNodeInfoBeans = treeService.listChildNodes(treeDefId, nodeInfoBean,
                        false, modelCore);
                for (NodeInfoBean infoBean : thirdLevelChildNodeInfoBeans) {
                    String roadName = infoBean.getNAME();
                    String roadId = roadNameIdMap.get(roadName);
                    System.out.println("road name: " + nodeName);
                    FilteringItem filteringItem = new EqualFilteringItem("roadId", roadId);
                    ExploreParameters ep = new ExploreParameters();
                    ep.setType(objType);
                    ep.setDefaultFilteringItem(filteringItem);

                    List<Fact> factList = cds.getInformationExplorer().discoverInheritFacts(ep);
                    System.out.println(String.format("object Type [%s], road name [%s], child node size [%s]",
                            nodeName, roadName, factList.size()));

                    List<String> rids = new ArrayList<>();
                    for (Fact fact : factList) {
                        rids.add(fact.getId());
                    }

                    List<String> resultRids = TreeInitination.addChildNodeByRid(tenantId, treeDefId, infoBean, rids);
                    System.out.println("node add result: " + JSON.toJSONString(resultRids));
                }
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }


    }

}