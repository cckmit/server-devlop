package com.glodon.pcop.cimsvc.util.tree;

import com.glodon.pcop.cim.common.util.CimConstants.BaseDataSetKeys;
import com.glodon.pcop.cim.common.util.CimConstants.TreeDefinitionProperties;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.glodon.pcop.cimsvc.service.tree.TreeService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeInitination {

    private static Logger log = LoggerFactory.getLogger(TreeInitination.class);

    public static void addTreeDefinitionAndObjectType(String cimSpaceName, String tenantId) throws DataServiceUserException {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        InfoObjectDef infoObjectDef = QingdaoCityComponentsTreeTypeInitiation.dataMagagerTreeInitiation(cimSpaceName);

        if (infoObjectDef != null) {
            CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(cimSpaceName, tenantId);

            InfoObjectDef treeDefinitionObjectType = modelCore.getInfoObjectDef("TREE_DEFINITION");

            if (treeDefinitionObjectType != null) {
                Map<String, Object> values = new HashMap<>();
                values.put(TreeDefinitionProperties.ID, infoObjectDef.getObjectTypeName());
                values.put(TreeDefinitionProperties.NAME, infoObjectDef.getObjectTypeDesc());
                values.put(TreeDefinitionProperties.TREE_NODE_OBJECT, infoObjectDef.getObjectTypeName());
                values.put(TreeDefinitionProperties.COMMENT, infoObjectDef.getObjectTypeDesc());
                values.put(TreeDefinitionProperties.CREATE_TIME, new Date());
                values.put(TreeDefinitionProperties.UPDATE_TIME, new Date());

                InfoObjectValue objectValue = new InfoObjectValue();
                objectValue.setBaseDatasetPropertiesValue(values);

                InfoObject infoObject = treeDefinitionObjectType.newObject(objectValue, false);
            }
        }
    }

    public static List<String> addRootNodesFromObjectType(String treeObjectTypeId) {
        List<String> rids = new ArrayList<>();
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        String cimSpaceName = "gdc";
        String tenantId = "CIM_BUILDIN_PUBLIC_TENANT";
        String objectTypeId = "Road";
//         String treeObjectTypeId = "DATA_MANAGER_CONTENT_TREE";
        NodeInfoBean.NodeType nodeType = NodeInfoBean.NodeType.INSTANCE;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(cimSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef objectDef = modelCore.getInfoObjectDef(objectTypeId);
            if (objectDef != null) {
                InfoObjectRetrieveResult retrieveResult = objectDef.getObjects(null);
                List<InfoObject> objectList = retrieveResult.getInfoObjects();
                for (InfoObject object : objectList) {
                    Map<String, Object> baseInfo = object.getInfo();
                    NodeInfoBean infoBean = new NodeInfoBean();
                    if (baseInfo.containsKey(BaseDataSetKeys.ID)) {
                        infoBean.setRefCimId(baseInfo.get(BaseDataSetKeys.ID).toString());
                    } else {
                        log.error("ID of rid={} is mandatary", object.getObjectInstanceRID());
                        continue;
                    }
                    if (baseInfo.containsKey(BaseDataSetKeys.NAME)) {
                        infoBean.setNAME(baseInfo.get(BaseDataSetKeys.NAME).toString());
                    }
                    infoBean.setNodeType(nodeType);
                    infoBean.setRefObjType(objectTypeId);
                    infoBean.setTreeDefId(treeObjectTypeId);
                    Map<String, Object> metadata = new HashMap<>();
                    String nodeRid = TreeService.addNodeInfo(cds, tenantId, null, infoBean, metadata);
                    log.info("new node rid: {}", nodeRid);
                    Fact fact = cds.getFactById(nodeRid);
                    Relation relation = fact.addToRelation(cds.getFactById(object.getObjectInstanceRID()),
                            BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
                    log.info("new relation rid: {}", relation.getId());
                    rids.add(nodeRid);
                }
            }
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (DataServiceUserException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return rids;
    }

    public static List<String> addObjectTypeNodes(String tenantId, String treeObjectTypeId, NodeInfoBean parentNode,
                                                  List<String> objectTypeIds) {
        List<String> rids = new ArrayList<>();
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        String cimSpaceName = "pcopcim";
        // String tenantId = "2";
        // String treeObjectTypeId = "QingDaoCityComponentTree";
        NodeInfoBean.NodeType nodeType = NodeInfoBean.NodeType.OBJECT;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
            for (String objId : objectTypeIds) {
                NodeInfoBean infoBean = new NodeInfoBean();
                Fact fact = InfoObjectFeatures.getInfoObjectTypeStatusRecord(cds, objId);

                // if (fact.hasProperty("infoObjectTypeName")) {
                //     infoBean.setID(fact.getProperty("infoObjectTypeName").getPropertyValue().toString());
                // } else {
                //     log.error("object type status of {} not found", objId);
                //     continue;
                // }
                infoBean.setRefObjType(fact.getProperty("infoObjectTypeName").getPropertyValue().toString());
                infoBean.setNAME(fact.getProperty("infoObjectTypeDesc").getPropertyValue().toString());
                infoBean.setNodeType(nodeType);
                infoBean.setTreeDefId(treeObjectTypeId);
                Map<String, Object> metadata = new HashMap<>();

                String nodeRid = TreeService.addNodeInfo(cds, tenantId, parentNode, infoBean, metadata);
                log.info("new node rid: {}", nodeRid);
                rids.add(nodeRid);
            }
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (DataServiceUserException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return rids;
    }

    public static List<String> addChildNodeByRid(String tenantId, String treeObjectTypeId, NodeInfoBean parentNode,
                                                 List<String> factRids) {
        List<String> rids = new ArrayList<>();
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        String cimSpaceName = "pcopcim";
        NodeInfoBean.NodeType nodeType = NodeInfoBean.NodeType.INSTANCE;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
            for (String rid : factRids) {
                NodeInfoBean infoBean = new NodeInfoBean();
                Fact fact = cds.getFactById(rid);

                if (fact.hasProperty(BaseDataSetKeys.ID)) {
                    infoBean.setRefCimId(fact.getProperty(BaseDataSetKeys.ID).getPropertyValue().toString());
                } else {
                    log.error("fact of {} not found", rid);
                    continue;
                }
                if (fact.hasProperty(BaseDataSetKeys.NAME)) {
                    infoBean.setNAME(fact.getProperty(BaseDataSetKeys.NAME).getPropertyValue().toString());
                }
                infoBean.setNodeType(nodeType);
                infoBean.setTreeDefId(treeObjectTypeId);
                infoBean.setRefObjType(fact.getType());
                Map<String, Object> metadata = new HashMap<>();

                String nodeRid = TreeService.addNodeInfo(cds, tenantId, parentNode, infoBean, metadata);
                log.info("new node rid: {}", nodeRid);
                Fact nodeFact = cds.getFactById(nodeRid);
                Relation relation = nodeFact.addToRelation(cds.getFactById(rid),
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);
                log.info("new relation rid: {}", relation.getId());
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
        return rids;
    }

     public static void main(String[] args) {
         PropertyHandler.map = OrientdbConfigUtil.getParameters();
         String cimSpaceName = "gdc";
         CimDataSpace cds = null;
         try {
             cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
              String tenantId = "CIM_BUILDIN_PUBLIC_TENANT";
              try {
                  addTreeDefinitionAndObjectType(cimSpaceName, tenantId);
              } catch (DataServiceUserException e) {
                  e.printStackTrace();
              }
             log.info("start...");

              String treeDefId = "MASTER_DATA_DEFINITION";
              addRootNodesFromObjectType(treeDefId);
//             TreeService treeService = new TreeService();
             //List<NodeInfoBean> nodeInfoBeans = treeService.listRootNodes("1", treeDefId, true, cds);
//             log.info("node info beans: \n{}", new Gson().toJson(nodeInfoBeans));
             log.info("complete!!!");
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             if (cds != null) {
                 cds.closeSpace();
             }
         }

     }


}
