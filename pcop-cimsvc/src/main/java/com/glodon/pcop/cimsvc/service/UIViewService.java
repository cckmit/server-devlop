package com.glodon.pcop.cimsvc.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.UIViewFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.UIViewVo;
import com.glodon.pcop.cimsvc.model.PropertyBean;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import com.glodon.pcop.cimsvc.model.v2.DimensionQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.InstancesQueryInput;
import com.glodon.pcop.cimsvc.util.condition.QueryConditionParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIViewService {
    private static Logger log = LoggerFactory.getLogger(UIViewService.class);
    private static ObjectMapper objectMapper =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static final String UI_VIEW = "UI_VIEW";

    private static Map<String, String> keyMapping = new HashMap<>();

    static {
        keyMapping.put("info_object_type_name", "infoObjectTypeName");
        keyMapping.put("view_id", "viewId");
        keyMapping.put("view_data", "viewData");
        keyMapping.put("view_sql", "viewSql");
    }

    public static Map<String, String> getOutputProperty(String dataSpaceName, String viewId) {
        Map<String, String> propertyMap = new HashMap<>();

        UIViewVo viewVo = UIViewFeatures.getUIViewById(dataSpaceName, viewId);
        if (viewVo == null || StringUtils.isBlank(viewVo.getViewData())) {
            log.info("ui view of viewId={} not found", viewId);
            return propertyMap;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(viewVo.getViewData());
            if (!jsonNode.has("properties")) {
                log.info("properties is not found in viewData 0f viewId={}", viewId);
                return propertyMap;
            }
            List<PropertyBean> propertyBeans = objectMapper.readValue(jsonNode.get("properties").traverse(),
                    new TypeReference<List<PropertyBean>>() {
                    });
            if (propertyBeans != null) {
                log.info("propertyBeans size is: {}", propertyBeans.size());
                for (PropertyBean bean : propertyBeans) {
                    log.info("id={}, name={}", bean.getId(), bean.getName());
                    propertyMap.put(bean.getId(), bean.getName());
                }
            } else {
                log.info("propertyBeans is null");
            }
        } catch (IOException e) {
            log.error("view data parser failed, viewId={}", viewId);
            e.printStackTrace();
        }

        return propertyMap;
    }

    /**
     * 按对象类型查询UI view
     *
     * @param objectTypeId
     * @return
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     */
    public static List<UIViewVo> queryUIViewByObjectType(String objectTypeId) throws CimDataEngineRuntimeException,
            CimDataEngineInfoExploreException {
        List<UIViewVo> viewVoList = new ArrayList<>();
        if (StringUtils.isBlank(objectTypeId)) {
            log.error("input objectTypeId is empty");
            return viewVoList;
        }
        FilteringItem filteringItem =
                new EqualFilteringItem(BusinessLogicConstant.UI_VIEW_DIMENSION_TYPE_FIELDNAME_INFOOBJECTTYPENAME,
                        objectTypeId.trim());

        ExploreParameters exploreParameters = new ExploreParameters();
        exploreParameters.setType(BusinessLogicConstant.UI_VIEW_DIMENSION_TYPE_NAME);
        exploreParameters.setDefaultFilteringItem(filteringItem);

        return UIViewFeatures.queryUIViews(CimConstants.defauleSpaceName, exploreParameters);
    }

    public static DimensionQueryOutput queryUIView(InstancesQueryInput queryConditions) {
        DimensionQueryOutput queryOutput = new DimensionQueryOutput();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            List<CommonQueryConditionsBean> commonQueryConditionsBeans = queryConditions.getConditions();
            for (CommonQueryConditionsBean conditionsBean : commonQueryConditionsBeans) {
                String propertyName = conditionsBean.getPropertyName();
                if (keyMapping.containsKey(propertyName)) {
                    conditionsBean.setPropertyName(keyMapping.get(propertyName));
                } else {
                    log.error("error property name: {}", propertyName);
                }
            }

            ExploreParameters exploreParameters = QueryConditionParser.parserDimensionQueryInput(cds,
                    BusinessLogicConstant.UI_VIEW_DIMENSION_TYPE_NAME, queryConditions);
            queryOutput.setDimensions(UIViewFeatures.queryUIViews(cds, exploreParameters));
            queryOutput.setTotalCount(cds.getInformationExplorer().countDimensions(exploreParameters));
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return queryOutput;
    }

}
