package com.glodon.pcop.cimsvc.util.condition;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataAnalysis.StatParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.*;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.GlobalDimensionFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.exception.PropertyTypeException;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import com.glodon.pcop.cimsvc.model.v2.InstancesQueryInput;
import com.glodon.pcop.cimsvc.util.QueryConditionsUtil;
import com.glodon.pcop.cimsvc.util.SqlUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryConditionParser {
    private static Logger log = LoggerFactory.getLogger(QueryConditionParser.class);


    public static StatParameters parserStatConditions(CimDataSpace cds, String objectTypeId,
                                                    List<CommonQueryConditionsBean> queryConditionsBeanList,
                                                    StatParameters exploreParameters
    ) throws InputErrorException {
        if (queryConditionsBeanList != null && queryConditionsBeanList.size() > 0) {
            Map<String, PropertyTypeVO> propertyTypes = getPropertiesByObjectType(cds, objectTypeId, false);
            if (propertyTypes.size() > 0) {
                for (int i = 0; i < queryConditionsBeanList.size(); i++) {
                    CommonQueryConditionsBean conditionsBean = queryConditionsBeanList.get(i);
                    if (conditionsBean != null) {
                        PropertyTypeVO typeVO = propertyTypes.get(conditionsBean.getPropertyName());
                        if (typeVO != null) {
                            FilteringItem filteringItem = parseCommonQueryCondition(conditionsBean, typeVO);
                            if (i == 0) {
                                exploreParameters.setDefaultFilteringItem(filteringItem);
                            } else {
                                exploreParameters.addFilteringItem(filteringItem,
                                        queryConditionsBeanList.get(i).getFilterLogical());
                            }
                        } else {
                            log.info("property of {} not found", conditionsBean.getPropertyName());
                        }
                    } else {
                        log.error("query condition bean is null");
                    }
                }
            } else {
                log.error("Object type of {} does not contain any property", objectTypeId);
            }
        }

        return exploreParameters;

    }


    public static ExploreParameters parserQueryInput(CimDataSpace cds, String objectTypeId,
                                                     InstancesQueryInput queryInput,
                                                     boolean isIncludeCollectionDataSet) throws InputErrorException {
        //NOSONAR
        ExploreParameters exploreParameters = null;
        if (queryInput != null) {
            exploreParameters = new ExploreParameters();
            if (queryInput.getStartPage() < 1) {
                exploreParameters.setStartPage(1);
            } else {
                exploreParameters.setStartPage(queryInput.getStartPage());
            }

            if (queryInput.getEndPage() < 1) {
                exploreParameters.setEndPage(2);
            } else {
                exploreParameters.setEndPage(queryInput.getEndPage());
            }

            if (queryInput.getPageSize() < 1) {
                exploreParameters.setPageSize(50);
            } else {
                exploreParameters.setPageSize(queryInput.getPageSize());
            }

            if (StringUtils.isNotBlank(queryInput.getSqlWhereCondition())) {
                log.info("raw where condition query: [{}]", queryInput.getSqlWhereCondition());
                if (SqlUtil.isContainSensiveKeyWords(queryInput.getSqlWhereCondition())) {
                    throw new InputErrorException("sqlWhereCondistion contain sensitive key word");
                } else {
                    exploreParameters.setSqlWhereCondition(queryInput.getSqlWhereCondition());
                }
            } else {
                List<CommonQueryConditionsBean> queryConditionsBeanList = queryInput.getConditions();
                if (queryConditionsBeanList != null && queryConditionsBeanList.size() > 0) {
                    Map<String, PropertyTypeVO> propertyTypes = getPropertiesByObjectType(cds, objectTypeId,
                            isIncludeCollectionDataSet);
                    if (propertyTypes.size() > 0) {
                        for (int i = 0; i < queryConditionsBeanList.size(); i++) {
                            CommonQueryConditionsBean conditionsBean = queryConditionsBeanList.get(i);
                            if (conditionsBean != null) {
                                PropertyTypeVO typeVO = propertyTypes.get(conditionsBean.getPropertyName());
                                if (typeVO != null) {
                                    FilteringItem filteringItem = parseCommonQueryCondition(conditionsBean, typeVO);
                                    if (i == 0) {
                                        exploreParameters.setDefaultFilteringItem(filteringItem);
                                    } else {
                                        exploreParameters.addFilteringItem(filteringItem,
                                                queryConditionsBeanList.get(i).getFilterLogical());
                                    }
                                } else {
                                    log.info("property of {} not found", conditionsBean.getPropertyName());
                                }
                            } else {
                                log.error("query condition bean is null");
                            }
                        }
                    } else {
                        log.error("Object type of {} does not contain any property", objectTypeId);
                    }
                }
            }

            if (queryInput.getSortAttributes() != null && queryInput.getSortAttributes().size() > 0 && queryInput.getSortingLogic() != null) {
                exploreParameters.setSortAttributes(queryInput.getSortAttributes());
                exploreParameters.setSortingLogic(queryInput.getSortingLogic());
            }
        }
        return exploreParameters;
    }


    public static ExploreParameters parserDimensionQueryInput(CimDataSpace cds, String dimensionType,
                                                              InstancesQueryInput queryInput) {//NOSONAR
        ExploreParameters exploreParameters = null;
        if (queryInput != null) {
            exploreParameters = new ExploreParameters();
            exploreParameters.setType(dimensionType);
            if (queryInput.getStartPage() < 1) {
                exploreParameters.setStartPage(1);
            } else {
                exploreParameters.setStartPage(queryInput.getStartPage());
            }

            if (queryInput.getEndPage() < 1) {
                exploreParameters.setEndPage(2);
            } else {
                exploreParameters.setEndPage(queryInput.getEndPage());
            }

            if (queryInput.getPageSize() < 1) {
                exploreParameters.setPageSize(50);
            } else {
                exploreParameters.setPageSize(queryInput.getPageSize());
            }

            List<CommonQueryConditionsBean> queryConditionsBeanList = queryInput.getConditions();
            if (queryConditionsBeanList != null && queryConditionsBeanList.size() > 0) {
                Map<String, PropertyTypeVO> propertyTypes = getPropertiesByDimensionType(cds, dimensionType);
                if (propertyTypes.size() > 0) {
                    for (int i = 0; i < queryConditionsBeanList.size(); i++) {
                        CommonQueryConditionsBean conditionsBean = queryConditionsBeanList.get(i);
                        if (conditionsBean != null) {
                            PropertyTypeVO typeVO = propertyTypes.get(conditionsBean.getPropertyName());
                            if (typeVO != null) {
                                FilteringItem filteringItem = parseCommonQueryCondition(conditionsBean, typeVO);
                                if (i == 0) {
                                    exploreParameters.setDefaultFilteringItem(filteringItem);
                                } else {
                                    exploreParameters.addFilteringItem(filteringItem,
                                            queryConditionsBeanList.get(i).getFilterLogical());
                                }
                            } else {
                                log.info("property of {} not found", conditionsBean.getPropertyName());
                            }
                        } else {
                            log.error("query condition bean is null");
                        }
                    }
                } else {
                    log.error("Object type of {} does not contain any property", dimensionType);
                }
            }

            if (queryInput.getSortAttributes() != null && queryInput.getSortAttributes().size() > 0 && queryInput.getSortingLogic() != null) {
                exploreParameters.setSortAttributes(queryInput.getSortAttributes());
                exploreParameters.setSortingLogic(queryInput.getSortingLogic());
            }
        }
        return exploreParameters;
    }

    private static FilteringItem parseCommonQueryCondition(CommonQueryConditionsBean conditionsBean,
                                                           PropertyTypeVO propertyTypeVO) {
        FilteringItem filteringItem = null;
        String propertyName = conditionsBean.getPropertyName();
        try {
            // PropertyTypeVO propertyTypeVO = getPropertyTypeByName(cds, objectTypeId, conditionsBean
            // .getPropertyName(), isIncludeCollectionDataSet);
            String filterType = conditionsBean.getFilterType();
            String propertyType = propertyTypeVO.getPropertyFieldDataClassify();
            if (StringUtils.isBlank(propertyName) || StringUtils.isBlank(propertyType)) {
                log.error("Property type is not definied, propertyName={}", propertyName);
                throw new PropertyTypeException(EnumWrapper.CodeAndMsg.E05040004);
            }
            String firstParm = conditionsBean.getFirstParam();
            String secondParm = conditionsBean.getSecondParam();
            List<String> listParam = conditionsBean.getListParam();
            switch (filterType) {
                case "BetweenFilteringItem":
                    filteringItem = new BetweenFilteringItem(propertyName,
                            QueryConditionsUtil.strToObject(propertyType, firstParm),
                            QueryConditionsUtil.strToObject(propertyType, secondParm));
                    break;
                case "EqualFilteringItem":
                    filteringItem = new EqualFilteringItem(propertyName, QueryConditionsUtil.strToObject(propertyType
                            , firstParm));
                    break;
                case "GreaterThanFilteringItem":
                    filteringItem = new GreaterThanFilteringItem(propertyName,
                            QueryConditionsUtil.strToObject(propertyType, firstParm));
                    break;
                case "GreaterThanEqualFilteringItem":
                    filteringItem = new GreaterThanEqualFilteringItem(propertyName,
                            QueryConditionsUtil.strToObject(propertyType, firstParm));
                    break;
                case "LessThanFilteringItem":
                    filteringItem = new LessThanFilteringItem(propertyName,
                            QueryConditionsUtil.strToObject(propertyType, firstParm));
                    break;
                case "LessThanEqualFilteringItem":
                    filteringItem = new LessThanEqualFilteringItem(propertyName,
                            QueryConditionsUtil.strToObject(propertyType, firstParm));
                    break;
                case "NotEqualFilteringItem":
                    filteringItem = new NotEqualFilteringItem(propertyName,
                            QueryConditionsUtil.strToObject(propertyType, firstParm));
                    break;
                case "SimilarFilteringItem":
                    filteringItem = new SimilarFilteringItem(propertyName, firstParm,
                            SimilarFilteringItem.MatchingType.Contain);
                    break;
                case "InValueFilteringItem":
                    filteringItem = new InValueFilteringItem(propertyName,
                            QueryConditionsUtil.listStrToObject(propertyType, listParam));
                    break;
                default:
                    log.error("not support filter type");
                    break;
            }
        } catch (PropertyTypeException e) {
            e.printStackTrace();
        }
        return filteringItem;
    }

    private static Map<String, PropertyTypeVO> getPropertiesByObjectType(CimDataSpace cimDataSpace,
                                                                         String objectTypeId,
                                                                         boolean isIncludeCollectionDataSet) {
        Map<String, PropertyTypeVO> properTypes = new HashMap<>();
        try {
            InfoObjectTypeVO targetObjectType = InfoObjectFeatures.getInfoObjectTypeVOByType(cimDataSpace,
                    objectTypeId, true, true);
            List<DatasetVO> linkedDataset = targetObjectType.getLinkedDatasets();
            DatasetVO baseDatasetVO = DatasetFeatures.getBaseDatasetVO(cimDataSpace);
            if (linkedDataset != null && baseDatasetVO != null) {
                linkedDataset.add(baseDatasetVO);
            } else {
                linkedDataset = new ArrayList<>();
                linkedDataset.add(baseDatasetVO);
            }
            List<PropertyTypeVO> mergedPropertyTypes =
                    InfoObjectFeatures.mergePropertyTypesFromDatasets(linkedDataset, isIncludeCollectionDataSet);
            if (mergedPropertyTypes != null && mergedPropertyTypes.size() > 0) {
                for (PropertyTypeVO typeVO : mergedPropertyTypes) {
                    if (typeVO != null) {
                        properTypes.put(typeVO.getPropertyTypeName(), typeVO);
                    }
                }
            }
        } catch (Exception e) {
            log.error("get properties by object type failed", e);
        }
        return properTypes;
    }

    private static Map<String, PropertyTypeVO> getPropertiesByDimensionType(CimDataSpace cds, String dimensionType) {
        Map<String, PropertyTypeVO> properTypes = new HashMap<>();
        try {
            DatasetVO datasetVO = GlobalDimensionFeatures.getLinkedDataset(cds, dimensionType);
            Assert.notNull(datasetVO, "no dataset linked with this dimesion");
            List<PropertyTypeVO> propertyTypes = datasetVO.getLinkedPropertyTypes();
            if (propertyTypes != null && propertyTypes.size() > 0) {
                for (PropertyTypeVO typeVO : propertyTypes) {
                    if (typeVO != null) {
                        properTypes.put(typeVO.getPropertyTypeName(), typeVO);
                    }
                }
            }
        } catch (Exception e) {
            log.error("get properties by object type failed", e);
        }
        log.info("property of {}: [{}]", dimensionType, JSON.toJSONString(properTypes));
        return properTypes;
    }

}
