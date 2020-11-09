package com.glodon.pcop.cimsvc.service.v2;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.BatchDataOperationResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import com.glodon.pcop.cimsvc.model.v2.DeleteByConditionInputBean;
import com.glodon.pcop.cimsvc.model.v2.InstancesQueryInput;
import com.glodon.pcop.cimsvc.model.v2.SingleQueryOutput;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@Ignore
public class InstancesServiceTest {
    private static String tenantId = "1";
    private static InstancesService instancesService;
    //    private static String objectTypeId = "yuanjk0409aa";
    private static String objectTypeId = "Booth";

    @Before
    public void setUp() throws Exception {
        CimConstants.defauleSpaceName = "pcopcim";
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        // PropertyHandler.map.put("location", "remote:10.0.197.168/");
        instancesService = new InstancesService();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void queryInstanceSingle() throws InputErrorException {
        InstancesQueryInput queryInput = new InstancesQueryInput();
        SingleQueryOutput queryResult = instancesService.queryInstanceSingle(tenantId, objectTypeId, "", queryInput);
        System.out.println("query result count: " + queryResult.getTotalCount());
    }

    @Test
    public void addInstanceSingle() {
        Map<String, Object> rd1 = new HashMap<>();
        rd1.put("NAME", "record 01");

        Map<String, Object> rd2 = new HashMap<>();
        rd2.put("NAME", "通过ID更新实例01");

        Map<String, Object> rd3 = new HashMap<>();
        rd3.put("NAME", "通过ID更新实例02");

        Map<String, Object> rd4 = new HashMap<>();
        rd4.put("NAME", "error input");

        List<Map<String, Object>> updateInput = new ArrayList<>();
        updateInput.add(rd1);
        updateInput.add(rd2);
        updateInput.add(rd3);
        updateInput.add(rd4);

//        instancesService.addInstanceSingle(tenantId, objectTypeId, instancesService.addSingleObjectValuesFormal(updateInput), false);

        List<InfoObjectValue> infoObjectValueList = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            InfoObjectValue currentInfoObjectValue = new InfoObjectValue();
            Map<String, Object> currentBaseDatasetData = new HashMap<>();
            currentBaseDatasetData.put(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME, "ObjectID_00A_" + i);
            currentBaseDatasetData.put(BusinessLogicConstant.NAME_PROPERTY_TYPE_NAME, "ObjectName_00A_" + i);
            currentInfoObjectValue.setBaseDatasetPropertiesValue(currentBaseDatasetData);

            Map<String, Object> currentGeneralDatasetData = new HashMap<>();
            currentGeneralDatasetData.put("Property1", "I am a string value " + i);
            currentGeneralDatasetData.put("Property2", 200000 + i);
            if (i % 2 == 0) {
                currentGeneralDatasetData.put("Property3", 0);
            } else {
                currentGeneralDatasetData.put("Property3", 1);
            }
            currentInfoObjectValue.setGeneralDatasetsPropertiesValue(currentGeneralDatasetData);
            infoObjectValueList.add(currentInfoObjectValue);
        }
        instancesService.addInstanceSingle(null, objectTypeId, infoObjectValueList, false);
    }

    @Test
    public void updateInstanceSingle() {
        Map<String, Object> rd1 = new HashMap<>();
        rd1.put(CimConstants.INPUT_INSTANCE_RID, "#4959:104");
        rd1.put("name_vi", "record 01");
        rd1.put("geom", "polygon");
        rd1.put("note", "通过RID更新实例v2");

        Map<String, Object> rd2 = new HashMap<>();
        rd2.put(CimConstants.ID_PROPERTY_TYPE_NAME, "847");
        rd2.put("note", "通过ID更新实例01");
        rd2.put("geom", "line");

        Map<String, Object> rd3 = new HashMap<>();
        rd3.put(CimConstants.ID_PROPERTY_TYPE_NAME, "855");
        rd3.put("note", "通过ID更新实例02");
        rd3.put("geom", "point");

        Map<String, Object> rd4 = new HashMap<>();
        rd4.put("note", "error input");
        rd4.put("geom", "point");

        List<Map<String, Object>> updateInput = new ArrayList<>();
        updateInput.add(rd1);
        updateInput.add(rd2);
        updateInput.add(rd3);
        updateInput.add(rd4);

        BatchDataOperationResult updateResult = instancesService.updateInstanceSingle(tenantId, objectTypeId,
                updateInput);

        System.out.println("update instance result: " + JSON.toJSONString(updateResult));
    }

    @Test
    public void deleteInstanceSingle() {
        List<String> instanceRids = Arrays.asList("#4959:100", "#4959:101", "823", "831");

        BatchDataOperationResult deleteResult = instancesService.deleteInstanceSingle(tenantId, objectTypeId,
                instanceRids);
        System.out.println("delete result: " + JSON.toJSONString(deleteResult));
    }

    @Test
    public void deleteInstanceSingleByCondition() throws InputErrorException {
        DeleteByConditionInputBean inputBean = new DeleteByConditionInputBean();
        CommonQueryConditionsBean conditionsBean = new CommonQueryConditionsBean();
        conditionsBean.setFilterType("SimilarFilteringItem");
        conditionsBean.setPropertyName("ID");
        conditionsBean.setFirstParam("ObjectID_00A_38");
        List<CommonQueryConditionsBean> conditionList = new ArrayList<>();
        conditionList.add(conditionsBean);
        inputBean.setConditions(conditionList);

        inputBean.setProperty("ID");
        inputBean.setRelatedObjectTypeId("BusStopKiosk");
        inputBean.setRelatedProperty("code");

        BatchDataOperationResult operationResult = instancesService.deleteInstanceSingleByCondition(tenantId, objectTypeId, inputBean);
        System.out.println("operation result: [" + JSON.toJSONString(operationResult) + "]");
    }


    @Test
    public void addRelatedInstances() {
        Map<String, Object> rd1 = new HashMap<>();
        rd1.put("NAME", "name_01");
        rd1.put("administrativeDivision", "beijing");
        rd1.put("code", "ObjectID_00A_1");

        Map<String, Object> rd2 = new HashMap<>();
        rd2.put("NAME", "name_02");
        rd2.put("administrativeDivision", "beijing");
        rd2.put("code", "ObjectID_00A_1");

        Map<String, Object> rd3 = new HashMap<>();
        rd3.put("NAME", "name_03");
        rd3.put("administrativeDivision", "beijing");
        rd3.put("code", "ObjectID_00A_1");

        Map<String, Object> rd4 = new HashMap<>();
        rd4.put("NAME", "name_04");
        rd4.put("administrativeDivision", "beijing");
        rd4.put("code", "ObjectID_00A_11");

        Map<String, Object> rd5 = new HashMap<>();
        rd5.put("NAME", "name_04");
        rd5.put("administrativeDivision", "beijing");
        rd5.put("code", "ObjectID_00A_11");

        Map<String, Object> rd6 = new HashMap<>();
        rd6.put("NAME", "name_04");
        rd6.put("administrativeDivision", "beijing");
        rd6.put("code", "ObjectID_00A_16");

        List<Map<String, Object>> addInstancesInput = new ArrayList<>();
        addInstancesInput.add(rd1);
        addInstancesInput.add(rd2);
        addInstancesInput.add(rd3);
        addInstancesInput.add(rd4);
        addInstancesInput.add(rd5);
        addInstancesInput.add(rd6);

        List<InfoObjectValue> infoObjectValueList = new ArrayList<>();
        for (Map<String, Object> map : addInstancesInput) {
            InfoObjectValue currentInfoObjectValue = new InfoObjectValue();
            currentInfoObjectValue.setBaseDatasetPropertiesValue(map);
            infoObjectValueList.add(currentInfoObjectValue);
        }

        BatchDataOperationResult operationResult = instancesService.addInstanceSingle("1",
                "BusStopKiosk", infoObjectValueList, false);

        assertEquals("add related instances success", 6, operationResult.getOperationStatistics().getSuccessItemsCount());
    }

    @Test
    public void addInstances() {
        List<InfoObjectValue> infoObjectValueList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            InfoObjectValue currentInfoObjectValue = new InfoObjectValue();
            Map<String, Object> currentBaseDatasetData = new HashMap<>();
            currentBaseDatasetData.put(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME, "ObjectID_00A_" + i);
            currentBaseDatasetData.put(BusinessLogicConstant.NAME_PROPERTY_TYPE_NAME, "ObjectName_00A_" + i);
            currentInfoObjectValue.setBaseDatasetPropertiesValue(currentBaseDatasetData);

            Map<String, Object> currentGeneralDatasetData = new HashMap<>();
            currentGeneralDatasetData.put("Property1", "I am a string value " + i);
            currentGeneralDatasetData.put("Property2", 200000 + i);
            if (i % 2 == 0) {
                currentGeneralDatasetData.put("Property3", 0);
            } else {
                currentGeneralDatasetData.put("Property3", 1);
            }
            currentInfoObjectValue.setGeneralDatasetsPropertiesValue(currentGeneralDatasetData);
            infoObjectValueList.add(currentInfoObjectValue);
        }
        instancesService.addInstanceSingle("1", "Booth", infoObjectValueList, false);
    }

}