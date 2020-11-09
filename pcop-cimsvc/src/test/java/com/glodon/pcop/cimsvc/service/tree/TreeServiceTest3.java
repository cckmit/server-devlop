package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.common.util.TreeConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.*;
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
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.BatchDataOperationResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.config.properties.ServiceEhcacheConfig;
import com.glodon.pcop.cimsvc.model.tree.*;
import com.glodon.pcop.cimsvc.service.v2.InstancesService;
import com.glodon.pcop.cimsvc.util.ServiceCacheUtil;
import com.google.gson.Gson;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Ignore
public class TreeServiceTest3 {

    private static TreeService treeService = new TreeService();

    private final static String zhushujudingyi = "zhushujudingyi";


    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "gdc";

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void listChildNodes() {

    }

    @Test
    public void listRootNodes() {

    }

    @Test
    public void listChildNodesWithMetadata() {
        // CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        // try {
        //     Relationable relationable = cds.getFactById("#2812:0");
        //     String treeDefId = "DATA_MANAGER_CONTENT_TREE";
        //     List<String> keys = Arrays.asList("level");
        //     System.out.println("===" + (new Gson()).toJson(treeService.listChildNodesWithMetadata(treeDefId,
        //             relationable, true, keys)));
        // } catch (CimDataEngineRuntimeException e) {
        //     e.printStackTrace();
        // } finally {
        //     cds.closeSpace();
        // }
    }

    @Test
    public void listRootNodesWithMetadata() {
        // CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        // try {
        //     String treeDefId = "DATA_MANAGER_CONTENT_TREE";
        //     List<String> keys = Arrays.asList("level");
        //     System.out.println("===" + (new Gson()).toJson(treeService.listRootNodesWithMetadata(treeDefId,
        //             true, keys, cds)));
        // } catch (CimDataEngineRuntimeException e) {
        //     e.printStackTrace();
        // } catch (CimDataEngineInfoExploreException e) {
        //     e.printStackTrace();
        // } finally {
        //     cds.closeSpace();
        // }
    }

    @Test
    public void addNodeInfo() {
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        try {
            String tenantId = "CIM_BUILDIN_PUBLIC_TENANT";
            // NodeInfoBean parentNode = null;
            NodeInfoBean parentNode = new NodeInfoBean();
            parentNode.setID("zhushujudingyi_ae3c14af-16ea-483b-883a-5bcd8d46ca30_1591769045946");
            parentNode.setNAME("主数据定义");
            parentNode.setTreeDefId("zhushujudingyi");
            parentNode.setNodeType(NodeInfoBean.NodeType.INDUSTRY);

            NodeInfoBean childNode = new NodeInfoBean();
            childNode.setID("yunyinglei");
            childNode.setNAME("运行类");
            childNode.setTreeDefId("zhushujudingyi");
            childNode.setNodeType(NodeInfoBean.NodeType.OBJECT);


            Map<String, Object> metadata = new HashMap<>();
            metadata.put("submissionUnit", "广联达一期-118-111");
            metadata.put("forTheTribe", "为了联盟");
//			metadata.put("creator","")
            TreeService.addNodeInfo(cds, tenantId, parentNode, childNode, metadata);
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



    /**
     * 新增对象类型节点
     */
    @Test
    public void addIndustryNode() {
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        try {
            String tenantId = "CIM_BUILDIN_PUBLIC_TENANT";
            String userId = "1";


            IndustryNodeAddInputBean nodeInfo = new IndustryNodeAddInputBean();

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("industryName", "industryName001");
            metadata.put("industryId", UUID.randomUUID().toString());
            metadata.put("comment", "dddd");

            nodeInfo.setMetadata(metadata);
            String industryRid = treeService.addIndustryNode(tenantId, userId, zhushujudingyi, nodeInfo, false);
            Assert.assertNotNull(industryRid);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    /**
     * 新增对象类型节点
     */
    @Test
    public void addObjectTypeNode() {
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        try {
            String tenantId = "CIM_BUILDIN_PUBLIC_TENANT";
            String userId = "1";

            NodeInfoBean parentNode = new NodeInfoBean();
            parentNode.setTreeDefId(zhushujudingyi);
            parentNode.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
            parentNode.setID("zhushujudingyi_a27a8b57-a830-44eb-8417-56ec8af70626_1591786162843");
            parentNode.setNAME("运行类");
            parentNode.setRid("#153:0");

            ObjectTypeAddInputBean nodeInfo = new ObjectTypeAddInputBean();
            nodeInfo.setObjectTypeId("ceshiduixiang3");
            nodeInfo.setObjectTypeName("测试对象3");
            nodeInfo.setParentNodeInfo(parentNode);




//            parentNode.setTreeDefId();
            String objectTypeRid = treeService.addObjectTypeNode(tenantId, userId, zhushujudingyi, nodeInfo, false);
            Assert.assertNotNull(objectTypeRid);



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }


    @Test
    public void addDatasetNode() {
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        try {
            String tenantId = "CIM_BUILDIN_PUBLIC_TENANT";
            String userId = "1";

            NodeInfoBean parentNode = new NodeInfoBean();
            parentNode.setTreeDefId(zhushujudingyi);
            parentNode.setNodeType(NodeInfoBean.NodeType.OBJECT);
            parentNode.setID("zhushujudingyi_9eee431f-340a-47a5-a1ec-c0d154fa929c_1591862262035");
            parentNode.setNAME("测试对象3");
            parentNode.setRid("#153:1");

            DatasetAddInputBean nodeInfo = new DatasetAddInputBean();
            nodeInfo.setDatasetId("ceshishuxingji3");
            nodeInfo.setDatasetName("测试属性集3");
            nodeInfo.setDesc("mmmmm");
            nodeInfo.setParentNodeInfo(parentNode);




//            parentNode.setTreeDefId();
            String objectTypeRid = treeService.addDatasetNode(tenantId, userId, zhushujudingyi, nodeInfo, false);
            Assert.assertNotNull(objectTypeRid);



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }


    @Test
    public void addRootNodeInfo() {
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        try {
            String tenantId = "3";
            NodeInfoBean parentNode = null;
            NodeInfoBean childNode = new NodeInfoBean();
            childNode.setID("t003");
            childNode.setNAME("测试第一级节点003");
            childNode.setTreeDefId("DATA_MANAGER_CONTENT_TREE");
            childNode.setNodeType(NodeInfoBean.NodeType.INSTANCE);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("submissionUnit", "广联达三期");
            TreeService.addNodeInfo(cds, tenantId, parentNode, childNode, metadata);
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

    @Test
    public void getAncestrytNodes() {
        CimConstants.defauleSpaceName = "pcopcim";
        String treeDefId = "QingDaoCityComponentTree";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            Map<String, NodeInfoBean> infoBeanMap = new HashMap<>();

            Fact fact = cds.getFactById("#5920:63");

            treeService.getAncestryNodes(treeDefId, fact, null, infoBeanMap);
            System.out.println(new Gson().toJson(infoBeanMap));
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void exportUploadedFiles() {
        CimConstants.defauleSpaceName = "pcopcim";

        StringBuilder sb = new StringBuilder();
        sb.append("file_rid\t").append("file_name\t").append("industry_rid\t").append("industry_name\t").append(
                "tenant_rid\t").append("tenant_name").append("\n");

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            ExploreParameters ep = new ExploreParameters();
            ep.setType("BASE_FILE_METADATA_INFO");
            List<Fact> factList = cds.getInformationExplorer().discoverInheritFacts(ep);

            for (Fact fact : factList) {
                StringBuilder oneRow = new StringBuilder();
                oneRow.append(fact.getId()).append("\t");
                if (fact.hasProperty("srcFileName")) {
                    if (fact.getProperty("srcFileName") != null) {
                        oneRow.append(fact.getProperty("srcFileName").getPropertyValue().toString()).append("\t");
                    } else {
                        oneRow.append("\t");
                    }
                } else {
                    oneRow.append("\t");
                }

                List<Relation> relationList = fact.getAllSpecifiedRelations("CIM_BUILDIN_INDUSTRYTYPE_INFOOBJECT_LINK"
                        , RelationDirection.TO);

                if (relationList != null && relationList.size() > 0) {
                    // System.out.println("===has related industry");
                    if (relationList.size() > 1) {
                        System.out.println("fielRid=" + fact.getId() + " has " + relationList.size() + " industry");
                    }
                    Relationable relationable = relationList.get(0).getFromRelationable();
                    if (relationable.hasProperty(CimConstants.IndustryTypeNodeKeys.INDUSTRY_NAME)) {
                        oneRow.append(relationable.getId()).append("\t").append(relationable.getProperty(
                                CimConstants.IndustryTypeNodeKeys.INDUSTRY_NAME).getPropertyValue().toString()).append(
                                "\t");
                    } else {
                        oneRow.append("\t").append("\t");
                    }
                } else {
                    oneRow.append("\t").append("\t");
                }

                relationList = fact.getAllSpecifiedRelations("CIM_BUILDIN_RELATIONTYPE_BELONGSTOTENANT"
                        , RelationDirection.FROM);

                if (relationList != null && relationList.size() > 0) {
                    // System.out.println("###has related tenant");
                    if (relationList.size() > 1) {
                        System.out.println("fielRid=" + fact.getId() + " has " + relationList.size() + " tenant");
                    }
                    Relationable relationable = relationList.get(0).getToRelationable();
                    if (relationable.hasProperty("CIM_BUILDIN_TENANT_ID")) {
                        oneRow.append(relationable.getId()).append("\t").append(relationable.getProperty(
                                "CIM_BUILDIN_TENANT_ID").getPropertyValue().toString()).append("\t");
                    } else {
                        oneRow.append("\t").append("\t");
                    }
                }
                sb.append(oneRow.toString()).append("\n");
            }

            Files.write(Paths.get("E:\\work\\files_export.txt"), sb.toString().getBytes());
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void addRootIndustryNode() {
        String tenantId = "3";
        String userId = "1";
        String treeDefId = zhushujudingyi;
        IndustryNodeAddInputBean nodeInfo = new IndustryNodeAddInputBean();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(CimConstants.IndustryTypeNodeKeys.INDUSTRY_ID, tenantId + "_test_industry_002");
        metadata.put(CimConstants.IndustryTypeNodeKeys.INDUSTRY_NAME, "测试-第一级节点-自动添加权限测试-002");
        metadata.put(CimConstants.GeneralProperties.COMMENT, "自动添加权限测试");
        metadata.put(CimConstants.DataManagerTreeProperties.SUBMISSION_UNIT, "广联达");
        nodeInfo.setMetadata(metadata);

        try {
            String industryRid = treeService.addIndustryNode(tenantId, userId, treeDefId, nodeInfo, true);
            System.out.println("industry rid: " + industryRid);
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } catch (DataServiceUserException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addChildIndustryNode() {
        String tenantId = "3";
        String userId = "1001";
        String treeDefId = CimConstants.DataManagerTreeProperties.DATA_MANAGER_CONTENT_TREE;
        NodeInfoBean parentNode = new NodeInfoBean();
        parentNode.setID("DATA_MANAGER_CONTENT_TREE_1eea0be3-7846-4e17-acb3-ba15808417e0_1561012725915");
        parentNode.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
        parentNode.setTreeDefId(treeDefId);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(CimConstants.IndustryTypeNodeKeys.INDUSTRY_ID, "tets_industry_002-01");
        metadata.put(CimConstants.IndustryTypeNodeKeys.INDUSTRY_NAME, "测试-child节点-002-01");
        metadata.put(CimConstants.GeneralProperties.COMMENT, "新增行业分类节点测试-显示排序测试");
        metadata.put(CimConstants.DataManagerTreeProperties.SUBMISSION_UNIT, "广联达-一期");

        IndustryNodeAddInputBean nodeInfo = new IndustryNodeAddInputBean();
        nodeInfo.setMetadata(metadata);
        nodeInfo.setParentNodeInfo(parentNode);
        // String industryRid = treeService.addIndustryNode(tenantId, userId, treeDefId, nodeInfo);
        // System.out.println("industry rid: " + industryRid);
        System.out.println("industry rid: " + (new Gson().toJson(nodeInfo)));
    }

    @Test
    public void addRootInstanceNode() {
        String tenantId = "3";
        String userId = "1003";
        String objectTypeId = zhushujudingyi;
        String treeDefId = zhushujudingyi;
        IndustryNodeAddInputBean nodeInfo = new IndustryNodeAddInputBean();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(CimConstants.GeneralProperties.NAME, "数据权限方案-04");
        metadata.put(CimConstants.GeneralProperties.COMMENT, "数据权限方案");
        nodeInfo.setMetadata(metadata);
        try {
            String industryRid = treeService.addInstanceNode(tenantId, userId, treeDefId, objectTypeId, nodeInfo, true);
            System.out.println("industry rid: " + industryRid);
        } catch (DataServiceUserException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addChildInstanceNode() {
        String tenantId = "3";
        String userId = "1003";
        String objectTypeId = zhushujudingyi;
        String treeDefId = zhushujudingyi;
        NodeInfoBean parentNode = new NodeInfoBean();
        parentNode.setID("DATA_PERMISSION_SCHEMA_TREE_245c3a03-1423-4426-ab6b-8bd3972b232e_1561024587264");
        parentNode.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
        parentNode.setTreeDefId(treeDefId);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(CimConstants.GeneralProperties.NAME, "数据权限方案-04-01");
        metadata.put(CimConstants.GeneralProperties.COMMENT, "数据权限方案");

        IndustryNodeAddInputBean nodeInfo = new IndustryNodeAddInputBean();
        nodeInfo.setParentNodeInfo(parentNode);
        nodeInfo.setMetadata(metadata);
        // try {
        // String industryRid = treeService.addInstanceNode(tenantId, userId, treeDefId, objectTypeId, nodeInfo);
        // System.out.println("industry rid: " + industryRid);
        System.out.println("industry rid: " + (new Gson().toJson(nodeInfo)));
        // } catch (DataServiceUserException e) {
        //     e.printStackTrace();
        // } catch (CimDataEngineRuntimeException e) {
        //     e.printStackTrace();
        // }
    }


    @Test
    public void updateNodeMetadata() {
        String tenantId = "1";
        String userId = "1003";
        String treeDefId = zhushujudingyi;
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";

        NodeInfoBean nodeInfo = new NodeInfoBean();
        nodeInfo.setID("DATA_MANAGER_CONTENT_TREE_c6f9bc31-8fbe-4361-b7cb-5c9108f52cee_1561367472023");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(CimConstants.DataManagerTreeProperties.SUBMISSION_UNIT, "元数据更新123-02");
        metadata.put(CimConstants.GeneralProperties.NAME, "测试-显示名称更新");

        NodeMetadataUpdateInputBean updateInputBean = new NodeMetadataUpdateInputBean();
        updateInputBean.setMetadata(metadata);
        updateInputBean.setNodeInfo(nodeInfo);

        System.out.println("update node metadata: " + treeService.updateMetadataInfo(tenantId, userId, treeDefId,
                updateInputBean));

    }


    @Test
    public void deleteNodes() {
        String tenantId = "1";
        String userId = "1003";
        String treeDefId = zhushujudingyi;
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";

        NodeInfoBean nodeInfo1 = new NodeInfoBean();
        nodeInfo1.setID("DATA_MANAGER_CONTENT_TREE_666a17dc-1cf7-4f42-b6e6-3ed4097a6629_1562118181247");

        NodeInfoBean nodeInfo2 = new NodeInfoBean();
        nodeInfo2.setID("DATA_MANAGER_CONTENT_TREE_f6dd8f6e-c3b4-4ce5-9ed7-c6c2f92de0e5_1562118180422");

        List<NodeInfoBean> nodeInfoBeans = new ArrayList<>();
        nodeInfoBeans.add(nodeInfo1);
        nodeInfoBeans.add(nodeInfo2);

        Gson gson = new Gson();

        System.out.println("delete nodes:  " + gson.toJson(treeService.deleteNodesBatch(tenantId, userId, treeDefId,
                nodeInfoBeans)));
    }

    @Test
    public void deleteNodesResursivelyByRid() {
        String tenantId = "3";
        String userId = "1003";
        String treeDefId = zhushujudingyi;
        // String nodeRid = "#6339:0";
        List<String> nodeRids = Arrays.asList("#6339:6", "#6340:0", "#6343:4");
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";
        for (String rid : nodeRids) {
            treeService.deleteNodesResursivelyByRid(tenantId, userId, treeDefId, rid);
        }
    }


    @Test
    public void getChildNames() {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            String parentRid = "#5626:364";
            String childRid = "#5626:348";

            Fact parentFact = cds.getFactById(parentRid);
            Fact childFact = cds.getFactById(childRid);

            treeService.updateDisplayNameByMove(cds, parentFact, childFact, false);
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }


    @Test
    public void addTreeNode() throws CimDataEngineRuntimeException, DataServiceModelRuntimeException,
            DataServiceUserException {
        String treeDefId = "FuZhouCityComponentTree";
        String treeName = "福州城市部件树";

        String tennatId = "1";
        String userId = "6435737162427609322";
        // String tennatId = "2";
        // String userId = "6468343598790656434";

        System.out.println("result: " + treeService.addSceneTreeNodeWithoutObjectType(tennatId, userId, treeDefId,
                treeName));

    }

    @Test
    public void addDefaultDataContext() {
        String tenantId = "100100";
        String userId = "100100";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            //权限树添加默认分类和默认方案
            IndustryNodeAddInputBean nodeInfo = new IndustryNodeAddInputBean();
            Map<String, Object> mapInfo = new HashMap<>();
            mapInfo.put(CimConstants.IndustryTypeNodeKeys.INDUSTRY_ID, UUID.randomUUID().toString());
            mapInfo.put(CimConstants.IndustryTypeNodeKeys.INDUSTRY_NAME, TreeConstants.DEFAULT_CLASSIFICATION);
            nodeInfo.setMetadata(mapInfo);
            String treeDefId = CimConstants.DataPermissionSchemaProperties.DATA_PERMISSION_SCHEMA_TREE;
            //添加分类

            String permissionClassificationRid = treeService.addIndustryNode(tenantId, userId, treeDefId, nodeInfo, false);


            Dimension classificationDimension = cds.getDimensionById(permissionClassificationRid);
            List<Relation> allSpecifiedRelations = classificationDimension.getAllSpecifiedRelations(BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE, RelationDirection.TO);
            Relationable relationable = allSpecifiedRelations.get(0).getFromRelationable();

            NodeInfoBean nodeInfoBean = new NodeInfoBean();
            relationableToNodeInfo(relationable, nodeInfoBean);
            String objectTypeId = CimConstants.DataPermissionSchemaProperties.DATA_PERMISSION_SCHEMA;
            //setTreeDefId
            nodeInfoBean.setTreeDefId(treeDefId);
            IndustryNodeAddInputBean planNodeInfo = new IndustryNodeAddInputBean();
            Map<String, Object> planMap = new HashMap<>();
            planMap.put(CimConstants.TreeNodeBaseInfo.NAME, TreeConstants.DATA_CATALOG_PERMISSIONS);
            planNodeInfo.setMetadata(planMap);
            planNodeInfo.setParentNodeInfo(nodeInfoBean);
            //添加默认方案
            String instanceRid = treeService.addInstanceNode(tenantId, userId, treeDefId, objectTypeId, planNodeInfo, false);
            Fact planFact = cds.getFactById(instanceRid);

            //从方案表中找出id 给权限树用
            String id = planFact.getProperty(CimConstants.TreeNodeBaseInfo.ID).getPropertyValue().toString();
            //权限树
            FilteringItem filteringItem = new EqualFilteringItem(CimConstants.TreeNodeBaseInfo.REF_CIM_ID, id);
            ExploreParameters ep = new ExploreParameters();
            ep.setType(treeDefId);
            ep.setDefaultFilteringItem(filteringItem);

            InfoObjectDef objectDef = modelCore.getInfoObjectDef(treeDefId);
            InfoObjectRetrieveResult objects = objectDef.getObjects(ep);
            if (!objects.getInfoObjects().isEmpty()) {
                InfoObject infoObject = objects.getInfoObjects().get(0);
                String objectInstanceRID = infoObject.getObjectInstanceRID();
                Fact pTreeFact = cds.getFactById(objectInstanceRID);
                String name = pTreeFact.getProperty(CimConstants.TreeNodeBaseInfo.NAME).getPropertyValue().toString();
                String pTreeId = pTreeFact.getProperty(CimConstants.TreeNodeBaseInfo.ID).getPropertyValue().toString();
                Map<String, Object> userIdAndDataPmapping = new HashMap<>();
                userIdAndDataPmapping.put(CimConstants.TreeNodeBaseInfo.NAME, name);
                userIdAndDataPmapping.put(CimConstants.UserIdAndDataPermissionProperties.DATA_PERMISSSION_ID, pTreeId);
                userIdAndDataPmapping.put(CimConstants.UserIdAndDataPermissionProperties.USER_ID, userId);
                userIdAndDataPmapping.put(CimConstants.CREATE_TIME, new Date());
                userIdAndDataPmapping.put(CimConstants.UPDATE_TIME, new Date());
                List<Map<String, Object>> singletonList = Collections.singletonList(userIdAndDataPmapping);
                String useridAndDataPermissionType = CimConstants.UserIdAndDataPermissionProperties.USERID_AND_DATA_PERMISSION_MAPPING;

//				BatchDataOperationResult batchDataOperationResult = instancesService.addInstanceSingle(tenantId, useridAndDataPermissionType, instancesService.addSingleObjectValuesFormal(singletonList), false);


//				List<String> successDataInstanceRIDs = batchDataOperationResult.getSuccessDataInstanceRIDs();
                String sceneTreeRid = treeService.addDefaultSceneTreeNode(tenantId, userId);
                System.out.println(sceneTreeRid);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }


    /**
     * 单个节点基本信息
     *
     * @param rlab
     * @return
     */
    protected void relationableToNodeInfo(Relationable rlab, NodeInfoBean infoBean) {
        infoBean.setRid(rlab.getId());

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.ID)) {
            infoBean.setID(rlab.getProperty(CimConstants.TreeNodeBaseInfo.ID).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.NAME)) {
            infoBean.setNAME(rlab.getProperty(CimConstants.TreeNodeBaseInfo.NAME).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.NODE_TYPE)) {
            infoBean.setNodeType(NodeInfoBean.NodeType.valueOf(
                    rlab.getProperty(CimConstants.TreeNodeBaseInfo.NODE_TYPE).getPropertyValue().toString()));
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.REF_OBJECT_TYPE)) {
            infoBean.setRefObjType(rlab.getProperty(CimConstants.TreeNodeBaseInfo.REF_OBJECT_TYPE).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.REF_CIM_ID)) {
            infoBean.setRefCimId(rlab.getProperty(CimConstants.TreeNodeBaseInfo.REF_CIM_ID).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.RELATION_TYPE)) {
            infoBean.setRelationType(rlab.getProperty(CimConstants.TreeNodeBaseInfo.RELATION_TYPE).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.FILTER)) {
            infoBean.setFilter(rlab.getProperty(CimConstants.TreeNodeBaseInfo.FILTER).getPropertyValue().toString());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.CREATE_TIME)) {
            infoBean.setCreateTime((Date) rlab.getProperty(CimConstants.TreeNodeBaseInfo.CREATE_TIME).getPropertyValue());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.UPDATE_TIME)) {
            infoBean.setUpdateTime((Date) rlab.getProperty(CimConstants.TreeNodeBaseInfo.UPDATE_TIME).getPropertyValue());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.LEVEL)) {
            infoBean.setLevel((Integer) rlab.getProperty(CimConstants.TreeNodeBaseInfo.LEVEL).getPropertyValue());
        }

        if (rlab.hasProperty(CimConstants.TreeNodeBaseInfo.IDX)) {
            infoBean.setIdx((Double) rlab.getProperty(CimConstants.TreeNodeBaseInfo.IDX).getPropertyValue());
        }

    }

}