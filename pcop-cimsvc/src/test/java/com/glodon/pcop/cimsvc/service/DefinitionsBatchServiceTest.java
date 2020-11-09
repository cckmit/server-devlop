package com.glodon.pcop.cimsvc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.definition.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefinitionsBatchServiceTest {
    private static String tenantId = "1";
    private static String creator = "1";

    private static CIMModelCore modelCore;
    private static CimDataSpace cds;


    // @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "yuanjk";
//        CacheUtil.cacheManagerInit();
        cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
    }

    // @Test
    public void addDefinitionsBatch() throws JsonProcessingException {
        DefinitionsBean definitionsBean = new DefinitionsBean();

        definitionsBean.setIndustries(getIndustryBatchBeans());
        definitionsBean.setObjectTypes(getObjectBatchBeans());
        definitionsBean.setDataSets(getDataSetBatchBeans());
        definitionsBean.setProperties(getPropertyBatchBeans());

        DefinitionsBatchService.addDefinitionsBatch(tenantId, creator, definitionsBean);
    }

    // @Test
    public void isIndustryTypeExists() throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        System.out.print(DefinitionsBatchService.isIndustryTypeExists(cds, "批量-行业分类1"));

    }

    // @Test
    public void industryBatchBeanSort() {
    }

    // @Test
    public void addIndustryTypesBatch() throws JsonProcessingException {
        Map<Integer, String> result = DefinitionsBatchService.addIndustryTypesBatch(tenantId, creator, modelCore, cds, getIndustryBatchBeans());

        System.out.print(result);
    }

    // @Test
    public void isObjectTypeExists() {
    }

    // @Test
    public void addObjectTypesBatch() throws JsonProcessingException {
        DefinitionsBatchService.addObjectTypesBatch(modelCore, cds, getObjectBatchBeans(), DefinitionsBatchService.addIndustryTypesBatch(tenantId, creator, modelCore, cds, getIndustryBatchBeans()));
    }

    // @Test
    public void getAllSelfDataSetNameByObject() {
    }

    // @Test
    public void addDataSetBatch() {
        Map<Integer, String> result = DefinitionsBatchService.addDataSetBatch(modelCore, cds, getDataSetBatchBeans());
        System.out.println(result);
    }

    // @Test
    public void getAllPropertyNameByDataSet() {
    }

    // @Test
    public void addPropertiesBatch() {
        System.out.print(DefinitionsBatchService.addPropertiesBatch(modelCore, cds, getPropertyBatchBeans(), DefinitionsBatchService.addDataSetBatch(modelCore, cds, getDataSetBatchBeans())));
    }


    private static List<IndustryBatchBean> getIndustryBatchBeans() {
        List<IndustryBatchBean> industryBatchBeans = new ArrayList<>();

        IndustryBatchBean batchBean1 = new IndustryBatchBean();
        batchBean1.setIndex(1);
        batchBean1.setName("批量-行业分类1");

        IndustryBatchBean batchBean2 = new IndustryBatchBean();
        batchBean2.setIndex(2);
        batchBean2.setName("批量-行业分类2");
        batchBean2.setParentIndex(1);

        IndustryBatchBean batchBean3 = new IndustryBatchBean();
        batchBean3.setIndex(3);
        batchBean3.setName("批量-行业分类3");
        batchBean3.setParentIndex(2);

        IndustryBatchBean batchBean4 = new IndustryBatchBean();
        batchBean4.setIndex(4);
        batchBean4.setName("批量-行业分类4");

        industryBatchBeans.add(batchBean1);
        industryBatchBeans.add(batchBean4);
        industryBatchBeans.add(batchBean2);
        industryBatchBeans.add(batchBean3);

        return industryBatchBeans;
    }


    private static List<ObjectBatchBean> getObjectBatchBeans() {
        List<ObjectBatchBean> objectBatchBeans = new ArrayList<>();

        ObjectBatchBean bean1 = new ObjectBatchBean();
        bean1.setObjectTypeId("batch_object_id1");
        bean1.setName("批量-对象类型1");
        bean1.setIndustryIndex(1);


        ObjectBatchBean bean2 = new ObjectBatchBean();
        bean2.setObjectTypeId("batch_object_id2");
        bean2.setName("批量-对象类型2");
        bean2.setIndustryIndex(2);


        objectBatchBeans.add(bean1);
        objectBatchBeans.add(bean2);
        return objectBatchBeans;
    }


    private static List<DataSetBatchBean> getDataSetBatchBeans() {
        List<DataSetBatchBean> dataSetBatchBeans = new ArrayList<>();

        DataSetBatchBean bean1 = new DataSetBatchBean();
        bean1.setIndex(1);
        bean1.setName("批量-属性集1");
        bean1.setObjectTypeId("batch_object_id1");
        bean1.setDataSetType("实例属性集");
        bean1.setDataStructure("一般属性集");
        bean1.setDataType("通用类型");


        DataSetBatchBean bean2 = new DataSetBatchBean();
        bean2.setIndex(2);
        bean2.setName("批量-属性集2");
        bean2.setObjectTypeId("batch_object_id2");
        bean2.setDataSetType("实例属性集");
        bean2.setDataStructure("一般属性集");
        bean2.setDataType("通用类型");

        dataSetBatchBeans.add(bean1);
        dataSetBatchBeans.add(bean2);

        return dataSetBatchBeans;
    }

    private static List<PropertyBatchBean> getPropertyBatchBeans() {
        List<PropertyBatchBean> propertyBatchBeans = new ArrayList<>();

        PropertyBatchBean bean1 = new PropertyBatchBean();
        bean1.setIndex(1);
        bean1.setName("code");
        bean1.setAlias("用地分类代码");
        bean1.setType("字符型");
        bean1.setIsNull(false);
        bean1.setIsPrimary(true);
        bean1.setDefaultValue("dv");

        PropertyBatchBean bean2 = new PropertyBatchBean();
        bean2.setIndex(1);
        bean2.setName("blockarea");
        bean2.setAlias("用地面积");
        bean2.setType("双精度浮点型");
        bean2.setIsNull(false);
        bean2.setIsPrimary(true);
        bean2.setDefaultValue("dvc");

        propertyBatchBeans.add(bean1);
        propertyBatchBeans.add(bean2);
        return propertyBatchBeans;
    }
}