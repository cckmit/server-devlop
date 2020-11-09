package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.SupportPropertyTypes;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.PropertyTypeClassification;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuanjk
 * @description 数据库操作封装工具
 * @date 2018/9/20 18:24
 */
public class OrientDBCommonUtil {
    private static Logger log = LoggerFactory.getLogger(OrientDBCommonUtil.class);

    /**
     * 默认时间格式
     */
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /**
     * 通过新模型获取对象模型定义
     *
     * @param tenantId
     * @param objectTypeId
     * @return
     * @throws EntityNotFoundException
     */
    public static InfoObjectDef getInfoObjectDef(String tenantId, String objectTypeId) throws EntityNotFoundException, DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(objectTypeId);
        if (infoObjectDef == null) {
            String msg = String.format("InfoObjectType is not definition: infoObjectTypeName=%s", objectTypeId);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        } else {
            return infoObjectDef;
        }
    }

    /**
     * 获取指定的对象实例
     *
     * @param tenantId
     * @param objectTypeId
     * @param instanceRid
     * @return
     * @throws EntityNotFoundException
     * @throws DataServiceModelRuntimeException
     */
    public static InfoObject getInfoObject(String tenantId, String objectTypeId, String instanceRid) throws EntityNotFoundException, DataServiceModelRuntimeException {
        InfoObject infoObject = getInfoObjectDef(tenantId, objectTypeId).getObject(instanceRid);
        if (infoObject == null) {
            String msg = String.format("InfoObject is not definition: objectTypeId=%s, instanceRid=%s", objectTypeId, instanceRid);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        } else {
            return infoObject;
        }
    }

    /**
     * 数据类型转换
     *
     * @param dataTypeMap
     * @param dataMap
     * @return
     */
    @Deprecated
    public static Map<String, Object> dataTypeCast(Map<String, String> dataTypeMap, Map<String, Object> dataMap, Boolean flag) {
        Map<String, Object> formalDataMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            String dataType = null;
            if (dataTypeMap.containsKey(entry.getKey())) {
                dataType = dataTypeMap.get(entry.getKey()).toUpperCase();
            }
            String valueStr = entry.getValue().toString();
            Object value = null;
            if (StringUtils.isNotBlank(dataType)) {
                PropertyTypeClassification typeClass = PropertyTypeClassification.valueOf(dataType);
                switch (typeClass) {
                    case BOOLEAN:
                        value = Boolean.valueOf(valueStr);
                        break;
                    case INT:
                        value = Integer.valueOf(valueStr);
                        break;
                    case SHORT:
                        value = Short.valueOf(valueStr);
                        break;
                    case LONG:
                        value = Long.valueOf(valueStr);
                        break;
                    case FLOAT:
                        value = Float.valueOf(valueStr);
                        break;
                    case DOUBLE:
                        value = Double.valueOf(valueStr);
                        break;
                    case DATE:
                        if (valueStr.matches("\\d*")) {
                            value = new Date(Long.valueOf(valueStr));
                        } else {
                            value = dateFormat.format(valueStr);
                        }
                        break;
                    case STRING:
                        value = valueStr;
                        break;
                    case BYTE:
                        value = Byte.valueOf(valueStr);
                        break;
                    case BINARY:
                        value = (byte[]) entry.getValue();
                        break;
                    default:
                        log.error("data type is not support currently!");
                        break;
                }
                formalDataMap.put(entry.getKey(), value);
            }
        }
        return formalDataMap;
    }

    public static Map<String, Object> valuesTypeCast(Map<String, String> dataTypeMap, Map<String, ? extends Object> dataMap) {
        Map<String, Object> formalDataMap = new HashMap<>();
        for (Map.Entry<String,? extends Object> entry : dataMap.entrySet()) {
            String dataType = null;
            if (dataTypeMap.containsKey(entry.getKey())) {
                dataType = dataTypeMap.get(entry.getKey()).toUpperCase();
            }
            String valueStr = entry.getValue().toString();
            // Object value = null;
            if (StringUtils.isNotBlank(dataType)) {
                Object value = dataTypeCast(dataType, valueStr);
                formalDataMap.put(entry.getKey(), value);
            }
        }
        return formalDataMap;
    }

    public static Object dataTypeCast(String type, String valueStr) {
        Object value;
        switch (type) {
            case SupportPropertyTypes.INT:
                value = Integer.valueOf(valueStr);
                break;
            case SupportPropertyTypes.SHORT:
                value = Short.valueOf(valueStr);
                break;
            case SupportPropertyTypes.BYTE:
                value = Byte.valueOf(valueStr);
                break;
            case SupportPropertyTypes.LONG:
                value = Long.valueOf(valueStr);
                break;
            case SupportPropertyTypes.FLOAT:
                value = Float.valueOf(valueStr);
                break;
            case SupportPropertyTypes.DOUBLE:
                value = Double.valueOf(valueStr);
                break;
            case SupportPropertyTypes.DATE:
                if (valueStr.matches("\\d*")) {
                    value = new Date(Long.valueOf(valueStr));
                } else {
                    value = dateFormat.format(valueStr);
                }
                break;
            case SupportPropertyTypes.STRING:
                value = valueStr;
                break;
            case SupportPropertyTypes.BOOLEAN:
                value = Boolean.valueOf(valueStr);
                break;
            case SupportPropertyTypes.BINARY:
                value = valueStr;
                break;
            default:
                log.error("not support data type: {}", type);
                value = null;
        }
        return value;
    }

    /**
     * 获取base属性集中的属性的数据类型定义
     *
     * @return
     * @throws EntityNotFoundException
     */
    public static Map<String, String> baseDataSetPropertyType() throws EntityNotFoundException {
        Map<String, String> dataTypeMap = new HashMap<>();
        DatasetVO baseDatasetVO = DatasetFeatures.getBaseDatasetVO(CimConstants.defauleSpaceName);
        if (baseDatasetVO != null) {
            List<PropertyTypeVO> propertyTypeVOList = baseDatasetVO.getLinkedPropertyTypes();
            if (propertyTypeVOList != null && propertyTypeVOList.size() > 0) {
                for (PropertyTypeVO propertyTypeVO : propertyTypeVOList) {
                    dataTypeMap.put(propertyTypeVO.getPropertyTypeName(), propertyTypeVO.getPropertyFieldDataClassify());
                }
            }
        } else {
            String msg = String.format("Base data set not found, dataset name=%s", BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME);
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
        return dataTypeMap;
    }

    public static Map<String, String> mergeDataTypeDef(List<DatasetVO> datasetVOList) {
        Map<String, String> dataTypes = new HashMap<>();

        if (datasetVOList != null && datasetVOList.size() > 0) {
            for (DatasetVO datasetVO : datasetVOList) {
                List<PropertyTypeVO> propertyTypeVOList = datasetVO.getLinkedPropertyTypes();
                if (propertyTypeVOList != null && propertyTypeVOList.size() > 0) {
                    for (PropertyTypeVO propertyTypeVO : propertyTypeVOList) {
                        dataTypes.put(propertyTypeVO.getPropertyTypeName(), propertyTypeVO.getPropertyFieldDataClassify());
                    }
                }
            }
        }

        return dataTypes;
    }

}
