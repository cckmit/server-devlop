package com.glodon.pcop.spacialimportsvc.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.KafkaGisDataBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.spacialimportsvc.exception.InfoObjectNotFoundException;
import com.glodon.pcop.spacialimportsvc.model.FilePropertyMappingBean;
import com.glodon.pcop.spacialimportsvc.util.ImportCimConstants;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SaveKafkaFileDataTest {
    private SaveKafkaFileData saveKafkaFileData;
    private static Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        ImportCimConstants.defauleSpaceName = "pcopcim";
        CimConstants.defauleSpaceName = "pcopcim";
        saveKafkaFileData = new SaveKafkaFileData();
        ObjectMapper objectMapper = new ObjectMapper();
        saveKafkaFileData.setObjectMapper(objectMapper);
    }

    @Test
    public void getMappingInfoByTaskId() throws CimDataEngineInfoExploreException, IOException,
            CimDataEngineRuntimeException {
        FilePropertyMappingBean mappingBean = saveKafkaFileData.getMappingInfoByTaskId("taskId-06");
        System.out.println(gson.toJson(mappingBean));
    }

    @Test
    public void saveMessageData() {
    }

    @Test
    public void saveGisData() {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            String message = "{\"taskId\":1908196301789,\"objectName\":\"instance_related_object_test_yuanjk\"," +
                    "\"isUpdate\":true,\"tenantId\":\"1\",\"data\":{\"type\":\"518035\",\"NAME\":\"549425\"," +
                    "\"ID\":\"1676288514074048_518035\",\"totalCount\":167628851}}";

            saveKafkaFileData.saveGisData(cds, message);
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (DataServiceUserException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void addInstanceByMessage() {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            String message = "{\"taskId\":1908196301789,\"objectName\":\"instance_related_object_test_yuanjk\"," +
                    "\"isUpdate\":true,\"tenantId\":\"1\",\"data\":{\"type\":\"518035\",\"NAME\":\"549425\"," +
                    "\"ID\":\"1676288514074048_518036\",\"totalCount\":167628851}}";
            KafkaGisDataBean dataBean = JSON.parseObject(message, KafkaGisDataBean.class);
            String tenantId = dataBean.getTenantId();
            Fact fact = saveKafkaFileData.addInstanceByMessage(tenantId, cds, dataBean);
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException | InfoObjectNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

}