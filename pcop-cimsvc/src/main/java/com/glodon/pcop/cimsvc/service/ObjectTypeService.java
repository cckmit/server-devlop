package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.SimilarFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.PropertyTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cimapi.model.InstanceQueryInputBean;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.ObjectQueryOutput;
import com.glodon.pcop.cimsvc.model.ObjectTypeBean;
import com.glodon.pcop.cimsvc.model.PropertySetBean;
import com.glodon.pcop.cimsvc.model.adapter.ObjectTypeAdapter;
import com.glodon.pcop.cimsvc.model.adapter.PropertySetAdapter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author yuanjk(yuanjk @ glodon.com), 2018-07-24 10:33:13
 */
@Service
public class ObjectTypeService {
    static Logger log = LoggerFactory.getLogger(ObjectTypeService.class);

    private static final String INFO_OBJECT_TYPE_DESC = "infoObjectTypeDesc";

    @Autowired
    private PropertySetService propertySetService;

    /**
     * @param objectTypeBean
     * @param creator
     * @return
     */
    public ObjectTypeBean addObjectType(ObjectTypeBean objectTypeBean, String creator) {
        boolean flag;
        InfoObjectTypeVO infoObjectTypeVO = ObjectTypeAdapter.objectTypeAdapter(objectTypeBean);
        // 创建对象模型
        if (StringUtils.isBlank(objectTypeBean.getParentTypeId())) {
            flag = InfoObjectFeatures.addRootInfoObjectType(CimConstants.defauleSpaceName, infoObjectTypeVO);
        } else {
            flag = InfoObjectFeatures.addChildInfoObjectType(CimConstants.defauleSpaceName, infoObjectTypeVO, objectTypeBean.getParentTypeId());
        }
        if (flag) {
            // 关联对象和行业分类
            InfoObjectFeatures.linkInfoObjectTypeWithIndustryType(CimConstants.defauleSpaceName, infoObjectTypeVO.getObjectId(), objectTypeBean.getIndustryTypeId());
            // 创建该对象关联的属性集并与对象关联起来
            for (PropertySetBean psb : objectTypeBean.getPropertySet()) {
                DatasetVO ds = propertySetService.addPropertySet(psb);
                InfoObjectFeatures.linkInfoObjectTypeWithDataset(CimConstants.defauleSpaceName, infoObjectTypeVO.getObjectId(), ds.getDatasetId());
            }
        } else {
            log.error("object type add failed: objectTypeName={}", objectTypeBean.getTypeName());
        }
        InfoObjectTypeVO infoObjectTypeVOByType = InfoObjectFeatures.getInfoObjectTypeVOByType(CimConstants.defauleSpaceName, objectTypeBean.getTypeId(), true, true);
        return ObjectTypeAdapter.objectTypeAdapter(infoObjectTypeVOByType);
    }

    /**
     * 更新对象模型，暂不支持更新对象名称，基对象信息
     *
     * @param objectTypeId
     * @param objectTypeBean
     * @param creator
     * @return
     */
    public ObjectTypeBean updateObjectType(String objectTypeId, ObjectTypeBean objectTypeBean, String creator) throws EntityNotFoundException {//NOSONAR

        InfoObjectTypeVO infoObjectTypeVO = InfoObjectFeatures.getInfoObjectTypeVOByType(CimConstants.defauleSpaceName, objectTypeId, true, true);
        if (infoObjectTypeVO == null) {
            throw new EntityNotFoundException("Object type not found, objectId=" + objectTypeId);
        }
        Fact fact = InfoObjectFeatures.updateObjectTypeStatus(CimConstants.defauleSpaceName, objectTypeId, objectTypeBean.getTypeName());
        if (fact == null) {
            log.error("Update object type name failed: objectTypeId = {}", objectTypeId);
            return null;
        }
        //更新行业分类
        List<IndustryTypeVO> industryTypeVOList = InfoObjectFeatures.getLinkedIndustryTypes(CimConstants.defauleSpaceName, objectTypeId);
        if (industryTypeVOList != null && industryTypeVOList.size() > 0) {
            IndustryTypeVO industryTypeVO = industryTypeVOList.get(0);
            InfoObjectFeatures.unlinkInfoObjectTypeAttachedIndustryType(CimConstants.defauleSpaceName, objectTypeId, industryTypeVO.getIndustryTypeId());
            String industryTypeId = objectTypeBean.getIndustryTypeId();
            if (StringUtils.isNotBlank(industryTypeId)) {
                industryTypeVO = IndustryTypeFeatures.getIndustryTypeVOById(CimConstants.defauleSpaceName, industryTypeId);
                if (industryTypeVO != null) {
                    InfoObjectFeatures.linkInfoObjectTypeWithIndustryType(CimConstants.defauleSpaceName, objectTypeId, industryTypeVO.getIndustryTypeId());
                }
            }
        }
        //更新属性集和属性
        //断开对象与所有属性集、属性的关联
        List<DatasetVO> datasetVOList = infoObjectTypeVO.getLinkedDatasets();
        if (datasetVOList != null) {
            for (DatasetVO datasetVO : datasetVOList) {
                //断开object和dataset的关联
                InfoObjectFeatures.unlinkInfoObjectTypeAttachedDataset(CimConstants.defauleSpaceName, objectTypeId, datasetVO.getDatasetId());
                List<PropertyTypeVO> tmpPropertyTypeVOList = datasetVO.getLinkedPropertyTypes();
                if (tmpPropertyTypeVOList != null) {
                    for (PropertyTypeVO propertyTypeVO : tmpPropertyTypeVOList) {
                        //断开property和dataset的关联
                        DatasetFeatures.breakPropertyTypeLink(CimConstants.defauleSpaceName, datasetVO.getDatasetId(), propertyTypeVO.getPropertyTypeId(), false);
                    }
                }
            }
        }
        //更新属性集和属性
        List<PropertySetBean> propertySetBeanList = objectTypeBean.getPropertySet();
        if (propertySetBeanList != null) {
            for (PropertySetBean propertySetBean : propertySetBeanList) {
                if (propertySetBean.getProperties() != null) {
                    log.info("update object type definition, propertySetName={}, property size={}", propertySetBean.getName(), propertySetBean.getProperties().size());
                } else {
                    log.info("update object type definition, propertySetName={}, property is nul", propertySetBean.getName());
                }

                DatasetVO datasetVO = PropertySetAdapter.propertySetAdapter(propertySetBean);
                if (StringUtils.isNotBlank(datasetVO.getDatasetId()) && datasetVO.getDatasetId().startsWith("#")) {
                    //更新属性集
                    log.info("update object type definition update property set: typeId={}, typeName={}", datasetVO.getDatasetId(), datasetVO.getDatasetName());
                    DatasetFeatures.updateDataset(CimConstants.defauleSpaceName, datasetVO.getDatasetId(), datasetVO);
                    List<PropertyTypeVO> propertyTypeVOList = datasetVO.getLinkedPropertyTypes();
                    if (propertyTypeVOList != null) {
                        for (PropertyTypeVO propertyTypeVO : propertyTypeVOList) {
                            String propertyTypeId = propertyTypeVO.getPropertyTypeId();
                            PropertyTypeVO typeVO;
                            if (StringUtils.isNotBlank(propertyTypeId) && propertyTypeVO.getPropertyTypeId().startsWith("#")) {
                                //更新属性
                                log.info("update object type definition update property: typeId={}, typeName={}", propertyTypeVO.getPropertyTypeId(), propertyTypeVO.getPropertyTypeName());
                                typeVO = PropertyTypeFeatures.updatePropertyType(CimConstants.defauleSpaceName, propertyTypeId, propertyTypeVO);
                            } else {
                                //新增属性
                                log.info("update object type definition add property: typeName={}", propertyTypeVO.getPropertyTypeName());
                                typeVO = PropertyTypeFeatures.addPropertyType(CimConstants.defauleSpaceName, propertyTypeVO);
                            }
                            log.info("update object type link propertySetId={} with propertyId={}", datasetVO.getDatasetId(), typeVO.getPropertyTypeId());
                            DatasetFeatures.addPropertyTypeLink(CimConstants.defauleSpaceName, datasetVO.getDatasetId(), typeVO.getPropertyTypeId());
                        }
                    } else {
                        log.info("update object type definition no proeprty is updated");
                    }
                } else {
                    //新增属性集
                    log.info("update object type definition add property set: typeName={}", datasetVO.getDatasetName());
                    datasetVO = DataEngineService.addDataSet(CimConstants.defauleSpaceName, datasetVO);
                }
                log.info("update object type link objectId={} with propertySetId={}", objectTypeId, datasetVO.getDatasetId());
                InfoObjectFeatures.linkInfoObjectTypeWithDataset(CimConstants.defauleSpaceName, objectTypeId, datasetVO.getDatasetId());
            }
        } else {
            log.info("update object type definition no proeprty set is updated");
        }

        InfoObjectTypeVO objectTypeVO = InfoObjectFeatures.getInfoObjectTypeVOByType(CimConstants.defauleSpaceName, objectTypeId, true, true);
        return ObjectTypeAdapter.objectTypeAdapter(objectTypeVO);
    }

    /**
     * 删除对象模型定义
     *
     * @param objectTypeId
     * @return
     */
    public Boolean deleteObjectType(String objectTypeId) {
        boolean flag = InfoObjectFeatures.disableInfoObjectType(CimConstants.defauleSpaceName, objectTypeId);
        return flag;
    }

    /**
     * 查询所有已定义对象模型
     *
     * @return
     */
    public List<ObjectTypeBean> getAllObjectType(String kw) {
        List<InfoObjectTypeVO> objects = InfoObjectFeatures.listInfoObjectTypesFlat(CimConstants.defauleSpaceName, true, false);
        List<ObjectTypeBean> allObjectBeans = new ArrayList<ObjectTypeBean>();
        for (InfoObjectTypeVO entity : objects) {
            if (!entity.isDisabled()) {
                if (StringUtils.isNotBlank(kw)) {
                    if (entity.getObjectId().contains(kw)) {
                        allObjectBeans.add(ObjectTypeAdapter.objectTypeAdapterWithoutProperty(entity));
                    }
                } else {
                    allObjectBeans.add(ObjectTypeAdapter.objectTypeAdapterWithoutProperty(entity));
                }
            }
        }
        return allObjectBeans;
    }


    public ObjectQueryOutput queryAllObjectsByPage(InstanceQueryInputBean queryInputBean) {
        ObjectQueryOutput objectQueryOutput = new ObjectQueryOutput();
        List<ObjectTypeBean> objectTypeBeanList = new ArrayList<>();
        Map<String, String> conditions = queryInputBean.getConditions();

        ExploreParameters ep = new ExploreParameters();
        ep.setType(BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME);
        if (conditions != null && StringUtils.isNotBlank(conditions.get(INFO_OBJECT_TYPE_DESC))) {
            FilteringItem filter = new SimilarFilteringItem(INFO_OBJECT_TYPE_DESC, conditions.get(INFO_OBJECT_TYPE_DESC), SimilarFilteringItem.MatchingType.Contain, false);
            ep.setDefaultFilteringItem(filter);
        }

        //查询符合条件的总量
        ep.setResultNumber(Integer.MAX_VALUE);
        objectQueryOutput.setTotalCount(countObjectTypes(ep));

        //分页查询
        ep.setPageSize(queryInputBean.getPageSize());
        ep.setStartPage(queryInputBean.getStartPage());
        ep.setEndPage(queryInputBean.getEndPage());
        List<Fact> factList = queryObjectTypes(ep);
        if (factList != null) {
            for (Fact fact : factList) {
                if (fact.hasProperty("infoObjectTypeName")) {
                    String objTypeName = fact.getProperty("infoObjectTypeName").getPropertyValue().toString();
                    InfoObjectTypeVO infoObjectTypeVO = InfoObjectFeatures.getInfoObjectTypeVOByType(CimConstants.defauleSpaceName, objTypeName, true, false);
                    if (infoObjectTypeVO != null) {
                        ObjectTypeBean objectTypeBean = ObjectTypeAdapter.objectTypeAdapterWithoutProperty(infoObjectTypeVO);
                        objectTypeBeanList.add(objectTypeBean);
                    } else {
                        log.error("Object type is not definition, objectTypeName={}", objTypeName);
                    }
                } else {
                    log.error("InfoObjectType doesnot has infoObjectTypeName, faceId={}", fact.getId());
                }
            }
        } else {
            log.info("No object type contains the keyWord={}", conditions.get(INFO_OBJECT_TYPE_DESC));
        }
        objectQueryOutput.setObjects(objectTypeBeanList);
        return objectQueryOutput;
    }

    /**
     * 分页查询对象模型
     *
     * @param exploreParameters
     * @return
     */
    private List<Fact> queryObjectTypes(ExploreParameters exploreParameters) {
        List<Fact> factList = null;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            InformationExplorer informationExplorer = cds.getInformationExplorer();
            if (exploreParameters != null) {
                factList = informationExplorer.discoverFacts(exploreParameters);
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
        return factList;
    }


    /**
     * 统计满足查询条件的对象模型数量
     *
     * @param exploreParameters
     * @return
     */
    private long countObjectTypes(ExploreParameters exploreParameters) {
        long totalCount = 0L;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            InformationExplorer informationExplorer = cds.getInformationExplorer();
            if (exploreParameters != null) {
                totalCount = informationExplorer.countFacts(exploreParameters);
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
        return totalCount;
    }

    /**
     * 查询object type id 是否可用，根据是否已创建以该id命名的表判断
     *
     * @param objectTypeId
     * @return
     */
    public boolean isObjectTypeIdAvailable(String objectTypeId) {
        CimDataSpace ids = null;
        try {
            ids = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            if (ids.hasInheritFactType(objectTypeId)) {
                return false;
            } else {
                return true;
            }
        } finally {
            if (ids != null) {
                ids.closeSpace();
            }
        }
    }

}
