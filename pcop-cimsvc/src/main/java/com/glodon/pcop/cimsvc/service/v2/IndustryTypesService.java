package com.glodon.pcop.cimsvc.service.v2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.bim.BimFileUploadTranslateBean;
import com.glodon.pcop.cim.common.model.entity.AddIndustryTypeInputBean;
import com.glodon.pcop.cim.common.model.entity.AddIndustryTypeOutputBean;
import com.glodon.pcop.cim.common.model.entity.DataSetEntity;
import com.glodon.pcop.cim.common.model.entity.IndustryTypeEntity;
import com.glodon.pcop.cim.common.model.entity.UpdateIndustryTypeInputBean;
import com.glodon.pcop.cim.common.model.entity.UpdateIndustryTypeOutputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.BaseDataSetKeys;
import com.glodon.pcop.cim.common.util.CimConstants.BaseFileInfoKeys;
import com.glodon.pcop.cim.common.util.CimConstants.IndustryTypeNodeKeys;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Dimension;
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
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineDataMartException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryType;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryTypes;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.DouplicateNameException;
import com.glodon.pcop.cimsvc.exception.EntityAddException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.exception.InputNotEnoughException;
import com.glodon.pcop.cimsvc.model.FileMetadataUploadBean;
import com.glodon.pcop.cimsvc.model.input.TreeNodeInputBean;
import com.glodon.pcop.cimsvc.model.output.TreeNodeOutputBean;
import com.glodon.pcop.cimsvc.model.transverter.DataSetTsr;
import com.glodon.pcop.cimsvc.model.transverter.IndustryTypeTsr;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.glodon.pcop.cimsvc.model.v2.IndustryTypeTreeQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.LinkObjectAndInstanceInputBean;
import com.glodon.pcop.cimsvc.model.v2.LinkObjectAndInstanceOutputBean;
import com.glodon.pcop.cimsvc.service.MinioService;
import com.glodon.pcop.cimsvc.service.OrientDBCommonUtil;
import com.glodon.pcop.cimsvc.service.StandFoldersService;
import com.glodon.pcop.cimsvc.service.kafka.SendMessageUtil;
import com.glodon.pcop.cimsvc.service.tree.TreeNodeService;
import com.glodon.pcop.cimsvc.service.v2.engine.IndustryTypeDefService;
import com.glodon.pcop.cimsvc.service.v2.engine.InfoObjectTypeDefService;
import com.glodon.pcop.cimsvc.util.DateUtil;
import com.glodon.pcop.core.models.IdResult;
import com.glodon.pcop.jobapi.JobResponse;
import com.glodon.pcop.jobapi.dto.JobParmDTO;
import com.glodon.pcop.jobapi.dto.JobPropsDTO;
import com.glodon.pcop.jobapi.type.JobStatusEnum;
import com.glodon.pcop.jobclt.client.JobInfoClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IndustryTypesService {
    private static Logger log = LoggerFactory.getLogger(IndustryTypesService.class);

    @Autowired
    public InfoObjectTypeDefService objectTypeDefService;

    @Autowired
    private IndustryTypeDefService industryTypeDefService;

    @Autowired
    private BimFaceService bimFaceService;

    @Autowired
    private MinioService minioService;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JobInfoClient jobInfoClient;

    @Autowired
    private TreeNodeService treeNodeService;

    @Autowired
    private SendMessageUtil sendMessageUtil;

    @Autowired
    private OfficeConverterService converterService;


    private String fileUpload = "fileUpload";

    private String fileUploadPrefix = "fileUploadPrefix";

    private static final String INFO_OBJECT_TYPE_NAME = "infoObjectTypeName";
    private static final String FILE_NODE_INSTANCE_ADD_FAILED = "file node instance add failed";
    private static final String INDUSTRY_TYPE_OF_S_NOT_FOUND = "industry type of %s not found";

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * 新增行业分类定义
     *
     * @param tenantId
     * @param industryTypeEntity
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public IndustryTypeEntity addIndustryType(String tenantId, IndustryTypeEntity industryTypeEntity) throws DataServiceModelRuntimeException {
        IndustryTypeVO industryTypeVO = IndustryTypeTsr.entityToVoWithoutChildren(industryTypeEntity);
        industryTypeVO = industryTypeDefService.addIndustryType(tenantId, industryTypeVO);
        return IndustryTypeTsr.voToEntityWithoutChildren(industryTypeVO);
    }

    public AddIndustryTypeOutputBean addIndustryType(String tenantId, String userId,
                                                     AddIndustryTypeInputBean inputBean) throws DataServiceModelRuntimeException {
        IndustryTypeVO industryTypeVO = IndustryTypeTsr.addInputBeanToVo(userId, inputBean);
        industryTypeVO = industryTypeDefService.addIndustryType(tenantId, industryTypeVO);
        return IndustryTypeTsr.voToAddOutputBean(industryTypeVO);
    }

    /**
     * 删除行业分类定义及其子类
     *
     * @param tenantId
     * @param industryTypeRid
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public boolean removeIndustryType(String tenantId, String industryTypeRid) throws DataServiceModelRuntimeException {
        return industryTypeDefService.removeIndustryType(tenantId, industryTypeRid);
    }

    /**
     * 更新行业分类名称
     *
     * @param tenantId
     * @param industryTypeId
     * @param industryTypeEntity
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public IndustryTypeEntity updateIndustryType(String tenantId, String industryTypeId,
                                                 IndustryTypeEntity industryTypeEntity) throws DataServiceModelRuntimeException, EntityNotFoundException {
        IndustryTypeVO industryTypeVO = IndustryTypeTsr.entityToVoWithoutChildren(industryTypeEntity);
        industryTypeVO = industryTypeDefService.updateIndustryType(tenantId, industryTypeId, industryTypeVO);
        return IndustryTypeTsr.voToEntityWithoutChildren(industryTypeVO);
    }

    public UpdateIndustryTypeOutputBean updateIndustryType(String tenantId, String userId, String industryTypeId,
                                                           UpdateIndustryTypeInputBean typeInputBean) throws DataServiceModelRuntimeException, EntityNotFoundException {
        IndustryTypeVO industryTypeVO = IndustryTypeTsr.updateInputBeanToVo(userId, typeInputBean);
        industryTypeVO = industryTypeDefService.updateIndustryType(tenantId, industryTypeId, industryTypeVO);
        return IndustryTypeTsr.voToUpdateOutputBean(industryTypeVO);
    }

    /**
     * 根据rid查询行业分类定义详情
     *
     * @param tenantId
     * @param industryTypeId
     * @return
     * @throws EntityNotFoundException
     */
    public IndustryTypeEntity getIndustryType(String tenantId, String industryTypeId) throws EntityNotFoundException {
        return IndustryTypeTsr.voToEntityWithoutChildren(industryTypeDefService.getIndustryType(tenantId,
                industryTypeId));
    }

    /**
     * 获取指定行业分类的所有子类及与其关联的对象模型
     *
     * @param tenantId
     * @param industryTypeId 若为空，则返回第一级（root industry type）行业分类
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws EntityNotFoundException
     */
    public IndustryTypeTreeQueryOutput getAllChildIndustryTypesAndLinkedObjectTypes(String tenantId,
                                                                                    String industryTypeId) throws DataServiceModelRuntimeException, EntityNotFoundException {
        return industryTypeDefService.getAllChildIndustryTypesAndLinkedObjectTypes(tenantId, industryTypeId);
    }

    public List<DataSetEntity> getDataSetDefs(String tenantId, String dataSetName, Boolean isIncludedProperty) {
        List<DataSetEntity> dataSetEntityList = new ArrayList<>();

        List<DatasetDef> datasetDefList = new ArrayList<>();
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            modelCore.setCimDataSpace(cds);

            IndustryTypes types = modelCore.getIndustryTypes();
            if (StringUtils.isBlank(dataSetName)) {
                datasetDefList = types.getDatasetDefs();
            } else {
                datasetDefList.add(types.getDatasetDef(dataSetName));
            }

            if (datasetDefList != null && datasetDefList.size() > 0) {
                for (DatasetDef datasetDef : datasetDefList) {
                    if (datasetDef != null) {
                        DatasetVO datasetVO = DatasetFeatures.getDatasetVOById(cds, datasetDef.getDatasetRID(),
                                isIncludedProperty);
                        if (datasetVO != null) {
                            dataSetEntityList.add(DataSetTsr.voToEntityV2(datasetVO, isIncludedProperty));
                        }
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

        return dataSetEntityList;
    }

    /**
     * 增加行业分类属性集
     *
     * @param tenantId
     * @param creator
     * @param metadataValues
     * @return
     * @throws DouplicateNameException
     */
    public String addIndustryTypeDataSet(String tenantId, String creator, Map<String, Object> metadataValues,
                                         Boolean isGeneral) throws DouplicateNameException {//NOSONAR
        IndustryTypeVO industryTypeVO = new IndustryTypeVO();
        industryTypeVO.setIndustryTypeName(metadataValues.get(IndustryTypeNodeKeys.INDUSTRY_ID).toString());
        industryTypeVO.setIndustryTypeDesc(metadataValues.get(IndustryTypeNodeKeys.INDUSTRY_NAME).toString());
        industryTypeVO.setCreatorId(creator);
        if (metadataValues.get(IndustryTypeNodeKeys.PARENT_INDUSTRY_RID) != null) {
            industryTypeVO.setParentIndustryTypeId(metadataValues.get(IndustryTypeNodeKeys.PARENT_INDUSTRY_RID).toString());
        }

        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        IndustryType industryType = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            cimModelCore.setCimDataSpace(cds);
            IndustryTypes industryTypes = cimModelCore.getIndustryTypes();
            Map<String, Object> formalValues = valueDataTypeCast(industryTypes, cds, metadataValues);
            formalValues.put(IndustryTypeNodeKeys.CREATOR_ID, creator);
            formalValues.put(IndustryTypeNodeKeys.UPDATOR_ID, creator);
            formalValues.put(IndustryTypeNodeKeys.CREATE_TIME, new Date());
            formalValues.put(IndustryTypeNodeKeys.UPDATE_TIME, new Date());
            //判断节点是不是独立的分类
            if (isGeneral) {
                //add parent relation
                String parentIndustryTypeId = industryTypeVO.getParentIndustryTypeId();
                //生成根分类
                if (StringUtils.isBlank(parentIndustryTypeId)) {
                    Set<String> nameSet = getAllChildNodeNamesByType(null, cds, tenantId,
                            TreeNodeInputBean.TreeNodeTypeEnum.INDUSTRY);
                    if (nameSet != null && nameSet.contains(metadataValues.get(IndustryTypeNodeKeys.INDUSTRY_NAME).toString())) {
                        throw new DouplicateNameException(EnumWrapper.CodeAndMsg.E05050002);
                    }
                    industryType = industryTypes.addRootIndustryType(industryTypeVO);
                } else {
                    Set<String> nameSet =
                            getAllChildNodeNamesByType(industryTypes.getIndustryType(parentIndustryTypeId), cds,
                                    tenantId, TreeNodeInputBean.TreeNodeTypeEnum.INDUSTRY);
                    if (nameSet != null && nameSet.contains(metadataValues.get(IndustryTypeNodeKeys.INDUSTRY_NAME).toString())) {
                        throw new DouplicateNameException(EnumWrapper.CodeAndMsg.E05050002);
                    }
                    industryType = industryTypes.addChildIndustryType(industryTypeVO, parentIndustryTypeId);
                }
                if (industryType != null) {
                    for (Map.Entry<String, Object> entry : formalValues.entrySet()) {
                        industryType.addOrUpdateProperty(entry.getKey(), entry.getValue());
                    }
                } else {
                    log.error("industry type add failed");
                }
            } else {
                industryType = StandFoldersService.addIndustryTypeDataSet(cds, tenantId, creator, metadataValues);
            }


        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineDataMartException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        if (industryType != null) {
            return industryType.getIndustryTypeRID();
        } else {
            return null;
        }
    }

    public Map<String, Object> getIndustryTypeDataSet(String tenantId, String industryRid, String dataSetName) {
        Map<String, Object> values = new HashMap<>();
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            modelCore.setCimDataSpace(cds);
            IndustryTypes industryTypes = modelCore.getIndustryTypes();
            // IndustryType industryType = modelCore.getIndustryType(industryRid);
            IndustryType industryType = industryTypes.getIndustryType(industryRid);
            if (industryType != null) {
                if (StringUtils.isNotBlank(dataSetName)) {
                    values = industryType.getPropertiesByDataset(dataSetName);
                } else {
                    log.error("industry type data set name is mandatory");
                }
            } else {
                log.error("industry type is null or is not belong to this tenant, tenantId={}, industryRid={}",
                        tenantId, industryRid);
            }

        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return values;
    }

    public String updateIndustryTypeDataSet(String tenantId, String creator, String industryRid,
                                            Map<String, Object> values) throws DouplicateNameException {//NOSONAR
        IndustryType industryType = null;
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            modelCore.setCimDataSpace(cds);
            IndustryTypes industryTypes = modelCore.getIndustryTypes();
            Map<String, Object> formalValues = valueDataTypeCast(industryTypes, cds, values);
            formalValues.put(IndustryTypeNodeKeys.UPDATOR_ID, creator);
            formalValues.put(IndustryTypeNodeKeys.UPDATE_TIME, new Date());
            industryType = industryTypes.getIndustryType(industryRid);
            if (industryType != null) {
                //child nodes duplicate names check
                String parentIndustryRid = "";
                Relation parentRelation = getParentIndustryType(cds, industryRid);
                if (values.containsKey(IndustryTypeNodeKeys.PARENT_INDUSTRY_RID) && StringUtils.isNotBlank(values.get(IndustryTypeNodeKeys.PARENT_INDUSTRY_RID).toString())) {
                    parentIndustryRid = values.get(IndustryTypeNodeKeys.PARENT_INDUSTRY_RID).toString();
                } else if (parentRelation != null) {
                    parentIndustryRid = parentRelation.getFromRelationable().getId();
                } else {
                    log.debug("this is root industry");
                }
                IndustryType parentIndustry = null;
                if (StringUtils.isNotBlank(parentIndustryRid)) {
                    parentIndustry = industryTypes.getIndustryType(parentIndustryRid);
                }
                Map<String, String> nameMap = getAllChildNodeNamesMapByType(parentIndustry, cds, tenantId,
                        TreeNodeInputBean.TreeNodeTypeEnum.INDUSTRY);
                if (nameMap != null && nameMap.containsKey(values.get(IndustryTypeNodeKeys.INDUSTRY_NAME).toString())) {
                    if (!nameMap.get(values.get(IndustryTypeNodeKeys.INDUSTRY_NAME).toString()).equals(industryRid)) {
                        log.error("douplicate child industry names={} of parentIndustryRid={}",
                                values.get(IndustryTypeNodeKeys.INDUSTRY_NAME).toString(), parentIndustryRid);
                        throw new DouplicateNameException(EnumWrapper.CodeAndMsg.E05050002);
                    }
                }
                //update parent industry
                if (values.get(IndustryTypeNodeKeys.PARENT_INDUSTRY_RID) != null && parentRelation != null && StringUtils.isNotBlank(values.get(IndustryTypeNodeKeys.PARENT_INDUSTRY_RID).toString()) && !parentRelation.getToRelationable().getId().equals(values.get(IndustryTypeNodeKeys.PARENT_INDUSTRY_RID))) {
                    cds.removeRelation(parentRelation.getId());
                    Dimension parentDimension =
                            cds.getDimensionById(values.get(IndustryTypeNodeKeys.PARENT_INDUSTRY_RID).toString());
                    Dimension currentDimension = cds.getDimensionById(industryRid);
                    cds.addDirectionalDimensionRelation(parentDimension, currentDimension,
                            BusinessLogicConstant.IS_PARENT_INDUSTRY_TYPE_RELATION_TYPE_NAME);
                    log.info("parent industry type is updated");
                } else {
                    log.info("parent industry should not be updated");
                }
                //update other metadata
                if (formalValues != null) {
                    for (Map.Entry<String, Object> entry : formalValues.entrySet()) {
                        if (entry.getKey().equals(IndustryTypeNodeKeys.CREATE_TIME) || entry.getKey().equals(IndustryTypeNodeKeys.INDUSTRY_ID)) {
                            log.error("create date time and industry type id can not be updated");
                            continue;
                        }
                        industryType.addOrUpdateProperty(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                log.error("industry type not exists or not belong to this tenant");
            }
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        if (industryType != null) {
            return industryType.getIndustryTypeRID();
        } else {
            return null;
        }
    }

    private Map<String, Object> valueDataTypeCast(IndustryTypes industryTypes, CimDataSpace cds,
                                                  Map<String, Object> originalValues) throws CimDataEngineRuntimeException {
        // IndustryTypes industryTypes = cimModelCore.getIndustryTypes();
        //数据类型转换
        List<DatasetDef> datasetDefList = industryTypes.getDatasetDefs();
        if (datasetDefList == null || datasetDefList.size() < 1) {
            log.error("no data set of industry type is found");
            return null;
        }
        List<DatasetVO> datasetVOList = new ArrayList<>();
        for (DatasetDef datasetDef : datasetDefList) {
            datasetVOList.add(DatasetFeatures.getDatasetVOById(cds, datasetDef.getDatasetRID(), true));
        }
        Map<String, Object> formalValues =
                OrientDBCommonUtil.valuesTypeCast(OrientDBCommonUtil.mergeDataTypeDef(datasetVOList), originalValues);

        return formalValues;
    }

    private Map<String, Object> valueDataTypeCast(InfoObjectDef infoObjectDef, CimDataSpace cds,
                                                  Map<String, Object> originalValues) throws CimDataEngineRuntimeException {
        // IndustryTypes industryTypes = cimModelCore.getIndustryTypes();
        //数据类型转换
        List<DatasetDef> datasetDefList = infoObjectDef.getDatasetDefs();
        if (datasetDefList == null || datasetDefList.size() < 1) {
            log.error("no data set of industry type is found");
            return null;
        }
        List<DatasetVO> datasetVOList = new ArrayList<>();
        for (DatasetDef datasetDef : datasetDefList) {
            datasetVOList.add(DatasetFeatures.getDatasetVOById(cds, datasetDef.getDatasetRID(), true));
        }
        Map<String, Object> formalValues =
                OrientDBCommonUtil.valuesTypeCast(OrientDBCommonUtil.mergeDataTypeDef(datasetVOList), originalValues);

        return formalValues;
    }

    /**
     * 查询指定分类的父类
     *
     * @param cimDataSpace
     * @param industryRid
     * @return
     */
    public Relation getParentIndustryType(CimDataSpace cimDataSpace, String industryRid) {
        Relation relation = null;
        try {
            Dimension dimension = cimDataSpace.getDimensionById(industryRid);
            if (dimension != null) {
                List<Relation> relationList =
                        dimension.getAllSpecifiedRelations(BusinessLogicConstant.IS_PARENT_INDUSTRY_TYPE_RELATION_TYPE_NAME, RelationDirection.TO);
                if (relationList != null && relationList.size() > 0) {
                    //parent industry is unique
                    relation = relationList.get(0);
                } else {
                    log.info("no parent industry of {} is found", industryRid);
                }
            } else {
                log.error("industry dimension of {} not found", industryRid);
            }

        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }
        return relation;
    }

    public synchronized TreeNodeOutputBean addFileNode(String tenantId, String creator, String industryRid,
                                                       Map<String, Object> fileMetadata) throws EntityNotFoundException, InputNotEnoughException, EntityAddException {//NOSONAR
        TreeNodeOutputBean nodeOutputBean = new TreeNodeOutputBean();
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            modelCore.setCimDataSpace(cds);
            IndustryTypes industryTypes = modelCore.getIndustryTypes();
            IndustryType industryType = industryTypes.getIndustryType(industryRid);
            if (industryType == null) {
                String msg = String.format(INDUSTRY_TYPE_OF_S_NOT_FOUND, industryRid);
                log.error(msg);
                throw new EntityNotFoundException(msg);
            }

            InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(BaseFileInfoKeys.BaseFileObjectTypeName);
            if (infoObjectDef == null) {
                String msg = String.format("info object type of %s not found", BaseFileInfoKeys.BaseFileObjectTypeName);
                log.error(msg);
                throw new EntityNotFoundException(msg);
            }
            if (!fileMetadata.containsKey(BaseFileInfoKeys.BUCKET_NAME) || !fileMetadata.containsKey(BaseFileInfoKeys.FILE_DATA_NAME) || !fileMetadata.containsKey(BaseFileInfoKeys.FILE_DATA_ID) || !fileMetadata.containsKey(BaseFileInfoKeys.SRC_FILE_NAME)) {
                String msg = String.format("create file node failed, parameters is not enough: bucketName, " +
                        "fileDataName, fileDataId and srcFileName are mandatory, input parameters=%s", fileMetadata);
                log.error(msg);
                throw new InputNotEnoughException(msg);
            }
            String bucketName = fileMetadata.get(BaseFileInfoKeys.BUCKET_NAME).toString();
            String fileDataName = fileMetadata.get(BaseFileInfoKeys.FILE_DATA_NAME).toString();
            String fileDataId = fileMetadata.get(BaseFileInfoKeys.FILE_DATA_ID).toString();
            // String fileType = fileMetadata.get(BaseFileInfoKeys.FILE_TYPE).toString();
            String srcFileName = fileMetadata.get(BaseFileInfoKeys.SRC_FILE_NAME).toString();

            if (StringUtils.isNotBlank(bucketName) && StringUtils.isNotBlank(fileDataName) && StringUtils.isNotBlank(fileDataId) && StringUtils.isNotBlank(srcFileName)) {
                Map<String, Object> baseInfo = new HashMap<>();
                baseInfo.put(BaseDataSetKeys.ID, fileDataId);
                baseInfo.put(BaseDataSetKeys.NAME, fileDataName);

                Map<String, Object> generalInfo = new HashMap<>();
                generalInfo.putAll(valueDataTypeCast(infoObjectDef, cds, fileMetadata));
                generalInfo.put(BaseFileInfoKeys.CREATOR, creator);
                generalInfo.put(BaseFileInfoKeys.UPDATOR, creator);
                generalInfo.put(BaseFileInfoKeys.CREATE_TIME, new Date());
                generalInfo.put(BaseFileInfoKeys.UPDATE_TIME, new Date());
                String fileExt = srcFileName.substring(srcFileName.lastIndexOf(".") + 1);
                if (StringUtils.isNotBlank(fileExt)) {
                    fileExt = fileExt.toUpperCase();
                }
                String fileType = CimConstants.FILE_TYPES_MAPPING.get(fileExt);
                generalInfo.put(BaseFileInfoKeys.FILE_TYPE, fileType);
                InfoObjectValue objectValue = new InfoObjectValue();
                objectValue.setBaseDatasetPropertiesValue(baseInfo);
                objectValue.setGeneralDatasetsPropertiesValue(generalInfo);

                InfoObject infoObject = infoObjectDef.newObject(objectValue, false);
                if (infoObject != null) {
                    industryType.linkChildInfoObject(infoObject.getObjectInstanceRID());
                    nodeOutputBean.setId(fileDataId);
                    nodeOutputBean.setInstanceRid(infoObject.getObjectInstanceRID());
                    nodeOutputBean.setNodeType(TreeNodeOutputBean.TreeNodeTypeEnum.FILE);
                    nodeOutputBean.setDataType(fileType);
                    nodeOutputBean.setName(fileDataName);
                    nodeOutputBean.setSrcFileName(srcFileName);
                    if (CimConstants.BIM_FILE_TYPES.contains(fileExt)) {
                        log.info("bim file should be uploaded and translated: fileType={}", fileType);
                        bimFaceService.uploadTask(fileDataName, bucketName, CimConstants.defauleSpaceName,
                                infoObject.getObjectInstanceRID());
                    } else {
                        log.info("not bim file, should not be uploaded and translated: fileType={}", fileType);
                    }
                } else {
                    String msg = String.format(FILE_NODE_INSTANCE_ADD_FAILED);
                    log.error(msg);
                    throw new EntityAddException(msg);
                }
            } else {
                String msg = String.format("create file node failed, parameters is not enough: bucketName=%s, " +
                                "fileDataName=%s, fileDataId=%s, srcFileName=%s", bucketName, fileDataName, fileDataId,
                        srcFileName);
                log.error(msg);
                throw new InputNotEnoughException(msg);
            }
        } catch (DataServiceUserException e) {
            String msg = String.format(FILE_NODE_INSTANCE_ADD_FAILED);
            log.error(msg, e);
            throw new EntityAddException(msg);
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            String msg = String.format("input value data type cast");
            log.error(msg, e);
            throw new EntityAddException(msg);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return nodeOutputBean;
    }

    /**
     * 创建新的文件节点
     *
     * @param industryType
     * @param infoObjectDef
     * @param creator
     * @param fileMetadata
     * @return
     * @throws InputNotEnoughException
     * @throws EntityAddException
     */
    public String createFileNode(CimDataSpace cds, IndustryType industryType, InfoObjectDef infoObjectDef,
                                 Fact instanceFact, String creator, Map<String, ? extends Object> fileMetadata) throws InputNotEnoughException, EntityAddException, CimDataEngineRuntimeException {
        String instanceRid = null;
        try {
            if (!fileMetadata.containsKey(BaseFileInfoKeys.BUCKET_NAME) || !fileMetadata.containsKey(BaseFileInfoKeys.FILE_DATA_NAME) || !fileMetadata.containsKey(BaseFileInfoKeys.FILE_DATA_ID) || !fileMetadata.containsKey(BaseFileInfoKeys.SRC_FILE_NAME)) {
                String msg = String.format("create file node failed, parameters is not enough: bucketName, " +
                        "fileDataName, fileDataId and srcFileName are mandatory, input parameters=%s", fileMetadata);
                log.error(msg);
                throw new InputNotEnoughException(msg);
            }
            String bucketName = fileMetadata.get(BaseFileInfoKeys.BUCKET_NAME).toString();
            String fileDataName = fileMetadata.get(BaseFileInfoKeys.FILE_DATA_NAME).toString();
            String fileDataId = fileMetadata.get(BaseFileInfoKeys.FILE_DATA_ID).toString();
            String srcFileName = fileMetadata.get(BaseFileInfoKeys.SRC_FILE_NAME).toString();

            if (StringUtils.isNotBlank(bucketName) && StringUtils.isNotBlank(fileDataName) && StringUtils.isNotBlank(fileDataId) && StringUtils.isNotBlank(srcFileName)) {
                Map<String, Object> baseInfo = new HashMap<>();
                baseInfo.put(BaseDataSetKeys.ID, fileDataId);
                baseInfo.put(BaseDataSetKeys.NAME, fileDataName);

                Map<String, Object> generalInfo = new HashMap<>();
                generalInfo.putAll(fileMetadata);
                generalInfo.put(BaseFileInfoKeys.CREATOR, creator);
                generalInfo.put(BaseFileInfoKeys.UPDATOR, creator);
                generalInfo.put(BaseFileInfoKeys.CREATE_TIME, new Date());
                generalInfo.put(BaseFileInfoKeys.UPDATE_TIME, new Date());
                generalInfo.put(BaseFileInfoKeys.INDUSTRY_NAME, industryType.getIndustryTypeDesc());

                InfoObjectValue objectValue = new InfoObjectValue();
                objectValue.setBaseDatasetPropertiesValue(baseInfo);
                objectValue.setGeneralDatasetsPropertiesValue(generalInfo);

                InfoObject infoObject = infoObjectDef.newObject(objectValue, false);
                if (infoObject != null) {
                    if (instanceFact == null) {
                        industryType.linkChildInfoObject(infoObject.getObjectInstanceRID());
                        instanceRid = infoObject.getObjectInstanceRID();
                    } else {
                        Map<String, Object> parms = new HashMap<>();
                        parms.put(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_INDUSTRY_RID,
                                industryType.getIndustryTypeRID());
                        Fact fileFact = cds.getFactById(infoObject.getObjectInstanceRID());
                        instanceFact.addFromRelation(fileFact,
                                BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_TYPE_NAME, parms);
                        instanceRid = infoObject.getObjectInstanceRID();
                    }
                } else {
                    String msg = String.format(FILE_NODE_INSTANCE_ADD_FAILED);
                    log.error(msg);
                    throw new EntityAddException(msg);
                }
            } else {
                String msg = String.format("create file node failed, parameters is not enough: bucketName=%s, " +
                                "fileDataName=%s, fileDataId=%s, srcFileName=%s", bucketName, fileDataName, fileDataId,
                        srcFileName);
                log.error(msg);
                throw new InputNotEnoughException(msg);
            }
        } catch (DataServiceUserException e) {
            String msg = String.format(FILE_NODE_INSTANCE_ADD_FAILED);
            log.error(msg, e);
            throw new EntityAddException(msg);
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        }
        return instanceRid;
    }

    /**
     * 更新文件节点信息，目前
     *
     * @param cds
     * @param creator
     * @param industryType
     * @param fileMetadata
     * @return
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     */
    public String updateFileNode(CimDataSpace cds, String creator, IndustryType industryType, Map<String, ?
            extends Object> fileMetadata) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        String instanceRid = null;
        if (!fileMetadata.containsKey(BaseFileInfoKeys.SRC_FILE_NAME) || fileMetadata.get(BaseFileInfoKeys.SRC_FILE_NAME) == null) {
            log.error("{} is mandatory", BaseFileInfoKeys.SRC_FILE_NAME);
            return instanceRid;
        }
        String minioObjectName = getMinioObjectName(industryType.getIndustryTypeRID(),
                industryType.getIndustryTypeName(), fileMetadata.get(BaseFileInfoKeys.SRC_FILE_NAME).toString());
        FilteringItem item = new EqualFilteringItem(BaseFileInfoKeys.MINIO_OBJECT_NAME, minioObjectName);
        ExploreParameters exploreParameters = new ExploreParameters();
        exploreParameters.setDefaultFilteringItem(item);
        exploreParameters.setType(BaseFileInfoKeys.BaseFileObjectTypeName);

        InformationExplorer ip = cds.getInformationExplorer();
        List<Fact> factList = ip.discoverInheritFacts(exploreParameters);
        if (factList != null && factList.size() > 0) {
            Fact fact = factList.get(0);
            fact.updateProperty(BaseFileInfoKeys.UPDATOR, creator);
            fact.updateProperty(BaseFileInfoKeys.UPDATE_TIME, new Date());
            if (fact.hasProperty(BaseFileInfoKeys.BUCKET_NAME)) {
                fact.updateProperty(BaseFileInfoKeys.BUCKET_NAME,
                        fileMetadata.get(BaseFileInfoKeys.BUCKET_NAME).toString());
            }
            instanceRid = fact.getId();
        } else {
            log.error("file node of {} not found", minioObjectName);
        }
        return instanceRid;
    }

    /**
     * 上传文件到行业分类或实例树节点下，并创建相应的元数据
     *
     * @param tenantId
     * @param creator
     * @param bucketName
     * @param treeNode
     * @param isOverride
     * @param metadata
     * @param uploadingFiles
     * @return
     * @throws EntityNotFoundException
     * @throws IOException
     * @throws CimDataEngineRuntimeException
     * @throws InputNotEnoughException
     */
    public List<Map<String, Object>> uploadFilesAndAddFileNodes(String tenantId, String creator, String bucketName,
                                                                String treeNode, Boolean isOverride, String metadata,
                                                                MultipartFile[] uploadingFiles) throws EntityNotFoundException, IOException, CimDataEngineRuntimeException, InputNotEnoughException {
        List<Map<String, Object>> resultList;
        TreeNodeInputBean inputBean = objectMapper.readValue(treeNode, new TypeReference<TreeNodeInputBean>() {
        });


        switch (inputBean.getNodeType()) {
            case INDUSTRY:
                resultList = uploadFilesAndAddFileNodesToIndustry(tenantId, creator, bucketName,
                        inputBean.getInstanceRid(), isOverride, metadata, uploadingFiles);
                break;
            case INSTANCE:
                resultList = uploadFilesAndAddFileNodesToInstance(tenantId, creator, bucketName, inputBean,
                        isOverride, metadata, uploadingFiles);
                break;
            default:
                String msg = "file can only be uploaded to industry or instance";
                log.error(msg);
                throw new InputNotEnoughException(msg);
        }
        return resultList;
    }

    /**
     * 批量上传文件到Minio并在行业分类下创建文件节点
     *
     * @param industryRid
     * @param metadata
     * @return
     * @throws EntityNotFoundException
     */
    public List<Map<String, Object>> uploadFilesAndAddFileNodesToIndustry(String tenantId, String creator,
                                                                          String bucketName, String industryRid,
                                                                          Boolean isOverride, String metadata,
                                                                          MultipartFile[] uploadingFiles) throws EntityNotFoundException, IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            modelCore.setCimDataSpace(cds);
            IndustryTypes industryTypes = modelCore.getIndustryTypes();
            IndustryType industryType = industryTypes.getIndustryType(industryRid);
            if (industryType == null) {
                String msg = String.format(INDUSTRY_TYPE_OF_S_NOT_FOUND, industryRid);
                log.error(msg);
                throw new EntityNotFoundException(msg);
            }
            //处理文件元数据
            Map<String, Map<String, String>> fileMetadata = new HashMap<>();
            String minioPrefix = (new StringBuffer())
                    .append(industryRid.replace("#", "C").replace(":", "P"))
                    .append(industryType.getIndustryTypeName().replaceAll("\\s+", ""))
                    .toString();
            fileMetadataParser(fileMetadata, industryType, metadata, minioPrefix, bucketName);
            // List<FileMetadataUploadBean> metdataBeans = objectMapper.readValue(metadata, new
            // TypeReference<List<FileMetadataUploadBean>>() {
            // });
            // for (FileMetadataUploadBean bean : metdataBeans) {
            //     try {
            //         if (StringUtils.isNotBlank(bean.getSrcFileName()) && StringUtils.isNotBlank(bean
            // .getFileDataName()) && StringUtils.isNotBlank(bean.getFileDataId())) {
            //             Map<String, String> tempMap = new HashMap<>();
            //             tempMap.put(BaseFileInfoKeys.SRC_FILE_NAME, bean.getSrcFileName());
            //             tempMap.put(BaseFileInfoKeys.FILE_DATA_ID, bean.getFileDataId());
            //             tempMap.put(BaseFileInfoKeys.FILE_DATA_NAME, bean.getFileDataName());
            //             tempMap.put(BaseFileInfoKeys.BUCKET_NAME, bucketName);
            //
            //             String srcFileName = bean.getSrcFileName();
            //             String fileExt = srcFileName.substring(srcFileName.lastIndexOf(".") + 1);
            //             if (StringUtils.isNotBlank(fileExt)) {
            //                 fileExt = fileExt.toUpperCase();
            //             }
            //             tempMap.put(BaseFileInfoKeys.FILE_CONTENT_TYPE, fileExt);
            //
            //             String fileType = CimConstants.FILE_TYPES_MAPPING.get(fileExt);
            //             tempMap.put(BaseFileInfoKeys.FILE_TYPE, fileType);
            //
            //             String mon = getMinioObjectName(industryType.getIndustryTypeRID(), industryType
            // .getIndustryTypeName(), srcFileName);
            //             tempMap.put(BaseFileInfoKeys.MINIO_OBJECT_NAME, mon);
            //
            //             tempMap.put(BaseFileInfoKeys.INDUSTRY_NAME, industryType.getIndustryTypeDesc());
            //
            //             fileMetadata.put(tempMap.get(BaseFileInfoKeys.SRC_FILE_NAME), tempMap);
            //         } else {
            //             log.error("{}, {}, {} are mandatary", BaseFileInfoKeys.SRC_FILE_NAME, BaseFileInfoKeys
            // .FILE_DATA_ID, BaseFileInfoKeys.FILE_DATA_NAME);
            //         }
            //     } catch (Exception e) {
            //         log.error("bean metadata input error: beanStr={}", objectMapper.writeValueAsString(bean));
            //         e.printStackTrace();
            //     }
            // }

            log.info("formal file metadata: {}", fileMetadata);
            if (fileMetadata.size() <= 0) {
                log.error("no file metadata is provided");
                return result;
            }

            Set<String> minioObjectNames = getAllRelatedMinioObjectNamesByIndustry(cds, industryRid);
            log.info("related minio object names: {}", objectMapper.writeValueAsString(minioObjectNames));
            // InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(BaseFileInfoKeys.BaseFileObjectTypeName);
            // if (infoObjectDef == null) {
            //     String msg = String.format("info object type of %s not found", BaseFileInfoKeys
            // .BaseFileObjectTypeName);
            //     log.error(msg);
            //     throw new EntityNotFoundException(msg);
            // }
            // for (MultipartFile file : uploadingFiles) {
            //     String orgFileName = file.getOriginalFilename();
            //     Map<String, Object> singleResult = new HashMap<>();
            //     singleResult.put(BaseFileInfoKeys.SRC_FILE_NAME, orgFileName);
            //     singleResult.put(BaseFileInfoKeys.RESULT_STATUS, false);
            //     result.add(singleResult);
            //     if (fileMetadata.containsKey(orgFileName)) {
            //         Map<String, String> singleMetadata = fileMetadata.get(orgFileName);
            //         String mon = singleMetadata.get(BaseFileInfoKeys.MINIO_OBJECT_NAME);
            //         try {
            //             String instanceRid;
            //             //创建或者更新文件节点元数据
            //             if (minioObjectNames.contains(mon)) {
            //                 if (isOverride) {
            //                     instanceRid = updateFileNode(cds, creator, industryType, fileMetadata.get
            // (orgFileName));
            //                 } else {
            //                     log.warn("{} is already uploaded and isOverride={}", mon, isOverride);
            //                     continue;
            //                 }
            //             } else {
            //                 instanceRid = createFileNode(industryType, infoObjectDef, creator, singleMetadata);
            //             }
            //             //上传文件到Minio
            //             minioService.uploader(bucketName, mon, file.getInputStream(), isOverride);
            //             //上传到BIMFace
            //             if (CimConstants.BIM_FILE_TYPES.contains(singleMetadata.get(BaseFileInfoKeys
            // .FILE_CONTENT_TYPE))) {
            //                 log.info("bim file should be uploaded and translated: fileContentType={}",
            // singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE));
            //                 bimFaceService.uploadTask(mon, bucketName, CimConstants.defauleSpaceName, instanceRid);
            //             } else {
            //                 log.info("not bim file, should not be uploaded and translated: fileContentType={}",
            // singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE));
            //             }
            //             singleResult.put(BaseFileInfoKeys.RESULT_STATUS, true);
            //             singleResult.put(CimConstants.INSTANCE_RID, instanceRid);
            //             singleResult.put(BaseFileInfoKeys.MINIO_OBJECT_NAME, mon);
            //         } catch (Exception e) {
            //             e.printStackTrace();
            //         }
            //     } else {
            //         log.error("no metadata is linked with {}", orgFileName);
            //     }
            // }
            result = filesUpload(uploadingFiles, isOverride, bucketName, creator, cds, modelCore, industryType,
                    fileMetadata, minioObjectNames, null);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return result;
    }

    /**
     * 批量上传文件到Minio并在实例下创建文件节点
     *
     * @param tenantId
     * @param creator
     * @param bucketName
     * @param inputBean
     * @param isOverride
     * @param metadata
     * @param uploadingFiles
     * @return
     * @throws EntityNotFoundException
     * @throws IOException
     * @throws CimDataEngineRuntimeException
     */
    public List<Map<String, Object>> uploadFilesAndAddFileNodesToInstance(String tenantId, String creator,
                                                                          String bucketName,
                                                                          TreeNodeInputBean inputBean,
                                                                          Boolean isOverride, String metadata,
                                                                          MultipartFile[] uploadingFiles) throws EntityNotFoundException, IOException, CimDataEngineRuntimeException {
        String industryRid = inputBean.getParentIndustryRid();
        String instanceRid = inputBean.getInstanceRid();
        List<Map<String, Object>> result = new ArrayList<>();
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            modelCore.setCimDataSpace(cds);
            IndustryTypes industryTypes = modelCore.getIndustryTypes();
            IndustryType industryType = industryTypes.getIndustryType(industryRid);
            if (industryType == null) {
                String msg = String.format(INDUSTRY_TYPE_OF_S_NOT_FOUND, industryRid);
                log.error(msg);
                throw new EntityNotFoundException(msg);
            }

            Fact instanceFact = cds.getFactById(instanceRid);
            if (instanceFact == null) {
                String msg = String.format("fact of %s not found", instanceRid);
                log.error(msg);
                throw new EntityNotFoundException(msg);
            }

            Relation relation = ContentService.industryAndInstanceRelation(cds, inputBean);
            if (relation == null) {
                String msg = String.format("relation between industry={} and instance={} not found", industryRid,
                        instanceRid);
                log.error(msg);
                throw new EntityNotFoundException(msg);
            }

            //处理文件元数据
            Map<String, Map<String, String>> fileMetadata = new HashMap<>();
            String minioNamePrefix = (new StringBuffer()).append(instanceRid.replace("#", "C")
                    .replace(":", "P"))
                    .append("__").append(industryRid.replace("#", "C").replace(":", "P"))
                    .toString();

            fileMetadataParser(fileMetadata, industryType, metadata, minioNamePrefix, bucketName);
            log.info("formal file metadata: {}", fileMetadata);
            if (fileMetadata.size() <= 0) {
                log.error("no file metadata is provided");
                return result;
            }

            Set<String> minioObjectNames = getAllRelatedMinioObjectNamesByInstance(cds, industryRid, instanceRid);
            log.info("related minio object names: {}", objectMapper.writeValueAsString(minioObjectNames));
            // InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(BaseFileInfoKeys.BaseFileObjectTypeName);
            // if (infoObjectDef == null) {
            //     String msg = String.format("info object type of %s not found", BaseFileInfoKeys
            // .BaseFileObjectTypeName);
            //     log.error(msg);
            //     throw new EntityNotFoundException(msg);
            // }
            // for (MultipartFile file : uploadingFiles) {
            //     String orgFileName = file.getOriginalFilename();
            //     Map<String, Object> singleResult = new HashMap<>();
            //     singleResult.put(BaseFileInfoKeys.SRC_FILE_NAME, orgFileName);
            //     singleResult.put(BaseFileInfoKeys.RESULT_STATUS, false);
            //     result.add(singleResult);
            //     if (fileMetadata.containsKey(orgFileName)) {
            //         Map<String, String> singleMetadata = fileMetadata.get(orgFileName);
            //         String mon = singleMetadata.get(BaseFileInfoKeys.MINIO_OBJECT_NAME);
            //         try {
            //             String fileFactRid;
            //             //创建或者更新文件节点元数据
            //             if (minioObjectNames.contains(mon)) {
            //                 if (isOverride) {
            //                     fileFactRid = updateFileNode(cds, creator, industryType, fileMetadata.get
            // (orgFileName));
            //                 } else {
            //                     log.warn("{} is already uploaded and isOverride={}", mon, isOverride);
            //                     continue;
            //                 }
            //             } else {
            //                 fileFactRid = createFileNode(industryType, infoObjectDef, creator, singleMetadata);
            //             }
            //             //上传文件到Minio
            //             minioService.uploader(bucketName, mon, file.getInputStream(), isOverride);
            //             //上传到BIMFace
            //             if (CimConstants.BIM_FILE_TYPES.contains(singleMetadata.get(BaseFileInfoKeys
            // .FILE_CONTENT_TYPE))) {
            //                 log.info("bim file should be uploaded and translated: fileContentType={}",
            // singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE));
            //                 bimFaceService.uploadTask(mon, bucketName, CimConstants.defauleSpaceName, fileFactRid);
            //             } else {
            //                 log.info("not bim file, should not be uploaded and translated: fileContentType={}",
            // singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE));
            //             }
            //             singleResult.put(BaseFileInfoKeys.RESULT_STATUS, true);
            //             singleResult.put(CimConstants.INSTANCE_RID, fileFactRid);
            //             singleResult.put(BaseFileInfoKeys.MINIO_OBJECT_NAME, mon);
            //         } catch (Exception e) {
            //             e.printStackTrace();
            //         }
            //     } else {
            //         log.error("no metadata is linked with {}", orgFileName);
            //     }
            // }
            result = filesUpload(uploadingFiles, isOverride, bucketName, creator, cds, modelCore, industryType,
                    fileMetadata, minioObjectNames, instanceFact);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return result;
    }

    /**
     * 文件元数据处理
     *
     * @param fileMetadata
     * @param industryType
     * @param metadata
     * @param minioNamePrefix
     * @param bucketName
     * @throws IOException
     */
    private void fileMetadataParser(Map<String, Map<String, String>> fileMetadata, IndustryType industryType,
                                    String metadata, String minioNamePrefix, String bucketName) throws IOException {
        List<FileMetadataUploadBean> metdataBeans = objectMapper.readValue(metadata,
                new TypeReference<List<FileMetadataUploadBean>>() {
                });
        for (FileMetadataUploadBean bean : metdataBeans) {
            try {
                if (StringUtils.isNotBlank(bean.getSrcFileName()) && StringUtils.isNotBlank(bean.getFileDataName()) && StringUtils.isNotBlank(bean.getFileDataId())) {
                    Map<String, String> tempMap = new HashMap<>();
                    tempMap.put(BaseFileInfoKeys.SRC_FILE_NAME, bean.getSrcFileName());
                    tempMap.put(BaseFileInfoKeys.FILE_DATA_ID, bean.getFileDataId());
                    tempMap.put(BaseFileInfoKeys.FILE_DATA_NAME, bean.getFileDataName());
                    tempMap.put(BaseFileInfoKeys.BUCKET_NAME, bucketName);

                    String srcFileName = bean.getSrcFileName();
                    String fileExt = srcFileName.substring(srcFileName.lastIndexOf(".") + 1);
                    if (StringUtils.isNotBlank(fileExt)) {
                        fileExt = fileExt.toUpperCase();
                    }
                    tempMap.put(BaseFileInfoKeys.FILE_CONTENT_TYPE, fileExt);

                    String fileType = CimConstants.FILE_TYPES_MAPPING.get(fileExt);
                    tempMap.put(BaseFileInfoKeys.FILE_TYPE, fileType);

                    String mon = getMinioObjectName(minioNamePrefix, srcFileName);
                    tempMap.put(BaseFileInfoKeys.MINIO_OBJECT_NAME, mon);

                    tempMap.put(BaseFileInfoKeys.INDUSTRY_NAME, industryType.getIndustryTypeDesc());

                    fileMetadata.put(tempMap.get(BaseFileInfoKeys.SRC_FILE_NAME), tempMap);
                } else {
                    log.error("{}, {}, {} are mandatary", BaseFileInfoKeys.SRC_FILE_NAME,
                            BaseFileInfoKeys.FILE_DATA_ID, BaseFileInfoKeys.FILE_DATA_NAME);
                }
            } catch (Exception e) {
                log.error("bean metadata input error: metadata={}", metadata);
                e.printStackTrace();
            }
        }
    }

    private Map<String, Map<String, Object>> fileMetadataParser(String metadata, String parentNodeName,
                                                                String minioNamePrefix, String bucketName,
                                                                String userId) throws IOException {
        List<FileMetadataUploadBean> metdataBeans = objectMapper.readValue(metadata,
                new TypeReference<List<FileMetadataUploadBean>>() {
                });
        Map<String, Map<String, Object>> fileMetadata = new HashMap<>();
        for (FileMetadataUploadBean bean : metdataBeans) {
            try {
                if (StringUtils.isNotBlank(bean.getSrcFileName()) && StringUtils.isNotBlank(bean.getFileDataName()) && StringUtils.isNotBlank(bean.getFileDataId())) {
                    Map<String, Object> tempMap = new HashMap<>();
                    tempMap.put(BaseFileInfoKeys.FILE_DATA_ID, bean.getFileDataId());
                    if (StringUtils.isNotBlank(bean.getFileDataId())) {
                        tempMap.put(BaseDataSetKeys.ID, bean.getFileDataId());
                    }
                    tempMap.put(BaseFileInfoKeys.FILE_DATA_NAME, bean.getFileDataName());
                    tempMap.put(BaseDataSetKeys.NAME, bean.getFileDataName());
                    tempMap.put(BaseFileInfoKeys.SRC_FILE_NAME, bean.getSrcFileName());
                    tempMap.put(BaseFileInfoKeys.BUCKET_NAME, bucketName);
                    tempMap.put(BaseFileInfoKeys.CREATOR, userId);
                    tempMap.put(BaseFileInfoKeys.UPDATOR, userId);

                    String srcFileName = bean.getSrcFileName();
                    String fileExt = srcFileName.substring(srcFileName.lastIndexOf(".") + 1);
                    if (StringUtils.isNotBlank(fileExt)) {
                        fileExt = fileExt.toUpperCase();
                    }
                    tempMap.put(BaseFileInfoKeys.FILE_CONTENT_TYPE, fileExt);

                    String fileType = CimConstants.FILE_TYPES_MAPPING.get(fileExt);
                    tempMap.put(BaseFileInfoKeys.FILE_TYPE, fileType);

                    String mon = getMinioObjectName(minioNamePrefix, srcFileName);
                    tempMap.put(BaseFileInfoKeys.MINIO_OBJECT_NAME, mon);

                    tempMap.put(BaseFileInfoKeys.INDUSTRY_NAME, parentNodeName);

                    fileMetadata.put(tempMap.get(BaseFileInfoKeys.SRC_FILE_NAME).toString(), tempMap);
                } else {
                    log.error("{}, {}, {} are mandatary", BaseFileInfoKeys.SRC_FILE_NAME,
                            BaseFileInfoKeys.FILE_DATA_ID, BaseFileInfoKeys.FILE_DATA_NAME);
                }
            } catch (Exception e) {
                log.error("bean metadata input error: metadata={}", metadata);
                e.printStackTrace();
            }
        }
        return fileMetadata;
    }

    private List<Map<String, Object>> filesUpload(MultipartFile[] uploadingFiles, Boolean isOverride,
                                                  String bucketName, String creator, CimDataSpace cds,
                                                  CIMModelCore modelCore, IndustryType industryType, Map<String,
            Map<String, String>> fileMetadata, Set<String> minioObjectNames, Fact instanceFact) throws EntityNotFoundException {//NOSONAR
        List<Map<String, Object>> result = new ArrayList<>();
        InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(BaseFileInfoKeys.BaseFileObjectTypeName);
        if (infoObjectDef == null) {
            String msg = String.format("info object type of %s not found", BaseFileInfoKeys.BaseFileObjectTypeName);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
        for (MultipartFile file : uploadingFiles) {
            String orgFileName = file.getOriginalFilename();
            long size = file.getSize();
            Map<String, Object> singleResult = new HashMap<>();
            singleResult.put(BaseFileInfoKeys.SRC_FILE_NAME, orgFileName);
            singleResult.put(BaseFileInfoKeys.RESULT_STATUS, false);
            result.add(singleResult);
            if (fileMetadata.containsKey(orgFileName)) {
                Map<String, String> singleMetadata = fileMetadata.get(orgFileName);
                String mon = singleMetadata.get(BaseFileInfoKeys.MINIO_OBJECT_NAME);
                singleMetadata.put(BaseFileInfoKeys.FILE_SIZE, size + "");
                String taskId = null;
                try {
                    String instanceRid;
                    //创建或者更新文件节点元数据
                    if (minioObjectNames.contains(mon)) {
                        if (isOverride) {
                            instanceRid = updateFileNode(cds, creator, industryType, fileMetadata.get(orgFileName));
                        } else {
                            log.warn("{} is already uploaded and isOverride={}", mon, isOverride);
                            continue;
                        }
                    } else {
                        instanceRid = createFileNode(cds, industryType, infoObjectDef, instanceFact, creator,
                                singleMetadata);
                    }
                    //上传文件到Minio
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("fileName", orgFileName);
                    map.put("transferType", "upload");
                    map.put("fileSize", file.getSize() + "");
                    String json = objectMapper.writeValueAsString(map);
                    taskId = sendStartTaskMq(json) + "";
                    minioService.uploader(bucketName, mon, file.getInputStream(), isOverride);
                    if (StringUtils.isNotBlank(taskId)) {
                        sendTaskStatusMq(taskId, JobStatusEnum.ENDED.getCode());
                    }
                    //上传到BIMFace
                    if (CimConstants.BIM_FILE_TYPES.contains(singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE))) {
                        log.info("bim file should be uploaded and translated: fileContentType={}",
                                singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE));
                        bimFaceService.uploadTask(mon, bucketName, CimConstants.defauleSpaceName, instanceRid);
                    } else {
                        log.info("not bim file, should not be uploaded and translated: fileContentType={}",
                                singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE));
                    }
                    singleResult.put(BaseFileInfoKeys.RESULT_STATUS, true);
                    singleResult.put(CimConstants.INSTANCE_RID, instanceRid);
                    singleResult.put(BaseFileInfoKeys.MINIO_OBJECT_NAME, mon);
                } catch (Exception e) {
                    sendTaskStatusMq(taskId, JobStatusEnum.FAIL.getCode());
                    e.printStackTrace();
                }
            } else {
                log.error("no metadata is linked with {}", orgFileName);
            }
        }

        return result;
    }

    private List<Map<String, Object>> uploadFileBatch(MultipartFile[] uploadingFiles, Boolean isOverride,
                                                      String bucketName, CIMModelCore modelCore, Map<String,
            Map<String, Object>> fileMetadata) {//NOSONAR
        List<Map<String, Object>> result = new ArrayList<>();
        for (MultipartFile file : uploadingFiles) {
            String orgFileName = file.getOriginalFilename();
            if (fileMetadata.containsKey(orgFileName)) {
                Map<String, Object> singleMetadata = fileMetadata.get(orgFileName);
                //上传文件到Minio
                Map<String, Object> singleResult = uploadSingleFile(file, singleMetadata, isOverride, bucketName,
                        modelCore);
                //若文件上传成功，且为BIM类型，则上传到BIMFace
                if (singleResult.containsKey(CimConstants.INSTANCE_RID) && StringUtils.isNotBlank(singleResult.get(CimConstants.INSTANCE_RID).toString())) {
                    if (CimConstants.BIM_FILE_TYPES.contains(singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE))) {
                        log.info("bim file should be uploaded and translated: fileContentType={}",
                                singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE));
                        bimFaceService.uploadTask(singleMetadata.get(BaseFileInfoKeys.MINIO_OBJECT_NAME).toString(),
                                bucketName, CimConstants.defauleSpaceName,
                                singleResult.get(CimConstants.INSTANCE_RID).toString());
                    } else {
                        log.info("not bim file, should not be uploaded and translated: fileContentType={}",
                                singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE));
                    }
                } else {
                    log.error("file upload failed");
                }
                result.add(singleResult);
            } else {
                log.error("no metadata is linked with {}", orgFileName);
            }
        }
        return result;
    }

    public Map<String, Object> uploadSingleFile(MultipartFile file, Map<String, Object> metadata, Boolean isOverride,
                                                String bucketName, CIMModelCore modelCore) {

        String orgFileName = file.getOriginalFilename();
        long size = file.getSize();
        Map<String, Object> singleResult = new HashMap<>();
        singleResult.put(BaseFileInfoKeys.SRC_FILE_NAME, orgFileName);
        singleResult.put(BaseFileInfoKeys.RESULT_STATUS, false);

        String mon = metadata.get(BaseFileInfoKeys.MINIO_OBJECT_NAME).toString();
        metadata.put(BaseFileInfoKeys.FILE_SIZE, size);
        String taskId = null;
        try {
            //上传文件到Minio
            HashMap<String, String> map = new HashMap<>();
            map.put("fileName", orgFileName);
            map.put("transferType", "upload");
            map.put("fileSize", file.getSize() + "");
            String json = objectMapper.writeValueAsString(map);
            taskId = sendStartTaskMq(json) + "";
            minioService.uploader(bucketName, mon, file.getInputStream(), isOverride);
            //创建或者更新文件节点元数据
            InfoObject fileObject = addOrUpdateFileInstanceByMinioName(modelCore, mon, isOverride, metadata);
            Assert.notNull(fileObject, "add or update file instance failed");
            if (StringUtils.isNotBlank(taskId)) {
                sendTaskStatusMq(taskId, JobStatusEnum.ENDED.getCode());
            }
            singleResult.put(BaseFileInfoKeys.RESULT_STATUS, true);
            singleResult.put(CimConstants.INSTANCE_RID, fileObject.getObjectInstanceRID());
            singleResult.put(BaseFileInfoKeys.MINIO_OBJECT_NAME, mon);
        } catch (Exception e) {
            sendTaskStatusMq(taskId, JobStatusEnum.FAIL.getCode());
            e.printStackTrace();
        }

        return singleResult;
    }

    public InfoObject addOrUpdateFileInstanceByMinioName(CIMModelCore modelCore, String minioStorageName,
                                                         Boolean isOverride, Map<String, Object> values) {
        InfoObjectDef objectDef = modelCore.getInfoObjectDef(BaseFileInfoKeys.BaseFileObjectTypeName);
        Assert.notNull(objectDef, "base file import object not found");

        FilteringItem filteringItem = new EqualFilteringItem(BaseFileInfoKeys.MINIO_OBJECT_NAME, minioStorageName);
        ExploreParameters ep = new ExploreParameters();
        ep.setDefaultFilteringItem(filteringItem);

        List<InfoObject> infoObjectList = objectDef.getObjects(ep).getInfoObjects();
        InfoObject fileObject = null;
        try {
            if (infoObjectList != null && infoObjectList.size() > 0) {
                if (isOverride) {
                    fileObject = infoObjectList.get(0);
                    fileObject.addOrUpdateObjectProperty(BaseFileInfoKeys.UPDATOR,
                            values.get(BaseFileInfoKeys.UPDATOR));
                    fileObject.addOrUpdateObjectProperty(BaseFileInfoKeys.FILE_SIZE,
                            values.get(BaseFileInfoKeys.FILE_SIZE));
                    fileObject.addOrUpdateObjectProperty(BaseFileInfoKeys.UPDATE_TIME, new Date());
                } else {
                    log.warn("{} is already uploaded and isOverride={}", minioStorageName, isOverride);
                }
            } else {
                InfoObjectValue objectValue = new InfoObjectValue();
                objectValue.setBaseDatasetPropertiesValue(values);
                fileObject = objectDef.newObject(objectValue, false);
            }
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } catch (DataServiceUserException e) {
            e.printStackTrace();
        }
        return fileObject;
    }

    public List<Map<String, Object>> uploadFileAndAddNode(String tenantId, String userId, String treeDefId,
                                                          String bucketName, Boolean isOverride, String parentNodeStr
            , String metadataStr, MultipartFile[] uploadingFiles) throws IOException {
        List<Map<String, Object>> rs;
        NodeInfoBean parentNode = objectMapper.readValue(parentNodeStr,
                new TypeReference<NodeInfoBean>() {
                });
        log.info("file upload parent node info: {}", parentNode);
        Assert.hasText(parentNode.getID(), "parent node is mandatory");
        Map<String, Map<String, Object>> fileMetaDatas = fileMetadataParser(metadataStr, parentNode.getNAME(),
                parentNode.getID(), bucketName, userId);
        log.info("file upload file metadat parser result: {}", fileMetaDatas);
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            modelCore.setCimDataSpace(cds);
            rs = uploadFileBatch(uploadingFiles, isOverride, bucketName, modelCore, fileMetaDatas);
            List<String> childNodeRids = new ArrayList<>();
            for (Map<String, Object> map : rs) {
                if (map.get(CimConstants.INSTANCE_RID) != null && StringUtils.isNotBlank(map.get(CimConstants.INSTANCE_RID).toString())) {
                    childNodeRids.add(map.get(CimConstants.INSTANCE_RID).toString());
                }
            }
            if (childNodeRids.size() > 0) {
                treeNodeService.addFileNodeByRidWithMetadata(cds, tenantId, userId, treeDefId, parentNode,
                        childNodeRids);
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return rs;
    }


    /**
     * 查询与指定分类关联的所有文件名称
     *
     * @param cds
     * @param industryRid
     * @return
     */
    private static Set<String> getAllRelatedMinioObjectNamesByIndustry(CimDataSpace cds, String industryRid) {//NOSONAR
        Set<String> minioObjectNames = new HashSet<>();
        try {
            Dimension currentIndustryType = cds.getDimensionById(industryRid);
            List<Relation> relationList = null;
            relationList =
                    currentIndustryType.getAllSpecifiedRelations(BusinessLogicConstant.INDUSTRYTYPE_INFOOBJECT_LINK_RELATION_TYPE_NAME, RelationDirection.FROM);
            if (relationList != null) {
                log.info("relation size: {}", relationList.size());
                for (Relation currentRelation : relationList) {
                    Relationable targetRelationable = currentRelation.getToRelationable();
                    if (targetRelationable instanceof Fact) {
                        String factType = ((Fact) targetRelationable).getType();
                        if (factType.equals(BaseFileInfoKeys.BaseFileObjectTypeName)) {
                            if (targetRelationable.hasProperty(BaseFileInfoKeys.MINIO_OBJECT_NAME)) {
                                minioObjectNames.add(targetRelationable.getProperty(BaseFileInfoKeys.MINIO_OBJECT_NAME).getPropertyValue().toString());
                            }
                        }
                    } else {
                        log.info("not instance of Fact");
                    }
                }
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }

        return minioObjectNames;
    }

    /**
     * 查询关联到指定分类下的实例关联的所有文件名称
     *
     * @param cds
     * @param industryRid
     * @param instanceRid
     * @return
     * @throws EntityNotFoundException
     */
    private static Set<String> getAllRelatedMinioObjectNamesByInstance(CimDataSpace cds, String industryRid,
                                                                       String instanceRid) throws EntityNotFoundException {//NOSONAR
        Set<String> minioObjectNames = new HashSet<>();
        try {
            Fact fact = cds.getFactById(instanceRid);
            if (fact == null) {
                log.error("fact of intstanceRid={} not found", instanceRid);
                throw new EntityNotFoundException("instance not found");
            }
            List<Relation> relationList = null;
            ExploreParameters ep = new ExploreParameters();
            ep.addRelatedRelationType(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_TYPE_NAME);
            EqualFilteringItem filteringItem =
                    new EqualFilteringItem(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_INDUSTRY_RID,
                            industryRid);
            ep.setDefaultFilteringItem(filteringItem);
            relationList = fact.getSpecifiedRelations(ep, RelationDirection.FROM);
            if (relationList != null) {
                log.info("relation size: {}", relationList.size());
                for (Relation currentRelation : relationList) {
                    Relationable targetRelationable = currentRelation.getToRelationable();
                    if (targetRelationable instanceof Fact) {
                        String factType = ((Fact) targetRelationable).getType();
                        if (factType.equals(BaseFileInfoKeys.BaseFileObjectTypeName)) {
                            if (targetRelationable.hasProperty(BaseFileInfoKeys.MINIO_OBJECT_NAME)) {
                                minioObjectNames.add(targetRelationable.getProperty(BaseFileInfoKeys.MINIO_OBJECT_NAME).getPropertyValue().toString());
                            }
                        }
                    } else {
                        log.info("not instance of Fact");
                    }
                }
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }

        return minioObjectNames;
    }

    /**
     * 获取文件在minio的名称：industryRid__industryName_sourceFileName
     *
     * @param srcFileName
     * @return
     */
    private static String getMinioObjectName(String rid, String name, String srcFileName) {
        String minioObjectName;
        rid = rid.replace("#", "C").replace(":", "P");
        name = name.replaceAll("\\s+", "");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(rid).append("__").append(name).append("__").append(srcFileName);
        minioObjectName = stringBuffer.toString();
        return minioObjectName;
    }

    private static String getMinioObjectName(String prefix, String srcFileName) {
        String minioObjectName;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(prefix).append("__").append(srcFileName);
        minioObjectName = stringBuffer.toString();
        return minioObjectName;
    }

    /**
     * 挂载实例到分类
     *
     * @param tenantId
     * @param creatorId
     * @param industryRid
     * @param objectInstances
     * @return
     * @throws EntityNotFoundException
     */
    public List<LinkObjectAndInstanceOutputBean> updateLinkedObjectsAndInstances(String tenantId, String creatorId,
                                                                                 String industryRid,
                                                                                 List<LinkObjectAndInstanceInputBean> objectInstances) throws EntityNotFoundException {//NOSONAR
        List<LinkObjectAndInstanceOutputBean> outputBeans = new ArrayList<>();

        if (objectInstances == null || objectInstances.size() < 1) {
            log.error("input error");
            return outputBeans;
        }
        CimDataSpace cds = null;
        try {
            Map<String, LinkObjectAndInstanceOutputBean> outputBeanMap = new HashMap<>();
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            IndustryTypes industryTypes = modelCore.getIndustryTypes();
            IndustryType industryType = industryTypes.getIndustryType(industryRid);
            if (industryType != null) {
                Map<String, String> objectTypes = new HashMap<>();
                Map<String, String> instanceAndObject = new HashMap<>();
                for (LinkObjectAndInstanceInputBean inputBean : objectInstances) {
                    objectTypes.put(inputBean.getObjectTypeId(), inputBean.getObjectTypeName());
                    List<String> instenceRids = inputBean.getInstanceRids();
                    if (instenceRids != null && instenceRids.size() > 0) {
                        for (String rid : instenceRids) {
                            instanceAndObject.put(rid, inputBean.getObjectTypeId());
                        }
                    }
                }
                // update linked object types
                updateLinkesObjects(cds, modelCore, industryType, objectTypes, outputBeanMap);
                // update linked instances
                updateLinkedInstances(tenantId, creatorId, cds, modelCore, industryType, instanceAndObject,
                        outputBeanMap);
                if (outputBeanMap.size() > 0) {
                    for (Map.Entry<String, LinkObjectAndInstanceOutputBean> entry : outputBeanMap.entrySet()) {
                        outputBeans.add(entry.getValue());
                    }
                }
            } else {
                log.error("industry type of rid={} not found, or not belong to this tenant of tenantId={}",
                        industryRid, tenantId);
                throw new EntityNotFoundException("industry of rid=" + industryRid + " not found");
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return outputBeans;
    }

    /**
     * 更新与行业分类关联的对象类型
     *
     * @param cds
     * @param industryType
     * @param objectTypes
     * @param outputBeanMap
     */
    private static void updateLinkesObjects(CimDataSpace cds, CIMModelCore modelCore, IndustryType industryType,
                                            Map<String, String> objectTypes, Map<String,
            LinkObjectAndInstanceOutputBean> outputBeanMap) {//NOSONAR
        List<InfoObjectDef> objectDefList = industryType.getLinkedInfoObjectDefs();
        Set<String> tmpObjectDefs = new HashSet<>();
        //unlink object types
        if (objectDefList != null && objectDefList.size() > 0) {
            for (InfoObjectDef objectDef : objectDefList) {
                if (objectTypes.containsKey(objectDef.getObjectTypeName())) {
                    tmpObjectDefs.add(objectDef.getObjectTypeName());
                    LinkObjectAndInstanceOutputBean outputBean = new LinkObjectAndInstanceOutputBean();
                    outputBean.setObjectTypeId(objectDef.getObjectTypeName());
                    outputBean.setObjectTypeName(objectDef.getObjectTypeDesc());
                    outputBeanMap.put(objectDef.getObjectTypeName(), outputBean);
                    log.info("link object type of objectType={} with industry of indysrtyRid={} success",
                            objectDef.getObjectTypeName(), industryType.getIndustryTypeRID());
                } else {
                    try {
                        InfoObjectFeatures.unlinkInfoObjectTypeAttachedIndustryType(cds,
                                objectDef.getObjectTypeName(), industryType.getIndustryTypeRID());
                    } catch (CimDataEngineRuntimeException e) {
                        e.printStackTrace();
                    } catch (CimDataEngineInfoExploreException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //link object types
        for (Map.Entry<String, String> entry : objectTypes.entrySet()) {
            if (!tmpObjectDefs.contains(entry.getKey())) {
                LinkObjectAndInstanceOutputBean outputBean = new LinkObjectAndInstanceOutputBean();
                outputBean.setObjectTypeId(entry.getKey());
                outputBean.setObjectTypeName(entry.getValue());
                try {
                    InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(entry.getKey());
                    if (infoObjectDef != null) {
                        if (infoObjectDef.linkIndustryType(industryType.getIndustryTypeRID())) {
                            log.info("link object type of objectType={} with industry of industryRid={} success",
                                    entry.getKey(), industryType.getIndustryTypeRID());
                        } else {
                            String msg = String.format("link object type of objectType=%s with industry of " +
                                    "industryRid=%s failed", entry.getKey(), industryType.getIndustryTypeRID());
                            outputBean.setMessage(msg);
                            outputBean.setSuccess(false);
                            log.error(msg);
                        }
                    } else {
                        log.error("object type of {} not found or not belong to this tenant", entry.getKey());
                    }
                } catch (DataServiceModelRuntimeException e) {
                    String msg = String.format("link object type of objectType=%s with industry of industryRid=%s " +
                            "failed", entry.getKey(), industryType.getIndustryTypeRID());
                    outputBean.setMessage(msg);
                    outputBean.setSuccess(false);
                    log.info(msg, e);
                }
                outputBeanMap.put(entry.getKey(), outputBean);
            }
        }

    }

    /**
     * 更新与行业分类关联的实例
     *
     * @param industryType
     * @param instanceAndObject
     * @param outputBeanMap
     */
    private void updateLinkedInstances(String tenantId, String creatorId, CimDataSpace cds, CIMModelCore modelCore,
                                       IndustryType industryType, Map<String, String> instanceAndObject, Map<String,
            LinkObjectAndInstanceOutputBean> outputBeanMap) {//NOSONAR
        //unlik instance
        Map<String, String> tmpInstanceAndObject = new HashMap<>();
        List<InfoObject> childInfoObjects = industryType.getChildInfoObjects();
        if (childInfoObjects != null && childInfoObjects.size() > 0) {
            for (InfoObject instance : childInfoObjects) {
                if (instanceAndObject.containsKey(instance.getObjectInstanceRID())) {
                    log.info("link instanceRid={} of objectType={} with industry of indysrtyRid={} success",
                            instance.getObjectInstanceRID(), instance.getObjectTypeName(),
                            industryType.getIndustryTypeRID());
                    tmpInstanceAndObject.put(instance.getObjectInstanceRID(), instance.getObjectTypeName());
                } else {
                    try {
                        if (!instance.getObjectTypeName().equals(BaseFileInfoKeys.BaseFileObjectTypeName)) {
                            industryType.unlinkChildInfoObject(instance.getObjectInstanceRID());
                            log.info("unlink child instance of instanceRid={} with industry of industryRid={}",
                                    instance.getObjectInstanceRID(), industryType.getIndustryTypeRID());
                        }
                    } catch (DataServiceModelRuntimeException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //link instance
        for (Map.Entry<String, String> entry : instanceAndObject.entrySet()) {
            if (!tmpInstanceAndObject.containsKey(entry.getKey())) {
                List<String> linkFiledRids = new ArrayList<>();
                try {
                    industryType.linkChildInfoObject(entry.getKey());
                    if (entry.getValue().equals("projectV1") || entry.getValue().equals("项目")) {
                        log.info("添加标准目录：");
                        Set<String> ridSet = StandFoldersService.addStandFolders(cds, tenantId, creatorId, this);
                        if (ridSet != null && ridSet.size() > 0) {
                            for (String rid : ridSet) {
                                log.info("添加标准目录：link industry={} to instance={}, parentIndustryRid={}", rid,
                                        entry.getKey(), industryType.getIndustryTypeRID());
                                try {
                                    linkIndustryToInstance(cds, rid, entry.getKey(), industryType.getIndustryTypeRID());
                                } catch (CimDataEngineRuntimeException e) {
                                    e.printStackTrace();
                                }
                                // IndustryType type = modelCore.getIndustryType(rid);
                                // type.linkParentInfoObject(entry.getKey());
                            }
                        }
                    }
                    log.info("link instanceRid={} of objectType={} with industry of indysrtyRid={} success",
                            entry.getKey(), entry.getValue(), industryType.getIndustryTypeRID());
                } catch (DataServiceModelRuntimeException e) {
                    String msg = String.format("link instanceRid=%s of objectType=%s with industry of indysrtyRid=%s " +
                            "failed", entry.getKey(), entry.getValue(), industryType.getIndustryTypeRID());
                    log.info(msg, e);
                    linkFiledRids.add(entry.getKey());
                    e.printStackTrace();
                }
                if (linkFiledRids.size() > 0) {
                    LinkObjectAndInstanceOutputBean outputBean = outputBeanMap.get(entry.getValue());
                    if (outputBean != null) {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < linkFiledRids.size(); i++) {
                            if (i == 0) {
                                sb.append("[").append(linkFiledRids.get(i)).append(",");
                            } else if (i == linkFiledRids.size() - 1) {
                                sb.append(linkFiledRids.get(i)).append("]");
                            } else {
                                sb.append(linkFiledRids.get(i)).append(",");
                            }
                        }
                        String msg = String.format("failed to link rids of %s to industry of %s", sb.toString(),
                                industryType.getIndustryTypeRID());
                        log.error(msg, entry);
                        outputBean.setSuccess(false);
                        outputBean.setMessage(msg);
                    }
                }
            }
        }
    }

    /**
     * 同一分类下节点不可重名
     *
     * @param tenantId
     * @param industryRid
     * @param names
     * @return
     */
    public Map<String, Boolean> namesAvailable(String tenantId, String industryRid,
                                               TreeNodeInputBean.TreeNodeTypeEnum childNodeType, List<String> names) throws InputNotEnoughException {
        Map<String, Boolean> namesMap = new HashMap<>();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            IndustryType industryType;
            if (StringUtils.isNotBlank(industryRid)) {
                industryType = modelCore.getIndustryType(industryRid);
            } else if (childNodeType.equals(TreeNodeInputBean.TreeNodeTypeEnum.INDUSTRY)) {
                industryType = null;
            } else {
                String msg = String.format("industryRid=%s, childNodeType=%s", industryRid, childNodeType);
                log.error(msg);
                throw new InputNotEnoughException(msg);
            }

            Set<String> nameSet = getAllChildNodeNamesByType(industryType, cds, tenantId, childNodeType);
            if (names != null && names.size() > 0) {
                for (String nm : names) {
                    if (nameSet.contains(nm)) {
                        namesMap.put(nm, false);
                    } else {
                        namesMap.put(nm, true);
                    }
                }
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return namesMap;
    }

    /**
     * 获取指定行业分类下的指定类型的所有子节点名称
     *
     * @param industryType
     * @param cds
     * @param tenantId
     * @param childNodeType
     * @return
     */
    public static Set<String> getAllChildNodeNamesByType(IndustryType industryType, CimDataSpace cds, String tenantId
            , TreeNodeInputBean.TreeNodeTypeEnum childNodeType) {//NOSONAR
        Set<String> nameSet = new HashSet<>();
        switch (childNodeType) {
            case INDUSTRY:
                //root insudtries
                if (industryType == null) {
                    try {
                        Dimension logicRootDimension = IndustryTypeFeatures.getIndustryTypeLogicRootNode(cds);
                        List<Relation> rootTypeRelationsList =
                                logicRootDimension.getAllSpecifiedRelations(BusinessLogicConstant.IS_ROOT_INDUSTRY_TYPE_RELATION_TYPE_NAME, RelationDirection.FROM);
                        if (rootTypeRelationsList != null && rootTypeRelationsList.size() > 0) {
                            for (Relation currentRelation : rootTypeRelationsList) {
                                Relationable currentTypeDimension = currentRelation.getToRelationable();
                                boolean isInValidTenant = CommonOperationUtil.isTenantContainsData(tenantId,
                                        currentTypeDimension);
                                if (isInValidTenant && currentTypeDimension.hasProperty(IndustryTypeNodeKeys.INDUSTRY_NAME)) {
                                    nameSet.add(currentTypeDimension.getProperty(IndustryTypeNodeKeys.INDUSTRY_NAME).getPropertyValue().toString());
                                }
                            }
                        }
                    } catch (CimDataEngineDataMartException e) {
                        e.printStackTrace();
                    } catch (CimDataEngineRuntimeException e) {
                        e.printStackTrace();
                    } catch (CimDataEngineInfoExploreException e) {
                        e.printStackTrace();
                    }
                    // return nameSet;
                } else {
                    List<IndustryType> childIndustries = industryType.getChildrenIndustryTypes();
                    if (childIndustries != null) {
                        for (IndustryType type : childIndustries) {
                            nameSet.add(type.getIndustryTypeDesc());
                        }
                    }
                }
                break;
            case OBJECT:
                List<Fact> industryAndObjectMap = null;
                try {
                    industryAndObjectMap = IndustryTypeFeatures.getIndustryTypeAndInfoObjectTypeMapping(cds,
                            industryType.getIndustryTypeRID());
                    if (industryAndObjectMap != null && industryAndObjectMap.size() > 0) {
                        List<Object> objectIds = new ArrayList<>();
                        for (Fact fact : industryAndObjectMap) {
                            if (fact.hasProperty(INFO_OBJECT_TYPE_NAME)) {
                                objectIds.add(fact.getProperty(INFO_OBJECT_TYPE_NAME).getPropertyValue().toString());
                            }
                        }

                        FilteringItem filteringItem = new InValueFilteringItem(INFO_OBJECT_TYPE_NAME, objectIds);
                        ExploreParameters exploreParameters = new ExploreParameters();
                        exploreParameters.setDefaultFilteringItem(filteringItem);
                        exploreParameters.setType(BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME);

                        InformationExplorer ip = cds.getInformationExplorer();
                        List<Fact> objectStatus = ip.discoverFacts(exploreParameters);
                        if (objectStatus != null && objectStatus.size() > 0) {
                            for (Fact fact : objectStatus) {
                                if (CommonOperationUtil.isTenantContainsData(tenantId, fact) && fact.hasProperty(BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME_DESC)) {
                                    nameSet.add(fact.getProperty(BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME_DESC).getPropertyValue().toString());
                                }
                            }
                        }
                    }
                } catch (CimDataEngineRuntimeException e) {
                    e.printStackTrace();
                } catch (CimDataEngineInfoExploreException e) {
                    e.printStackTrace();
                }
                break;
            case INSTANCE:
                List<InfoObject> linkedInstances = industryType.getChildInfoObjects();
                if (linkedInstances != null) {
                    for (InfoObject inst : linkedInstances) {
                        if (!inst.getObjectTypeName().equals(BaseFileInfoKeys.BaseFileObjectTypeName)) {
                            try {
                                Fact fact = cds.getFactById(inst.getObjectInstanceRID());
                                if (fact.hasProperty(BaseDataSetKeys.NAME)) {
                                    nameSet.add(fact.getProperty(BaseDataSetKeys.NAME).getPropertyValue().toString());
                                }
                            } catch (CimDataEngineRuntimeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
            case FILE:
                List<InfoObject> linkedFileInstances = industryType.getChildInfoObjects();
                if (linkedFileInstances != null) {
                    for (InfoObject inst : linkedFileInstances) {
                        if (inst.getObjectTypeName().equals(BaseFileInfoKeys.BaseFileObjectTypeName)) {
                            try {
                                Fact fact = cds.getFactById(inst.getObjectInstanceRID());
                                if (fact.hasProperty(BaseFileInfoKeys.FILE_DATA_NAME)) {
                                    nameSet.add(fact.getProperty(BaseFileInfoKeys.FILE_DATA_NAME).getPropertyValue().toString());
                                }
                            } catch (CimDataEngineRuntimeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
        }

        return nameSet;
    }

    public static Map<String, String> getAllChildNodeNamesMapByType(IndustryType industryType, CimDataSpace cds,
                                                                    String tenantId,
                                                                    TreeNodeInputBean.TreeNodeTypeEnum childNodeType) {//NOSONAR
        Map<String, String> nameMap = new HashMap<>();
        switch (childNodeType) {
            case INDUSTRY:
                //root insudtries
                if (industryType == null) {
                    try {
                        Dimension logicRootDimension = IndustryTypeFeatures.getIndustryTypeLogicRootNode(cds);
                        List<Relation> rootTypeRelationsList =
                                logicRootDimension.getAllSpecifiedRelations(BusinessLogicConstant.IS_ROOT_INDUSTRY_TYPE_RELATION_TYPE_NAME, RelationDirection.FROM);
                        if (rootTypeRelationsList != null && rootTypeRelationsList.size() > 0) {
                            for (Relation currentRelation : rootTypeRelationsList) {
                                Relationable currentTypeDimension = currentRelation.getToRelationable();
                                boolean isInValidTenant = CommonOperationUtil.isTenantContainsData(tenantId,
                                        currentTypeDimension);
                                if (isInValidTenant && currentTypeDimension.hasProperty(IndustryTypeNodeKeys.INDUSTRY_NAME)) {
                                    nameMap.put(currentTypeDimension.getProperty(IndustryTypeNodeKeys.INDUSTRY_NAME).getPropertyValue().toString(), currentTypeDimension.getId());
                                }
                            }
                        }
                    } catch (CimDataEngineDataMartException e) {
                        e.printStackTrace();
                    } catch (CimDataEngineRuntimeException e) {
                        e.printStackTrace();
                    } catch (CimDataEngineInfoExploreException e) {
                        e.printStackTrace();
                    }
                    // return nameSet;
                } else {
                    List<IndustryType> childIndustries = industryType.getChildrenIndustryTypes();
                    if (childIndustries != null) {
                        for (IndustryType type : childIndustries) {
                            log.debug("child industry name {} of {}", type.getIndustryTypeDesc(),
                                    type.getIndustryTypeRID());
                            nameMap.put(type.getIndustryTypeDesc(), type.getIndustryTypeRID());
                        }
                    }
                }
                break;
            case OBJECT:
                List<Fact> industryAndObjectMap = null;
                try {
                    industryAndObjectMap = IndustryTypeFeatures.getIndustryTypeAndInfoObjectTypeMapping(cds,
                            industryType.getIndustryTypeRID());
                    if (industryAndObjectMap != null && industryAndObjectMap.size() > 0) {
                        List<Object> objectIds = new ArrayList<>();
                        for (Fact fact : industryAndObjectMap) {
                            if (fact.hasProperty(INFO_OBJECT_TYPE_NAME)) {
                                objectIds.add(fact.getProperty(INFO_OBJECT_TYPE_NAME).getPropertyValue().toString());
                            }
                        }

                        FilteringItem filteringItem = new InValueFilteringItem(INFO_OBJECT_TYPE_NAME, objectIds);
                        ExploreParameters exploreParameters = new ExploreParameters();
                        exploreParameters.setDefaultFilteringItem(filteringItem);
                        exploreParameters.setType(BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME);

                        InformationExplorer ip = cds.getInformationExplorer();
                        List<Fact> objectStatus = ip.discoverFacts(exploreParameters);
                        if (objectStatus != null && objectStatus.size() > 0) {
                            for (Fact fact : objectStatus) {
                                if (CommonOperationUtil.isTenantContainsData(tenantId, fact) && fact.hasProperty(BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME_DESC)) {
                                    nameMap.put(fact.getProperty(BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME_DESC).getPropertyValue().toString(), fact.getId());
                                }
                            }
                        }
                    }
                } catch (CimDataEngineRuntimeException e) {
                    e.printStackTrace();
                } catch (CimDataEngineInfoExploreException e) {
                    e.printStackTrace();
                }
                break;
            case INSTANCE:
                List<InfoObject> linkedInstances = industryType.getChildInfoObjects();
                if (linkedInstances != null) {
                    for (InfoObject inst : linkedInstances) {
                        if (!inst.getObjectTypeName().equals(BaseFileInfoKeys.BaseFileObjectTypeName)) {
                            try {
                                Fact fact = cds.getFactById(inst.getObjectInstanceRID());
                                if (fact.hasProperty(BaseDataSetKeys.NAME)) {
                                    nameMap.put(fact.getProperty(BaseDataSetKeys.NAME).getPropertyValue().toString(),
                                            fact.getId());
                                }
                            } catch (CimDataEngineRuntimeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
            case FILE:
                List<InfoObject> linkedFileInstances = industryType.getChildInfoObjects();
                if (linkedFileInstances != null) {
                    for (InfoObject inst : linkedFileInstances) {
                        if (inst.getObjectTypeName().equals(BaseFileInfoKeys.BaseFileObjectTypeName)) {
                            try {
                                Fact fact = cds.getFactById(inst.getObjectInstanceRID());
                                if (fact.hasProperty(BaseFileInfoKeys.FILE_DATA_NAME)) {
                                    nameMap.put(fact.getProperty(BaseFileInfoKeys.FILE_DATA_NAME).getPropertyValue().toString(), fact.getId());
                                }
                            } catch (CimDataEngineRuntimeException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
        }

        return nameMap;
    }

    /**
     * 将行业分类挂载到实例
     *
     * @param parentNode
     * @param industryRid
     * @param tenantId
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public Boolean linkIndustryToInstance(TreeNodeInputBean parentNode, String industryRid, String tenantId) throws DataServiceModelRuntimeException {
        Boolean flag = false;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            IndustryType industryType = modelCore.getIndustryType(industryRid);
            if (industryType != null) {
                Dimension industry = cds.getDimensionById(industryRid);
                Fact instance = cds.getFactById(parentNode.getInstanceRid());
                if (industry != null && instance != null) {
                    Map<String, Object> parms = new HashMap<>();
                    parms.put(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_INDUSTRY_RID,
                            parentNode.getParentIndustryRid());
                    Relation relation = industry.addFromRelation(instance,
                            BusinessLogicConstant.INDUSTRYTYPE_INFOOBJECT_LINK_RELATION_TYPE_NAME, parms);
                    if (relation != null) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                } else {
                    log.error("industry of {} or instance of {} not found", industryRid, parentNode.getInstanceRid());
                }
            } else {
                log.error("industry of {} not found");
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return flag;
    }

    private boolean linkIndustryToInstance(CimDataSpace cds, String industryRid, String instanceRid,
                                           String parentIndustryRid) throws CimDataEngineRuntimeException {
        Dimension currentIndustryType = cds.getDimensionById(industryRid);
        List<Relation> relationList =
                currentIndustryType.getAllSpecifiedRelations(BusinessLogicConstant.INDUSTRYTYPE_INFOOBJECT_LINK_RELATION_TYPE_NAME, RelationDirection.TO);
        if (relationList != null) {
            for (Relation currentRelation : relationList) {
                Relationable targetRelationable = currentRelation.getFromRelationable();
                if (targetRelationable.getId().equals(instanceRid)) {
                    return false;
                }
            }
        }
        Fact fact = cds.getFactById(instanceRid);
        Map<String, Object> parms = new HashMap<>();
        parms.put(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_INDUSTRY_RID, parentIndustryRid);
        Relation addResult = currentIndustryType.addFromRelation(fact,
                BusinessLogicConstant.INDUSTRYTYPE_INFOOBJECT_LINK_RELATION_TYPE_NAME, parms);
        if (addResult != null) {
            return true;
        }
        return false;
    }


    public Long sendStartTaskMq(String json) {
        JobPropsDTO jobPropsDTO = new JobPropsDTO();
        // 文件传输 上传
        log.info("typeCode={}", fileUpload);
        jobPropsDTO.setJobName(fileUploadPrefix + "-" + DateUtil.getCurrentDateReadable());
        jobPropsDTO.setTypeCode(fileUpload);
        // 消息内容
        jobPropsDTO.setParam(json);
        JobResponse<IdResult> jobResponse = jobInfoClient.add(jobPropsDTO);
        log.info("SHP file import response status code: {}, content: {}", jobResponse.getCode(), jobResponse.getData());
        return jobResponse.getData().getId();
    }

    /**
     * 发送更改任务消息到MQ--更改文件任务状态
     *
     * @return
     */
    public void sendTaskStatusMq(String taskId, String status) {
        JobParmDTO jobParmDTO = new JobParmDTO();
        jobParmDTO.setStatus(status);
        jobParmDTO.setDate(DateUtil.getCurrentDate());
        log.info("更改文件任务状态开始{}", DateUtil.getCurrentDate());
        jobInfoClient.updateStatus(Long.valueOf(taskId), jobParmDTO);
    }


    //============================================================================================================================== added by wayne 20190716
    private Map<String, Map<String, Object>> fileMetadataParser_V1(String metadata, String parentNodeName,
                                                                   String minioNamePrefix, String bucketName,
                                                                   String userId) throws IOException {
        List<FileMetadataUploadBean> metdataBeans = objectMapper.readValue(metadata,
                new TypeReference<List<FileMetadataUploadBean>>() {
                });
        Map<String, Map<String, Object>> fileMetadata = new HashMap<>();
        for (FileMetadataUploadBean bean : metdataBeans) {
            try {
                if (StringUtils.isNotBlank(bean.getSrcFileName()) && StringUtils.isNotBlank(bean.getFileDataName()) && StringUtils.isNotBlank(bean.getFileDataId())) {
                    Map<String, Object> tempMap = new HashMap<>();
                    tempMap.put(BaseFileInfoKeys.FILE_DATA_ID, bean.getFileDataId());
                    if (StringUtils.isNotBlank(bean.getFileDataId())) {
                        tempMap.put(BaseDataSetKeys.ID, bean.getFileDataId());
                    }
                    tempMap.put(BaseFileInfoKeys.FILE_DATA_NAME, bean.getFileDataName());
                    tempMap.put(BaseDataSetKeys.NAME, bean.getFileDataName());
                    tempMap.put(BaseFileInfoKeys.SRC_FILE_NAME, bean.getSrcFileName());
                    tempMap.put(BaseFileInfoKeys.BUCKET_NAME, bucketName);
                    tempMap.put(BaseFileInfoKeys.CREATOR, userId);
                    tempMap.put(BaseFileInfoKeys.UPDATOR, userId);

                    String srcFileName = bean.getSrcFileName();
                    String fileExt = srcFileName.substring(srcFileName.lastIndexOf(".") + 1);
                    if (StringUtils.isNotBlank(fileExt)) {
                        fileExt = fileExt.toUpperCase();
                    }
                    tempMap.put(BaseFileInfoKeys.FILE_CONTENT_TYPE, fileExt);

                    String fileType = CimConstants.FILE_TYPES_MAPPING.get(fileExt);
                    tempMap.put(BaseFileInfoKeys.FILE_TYPE, fileType);

                    tempMap.put(BaseFileInfoKeys.MINIO_OBJECT_NAME, bean.getMinioObjectName());

                    tempMap.put(BaseFileInfoKeys.INDUSTRY_NAME, parentNodeName);

                    tempMap.put(CimConstants.INSTANCE_RID, bean.getInstanceRid());

                    fileMetadata.put(tempMap.get(BaseFileInfoKeys.SRC_FILE_NAME).toString(), tempMap);
                } else {
                    log.error("{}, {}, {} are mandatary", BaseFileInfoKeys.SRC_FILE_NAME,
                            BaseFileInfoKeys.FILE_DATA_ID, BaseFileInfoKeys.FILE_DATA_NAME);
                }
            } catch (Exception e) {
                log.error("bean metadata input error: metadata={}", metadata);
                e.printStackTrace();
            }
        }
        return fileMetadata;
    }

    // added by wayne 20190716
    public List<Map<String, Object>> fileInfoAndAddNode(String tenantId, String userId, String treeDefId,
                                                        String bucketName, Boolean isOverride, String parentNodeStr
            , String metadataStr) throws IOException {
        List<Map<String, Object>> rs;
        NodeInfoBean parentNode = objectMapper.readValue(parentNodeStr,
                new TypeReference<NodeInfoBean>() {
                });
        log.info("file upload parent node info: {}", parentNode);
        Assert.hasText(parentNode.getID(), "parent node is mandatory");
        Map<String, Map<String, Object>> fileMetaDatas = fileMetadataParser_V1(metadataStr, parentNode.getNAME(),
                parentNode.getID(), bucketName, userId);
        log.info("file upload file metadat parser result: {}", fileMetaDatas);
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            modelCore.setCimDataSpace(cds);
            rs = fileInfoBatch(tenantId, isOverride, bucketName, modelCore, fileMetaDatas);
            List<String> childNodeRids = new ArrayList<>();
            for (Map<String, Object> map : rs) {
                if (map.get(CimConstants.INSTANCE_RID) != null && StringUtils.isNotBlank(map.get(CimConstants.INSTANCE_RID).toString())) {
                    childNodeRids.add(map.get(CimConstants.INSTANCE_RID).toString());
                }
            }
            if (childNodeRids.size() > 0) {
                treeNodeService.addFileNodeByRidWithMetadata(cds, tenantId, userId, treeDefId, parentNode,
                        childNodeRids);
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return rs;
    }


    // added by wayne 20190716
    private List<Map<String, Object>> fileInfoBatch(String tenantId, Boolean isOverride, String bucketName,
                                                    CIMModelCore modelCore, Map<String,
            Map<String, Object>> fileMetadata) {//NOSONAR
        List<Map<String, Object>> result = new ArrayList<>();
        for (String orgFileName : fileMetadata.keySet()) {

//            Map<String, Object> curMap=fileMetadata.get(key);
//            String orgFileName = curMap.get(BaseFileInfoKeys.SRC_FILE_NAME).git diff toString();
//
            Map<String, Object> singleMetadata = fileMetadata.get(orgFileName);
            //上传文件到Minio
            Map<String, Object> singleResult = singleMetadata;
            //若文件上传成功，且为BIM类型，则上传到BIMFace
            if (singleResult.containsKey(CimConstants.INSTANCE_RID) && StringUtils.isNotBlank(singleResult.get(CimConstants.INSTANCE_RID).toString())) {
                if (CimConstants.BIM_FILE_TYPES.contains(singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE))) {
                    log.info("bim file should be uploaded and translated: fileContentType={}",
                            singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE));
                    // bimFaceService.uploadTask(singleMetadata.get(BaseFileInfoKeys.MINIO_OBJECT_NAME).toString(),
                    //         bucketName, CimConstants.defauleSpaceName,
                    //         singleResult.get(CimConstants.INSTANCE_RID).toString());

                    // sendMessageUtil.sendMessage(bucketName,
                    //         singleMetadata.get(BaseFileInfoKeys.MINIO_OBJECT_NAME).toString(),
                    //         BaseFileInfoKeys.BaseFileObjectTypeName);
                    BimFileUploadTranslateBean translateBean = new BimFileUploadTranslateBean(bucketName,
                            singleMetadata.get(BaseFileInfoKeys.MINIO_OBJECT_NAME).toString(),
                            BaseFileInfoKeys.BaseFileObjectTypeName,
                            singleMetadata.get(BaseFileInfoKeys.SRC_FILE_NAME).toString(), tenantId);
                    sendMessageUtil.sendMessage(translateBean);
                } else if (CimConstants.OFFICE_FILE_TYPES.contains(singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE))) {
                    log.info("office file should be convertered: tenantId={}, bucket={}, fileName={}",
                            tenantId, bucketName, singleMetadata.get(BaseFileInfoKeys.MINIO_OBJECT_NAME).toString());
                    DocConverterTask converterTask = null;
                    try {
                        converterTask = new DocConverterTask(minioService.getMinioClient(), converterService, tenantId,
                                BaseFileInfoKeys.BaseFileObjectTypeName, bucketName,
                                singleMetadata.get(BaseFileInfoKeys.MINIO_OBJECT_NAME).toString());
                    } catch (InvalidPortException | InvalidEndpointException e) {
                        log.error("create minio client failed", e);
                    }
                    executorService.submit(converterTask);
                } else {
                    log.info("not bim file, should not be uploaded and translated: fileContentType={}",
                            singleMetadata.get(BaseFileInfoKeys.FILE_CONTENT_TYPE));
                }
            } else {
                log.error("file upload failed");
            }
            result.add(singleResult);

        }
        return result;
    }

}
