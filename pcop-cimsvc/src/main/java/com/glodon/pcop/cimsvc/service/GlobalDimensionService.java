package com.glodon.pcop.cimsvc.service;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.entity.DataSetEntity;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.SimilarFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineDataMartException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.GlobalDimensionFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.PropertyTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.GlobalDimensionTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cimsvc.model.dimension.DimensionItemsOutput;
import com.glodon.pcop.cimsvc.model.dimension.DimensionTypeInputBean;
import com.glodon.pcop.cimsvc.model.dimension.DimensionTypeOutputBean;
import com.glodon.pcop.cimsvc.model.dimension.DimensionTypeWithDatasetOutputBean;
import com.glodon.pcop.cimsvc.model.gcAdapters.GlobalDimensionTypeAdapter;
import com.glodon.pcop.cimsvc.model.transverter.DataSetTsr;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GlobalDimensionService {

    private static Logger log = LoggerFactory.getLogger(
            GlobalDimensionService.class);

    public static final String DIMENSION_TYPE_NAME = "dimensionTypeName";
    public static final String DIMENSION_TYPE_DESC = "dimensionTypeDesc";

    /**
     * 新增配置类型定义
     *
     * @param tenantId
     * @param globalDimensionTypeBean
     * @return
     */
    public boolean addConfigurationDimension(String tenantId,
                                             DimensionTypeInputBean globalDimensionTypeBean) {//NOSONAR
        GlobalDimensionTypeVO globalDimensionTypeVO = GlobalDimensionTypeAdapter.typeCast(
                globalDimensionTypeBean);
        if (globalDimensionTypeVO != null) {
            globalDimensionTypeVO.setTenantId(tenantId);
        }
        CimDataSpace cds = null;
        boolean flag = false;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(
                    CimConstants.defauleSpaceName);
            flag = GlobalDimensionFeatures.addGlobalDimensionType(cds,
                    globalDimensionTypeVO);
            if (flag) {
                DatasetVO dataSetVO = DatasetFeatures.addDataset(cds,
                        globalDimensionTypeVO.getLinkedDataset());
                if (dataSetVO != null && StringUtils.isNotBlank(
                        dataSetVO.getDatasetId()) && globalDimensionTypeVO.getLinkedDataset() != null) {
                    List<PropertyTypeVO> propertyTypeVOList = globalDimensionTypeVO.getLinkedDataset().getLinkedPropertyTypes();
                    for (PropertyTypeVO propertyTypeVO : propertyTypeVOList) {
                        PropertyTypeVO tmp = PropertyTypeFeatures.addPropertyType(
                                cds, propertyTypeVO);
                        DatasetFeatures.addPropertyTypeLink(cds,
                                dataSetVO.getDatasetId(),
                                tmp.getPropertyTypeId());
                    }
                }
                if (dataSetVO != null && StringUtils.isNotBlank(
                        dataSetVO.getDatasetId())) {
                    log.debug("dataset is add successful: {}",
                            dataSetVO.getDatasetId());
                    flag = GlobalDimensionFeatures.linkGlobalDimensionTypeWithDataset(
                            cds, globalDimensionTypeVO.getDimensionTypeName(),
                            dataSetVO.getDatasetId());
                } else {
                    log.error("global dimension dataset add failed");
                    flag = false;
                }
            } else {
                log.error("add global dimension type failed");
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

        return flag;
    }

    /**
     * 查询指定配置类型下的所有配置项
     *
     * @param dimensionTypeName
     * @return
     */
    public List<Map<String, Object>> getGlobalDimensionItems(String dimensionTypeName, String sortProperty, ExploreParameters.SortingLogic sortingLogic) {
        CimDataSpace cds = null;
        List<Map<String, Object>> data = new ArrayList<>();
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            if (StringUtils.isBlank(sortProperty)) {
                sortProperty = "@rid";
                sortingLogic = ExploreParameters.SortingLogic.ASC;
            }

            if (sortingLogic == null) {
                sortingLogic = ExploreParameters.SortingLogic.ASC;
            }
            return GlobalDimensionFeatures.listGlobalDimensionItems(cds, dimensionTypeName, sortProperty, sortingLogic);
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return data;
    }

    /**
     * 为指定配置类型新增一个配置项
     *
     * @param dimensionTypeName
     * @param item
     * @return
     */
    public boolean addGlobalDimensionItem(String dimensionTypeName,
                                          Map<String, Object> item) {
        try {
            return GlobalDimensionFeatures.addGlobalDimensionItem(
                    CimConstants.defauleSpaceName, dimensionTypeName, item);
        } catch (CimDataEngineDataMartException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 批量查询配置项数据
     *
     * @param dimensionsTypes
     * @return
     */
    public List<DimensionItemsOutput> getGlobalDimensionItems(
            List<String> dimensionsTypes) {
        List<DimensionItemsOutput> dimensionTypeBeanList = new ArrayList<>();
        if (dimensionsTypes != null && dimensionsTypes.size() > 0) {
            CimDataSpace cds = null;
            try {
                cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(
                        CimConstants.defauleSpaceName);
                for (String dimensionType : dimensionsTypes) {
                    DimensionItemsOutput typeBean = new DimensionItemsOutput();
                    typeBean.setDimensionTypeName(dimensionType);
                    typeBean.setItems(
                            GlobalDimensionFeatures.listGlobalDimensionItems(cds, dimensionType, "@rid", ExploreParameters.SortingLogic.ASC));
                    dimensionTypeBeanList.add(typeBean);
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
        }
        return dimensionTypeBeanList;
    }


    /**
     * 字典配置类型列表
     *
     * @param keyWord 搜索关键词（可选）
     * @return
     */
    public List<DimensionTypeOutputBean> listGlobalDimensions(String keyWord, int pageIndex, int pageSize) {
        List<DimensionTypeOutputBean> dimensionTypeOutputBeans = new ArrayList<>();

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            dimensionTypeOutputBeans = listGlobalDimensions(cds, keyWord, keyWord, pageIndex, pageSize);
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return dimensionTypeOutputBeans;
    }

    public List<DimensionTypeOutputBean> listGlobalDimensions(CimDataSpace cds, String dimensionName,
                                                              String dimensionDesc, int pageIndex, int pageSize) throws
            CimDataEngineRuntimeException,
            CimDataEngineInfoExploreException {
        List<DimensionTypeOutputBean> dimensionTypeOutputBeans = new ArrayList<>();
        ExploreParameters ep = new ExploreParameters();
        ep.setType(
                BusinessLogicConstant.GLOBAL_DIMENSION_STATUS_FACT_TYPE_NAME);
        ep.setStartPage(pageIndex + 1);
        ep.setEndPage(pageIndex + 2);
        ep.setPageSize(pageSize);
        if (StringUtils.isNotBlank(dimensionName)) {
            FilteringItem nameFilterItem = new SimilarFilteringItem(
                    DIMENSION_TYPE_NAME, dimensionName,
                    SimilarFilteringItem.MatchingType.Contain, false);
            ep.setDefaultFilteringItem(nameFilterItem);
            if (StringUtils.isNotBlank(dimensionDesc)) {
                FilteringItem descFilterItem = new SimilarFilteringItem(
                        DIMENSION_TYPE_DESC, dimensionDesc,
                        SimilarFilteringItem.MatchingType.Contain, false);
                ep.addFilteringItem(descFilterItem,
                        ExploreParameters.FilteringLogic.OR);
            }
        } else if (StringUtils.isNotBlank(dimensionDesc)) {
            FilteringItem descFilterItem = new SimilarFilteringItem(
                    DIMENSION_TYPE_DESC, dimensionDesc,
                    SimilarFilteringItem.MatchingType.Contain, false);
            ep.setDefaultFilteringItem(descFilterItem);
        }

        List<Fact> dimensionStatusFacts = cds.getInformationExplorer().discoverFacts(ep);

        log.info("explore parameters: [{}]", JSON.toJSON(ep));

        if (CollectionUtils.isNotEmpty(dimensionStatusFacts)) {
            for (Fact statusFact : dimensionStatusFacts) {
                dimensionTypeOutputBeans.add(
                        statusFactToOutputBean(statusFact));
            }
        }

        return dimensionTypeOutputBeans;
    }

    private DimensionTypeOutputBean statusFactToOutputBean(Fact statusFact) {
        DimensionTypeOutputBean outputBean = new DimensionTypeOutputBean();
        outputBean.setDimensionTypeName(statusFact.getProperty(
                DIMENSION_TYPE_NAME).getPropertyValue().toString());
        if (statusFact.hasProperty(
                DIMENSION_TYPE_DESC) && statusFact.getProperty(
                DIMENSION_TYPE_DESC) != null) {
            outputBean.setDimensionTypeDesc(
                    statusFact.getProperty(
                            DIMENSION_TYPE_DESC).getPropertyValue().toString());
        } else {
            log.warn("dimension type desc of [{}] not exists",
                    statusFact.getId());
        }
        return outputBean;
    }

    /**
     * 字典类型定义：包含属性集和属性的定义
     *
     * @param dimensionName
     * @return
     */
    public DimensionTypeWithDatasetOutputBean getDimensionType(
            String dimensionName) {
        DimensionTypeWithDatasetOutputBean outputBean = new DimensionTypeWithDatasetOutputBean();
        outputBean.setDimensionTypeName(dimensionName);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            List<DimensionTypeOutputBean> typeOutputBeanList = listGlobalDimensions(cds, dimensionName, null, 0, 10);

            if (CollectionUtils.isNotEmpty(typeOutputBeanList)) {
                DimensionTypeOutputBean typeOutputBean = typeOutputBeanList.get(0);
                outputBean.setDimensionTypeDesc(typeOutputBean.getDimensionTypeDesc());
                DatasetVO datasetVO = GlobalDimensionFeatures.getLinkedDataset(cds, dimensionName);
                if (datasetVO != null) {
                    DataSetEntity dataSetEntity = DataSetTsr.voToEntityV2(datasetVO, true);
                    outputBean.setDataSet(dataSetEntity);
                }
            } else {
                log.info("dimension type of [{}] not found", dimensionName);
                return null;
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

        return outputBean;
    }

    /**
     * 删除字典项
     *
     * @param dimensionName
     * @param dimensionItemRid
     * @return
     */
    public boolean removeDimensionItem(String dimensionName, String dimensionItemRid) {
        return GlobalDimensionFeatures.removeGlobalDimensionTypeItem(CimConstants.defauleSpaceName, dimensionName,
                dimensionItemRid);
    }

    /**
     * 删除字典类型：类型定义和所有字典项
     *
     * @param dimensionName
     * @return
     */
    public boolean removeDimensionType(String dimensionName) {
        boolean flag = false;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            flag = GlobalDimensionFeatures.reInitGlobalDimensionType(cds, dimensionName);
            Fact statusFact = GlobalDimensionFeatures.getGlobalDimensionStatusRecord(cds, dimensionName);
            if (statusFact != null) {
                cds.removeFact(statusFact.getId());
            }
            if (cds.hasDimensionType(dimensionName)) {
                flag = cds.removeDimensionType(dimensionName);
            }
        } catch (CimDataEngineInfoExploreException | CimDataEngineRuntimeException | CimDataEngineDataMartException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return flag;
    }


}
