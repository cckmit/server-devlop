package com.glodon.pcop.cimsvc.util;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.SupportPropertyTypes;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.*;//NOSONAR
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cimsvc.exception.PropertyTypeException;
import com.glodon.pcop.cimsvc.model.QueryConditionsBean;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryConditionsUtil {
    private static final Logger log = LoggerFactory.getLogger(QueryConditionsUtil.class);

    public static Map<String, Class> filterTypeMap = new HashMap<>();

    private static final String BETWEEN_FILTERING_ITEM = "BetweenFilteringItem";
    private static final String EQUAL_FILTERING_ITEM = "EqualFilteringItem";
    private static final String GREATER_THAN_FILTERING_ITEM = "GreaterThanFilteringItem";
    private static final String GREATER_THAN_EQUAL_FILTERING_ITEM = "GreaterThanEqualFilteringItem";
    private static final String IN_VALUE_FILTERING_ITEM = "InValueFilteringItem";
    private static final String LESS_THAN_FILTERING_ITEM = "LessThanFilteringItem";

    private static final String LESS_THAN_EQUAL_FILTERING_ITEM = "LessThanEqualFilteringItem";
    private static final String NOT_EQUAL_FILTERING_ITEM = "NotEqualFilteringItem";
    private static final String SIMILAR_FILTERING_ITEM = "SimilarFilteringItem";

    static {
        filterTypeMap.put(BETWEEN_FILTERING_ITEM, BetweenFilteringItem.class);
        filterTypeMap.put(EQUAL_FILTERING_ITEM, EqualFilteringItem.class);
        filterTypeMap.put(GREATER_THAN_FILTERING_ITEM, GreaterThanFilteringItem.class);
        filterTypeMap.put(GREATER_THAN_EQUAL_FILTERING_ITEM, GreaterThanEqualFilteringItem.class);
        filterTypeMap.put(IN_VALUE_FILTERING_ITEM, InValueFilteringItem.class);
        filterTypeMap.put(LESS_THAN_FILTERING_ITEM, LessThanFilteringItem.class);
        filterTypeMap.put(LESS_THAN_EQUAL_FILTERING_ITEM, LessThanEqualFilteringItem.class);
        filterTypeMap.put(NOT_EQUAL_FILTERING_ITEM, NotEqualFilteringItem.class);
//        filterTypeMap.put("NullValueFilteringItem", NullValueFilteringItem.class);
//        filterTypeMap.put("RegularMatchFilteringItem", RegularMatchFilteringItem.class);
        filterTypeMap.put(SIMILAR_FILTERING_ITEM, SimilarFilteringItem.class);
    }

    public static FilteringItem parseQueryCondition(String objectTypeId, QueryConditionsBean conditionsBean,
                                                    boolean isIncludeCollectionDataSet) {
        FilteringItem filteringItem = null;
        CimDataSpace cds = null;
        String propertyName = conditionsBean.getPropertyName();
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            PropertyTypeVO propertyTypeVO = getPropertyTypeByName(cds, objectTypeId, conditionsBean.getPropertyName()
                    , isIncludeCollectionDataSet);
            String filterType = conditionsBean.getFilterType();
            String propertyType = propertyTypeVO.getPropertyFieldDataClassify();
            if (StringUtils.isBlank(propertyName) || StringUtils.isBlank(propertyType)) {
                log.error("Property type is not definied, propertyName={}", propertyName);
                throw new PropertyTypeException(EnumWrapper.CodeAndMsg.E05040004);
            }
            String firstParm = conditionsBean.getFirstParam();
            String secondParm = conditionsBean.getSecondParam();
            // List<String> listParam = conditionsBean.getListParam();
            switch (filterType) {
                case BETWEEN_FILTERING_ITEM:
                    filteringItem = new BetweenFilteringItem(propertyName, strToObject(propertyType, firstParm),
                            strToObject(propertyType, secondParm));
                    break;
                case EQUAL_FILTERING_ITEM:
                    filteringItem = new EqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                    break;
                case GREATER_THAN_FILTERING_ITEM:
                    filteringItem = new GreaterThanFilteringItem(propertyName, strToObject(propertyType, firstParm));
                    break;
                case GREATER_THAN_EQUAL_FILTERING_ITEM:
                    filteringItem = new GreaterThanEqualFilteringItem(propertyName, strToObject(propertyType,
                            firstParm));
                    break;
                case LESS_THAN_FILTERING_ITEM:
                    filteringItem = new LessThanFilteringItem(propertyName, strToObject(propertyType, firstParm));
                    break;
                case LESS_THAN_EQUAL_FILTERING_ITEM:
                    filteringItem = new LessThanEqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                    break;
                case NOT_EQUAL_FILTERING_ITEM:
                    filteringItem = new NotEqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                    break;
                case SIMILAR_FILTERING_ITEM:
                    filteringItem = new SimilarFilteringItem(propertyName, firstParm,
                            SimilarFilteringItem.MatchingType.Contain);
                    break;
                // case IN_VALUE_FILTERING_ITEM:
                //     filteringItem = new InValueFilteringItem(propertyName, listStrToObject(propertyType, listParam));
                //     break;
                default:
                    break;
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (PropertyTypeException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return filteringItem;
    }

    public static FilteringItem parseCommonQueryCondition(String objectTypeId,
                                                          CommonQueryConditionsBean conditionsBean,
                                                          boolean isIncludeCollectionDataSet) {
        FilteringItem filteringItem = null;
        CimDataSpace cds = null;
        String propertyName = conditionsBean.getPropertyName();
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            PropertyTypeVO propertyTypeVO = getPropertyTypeByName(cds, objectTypeId, conditionsBean.getPropertyName()
                    , isIncludeCollectionDataSet);
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
                case BETWEEN_FILTERING_ITEM:
                    filteringItem = new BetweenFilteringItem(propertyName, strToObject(propertyType, firstParm),
                            strToObject(propertyType, secondParm));
                    break;
                case EQUAL_FILTERING_ITEM:
                    filteringItem = new EqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                    break;
                case GREATER_THAN_FILTERING_ITEM:
                    filteringItem = new GreaterThanFilteringItem(propertyName, strToObject(propertyType, firstParm));
                    break;
                case GREATER_THAN_EQUAL_FILTERING_ITEM:
                    filteringItem = new GreaterThanEqualFilteringItem(propertyName, strToObject(propertyType,
                            firstParm));
                    break;
                case LESS_THAN_FILTERING_ITEM:
                    filteringItem = new LessThanFilteringItem(propertyName, strToObject(propertyType, firstParm));
                    break;
                case LESS_THAN_EQUAL_FILTERING_ITEM:
                    filteringItem = new LessThanEqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                    break;
                case NOT_EQUAL_FILTERING_ITEM:
                    filteringItem = new NotEqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                    break;
                case SIMILAR_FILTERING_ITEM:
                    filteringItem = new SimilarFilteringItem(propertyName, firstParm,
                            SimilarFilteringItem.MatchingType.Contain);
                    break;
                case IN_VALUE_FILTERING_ITEM:
                    filteringItem = new InValueFilteringItem(propertyName, listStrToObject(propertyType, listParam));
                    break;
                default:
                    log.error("not support filter type");
                    break;
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (PropertyTypeException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return filteringItem;
    }

    public static FilteringItem parseStringQueryCondition(CommonQueryConditionsBean conditionsBean) {
        FilteringItem filteringItem = null;
        String propertyName = conditionsBean.getPropertyName();
        String filterType = conditionsBean.getFilterType();
        String firstParm = conditionsBean.getFirstParam();
        String secondParm = conditionsBean.getSecondParam();
        List<String> listParam = conditionsBean.getListParam();
        switch (filterType) {
            case BETWEEN_FILTERING_ITEM:
                filteringItem = new BetweenFilteringItem(propertyName, firstParm, secondParm);
                break;
            case EQUAL_FILTERING_ITEM:
                filteringItem = new EqualFilteringItem(propertyName, firstParm);
                break;
            case GREATER_THAN_FILTERING_ITEM:
                filteringItem = new GreaterThanFilteringItem(propertyName, firstParm);
                break;
            case GREATER_THAN_EQUAL_FILTERING_ITEM:
                filteringItem = new GreaterThanEqualFilteringItem(propertyName, firstParm);
                break;
            case LESS_THAN_FILTERING_ITEM:
                filteringItem = new LessThanFilteringItem(propertyName, firstParm);
                break;
            case LESS_THAN_EQUAL_FILTERING_ITEM:
                filteringItem = new LessThanEqualFilteringItem(propertyName, firstParm);
                break;
            case NOT_EQUAL_FILTERING_ITEM:
                filteringItem = new NotEqualFilteringItem(propertyName, firstParm);
                break;
            case SIMILAR_FILTERING_ITEM:
                filteringItem = new SimilarFilteringItem(propertyName, firstParm,
                        SimilarFilteringItem.MatchingType.Contain);
                break;
            case IN_VALUE_FILTERING_ITEM:
                filteringItem = new InValueFilteringItem(propertyName, listStrToObject(listParam));
                break;
            default:
                log.error("not support filter type");
                break;
        }
        return filteringItem;
    }

    /**
     * 解析搜索条件
     *
     * @param conditionsBean
     * @return
     * @throws PropertyTypeException
     * @throws CimDataEngineRuntimeException
     */
    @Deprecated
    public static FilteringItem parseFilterItem(QueryConditionsBean conditionsBean) throws PropertyTypeException,
            CimDataEngineRuntimeException {
        FilteringItem filteringItem = null;
        CimDataSpace cds = null;
        String propertyDefId = conditionsBean.getPropertyName();
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            Fact fact = cds.getFactById(propertyDefId);
            if (fact != null) {
                String filterType = conditionsBean.getFilterType();
                String propertyName =
                        fact.getProperty(CimConstants.PROPERTY_TYPE_CIM_BUILDIN_PROPERTYTYPE_NAME).getPropertyValue().toString();
                String propertyType =
                        fact.getProperty(CimConstants.PROPERTY_TYPE_CIM_BUILDIN_PROPERTYTYPE_DATACLASSIFY).getPropertyValue().toString();
                if (StringUtils.isBlank(propertyName) || StringUtils.isBlank(propertyType)) {
                    log.error("Property type is not definied, id={}", propertyDefId);
                    throw new PropertyTypeException(EnumWrapper.CodeAndMsg.E05040004);
                }
                String firstParm = conditionsBean.getFirstParam();
                String secondParm = conditionsBean.getSecondParam();
                switch (filterType) {
                    case BETWEEN_FILTERING_ITEM:
                        filteringItem = new BetweenFilteringItem(propertyName, strToObject(propertyType, firstParm),
                                strToObject(propertyType, secondParm));
                        break;
                    case EQUAL_FILTERING_ITEM:
                        filteringItem = new EqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                        break;
                    case GREATER_THAN_FILTERING_ITEM:
                        filteringItem = new GreaterThanFilteringItem(propertyName, strToObject(propertyType,
                                firstParm));
                        break;
                    case GREATER_THAN_EQUAL_FILTERING_ITEM:
                        filteringItem = new GreaterThanEqualFilteringItem(propertyName, strToObject(propertyType,
                                firstParm));
                        break;
                    case LESS_THAN_FILTERING_ITEM:
                        filteringItem = new LessThanFilteringItem(propertyName, strToObject(propertyType, firstParm));
                        break;
                    case LESS_THAN_EQUAL_FILTERING_ITEM:
                        filteringItem = new LessThanEqualFilteringItem(propertyName, strToObject(propertyType,
                                firstParm));
                        break;
                    case NOT_EQUAL_FILTERING_ITEM:
                        filteringItem = new NotEqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                        break;
                    case SIMILAR_FILTERING_ITEM:
                        filteringItem = new SimilarFilteringItem(propertyName, firstParm,
                                SimilarFilteringItem.MatchingType.Contain);
                        break;
                    default:
                        break;
                }
            } else {
                log.error("Property type is not definied, id={}", propertyDefId);
                throw new PropertyTypeException(EnumWrapper.CodeAndMsg.E05040004);
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return filteringItem;
    }

    /**
     * 将string转换为指定的数据类型
     *
     * @param type  数据类型
     * @param value
     * @return
     */
    public static Object strToObject(String type, String value) {
        Object object;
        switch (type) {
            case SupportPropertyTypes.INT:
                object = Integer.valueOf(value);
                break;
            case SupportPropertyTypes.SHORT:
                object = Short.valueOf(value);
                break;
            case SupportPropertyTypes.BYTE:
                object = Byte.valueOf(value);
                break;
            case SupportPropertyTypes.LONG:
                object = Long.valueOf(value);
                break;
            case SupportPropertyTypes.FLOAT:
                object = Float.valueOf(value);
                break;
            case SupportPropertyTypes.DOUBLE:
                object = Double.valueOf(value);
                break;
            case SupportPropertyTypes.DATE:
                object = new Date(Long.valueOf(value));
                break;
            case SupportPropertyTypes.STRING:
                object = value;
                break;
            case SupportPropertyTypes.BOOLEAN:
                object = Boolean.valueOf(value);
                break;
            case SupportPropertyTypes.BINARY:
                object = value;
                break;
            default:
                log.error("not support data type: {}", type);
                object = null;
        }
        return object;
    }

    /**
     * 根据名称获取对应的属性定义
     *
     * @param cimDataSpace
     * @param objectTypeId
     * @param propertyName
     * @return
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     */
    private static PropertyTypeVO getPropertyTypeByName(CimDataSpace cimDataSpace, String objectTypeId,
                                                        String propertyName, boolean isIncludeCollectionDataSet) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException, PropertyTypeException {
        PropertyTypeVO selectedPropertyTypeVO = null;
        InfoObjectTypeVO targetObjectType = InfoObjectFeatures.getInfoObjectTypeVOByType(cimDataSpace, objectTypeId,
                true, true);
        List<DatasetVO> linkedDataset = targetObjectType.getLinkedDatasets();
        DatasetVO baseDatasetVO = DatasetFeatures.getBaseDatasetVO(cimDataSpace);
        if (linkedDataset != null && baseDatasetVO != null) {
            linkedDataset.add(baseDatasetVO);
        }
        List<PropertyTypeVO> mergedPropertyTypes = InfoObjectFeatures.mergePropertyTypesFromDatasets(linkedDataset,
                isIncludeCollectionDataSet);
        if (mergedPropertyTypes != null && mergedPropertyTypes.size() > 0) {
            for (PropertyTypeVO typeVO : mergedPropertyTypes) {
                if (typeVO.getPropertyTypeName().equals(propertyName)) {
                    selectedPropertyTypeVO = typeVO;
                }
            }
        }
        if (selectedPropertyTypeVO == null) {
            String msg = String.format("Object type of %s does not contain property type of %s", objectTypeId,
                    propertyName);
            throw new PropertyTypeException(EnumWrapper.CodeAndMsg.E05040002, msg);
        }
        return selectedPropertyTypeVO;
    }

    public static List<Object> listStrToObject(String type, List<String> values) {
        List<Object> valuesFormal = new ArrayList<>();

        if (values != null) {
            for (String val : values) {
                valuesFormal.add(strToObject(type, val));
            }
        }

        return valuesFormal;
    }

    public static List<Object> listStrToObject(List<String> values) {
        List<Object> valuesFormal = new ArrayList<>();

        if (values != null) {
            for (String val : values) {
                valuesFormal.add(val);
            }
        }

        return valuesFormal;
    }

}
