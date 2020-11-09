package com.glodon.pcop.cimsvc.service.v2.engine;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeRestrictVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.service.DataSetService;
import com.google.gson.Gson;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataSetDefServiceTest {
    private static DataSetService dataSetService;
    private static String tenantId;
    private static String objName;
    private static String dataSetId = "#141:32";
    private static String propertyId;
    private static String restrictId;

    // @BeforeClass
    public static void init() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "yuanjk";
//        CacheUtil.cacheManagerInit();
        dataSetService = new DataSetService();
        tenantId = "1";
        // objName = "test_instance_query";
        objName = "test_file_load";
    }

    // @Test
    public void getDataSetValueById() throws DataServiceModelRuntimeException {
        String tenantId = "1";
        String infoObjectName = "dataset_test_aa";
        String datSetName = "ds1";
        String businessId = "d1";

        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        List<Map<String, Object>> results = new ArrayList<>();
        if (cimModelCore != null) {
            InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(infoObjectName);
            if (infoObjectDef != null) {
                ExploreParameters exploreParameters = new ExploreParameters();
                FilteringItem filteringItem = new EqualFilteringItem("ID", businessId);
                exploreParameters.setDefaultFilteringItem(filteringItem);
                InfoObjectRetrieveResult infoObjectRetrieveResult = infoObjectDef.getObjects(exploreParameters);
                List<InfoObject> infoObjectList = infoObjectRetrieveResult.getInfoObjects();
                if (infoObjectList != null) {
                    for (InfoObject infoObject : infoObjectList) {
                        System.out.println("ObjectTypeName=" + infoObject.getObjectTypeName() + ", instanceId=" + infoObject.getObjectInstanceRID());
                        Map<String, Object> dataSetValue = infoObject.getObjectPropertiesByDataset(datSetName);
                        if (dataSetValue != null) {
                            results.add(dataSetValue);
                        }
                    }
                }

            }
        }
        System.out.println(results);
    }

    // @Test
    public void getDataSetValueById1() {
    }

    // @Test
    public void test01AddDataSetAndPropertyDef() throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        // DatasetVO datasetVO = getCollectionDataSetVo();
        DatasetVO datasetVO = getSingleDataSetVo();
        objName = "test_file_load";
        DatasetDef datasetDef = dataSetService.addDataSetAndPropertyDef(tenantId, objName, datasetVO);
        dataSetId = datasetDef.getDatasetRID();
        System.out.println("Data set id: " + datasetDef.getDatasetRID());
    }

    // @Test
    public void test02GetDataSetAndPropertyDef() {
        System.out.println("Start get data set test");
        Gson gson = new Gson();
        DatasetVO datasetVO = dataSetService.getDataSetAndPropertyDef(tenantId, objName, dataSetId);
        System.out.println(gson.toJson(datasetVO));
        propertyId = datasetVO.getLinkedPropertyTypes().get(0).getPropertyTypeId();
        System.out.println(gson.toJson(propertyId));
        assertNotNull(propertyId);
        restrictId = datasetVO.getLinkedPropertyTypes().get(0).getRestrictVO().getPropertyTypeRestrictId();
        System.out.println(gson.toJson(restrictId));
        assertNotNull(restrictId);
        System.out.println("End get data set test");
    }

    // @Test
    public void test03UpdateDataSetAndPropertyDef() throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        System.out.println("Start update data set test");
        DatasetVO datasetVO = getCollectionDataSetVo();
        datasetVO.setDatasetName("属性集更新-第一个属性集");
        datasetVO.getLinkedPropertyTypes().get(0).setPropertyTypeName("属性更新-第一个属性");
        datasetVO.getLinkedPropertyTypes().get(0).getRestrictVO().setDefaultValue("默认值");
        dataSetService.updateDataSetAndPropertyDef(tenantId, objName, dataSetId, datasetVO);
        System.out.println("End update data set test");
    }

//    @Test
    public void test04DeleteDataSetAndPropertyDef() throws DataServiceModelRuntimeException {
        System.out.println("Start delete data set test");
        dataSetService.deleteDataSetAndPropertyDef(tenantId, objName, dataSetId);
        System.out.println("Start delete data set test");
    }

    // @Test
    public void StringBuilderNull() {
        StringBuilder builder = new StringBuilder();

        System.out.println("[" + builder.toString() + "]");

        System.out.println("[" + builder.toString().replaceFirst(CimConstants.PROPERTY_PRIMAY_KEY_SEPARTOR, "") + "]");

    }

//        @AfterClass
//    @Test
    public static void removeDataSet() throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        System.out.println("Start clear data set test");
        DatasetFeatures.clearDataSetAndProperty(CimConstants.defauleSpaceName, dataSetId);
        System.out.println("End clear data set test");
    }

    // @Test
    public void addRelation() throws CimDataEngineRuntimeException {
        CimDataSpace cds = null;
        String factId = "#163:222";
        String relationId = "#78:274";
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
           /*if (cds.getFactById(factId) != null) {
               cds.removeFact(factId);
           }*/
            if (cds.getRelationById(relationId) != null) {
                cds.removeRelation(relationId);
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }

    private DatasetVO getCollectionDataSetVo() {
        PropertyTypeRestrictVO restrictVO1 = new PropertyTypeRestrictVO();
        restrictVO1.setPrimaryKey(true);
        restrictVO1.setDefaultValue("a");
        restrictVO1.setNull(false);
        restrictVO1.setPropertyTypeRestrictId(restrictId);
        PropertyTypeVO typeVO1 = new PropertyTypeVO();
        typeVO1.setPropertyTypeName("cl_ds1_pro1");
        typeVO1.setPropertyTypeDesc("第一个集合属性集-第1个属性");
        typeVO1.setPropertyFieldDataClassify("STRING");
        typeVO1.setRestrictVO(restrictVO1);
        typeVO1.setPropertyTypeId(propertyId);

        PropertyTypeRestrictVO restrictVO2 = new PropertyTypeRestrictVO();
        restrictVO2.setPrimaryKey(false);
        restrictVO2.setDefaultValue("b");
        restrictVO2.setNull(false);
        PropertyTypeVO typeVO2 = new PropertyTypeVO();
        typeVO2.setPropertyTypeName("cl_ds1_pro2");
        typeVO2.setPropertyTypeDesc("第一个集合属性集-第2个属性");
        typeVO2.setPropertyFieldDataClassify("STRING");
        typeVO2.setRestrictVO(restrictVO2);

        PropertyTypeRestrictVO restrictVO3 = new PropertyTypeRestrictVO();
        restrictVO3.setPrimaryKey(false);
        restrictVO3.setDefaultValue("c");
        restrictVO3.setNull(false);
        PropertyTypeVO typeVO3 = new PropertyTypeVO();
        typeVO3.setPropertyTypeName("cl_ds1_pro3");
        typeVO3.setPropertyTypeDesc("第一个集合属性集-第3个属性");
        typeVO3.setPropertyFieldDataClassify("STRING");
        typeVO3.setRestrictVO(restrictVO3);

        List<PropertyTypeVO> propertyTypeVOList = new ArrayList<>();
        propertyTypeVOList.add(typeVO1);
        propertyTypeVOList.add(typeVO2);
        propertyTypeVOList.add(typeVO3);

        DatasetVO datasetVO = new DatasetVO();
        datasetVO.setDatasetClassify("通用类型");
        datasetVO.setDatasetName("第一个集合属性集");
        datasetVO.setDatasetDesc("第一个集合属性集");
        datasetVO.setDataSetType(BusinessLogicConstant.DatasetType.INSTANCE);
        datasetVO.setDatasetStructure(DatasetDef.DatasetStructure.COLLECTION);
        datasetVO.setLinkedPropertyTypes(propertyTypeVOList);

        return datasetVO;
    }

    private DatasetVO getSingleDataSetVo() {
        PropertyTypeRestrictVO restrictVO1 = new PropertyTypeRestrictVO();
        restrictVO1.setPrimaryKey(true);
        restrictVO1.setDefaultValue("a");
        restrictVO1.setNull(false);
        restrictVO1.setPropertyTypeRestrictId(restrictId);
        PropertyTypeVO typeVO1 = new PropertyTypeVO();
        typeVO1.setPropertyTypeName("sg_ds1_pro1");
        typeVO1.setPropertyTypeDesc("第一个属性集-第1个属性");
        typeVO1.setPropertyFieldDataClassify("STRING");
        typeVO1.setRestrictVO(restrictVO1);
        typeVO1.setPropertyTypeId(propertyId);

        PropertyTypeRestrictVO restrictVO2 = new PropertyTypeRestrictVO();
        restrictVO2.setPrimaryKey(true);
        restrictVO2.setDefaultValue("b");
        restrictVO2.setNull(false);
        PropertyTypeVO typeVO2 = new PropertyTypeVO();
        typeVO2.setPropertyTypeName("sg_ds1_pro2");
        typeVO2.setPropertyTypeDesc("第一个属性集-第2个属性");
        typeVO2.setPropertyFieldDataClassify("STRING");
        typeVO2.setRestrictVO(restrictVO2);

        PropertyTypeRestrictVO restrictVO3 = new PropertyTypeRestrictVO();
        restrictVO3.setPrimaryKey(false);
        restrictVO3.setDefaultValue("c");
        restrictVO3.setNull(false);
        PropertyTypeVO typeVO3 = new PropertyTypeVO();
        typeVO3.setPropertyTypeName("sg_ds1_pro3");
        typeVO3.setPropertyTypeDesc("第一个属性集-第3个属性");
        typeVO3.setPropertyFieldDataClassify("STRING");
        typeVO3.setRestrictVO(restrictVO3);

        List<PropertyTypeVO> propertyTypeVOList = new ArrayList<>();
        propertyTypeVOList.add(typeVO1);
        propertyTypeVOList.add(typeVO2);
        propertyTypeVOList.add(typeVO3);

        DatasetVO datasetVO = new DatasetVO();
        datasetVO.setDatasetClassify("通用类型");

        datasetVO.setDatasetName("第一个属性集");
        datasetVO.setDatasetDesc("第一个属性集");
        datasetVO.setDataSetType( BusinessLogicConstant.DatasetType.INSTANCE);
        datasetVO.setDatasetStructure(DatasetDef.DatasetStructure.LINK);

        datasetVO.setDatasetName("第2个属性集");
        datasetVO.setDatasetDesc("第2个属性集");

        datasetVO.setDataSetType(BusinessLogicConstant.DatasetType.INSTANCE);
        datasetVO.setDatasetStructure(DatasetDef.DatasetStructure.SINGLE);

        datasetVO.setLinkedPropertyTypes(propertyTypeVOList);

        return datasetVO;
    }

}