package com.glodon.pcop.cimstatsvc.util;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.SupportPropertyTypes;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.*;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cimstatsvc.dao.DbExecute;
import com.glodon.pcop.cimstatsvc.model.QueryConditionsBean;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class QueryConditionsUtil {
    private static final Logger log = LoggerFactory.getLogger(QueryConditionsUtil.class);

    public static Map<String, Class> filterTypeMap = new HashMap<>();

    static {
        filterTypeMap.put("BetweenFilteringItem", BetweenFilteringItem.class);
        filterTypeMap.put("EqualFilteringItem", EqualFilteringItem.class);
        filterTypeMap.put("GreaterThanFilteringItem", GreaterThanFilteringItem.class);
        filterTypeMap.put("GreaterThanEqualFilteringItem", GreaterThanEqualFilteringItem.class);
        filterTypeMap.put("InValueFilteringItem", InValueFilteringItem.class);
        filterTypeMap.put("LessThanFilteringItem", LessThanFilteringItem.class);
        filterTypeMap.put("LessThanEqualFilteringItem", LessThanEqualFilteringItem.class);
        filterTypeMap.put("NotEqualFilteringItem", NotEqualFilteringItem.class);
        filterTypeMap.put("NullValueFilteringItem", NullValueFilteringItem.class);
//        filterTypeMap.put("RegularMatchFilteringItem", RegularMatchFilteringItem.class);
        filterTypeMap.put("SimilarFilteringItem", SimilarFilteringItem.class);
    }

    public static FilteringItem parseQueryCondition(CimDataSpace cimDataSpace,String objectTypeId, QueryConditionsBean conditionsBean) {
        FilteringItem filteringItem = null;
        CimDataSpace cds = null;
        String propertyName = conditionsBean.getPropertyName();
        PropertyTypeVO propertyTypeVO =  getPropertyTypeByName(cimDataSpace, objectTypeId, conditionsBean.getPropertyName());
        if (propertyTypeVO == null) {
            return null;
        }
        String filterType = conditionsBean.getFilterType();
        String propertyType = propertyTypeVO.getPropertyFieldDataClassify();
        if (StringUtils.isBlank(propertyName) || StringUtils.isBlank(propertyType)) {
            log.error("Property type is not definied, propertyName={}", propertyName);
//                throw new PropertyTypeException(EnumWrapper.CodeAndMsg.E05040004);
            return null;
        }
        String firstParm = conditionsBean.getFirstParm();
        String secondParm = conditionsBean.getSecondParm();
        switch (filterType) {
            case "NullValueFilteringItem":
                filteringItem = new NullValueFilteringItem(propertyName);
                break;
            case "BetweenFilteringItem":
                filteringItem = new BetweenFilteringItem(propertyName, strToObject(propertyType, firstParm), strToObject(propertyType, secondParm));
                break;
            case "EqualFilteringItem":
                filteringItem = new EqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                break;
            case "GreaterThanFilteringItem":
                filteringItem = new GreaterThanFilteringItem(propertyName, strToObject(propertyType, firstParm));
                break;
            case "GreaterThanEqualFilteringItem":
                filteringItem = new GreaterThanEqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                break;
            case "LessThanFilteringItem":
                filteringItem = new LessThanFilteringItem(propertyName, strToObject(propertyType, firstParm));
                break;
            case "LessThanEqualFilteringItem":
                filteringItem = new LessThanEqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                break;
            case "NotEqualFilteringItem":
                filteringItem = new NotEqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                break;
            case "SimilarFilteringItem":
                filteringItem = new SimilarFilteringItem(propertyName, firstParm, SimilarFilteringItem.MatchingType.Contain);
                break;
            case "InValueFilteringItem":
                if (conditionsBean.getListParm() != null) {
                    if (conditionsBean.getListParm().size() > 0) {
                        filteringItem = new InValueFilteringItem(propertyName, strsToObjects(propertyType, conditionsBean.getListParm()));
                        break;
                    }
                }
                if(conditionsBean.getInParms() != null) {
                    if (conditionsBean.getInParms().size() > 0) {
                        filteringItem = new InValueFilteringItem(propertyName, strsToObjects(propertyType, conditionsBean.getInParms()));
                    }
                }
                break;
            case "NotInValueFilteringItem":
                if (conditionsBean.getListParm() != null) {
                    if (conditionsBean.getListParm().size() > 0) {
                        filteringItem = new InValueFilteringItem(propertyName, strsToObjects(propertyType, conditionsBean.getListParm()));
                        break;
                    }
                }
                filteringItem = new InValueFilteringItem(propertyName, strsToObjects(propertyType, conditionsBean.getInParms()));
                filteringItem.reverseCondition();
                break;
            default:
                break;
        }

        return filteringItem;
    }

    /**
     * 解析搜索条件
     *
     * @param conditionsBean
     * @return
     * @throws CimDataEngineRuntimeException
     */
    @Deprecated
    public static FilteringItem parseFilterItem(QueryConditionsBean conditionsBean) {
        FilteringItem filteringItem = null;
        CimDataSpace cds = null;
        String propertyDefId = conditionsBean.getPropertyName();
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            Fact fact = null;
            try {
                fact = cds.getFactById(propertyDefId);
            } catch (CimDataEngineRuntimeException e) {
                return null;
            }
            if (fact != null) {
                String filterType = conditionsBean.getFilterType();
                String propertyName = fact.getProperty(CimConstants.PROPERTY_TYPE_CIM_BUILDIN_PROPERTYTYPE_NAME).getPropertyValue().toString();
                String propertyType = fact.getProperty(CimConstants.PROPERTY_TYPE_CIM_BUILDIN_PROPERTYTYPE_DATACLASSIFY).getPropertyValue().toString();
                if (StringUtils.isBlank(propertyName) || StringUtils.isBlank(propertyType)) {
                    log.error("Property type is not definied, id={}", propertyDefId);
                    return null;
                }
                String firstParm = conditionsBean.getFirstParm();
                String secondParm = conditionsBean.getSecondParm();
                switch (filterType) {
                    case "NullValueFilteringItem":
                        filteringItem = new NullValueFilteringItem(propertyName);
                        break;
                    case "BetweenFilteringItem":
                        filteringItem = new BetweenFilteringItem(propertyName, strToObject(propertyType, firstParm), strToObject(propertyType, secondParm));
                        break;
                    case "EqualFilteringItem":
                        filteringItem = new EqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                        break;
                    case "GreaterThanFilteringItem":
                        filteringItem = new GreaterThanFilteringItem(propertyName, strToObject(propertyType, firstParm));
                        break;
                    case "GreaterThanEqualFilteringItem":
                        filteringItem = new GreaterThanEqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                        break;
                    case "LessThanFilteringItem":
                        filteringItem = new LessThanFilteringItem(propertyName, strToObject(propertyType, firstParm));
                        break;
                    case "LessThanEqualFilteringItem":
                        filteringItem = new LessThanEqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                        break;
                    case "NotEqualFilteringItem":
                        filteringItem = new NotEqualFilteringItem(propertyName, strToObject(propertyType, firstParm));
                        break;
                    case "SimilarFilteringItem":
                        filteringItem = new SimilarFilteringItem(propertyName, firstParm, SimilarFilteringItem.MatchingType.Contain);
                        break;
                    case "InValueFilteringItem":
                        filteringItem = new InValueFilteringItem(propertyName, strsToObjects(propertyType, conditionsBean.getInParms()));
                        break;
                    case "NotInValueFilteringItem":
                        filteringItem = new InValueFilteringItem(propertyName, strsToObjects(propertyType, conditionsBean.getInParms()));
                        filteringItem.reverseCondition();
                        break;
                    default:
                        break;
                }
            } else {
                log.error("Property type is not definied, id={}", propertyDefId);
                return null;
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
     * @param type      数据类型
     * @param valueList
     * @return
     */
    private static List<Object> strsToObjects(String type, List<String> valueList) {
        List<Object> arrayList = new ArrayList<>();
        for (int i = 0; i < valueList.size(); i++) {
            arrayList.add(strToObject(type, valueList.get(i)));
        }
        return arrayList;
    }

    /**
     * 将string转换为指定的数据类型
     *
     * @param type  数据类型
     * @param value
     * @return
     */
    private static Object strToObject(String type, String value) {
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
    public static PropertyTypeVO getPropertyTypeByName(CimDataSpace cimDataSpace, String objectTypeId, String propertyName) {
        PropertyTypeVO selectedPropertyTypeVO = null;
        InfoObjectTypeVO targetObjectType = null;
        try {
            targetObjectType = InfoObjectFeatures.getInfoObjectTypeVOByType(cimDataSpace, objectTypeId, true, true);
        } catch (CimDataEngineRuntimeException e) {
            return null;
        } catch (CimDataEngineInfoExploreException e) {
            return null;
        }

        List<DatasetVO> linkedDataset = targetObjectType.getLinkedDatasets();
        DatasetVO baseDatasetVO = null;
        try {
            baseDatasetVO = DatasetFeatures.getBaseDatasetVO(cimDataSpace);
        } catch (CimDataEngineRuntimeException e) {
            return null;
        } catch (CimDataEngineInfoExploreException e) {
            return null;
        }
        if (linkedDataset != null && baseDatasetVO != null) {
            linkedDataset.add(baseDatasetVO);
        }
        List<PropertyTypeVO> mergedPropertyTypes = InfoObjectFeatures.mergePropertyTypesFromDatasets(linkedDataset, false);

        if (mergedPropertyTypes != null && mergedPropertyTypes.size() > 0) {
            for (PropertyTypeVO typeVO : mergedPropertyTypes) {
                if (typeVO.getPropertyTypeName().equals(propertyName)) {
                    selectedPropertyTypeVO = typeVO;
                }
            }
        }
        if (selectedPropertyTypeVO == null) {
            String msg = String.format("Object type of %s does not contain property type of %s", objectTypeId, propertyName);
            return null;
        }
        return selectedPropertyTypeVO;
    }
}
