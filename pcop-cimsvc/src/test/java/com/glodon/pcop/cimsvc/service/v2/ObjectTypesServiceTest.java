package com.glodon.pcop.cimsvc.service.v2;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.check.object.CheckAndAddDataSetInputBean;
import com.glodon.pcop.cim.common.model.check.object.CheckAndAddObjectTypeInputBean;
import com.glodon.pcop.cim.common.model.check.object.CheckAndAddObjectTypeOutputBean;
import com.glodon.pcop.cim.common.model.check.object.CheckAndAddPropertyInputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceCache.CimCacheConfig;
import com.glodon.pcop.cim.engine.dataServiceCache.CimCacheManager;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimsvc.model.v2.ObjectTypeQueryInput;
import com.glodon.pcop.cimsvc.service.v2.engine.DataSetDefService;
import com.glodon.pcop.cimsvc.service.v2.engine.InfoObjectTypeDefService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Ignore
public class ObjectTypesServiceTest {

    private ObjectTypesService objectTypesService;

    private static DataSetDefService dataSetDefService;

    private static InfoObjectTypeDefService infoObjectTypeDefService;

    @Before
    public void setUp() throws Exception {
        dataSetDefService = new DataSetDefService();
        infoObjectTypeDefService = new InfoObjectTypeDefService();

        objectTypesService = new ObjectTypesService();
        objectTypesService.setDataSetDefService(dataSetDefService);
        objectTypesService.setInfoObjectTypeDefService(infoObjectTypeDefService);

        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";

        CimCacheConfig ccc = new CimCacheConfig();
        ccc.setCacheEnable(true);
        ccc.setClusterCacheEnable(true);
        ccc.setClusterServiceLocation("terracotta://10.129.57.108:9410/clustered");
        ccc.setClusterServiceDefaultResourceId("main");
        ccc.setClusterServiceSharedResourceId("primary-server-resource");
        ccc.setClusterTimeToLiveSeconds(1000L);
        CimCacheManager.setCimCacheConfig(ccc);
    }

    @Test
    public void getObjectType() {
    }

    @Test
    public void addObjectType() {
    }

    @Test
    public void addObjectType1() {
    }

    @Test
    public void updateObjectType() {
    }

    @Test
    public void updateObjectType1() {
    }

    @Test
    public void deleteObjectType() {
    }

    @Test
    public void isObjectTypeIdAvailable() {
    }

    @Test
    public void getDataSetDef() {
    }

    @Test
    public void queryObjectTypes() throws DataServiceModelRuntimeException {
        ObjectTypeQueryInput queryInputBean = new ObjectTypeQueryInput();
        queryInputBean.setPageIndex(0);
        queryInputBean.setPageSize(10);
        long ft = System.currentTimeMillis();
        objectTypesService.queryObjectTypes("1", queryInputBean);
        Long sd = System.currentTimeMillis();
        System.out.println("first query used ms: " + (sd - ft));
        objectTypesService.queryObjectTypes("1", queryInputBean);
        System.out.println("used million seconds: " + (System.currentTimeMillis() - sd));
    }

    @Test
    public void queryRelationships() {
    }

    @Test
    public void checkAndCreateObjectDef() {
        CheckAndAddPropertyInputBean propertyInputBean1 = new CheckAndAddPropertyInputBean();
        propertyInputBean1.setName("type");
        propertyInputBean1.setDesc("类型");
        propertyInputBean1.setDataType(CheckAndAddPropertyInputBean.DataTypes.STRING);

        CheckAndAddPropertyInputBean propertyInputBean2 = new CheckAndAddPropertyInputBean();
        propertyInputBean2.setName("count");
        propertyInputBean2.setDesc("数量");
        propertyInputBean2.setDataType(CheckAndAddPropertyInputBean.DataTypes.INT);

        List<CheckAndAddPropertyInputBean> propertyInputBeans1 = new ArrayList<>();
        propertyInputBeans1.add(propertyInputBean1);
        propertyInputBeans1.add(propertyInputBean2);

        CheckAndAddDataSetInputBean dataSetInputBean1 = new CheckAndAddDataSetInputBean();
        dataSetInputBean1.setName("ds01");
        dataSetInputBean1.setDesc("属性集01");
        dataSetInputBean1.setLinkedProperties(propertyInputBeans1);

        CheckAndAddObjectTypeInputBean objectTypeInputBean1 = new CheckAndAddObjectTypeInputBean();
        objectTypeInputBean1.setName("test_check_and_update_01");
        objectTypeInputBean1.setDesc("检查新增对象模型-测试");
        objectTypeInputBean1.setDataSets(Arrays.asList(dataSetInputBean1));


        CheckAndAddObjectTypeInputBean objectTypeInputBean2 = new CheckAndAddObjectTypeInputBean();
        objectTypeInputBean2.setName("yuanjk0409bb");
        objectTypeInputBean2.setDesc("instance_related_object_test_yuanjk");
        dataSetInputBean1.setCreate(true);
        objectTypeInputBean2.setClean(true);
        objectTypeInputBean2.setDataSets(Arrays.asList(dataSetInputBean1));

        List<CheckAndAddObjectTypeOutputBean> outputBeanList = objectTypesService.checkAndCreateObjectDef("1",
                Arrays.asList(objectTypeInputBean2));

        System.out.println("result: " + JSON.toJSONString(outputBeanList));
    }
}