package com.glodon.pcop.spacialimportsvc.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.glodon.pcop.cim.common.model.*;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelFileImportTest {

    private static String tenantId = "1";
    private static String objectTypeId = "test_file_load";
    private static Gson gson = new Gson();
    private static ObjectMapper objectMapper;

    // @Before
    public void setUp() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        ImportCimConstants.defauleSpaceName = "yuanjk";
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                    throws IOException {
                jsonGenerator.writeString("");
            }
        });
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getExcelFileStructure() {
    }

    @Test
    public void excelDataImport() {
        System.out.println(CimConstants.JOB_END_IDENTIFIER);

    }

    @Test
    public void instanceNoMappingCollectionLoader() throws DataServiceModelRuntimeException {
        Map<String, String> structure = new HashMap<>();
        structure.put("k1", "STRING");
        structure.put("k2", "INTEGER");

        FileImportTaskInputBean taskInputBean = new FileImportTaskInputBean();
        taskInputBean.setObjectTypeId(objectTypeId);
        taskInputBean.setTenantId(tenantId);

        taskInputBean.setInstanceRid("#2017:0");
        taskInputBean.setIsMapping(false);
        taskInputBean.setDataSetName("新增属性集1");
        taskInputBean.setDataSetType(EnumWrapper.DATA_SET_TYPE.INSTANCE);
        taskInputBean.setDataSetStructure(EnumWrapper.DATA_SET_STRUCTURE.COLLECTION);
        taskInputBean.setDataType(EnumWrapper.DATA_SET_DATA_TYPE.通用属性集);

        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> vauleMap1 = new HashMap<>();
        vauleMap1.put("k1", "v1-1");
        vauleMap1.put("k2", "v2-1");
        contents.add(vauleMap1);

        Map<String, Object> valueMap2 = new HashMap<>();
        valueMap2.put("k1", "v1-2");
        valueMap2.put("k2", "v2-2");
        contents.add(valueMap2);

        System.out.println(gson.toJson(ImportCommonUtil.instanceCollectionLoader(taskInputBean, structure, contents)));
    }

    @Test
    public void instanceMappingCollectionLoader() throws DataServiceModelRuntimeException {
        Map<String, String> structure = new HashMap<>();
        structure.put("k1", "STRING");
        structure.put("k2", "INTEGER");

        List<PropertyMapping> propertyMappingList = new ArrayList<>();
        PropertyMapping propertyMapping1 = new PropertyMapping();
        propertyMapping1.setPropertyRid("#164:296");
        propertyMapping1.setTargetPropertyName("cl_ds1_pro1");
        propertyMapping1.setSourcePropertyName("k1");
        propertyMappingList.add(propertyMapping1);

        PropertyMapping propertyMapping2 = new PropertyMapping();
        propertyMapping2.setPropertyRid("#166:289");
        propertyMapping2.setTargetPropertyName("cl_ds1_pro3");
        propertyMapping2.setSourcePropertyName("k2");
        propertyMappingList.add(propertyMapping2);

        List<DataSetMapping> dataSetMappingList = new ArrayList<>();
        DataSetMapping dataSetMapping = new DataSetMapping();
        dataSetMapping.setDataSetId("#144:39");
        dataSetMapping.setDataSetName("第一个集合属性集");
        dataSetMapping.setPropertyMappings(propertyMappingList);
        dataSetMappingList.add(dataSetMapping);

        FileImportTaskInputBean taskInputBean = new FileImportTaskInputBean();
        taskInputBean.setObjectTypeId(objectTypeId);
        taskInputBean.setTenantId(tenantId);
        taskInputBean.setInstanceRid("#2018:0");
        taskInputBean.setIsMapping(true);
        // taskInputBean.setDataSetName("新增属性集1");
        // taskInputBean.setDataSetType(EnumWrapper.DATA_SET_TYPE.INSTANCE);
        // taskInputBean.setDataSetStructure(EnumWrapper.DATA_SET_STRUCTURE.COLLECTION);
        // taskInputBean.setDataType(EnumWrapper.DATA_SET_DATA_TYPE.通用属性集);
        taskInputBean.setDataSetMappings(dataSetMappingList);

        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> vauleMap1 = new HashMap<>();
        vauleMap1.put("k1", "v1-1");
        vauleMap1.put("k2", "v2-1");
        contents.add(vauleMap1);

        Map<String, Object> valueMap2 = new HashMap<>();
        valueMap2.put("k1", "v1-2");
        valueMap2.put("k2", "v2-2");
        contents.add(valueMap2);

        System.out.println(gson.toJson(ImportCommonUtil.instanceCollectionLoader(taskInputBean, structure, contents)));
    }

    @Test
    public void instanceMappingSingleLoader() throws DataServiceModelRuntimeException {
        List<PropertyMapping> propertyMappingList = new ArrayList<>();
        PropertyMapping propertyMapping1 = new PropertyMapping();
        propertyMapping1.setPropertyRid(" #167:287");
        propertyMapping1.setTargetPropertyName("sg_ds1_pro1");
        propertyMapping1.setSourcePropertyName("k1");
        propertyMappingList.add(propertyMapping1);

        PropertyMapping propertyMapping2 = new PropertyMapping();
        propertyMapping2.setPropertyRid("#168:284");
        propertyMapping2.setTargetPropertyName("sg_ds1_pro2");
        propertyMapping2.setSourcePropertyName("k2");
        propertyMappingList.add(propertyMapping2);

        PropertyMapping propertyMapping3 = new PropertyMapping();
        propertyMapping3.setPropertyRid("#161:301");
        propertyMapping3.setTargetPropertyName("sg_ds1_pro3");
        propertyMapping3.setSourcePropertyName("k3");
        propertyMappingList.add(propertyMapping3);

        List<DataSetMapping> dataSetMappingList = new ArrayList<>();
        DataSetMapping dataSetMapping = new DataSetMapping();
        dataSetMapping.setDataSetId("#137:57");
        dataSetMapping.setDataSetName("第2个属性集");
        dataSetMapping.setPropertyMappings(propertyMappingList);
        dataSetMappingList.add(dataSetMapping);

        FileImportTaskInputBean taskInputBean = new FileImportTaskInputBean();
        taskInputBean.setObjectTypeId(objectTypeId);
        taskInputBean.setTenantId(tenantId);
        // taskInputBean.setInstanceRid("#2018:0");
        taskInputBean.setIsMapping(true);
        // taskInputBean.setDataSetName("新增属性集1");
        // taskInputBean.setDataSetType(EnumWrapper.DATA_SET_TYPE.INSTANCE);
        // taskInputBean.setDataSetStructure(EnumWrapper.DATA_SET_STRUCTURE.COLLECTION);
        // taskInputBean.setDataType(EnumWrapper.DATA_SET_DATA_TYPE.通用属性集);
        taskInputBean.setDataSetMappings(dataSetMappingList);

        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> vauleMap1 = new HashMap<>();
        vauleMap1.put("k1", "v1-1");
        vauleMap1.put("k2", "v2-1");
        vauleMap1.put("k3", "v3-1");
        contents.add(vauleMap1);

        Map<String, Object> valueMap2 = new HashMap<>();
        valueMap2.put("k1", "v1-2");
        valueMap2.put("k2", "v2-2");
        valueMap2.put("k3", "v3-2");
        contents.add(valueMap2);

        Map<String, Object> valueMap3 = new HashMap<>();
        valueMap3.put("k1", "v1-3");
        valueMap3.put("k2", "v2-3");
        valueMap3.put("k3", "v3-3");
        contents.add(valueMap3);

        System.out.println(gson.toJson(ImportCommonUtil.instanceMappingSingleLoader(taskInputBean, contents)));
    }

    @Test
    public void inputJsonStructure() throws JsonProcessingException {
        List<PropertyMapping> propertyMappingList = new ArrayList<>();
        PropertyMapping propertyMapping1 = new PropertyMapping();
        propertyMapping1.setPropertyRid(" #167:287");
        propertyMapping1.setTargetPropertyName("sg_ds1_pro1");
        propertyMapping1.setSourcePropertyName("k1");
        propertyMappingList.add(propertyMapping1);

        PropertyMapping propertyMapping2 = new PropertyMapping();
        propertyMapping2.setPropertyRid("#168:284");
        propertyMapping2.setTargetPropertyName("sg_ds1_pro2");
        propertyMapping2.setSourcePropertyName("k2");
        propertyMappingList.add(propertyMapping2);

        PropertyMapping propertyMapping3 = new PropertyMapping();
        propertyMapping3.setPropertyRid("#161:301");
        propertyMapping3.setTargetPropertyName("sg_ds1_pro3");
        propertyMapping3.setSourcePropertyName("k3");
        propertyMappingList.add(propertyMapping3);

        List<DataSetMapping> dataSetMappingList = new ArrayList<>();
        DataSetMapping dataSetMapping = new DataSetMapping();
        dataSetMapping.setDataSetId("#137:57");
        dataSetMapping.setDataSetName("第2个属性集");
        dataSetMapping.setPropertyMappings(propertyMappingList);
        dataSetMappingList.add(dataSetMapping);

        RelationshipLinkLogicVO relationshipLinkLogicVO = new RelationshipLinkLogicVO();
        List<RelationshipLinkLogicVO> relationshipLinkLogicVOS = new ArrayList<>();
        relationshipLinkLogicVOS.add(relationshipLinkLogicVO);

        RelationshipMappingVO relationshipMappingVO = new RelationshipMappingVO();
        relationshipMappingVO.setLinkLogic(relationshipLinkLogicVOS);
        List<RelationshipMappingVO> relationshipMappingVOS = new ArrayList<>();
        relationshipMappingVOS.add(relationshipMappingVO);

        FileImportTaskInputBean taskInputBean = new FileImportTaskInputBean();
        taskInputBean.setObjectTypeId(objectTypeId);
        taskInputBean.setTenantId(tenantId);
        taskInputBean.setInstanceRid("#2018:0");
        taskInputBean.setIsMapping(true);
        taskInputBean.setDataSetName("新增属性集1");
        taskInputBean.setDataSetType(EnumWrapper.DATA_SET_TYPE.INSTANCE);
        taskInputBean.setDataSetStructure(EnumWrapper.DATA_SET_STRUCTURE.COLLECTION);
        taskInputBean.setDataType(EnumWrapper.DATA_SET_DATA_TYPE.通用属性集);
        taskInputBean.setDataSetMappings(dataSetMappingList);
        taskInputBean.setRelationshipMappings(relationshipMappingVOS);

        System.out.println(objectMapper.writeValueAsString(taskInputBean));

    }

    @Test
    public void stringBuilderTest() {
        StringBuilder stringBuilder = new StringBuilder();

        System.out.println("stringBuilder: " + stringBuilder.toString());


    }


}