package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.IndustryTypeNodeKeys;
import com.glodon.pcop.cim.engine.dataServiceCache.IndustryTypeCache;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Dimension;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineDataMartException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryType;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryTypes;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.IndustryTypeDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.StandardTreeNode;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.glodon.pcop.cimsvc.service.tree.DataPermissionService;
import com.glodon.pcop.cimsvc.service.tree.TreeService;
import com.glodon.pcop.cimsvc.service.v2.IndustryTypesService;
import com.glodon.pcop.cimsvc.util.ExcelImportUtil;
import com.glodon.pcop.cimsvc.util.PinyinUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class StandFoldersService {
    private static Logger log = LoggerFactory.getLogger(StandFoldersService.class);

    private static String excelAbsolutePath;

    private static final String CREATOR = "creator";

    @Autowired
    private TreeService treeService;

    @Autowired
    private DataPermissionService dataPermissionService;


    public static String getExcelAbsolutePath() {
        return excelAbsolutePath;
    }

    @Value("${my.excel-file.absolutePath}")
    public void setExcelAbsolutePath(String excelAbsolutePath) {
        StandFoldersService.excelAbsolutePath = excelAbsolutePath;//NOSONAR
    }

    @Deprecated
    public static Set<String> addStandFolders(CimDataSpace cds, String tenantId, String creator,
                                              IndustryTypesService typesService) {
        File file = new File(getExcelAbsolutePath());
        ExcelImportUtil excelImportUtil = new ExcelImportUtil();
        //配置文件解析
        if (file.exists()) {
            excelImportUtil.analyzeExcel(getExcelAbsolutePath(), ExcelImportUtil.EXT);
            log.info("配置文件解析");
        } else {
            //默认解析
            String fileName = "excel" + File.separator + "demo.xlsx";
            excelImportUtil.analyzeExcel(fileName, ExcelImportUtil.DEFAULT);
            log.info("默认配置文件解析");
        }
        List<StandardTreeNode> treeNodes = excelImportUtil.buildTree();
        Set<String> rootRids = addFolder(cds, tenantId, creator, typesService, treeNodes);
        return rootRids;
    }

    public List<String> addStandFolders(CimDataSpace cds, String tenantId, String userId, String treeDefId,
                                       Fact parentNodeFact) {
        File file = new File(getExcelAbsolutePath());
        ExcelImportUtil excelImportUtil = new ExcelImportUtil();
        //配置文件解析
        if (file.exists()) {
            excelImportUtil.analyzeExcel(getExcelAbsolutePath(), ExcelImportUtil.EXT);
            log.info("配置文件解析");
        } else {
            //默认解析
            String fileName = "excel" + File.separator + "demo.xlsx";
            excelImportUtil.analyzeExcel(fileName, ExcelImportUtil.DEFAULT);
            log.info("默认配置文件解析");
        }
        List<StandardTreeNode> treeNodes = excelImportUtil.buildTree();
        List<String> rootRids = addIndustruyNodes(cds, tenantId, userId, treeDefId, parentNodeFact, treeNodes);
        return rootRids;
    }

    private static Set<String> addFolder(CimDataSpace cds, String tenantId, String creator,
                                         IndustryTypesService typesService, List<StandardTreeNode> treeNodes) {
        Set<String> rootIndustryRids = new HashSet<>();
        Map<String, StandardTreeNode> standFoldersServiceMap = new HashMap<>();
        if (treeNodes == null || treeNodes.size() < 1) {
            log.error("standard folder is empty");
            return rootIndustryRids;
        }

        for (StandardTreeNode treeNode : treeNodes) {
            try {
                Map<String, Object> firstIndustry1 = new HashMap<>();
                firstIndustry1.put(IndustryTypeNodeKeys.INDUSTRY_ID, UUID.randomUUID().toString());
                firstIndustry1.put(IndustryTypeNodeKeys.INDUSTRY_NAME, treeNode.getTitle());
                // String industryRid = addIndustryTypeDataSet(cds, tenantId, creator, firstIndustry1);
                String industryRid = typesService.addIndustryTypeDataSet(tenantId, creator, firstIndustry1, false);
                rootIndustryRids.add(industryRid);
                treeNode.setRid(industryRid);
                standFoldersServiceMap.put(treeNode.getKey(), treeNode);

                List<StandardTreeNode> childFolders = treeNode.getChildren();
                if (CollectionUtils.isNotEmpty(childFolders)) {
                    for (StandardTreeNode childFolder : childFolders) {
                        Map<String, Object> tmpMap = new HashMap<>();
                        tmpMap.put(IndustryTypeNodeKeys.INDUSTRY_ID, UUID.randomUUID().toString());
                        tmpMap.put(IndustryTypeNodeKeys.INDUSTRY_NAME, childFolder.getTitle());
                        tmpMap.put(IndustryTypeNodeKeys.PARENT_INDUSTRY_RID, industryRid);
                        //true 走默认挂载
                        typesService.addIndustryTypeDataSet(tenantId, creator, tmpMap, true);
                    }
                }
                // }
            } catch (Exception e) {
                log.error("add standard failed", e);
            }
        }

        return rootIndustryRids;
    }

    public static IndustryType addIndustryTypeDataSet(CimDataSpace cds, String tenantId, String creator, Map<String,
            Object> metadataValues) throws CimDataEngineInfoExploreException, CimDataEngineRuntimeException,
            CimDataEngineDataMartException {
        IndustryTypeVO industryTypeVO = new IndustryTypeVO();
        industryTypeVO.setIndustryTypeName(metadataValues.get(IndustryTypeNodeKeys.INDUSTRY_ID).toString());
        industryTypeVO.setIndustryTypeDesc(metadataValues.get(IndustryTypeNodeKeys.INDUSTRY_NAME).toString());
        industryTypeVO.setCreatorId(creator);
        if (metadataValues.get(IndustryTypeNodeKeys.PARENT_INDUSTRY_RID) != null) {
            industryTypeVO.setParentIndustryTypeId(metadataValues.get(IndustryTypeNodeKeys.PARENT_INDUSTRY_RID).toString());
        }

        IndustryType industryType = null;
        Map<String, Object> formalValues = metadataValues;
        formalValues.put(IndustryTypeNodeKeys.CREATOR_ID, creator);
        formalValues.put(IndustryTypeNodeKeys.UPDATOR_ID, creator);
        formalValues.put(IndustryTypeNodeKeys.CREATE_TIME, new Date());
        formalValues.put(IndustryTypeNodeKeys.UPDATE_TIME, new Date());
        //add parent relation
        String parentIndustryTypeId = industryTypeVO.getParentIndustryTypeId();
        if (StringUtils.isBlank(parentIndustryTypeId)) {
            industryType = addRootIndustryType(cds, industryTypeVO, tenantId);
        } else {
            //todo 这个有没有pid和挂载无关
            industryType = addRootIndustryType(cds, industryTypeVO, tenantId);
        }

        for (Map.Entry<String, Object> entry : formalValues.entrySet()) {
            try {
                industryType.addOrUpdateProperty(entry.getKey(), entry.getValue());
            } catch (DataServiceModelRuntimeException e) {
                e.printStackTrace();
            }
        }

        if (industryType != null) {
            return industryType;
        } else {
            return null;
        }
    }

    public static IndustryType addRootIndustryType(CimDataSpace cds, IndustryTypeVO industryTypeVO, String tenantId) throws CimDataEngineInfoExploreException, CimDataEngineRuntimeException, CimDataEngineDataMartException {
        industryTypeVO.setTenantId(tenantId);
        IndustryTypeVO resultIndustryTypeVO = addRootIndustryType(cds, industryTypeVO);
        if (resultIndustryTypeVO != null) {
            IndustryTypeDSImpl industryTypeDSImpl = new IndustryTypeDSImpl(CimConstants.defauleSpaceName, tenantId,
                    resultIndustryTypeVO.getIndustryTypeId());
            industryTypeDSImpl.setIndustryTypeVO(resultIndustryTypeVO);
            return industryTypeDSImpl;
        }
        return null;
    }

    public static IndustryTypeVO addRootIndustryType(CimDataSpace cds, IndustryTypeVO industryTypeVO) throws CimDataEngineRuntimeException, CimDataEngineDataMartException, CimDataEngineInfoExploreException {
        Dimension targetDimension =
                CimDataEngineComponentFactory.createDimension(BusinessLogicConstant.INDUSTRY_TYPE_DIMENSION_TYPE_NAME);
        targetDimension.setInitProperty("industryTypeName", industryTypeVO.getIndustryTypeName());
        targetDimension.setInitProperty("industryTypeDesc", industryTypeVO.getIndustryTypeDesc());
        targetDimension.setInitProperty(CREATOR, industryTypeVO.getCreatorId());
        targetDimension.setInitProperty(CimConstants.CREATE_TIME, new Date());
        targetDimension.setInitProperty(CimConstants.UPDATE_TIME, new Date());
        targetDimension = cds.addDimension(targetDimension);

        IndustryTypeVO typeVO = new IndustryTypeVO();
        typeVO.setIndustryTypeId(targetDimension.getId());
        typeVO.setIndustryTypeDesc(targetDimension.getProperty("industryTypeDesc").getPropertyValue().toString());
        typeVO.setIndustryTypeName(targetDimension.getProperty("industryTypeName").getPropertyValue().toString());
        if (targetDimension.hasProperty(CREATOR)) {
            typeVO.setCreatorId(targetDimension.getProperty(CREATOR).getPropertyValue().toString());
        }
        if (targetDimension.hasProperty(CimConstants.CREATE_TIME)) {
            typeVO.setCreateDateTime((Date) (targetDimension.getProperty(CimConstants.CREATE_TIME).getPropertyValue()));
        }
        if (targetDimension.hasProperty(CimConstants.UPDATE_TIME)) {
            typeVO.setUpdateDateTime((Date) (targetDimension.getProperty(CimConstants.UPDATE_TIME).getPropertyValue()));
        }

        if (industryTypeVO.getTenantId() != null) {
            CommonOperationUtil.addToBelongingTenant(cds, industryTypeVO.getTenantId(), targetDimension);
        }

        IndustryTypeCache.addRootIndustryCacheItem(cds.getSpaceName(), typeVO);

        return typeVO;
    }

    public List<String> addIndustruyNodes(CimDataSpace cds, String tenantId, String userId, String treeDefId,
                                    Fact parentNodeFact, List<StandardTreeNode> treeNodes) {
        List<String> allNodeRids = new ArrayList<>();
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(cds.getSpaceName(), tenantId);
        modelCore.setCimDataSpace(cds);
        for (StandardTreeNode treeNode : treeNodes) {
            addIndustruyNode(modelCore, tenantId, userId, treeDefId, parentNodeFact, treeNode, allNodeRids);
        }

        if (allNodeRids.size() > 0) {
            dataPermissionService.addDataPermissionByUser(cds, userId, allNodeRids);
        }
        return allNodeRids;

    }

    public Fact addIndustruyNode(CIMModelCore modelCore, String tenantId, String userId, String treeDefId,
                                 Fact parentNodeFact, StandardTreeNode treeNode, List<String> nodeRids) {
        if (treeNode == null || StringUtils.isBlank(treeNode.getTitle())) {
            return null;
        }
        try {
            //add industry
            IndustryTypeVO industryTypeVO = new IndustryTypeVO();
            industryTypeVO.setIndustryTypeName(PinyinUtils.getPinYinWithoutSpecialChar(treeNode.getTitle()));
            industryTypeVO.setIndustryTypeDesc(treeNode.getTitle());
            industryTypeVO.setCreatorId(userId);
            IndustryTypes industryTypes = modelCore.getIndustryTypes();
            IndustryType industryType = industryTypes.addRootIndustryType(industryTypeVO);

            if (industryType != null) {
                //add node
                NodeInfoBean nodeInfoBean = new NodeInfoBean();
                nodeInfoBean.setNAME(treeNode.getTitle());
                nodeInfoBean.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
                nodeInfoBean.setTreeDefId(treeDefId);
                CimDataSpace cds = modelCore.getCimDataSpace();
                String nodeFactRid = treeService.addNodeInfoWithoutDataPermission(cds, tenantId, parentNodeFact,
                        nodeInfoBean, null);

                Fact nodeFact = cds.getFactById(nodeFactRid);
                nodeFact.addToRelation(cds.getDimensionById(industryType.getIndustryTypeRID()),
                        BusinessLogicConstant.TREE_NODE_TO_RALATIONABLE_RELATION_TYPE);

                List<StandardTreeNode> childNodeList = treeNode.getChildren();
                if (childNodeList != null && childNodeList.size() > 0) {
                    for (StandardTreeNode childNode : childNodeList) {
                        addIndustruyNode(modelCore, tenantId, userId, treeDefId, nodeFact, childNode, nodeRids);
                    }
                }
                nodeRids.add(nodeFactRid);
                return nodeFact;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("add standar folders failed", e);
        }
        return null;
    }

    public void setTreeService(TreeService treeService) {
        this.treeService = treeService;
    }
}
