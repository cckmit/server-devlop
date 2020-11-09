package com.glodon.pcop.cimsvc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.InputInstancesBean;
import com.glodon.pcop.cimsvc.model.InstanceBean;
import com.glodon.pcop.cimsvc.model.QueryConditionsBean;
import com.glodon.pcop.cimsvc.model.QueryInputBean;
import com.glodon.pcop.cimsvc.util.QueryConditionsUtil;
import com.google.gson.Gson;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectInstanceServiceTest {

    private static ObjectInstanceService instanceService;
    private static String tenantId;
    private static String objectTypeId;
    private static String dataSetId = "#140:35";
    private static String propertyId;
    private static String restrictId;
    private static String dataSetName = "第一个集合属性集";
    private static String instanceRid = "#1579:0";
    private static Gson gson = new Gson();

    // @BeforeClass
    public static void init() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "yuanjk";
//        CacheUtil.cacheManagerInit();
        instanceService = new ObjectInstanceService();
        tenantId = "1";
//        objectTypeId = "test_instance_query";
        objectTypeId = "test_instance_query";
    }

    // @Test
    public void getDataSetDef() throws DataServiceModelRuntimeException, EntityNotFoundException {
        List<DatasetVO> datasetVOList = instanceService.getDataSetDef(tenantId, objectTypeId, "");
        System.out.println(gson.toJson(datasetVOList));
    }

    // @Test
    public void queryInstanceSingle() throws DataServiceModelRuntimeException, EntityNotFoundException, JsonProcessingException {
        QueryInputBean queryInputBean = getQueryInputBean();
        List<Map<String, Map<String, Object>>> result = instanceService.queryInstanceSingle(tenantId, objectTypeId, "第一个属性集", queryInputBean);

        System.out.println(gson.toJson(result));
    }

    //    @Test
    public void addInstanceSingle() throws DataServiceModelRuntimeException, EntityNotFoundException {
        Map<String, Object> generalMap = new HashMap<>();
        generalMap.put("pro1", "pro1_03");
        generalMap.put("ds2_pro1", "ds_pro1_03");

        Map<String, Object> baseMap = new HashMap<>();
        baseMap.put("objecttypeID", "1");

        InfoObjectValue infoObjectValue = new InfoObjectValue();
        infoObjectValue.setBaseDatasetPropertiesValue(baseMap);
        infoObjectValue.setGeneralDatasetsPropertiesValue(generalMap);

        List<InfoObjectValue> infoObjectValueList = new ArrayList<>();
        infoObjectValueList.add(infoObjectValue);

        instanceService.addInstanceSingle(tenantId, objectTypeId, infoObjectValueList, false);
    }

    // @Test
    public void addInstanceCollection() throws DataServiceModelRuntimeException, EntityNotFoundException {

        List<Map<String, Object>> collectionDatasetValues = new ArrayList<>();

        Map<String, Object> map1 = new HashMap<>();
        map1.put("cl_ds1_pro1", "r1_p1");
        map1.put("cl_ds1_pro2", "r1_p2");
        map1.put("cl_ds1_pro3", "r1_p3");
        collectionDatasetValues.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("cl_ds1_pro1", "r2_p1");
        map2.put("cl_ds1_pro2", "r2_p2");
        map2.put("cl_ds1_pro3", "r2_p3");
        collectionDatasetValues.add(map2);

        System.out.println(gson.toJson(instanceService.addInstanceCollection(tenantId, objectTypeId, instanceRid, dataSetName, collectionDatasetValues)));

    }

    // @Test
    public void queryInstanceCollection() throws DataServiceModelRuntimeException, EntityNotFoundException {
        QueryInputBean queryInputBean = getQueryInputBean();

        List<QueryConditionsBean> conditionsBeanList = queryInputBean.getConditions();
        QueryConditionsBean conditionsBean = conditionsBeanList.get(0);
        conditionsBean.setPropertyName("cl_ds1_pro1");
        conditionsBean.setFirstParam("r2_p1");

        System.out.println(gson.toJson(instanceService.queryInstanceCollection(tenantId, objectTypeId, instanceRid, dataSetName, queryInputBean)));
    }


    //    @Test
    public void getDataSetVoByRid() {
        DatasetVO datasetVO = DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName, "#140:29", true);
        System.out.println("With property: " + gson.toJson(datasetVO));

        datasetVO = DatasetFeatures.getDatasetVOById(CimConstants.defauleSpaceName, "#140:29", false);
        System.out.println("Without property: " + gson.toJson(datasetVO));
    }

    //    @Test
    public void getInput() {
        InstanceBean bean = new InstanceBean();
        bean.setObjectTypeId("projectV1");
        Map<String, Object> value = new HashMap<>();
        value.put("#161:14", "1");
        bean.setPropertyValue(value);

        InputInstancesBean instancesBean = new InputInstancesBean();
        List<InstanceBean> instanceBeanList = new ArrayList<>();
        instanceBeanList.add(bean);
        instancesBean.setInstances(instanceBeanList);

        System.out.println("Without property: " + gson.toJson(instancesBean));
    }

    // @Test
    public void conditionParser() {
        QueryConditionsBean conditionsBean = new QueryConditionsBean();
        conditionsBean.setFilterType("EqualFilteringItem");
        conditionsBean.setFilterLogical(ExploreParameters.FilteringLogic.AND);
        conditionsBean.setPropertyName("pro1");
        conditionsBean.setFirstParam("pro1_01");

        System.out.println(gson.toJson(QueryConditionsUtil.parseQueryCondition(objectTypeId, conditionsBean, true)));
    }

    private QueryInputBean getQueryInputBean() {
        QueryConditionsBean conditionsBean = new QueryConditionsBean();
        conditionsBean.setFilterType("EqualFilteringItem");
        conditionsBean.setFilterLogical(ExploreParameters.FilteringLogic.AND);
        conditionsBean.setPropertyName("pro1");
        conditionsBean.setFirstParam("pro1_03");

        List<QueryConditionsBean> queryConditionsBeanList = new ArrayList<>();
        queryConditionsBeanList.add(conditionsBean);

        QueryInputBean queryInputBean = new QueryInputBean();
        queryInputBean.setPageSize(10);
        queryInputBean.setStartPage(1);
        queryInputBean.setEndPage(2);
        queryInputBean.setConditions(queryConditionsBeanList);
        return queryInputBean;
    }


    // @Test
    public void countByObjectType() {
        List<String> objectTypeIds = new ArrayList<>();
        objectTypeIds.add(objectTypeId);
        objectTypeIds.add("AloneLampdev");
        objectTypeIds.add("workType");
        objectTypeIds.add("LampPostDev");

        System.out.println(instanceService.countByObjectType(tenantId, objectTypeIds));

    }

}