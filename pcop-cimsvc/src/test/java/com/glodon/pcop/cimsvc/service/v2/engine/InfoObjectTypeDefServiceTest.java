package com.glodon.pcop.cimsvc.service.v2.engine;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InfoObjectTypeDefServiceTest {

    private InfoObjectTypeDefService typeService;

    private Gson gson = new Gson();

    // @Before
    public void init() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
//        CacheUtil.cacheManagerInit();
        CimConstants.defauleSpaceName = "yuanjk";
        typeService = new InfoObjectTypeDefService();
    }

    // @Test
    public void addObjectTypeDef() {

        // String objectName = "test_data_set_inherient_parent";
        String objectName = "YUANJKtESToBJECTaDD";
        String tenantId = "1";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            assert cds != null;
            if (cds.hasInheritFactType(objectName)) {
                InfoObjectFeatures.deleteInfoObjectType(CimConstants.defauleSpaceName, objectName);
            }

            InfoObjectTypeVO objectTypeVO = new InfoObjectTypeVO();
            objectTypeVO.setObjectId(objectName);
            objectTypeVO.setObjectName(objectName);
            objectTypeVO.setParentObjectTypeName("test_data_set_inherient_parent");
            assertEquals(typeService.addObjectTypeDef(tenantId, objectTypeVO), objectName);
            assertNotNull(typeService.getObjectTypeDef(tenantId, objectName, false, false));
            if (cds.hasInheritFactType(objectName)) {
               InfoObjectFeatures.deleteInfoObjectType(CimConstants.defauleSpaceName, objectName);
            }
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    // @Test
    public void getObjectTypeDef() {

        String objectName = "YUANJKtESToBJECTaDD";
        String tenantId = "1";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            assert cds != null;
            if (cds.hasInheritFactType(objectName)) {
                InfoObjectFeatures.deleteInfoObjectType(CimConstants.defauleSpaceName, objectName);
            }

            InfoObjectTypeVO objectTypeVO = new InfoObjectTypeVO();
            objectTypeVO.setObjectId(objectName);
            objectTypeVO.setObjectName(objectName);
            typeService.addObjectTypeDef(tenantId, objectTypeVO);

            InfoObjectTypeVO infoObjectTypeVO = typeService.getObjectTypeDef(tenantId, objectName, true, false);
            assertNotNull(infoObjectTypeVO);
            System.out.println(gson.toJson(infoObjectTypeVO));
            if (cds.hasInheritFactType(objectName)) {
                InfoObjectFeatures.deleteInfoObjectType(CimConstants.defauleSpaceName, objectName);
            }
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    // @Test
    public void updateObjectTypeDef() {

        String objectName = "YUANJKtESToBJECTaDD";
        String tenantId = "1";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            assert cds != null;
            if (cds.hasInheritFactType(objectName)) {
                InfoObjectFeatures.deleteInfoObjectType(CimConstants.defauleSpaceName, objectName);
            }
            InfoObjectTypeVO objectTypeVO = new InfoObjectTypeVO();
            objectTypeVO.setObjectId(objectName);
            objectTypeVO.setObjectName(objectName);
//            objectTypeVO.setParentObjectTypeName("cccc");
            assertEquals(typeService.addObjectTypeDef(tenantId, objectTypeVO), objectName);
            System.out.println("Before update: " + gson.toJson(typeService.getObjectTypeDef(tenantId, objectName, false, false)));

            String nObjectName = "模型更新测试";
            objectTypeVO.setObjectName(nObjectName);
            IndustryTypeVO industryTypeVO = IndustryTypeFeatures.getIndustryTypeByName(CimConstants.defauleSpaceName, "行政区划");
            String nIndustryId = null;
            objectTypeVO.setIndustryTypeId("#33:2");
            if (industryTypeVO != null) {
                nIndustryId = industryTypeVO.getIndustryTypeId();
            }
            objectTypeVO.setIndustryTypeId(nIndustryId);
            InfoObjectTypeVO resultTbjectTypeVO = typeService.updateObjectTypeDef(tenantId, objectName, objectTypeVO);
            assertNotNull(resultTbjectTypeVO);
            assertEquals(resultTbjectTypeVO.getObjectTypeDesc(), nObjectName);
            assertEquals(resultTbjectTypeVO.getIndustryTypeId(), nIndustryId);
            System.out.println("After update: " + gson.toJson(typeService.getObjectTypeDef(tenantId, objectName, false, false)));

            if (cds.hasInheritFactType(objectName)) {
                InfoObjectFeatures.deleteInfoObjectType(CimConstants.defauleSpaceName, objectName);
            }
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

}