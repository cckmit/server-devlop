package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters.FilteringLogic;
import com.glodon.pcop.cimsvc.model.QueryConditionsBean;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionDatasetServiceTest {

    @Test
    public void queryParameters() {
        String infogObjectType = "obj01";
        String instanceId = "instanceId01";
        List<QueryConditionsBean> queryConditionsBeanList = new ArrayList<>();

        QueryConditionsBean bean = new QueryConditionsBean();
        bean.setFilterType("EqualFilteringItem");
        bean.setFilterLogical(FilteringLogic.AND);

        Map<String, Object> map = new HashMap<>();
        map.put("attributeName", "nm1");
        map.put("attributeValue", "vl2");
//        bean.setFilterAttribute(map);

        queryConditionsBeanList.add(bean);

        CollectionDatasetService cds = new CollectionDatasetService();
//        cds.queryParameters(infogObjectType, instanceId, queryConditionsBeanList);

    }
}