package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.PropertyTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cimsvc.exception.EntityAddException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DataEngineService {
    private static Logger log = LoggerFactory.getLogger(DataEngineService.class);

    /**
     * 新增属性
     *
     * @param cimSpaceName
     * @param propertyTypeVO
     * @return
     */
    public static PropertyTypeVO addPropertyType(String cimSpaceName, PropertyTypeVO propertyTypeVO) {
        PropertyTypeVO typeVO = PropertyTypeFeatures.addPropertyType(cimSpaceName, propertyTypeVO);
        return typeVO;
    }


    /**
     * 新增属性集及其关联的属性
     *
     * @param cimSpaceName
     * @param datasetVO
     */
    public static DatasetVO addDataSet(String cimSpaceName, DatasetVO datasetVO) {
        DatasetVO dsVO = DatasetFeatures.addDataset(cimSpaceName, datasetVO);
        List<PropertyTypeVO> propertyTypeVOList = datasetVO.getLinkedPropertyTypes();
        if (propertyTypeVOList != null) {
            for (PropertyTypeVO propertyTypeVO : propertyTypeVOList) {
                PropertyTypeVO typeVO = addPropertyType(cimSpaceName, propertyTypeVO);
                if (typeVO != null) {
                    DatasetFeatures.addPropertyTypeLink(cimSpaceName, dsVO.getDatasetId(), typeVO.getPropertyTypeId());
                }
            }
        }
        return dsVO;
    }

}
