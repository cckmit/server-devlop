package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.GeneralProperties;
import com.glodon.pcop.cim.common.util.CimConstants.TreeNodeBaseInfo;
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
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.SimilarFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryType;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryTypes;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.optionalInit.TreeInitiationUtil;
import com.glodon.pcop.cimsvc.model.tree.ChildNodeCountBean;
import com.glodon.pcop.cimsvc.model.tree.DataPermissionBean;
import com.glodon.pcop.cimsvc.model.tree.IndustryNodeAddInputBean;
import com.glodon.pcop.cimsvc.model.tree.NodeDeleteOutputBean;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.glodon.pcop.cimsvc.model.tree.NodeMetadataUpdateInputBean;
import com.glodon.pcop.cimsvc.model.tree.NodeMoveInputBean;
import com.glodon.pcop.cimsvc.model.tree.SceneTreeNodeAddInputBean;
import com.glodon.pcop.cimsvc.util.PinyinUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SceneTreeService {
    private static final Logger log = LoggerFactory.getLogger(SceneTreeService.class);

    @Autowired
    private DataPermissionService dataPermissionService;

    @Autowired
    private TreeService treeService;

    /**
     * 列出所有的场景树节点
     *
     * @param tenantId
     * @param userId
     * @return
     */
    public List<NodeInfoBean> listSceneTreeNodes(String tenantId, String userId, Boolean filterByPermission) {
        List<NodeInfoBean> childNodes = new ArrayList<>();

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef objectDef =
                    modelCore.getInfoObjectDef(CimConstants.TreeDefinitionProperties.TREE_DEFINITION_OBJECT_TYPE);
            InfoObjectRetrieveResult retrieveResult = objectDef.getObjects(null);
            List<InfoObject> infoObjectList = retrieveResult.getInfoObjects();
            log.debug("scene tree size: {}", infoObjectList.size());
            if (infoObjectList != null && infoObjectList.size() > 0) {
                List<String> schemaIds = dataPermissionService.getPermissionSchemaByUser(cds, userId);
                for (InfoObject object : infoObjectList) {
                    NodeInfoBean infoBean = new NodeInfoBean();
                    Fact rlab = cds.getFactById(object.getObjectInstanceRID());
                    List<Relation> relationList =
                            rlab.getAllSpecifiedRelations(BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE,
                                    RelationDirection.FROM);
                    if (relationList == null || relationList.size() < 1) {
                        log.debug("tree definition fact related tree node not found: [{}]", rlab.getId());
                        continue;
                    }
                    Relation firstRelation = relationList.get(0);
                    if (filterByPermission) {
                        DataPermissionBean dataPermissionBean =
                                dataPermissionService.getDataPermissionByUser(firstRelation.getToRelationable().getId(),
                                        schemaIds);
                        if (dataPermissionBean == null || dataPermissionBean.getReadPermission().equals(0)) {
                            log.debug("filter out by permission: userId={}, rid={}", userId,
                                    firstRelation.getToRelationable().getId());
                            continue;
                        }
                    }
                    TreeServiceUtil.relationableToNodeInfo(firstRelation.getToRelationable(), infoBean);
                    if (StringUtils.isNotBlank(infoBean.getID()) && StringUtils.isNotBlank(infoBean.getNAME())) {
                        infoBean.setTreeDefId(rlab.getProperty(CimConstants.TreeDefinitionProperties.TREE_NODE_OBJECT).getPropertyValue().toString());
                        childNodes.add(infoBean);
                    } else {
                        log.error("tree node info ID and NAME are mandatary");
                    }
                }
            }
        } catch (CimDataEngineRuntimeException e) {
            log.error("query tree fact related node failed", e);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return childNodes;
    }
}
