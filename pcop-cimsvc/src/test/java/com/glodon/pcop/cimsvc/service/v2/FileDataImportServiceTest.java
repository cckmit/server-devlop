package com.glodon.pcop.cimsvc.service.v2;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.PropertyMappingInputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimapi.exception.MinioClientException;
import com.glodon.pcop.cimsvc.model.v2.mapping.FileStructBean;
import com.glodon.pcop.cimsvc.model.v2.mapping.DataSetMappingInputBean;
import com.glodon.pcop.cimsvc.model.v2.mapping.ObjectMappingInputBean;
import com.glodon.pcop.cimsvc.util.DateUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class FileDataImportServiceTest {

    @Autowired
    private FileDataImportService fileDataImportService;
    private static String tenantId;

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";
        // fileDataImportService = new FileDataImportService();
        // ObjectMapper objectMapper = new ObjectMapper();
        // fileDataImportService.setObjectMapper(objectMapper);
        tenantId = "2";
    }

    @Test
    public void getSpacialFileStruct() throws MinioClientException {
        String bucket = "pan-test";
        String fileName = "building_zjf_test.zip";

        System.out.println("===shp file struct: " + JSON.toJSONString(fileDataImportService.getSpacialFileStruct(bucket,
                fileName)));

    }

    @Test
    public void startImportTask() {
    }

    @Test
    public void sendStartTaskMq() {
    }

    @Test
    public void sendStartTaskToGis() {
    }

    @Test
    public void getFileStoreStruct() {
    }

    // @Test
    public void parserObjectMapping() {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef infoObjectDef =
                    modelCore.getInfoObjectDef(CimConstants.FileImportProperties.DATA_IMPORT_MAPPING_INFO_TYPE_NAME);

            // fileDataImportService.parserObjectMapping("fileDataId-01", "taskId-01", getManuallyObjectMapping(),
            // null, infoObjectDef, modelCore, cds, tenantId);
            // fileDataImportService.parserObjectMapping("fileDataId-03", "taskId-03", getAutomaticallyObjectMapping
            // (), getFileStructBean(), infoObjectDef, modelCore, cds, tenantId);
            fileDataImportService.parserObjectMapping("fileDataId-06", "taskId-06", getCreateObjectMapping(),
                    getFileStructBean(), infoObjectDef, modelCore, cds, tenantId);
        } catch (DataServiceUserException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }

    // @Test
    public void parserObjectMappingV2() {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef infoObjectDef =
                    modelCore.getInfoObjectDef(CimConstants.FileImportProperties.DATA_IMPORT_MAPPING_INFO_TYPE_NAME);

            String taskId = "taskId-" + DateUtil.getCurrentDateMs();
            System.out.println("task id=" + taskId);
            // fileDataImportService.parserObjectMappingV2("fileDataId-" + DateUtil.getCurrentDateMs(), taskId,
            // getCreateObjectMapping(), getFileStructBean(), infoObjectDef, modelCore, cds, tenantId);
            // fileDataImportService.parserObjectMappingV2("fileDataId-" + DateUtil.getCurrentDateMs(), taskId,
            // getAutomaticallyObjectMapping(), getFileStructBean(), infoObjectDef, modelCore, cds, tenantId);
            // fileDataImportService.parserObjectMappingV2("fileDataId-" + DateUtil.getCurrentDateMs(), taskId,
            // getManuallyObjectMapping(), getFileStructBean(), infoObjectDef, modelCore, cds, tenantId);
            fileDataImportService.parserObjectMappingV2("fileDataId-" + DateUtil.getCurrentDateMs(), taskId,
                    getMixMapping(), getFileStructBean(), infoObjectDef, modelCore, cds, tenantId);
        } catch (DataServiceUserException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }

    private ObjectMappingInputBean getManuallyObjectMapping() {
        ObjectMappingInputBean mappingInputBean = new ObjectMappingInputBean();
        mappingInputBean.setClean(false);
        mappingInputBean.setCreate(false);
        mappingInputBean.setObjectTypeId("test_file_data_import_destinition_type");
        mappingInputBean.setObjectTypeName("test_file_data_import_destinition_type");
        mappingInputBean.setSingleFileName("src_file_name.shp");

        List<DataSetMappingInputBean> dataSets = new ArrayList<>();
        DataSetMappingInputBean dsBean1 = new DataSetMappingInputBean();
        dsBean1.setId("#-1:-1");
        dsBean1.setName("test_data_set");
        dsBean1.setDesc("测试属性集");

        List<PropertyMappingInputBean> properties = new ArrayList<>();
        PropertyMappingInputBean propBean1 = new PropertyMappingInputBean();
        propBean1.setSrcPropertyName("sk1");
        propBean1.setDesPropertyName("dk1");
        propBean1.setPropertyType("STRING");
        properties.add(propBean1);

        PropertyMappingInputBean propBean2 = new PropertyMappingInputBean();
        propBean2.setSrcPropertyName("sk2");
        propBean2.setDesPropertyName("dk2");
        propBean2.setPropertyType("STRING");
        properties.add(propBean2);

        dsBean1.setProperties(properties);
        dsBean1.setCreate(false);
        dataSets.add(dsBean1);

        DataSetMappingInputBean dsBean2 = new DataSetMappingInputBean();
        dsBean2.setId("#-1:-1");
        dsBean2.setName("test_data_set_b");
        dsBean2.setDesc("测试属性集b");

        List<PropertyMappingInputBean> properties_b = new ArrayList<>();
        PropertyMappingInputBean propBean3 = new PropertyMappingInputBean();
        propBean3.setSrcPropertyName("skb1");
        propBean3.setDesPropertyName("dkb1");
        propBean3.setPropertyType("STRING");
        properties_b.add(propBean3);

        PropertyMappingInputBean propBean4 = new PropertyMappingInputBean();
        propBean4.setSrcPropertyName("skb2");
        propBean4.setDesPropertyName("dkb2");
        propBean4.setPropertyType("STRING");
        properties_b.add(propBean4);

        dsBean2.setProperties(properties_b);
        dsBean2.setCreate(false);
        dataSets.add(dsBean2);
        mappingInputBean.setDataSets(dataSets);

        return mappingInputBean;
    }

    private ObjectMappingInputBean getAutomaticallyObjectMapping() {
        ObjectMappingInputBean mappingInputBean = new ObjectMappingInputBean();
        mappingInputBean.setClean(false);
        mappingInputBean.setCreate(false);
        mappingInputBean.setObjectTypeId("gldtestt");
        mappingInputBean.setObjectTypeName("gldtestt");
        mappingInputBean.setSingleFileName("src_file_name.shp");

        return mappingInputBean;
    }

    private ObjectMappingInputBean getMixMapping() {
        ObjectMappingInputBean mappingInputBean = new ObjectMappingInputBean();
        mappingInputBean.setClean(false);
        mappingInputBean.setCreate(false);
        mappingInputBean.setObjectTypeId("file_data_import_20190325143220");
        mappingInputBean.setObjectTypeName("file_data_import_20190325143220");
        mappingInputBean.setSingleFileName("src_file_name.shp");

        List<DataSetMappingInputBean> dataSets = new ArrayList<>();
        DataSetMappingInputBean dsBean1 = new DataSetMappingInputBean();
        dsBean1.setId("#-1:-1");
        dsBean1.setName("test_data_set");
        dsBean1.setDesc("测试属性集");

        List<PropertyMappingInputBean> properties = new ArrayList<>();
        PropertyMappingInputBean propBean1 = new PropertyMappingInputBean();
        propBean1.setSrcPropertyName("sk1");
        propBean1.setDesPropertyName("dk1");
        propBean1.setPropertyType("STRING");
        properties.add(propBean1);

        PropertyMappingInputBean propBean2 = new PropertyMappingInputBean();
        propBean2.setSrcPropertyName("sk2");
        propBean2.setDesPropertyName("dk2");
        propBean2.setPropertyType("STRING");
        properties.add(propBean2);

        dsBean1.setProperties(properties);
        dsBean1.setCreate(false);
        dataSets.add(dsBean1);

        DataSetMappingInputBean dsBean2 = new DataSetMappingInputBean();
        dsBean2.setName("test_data_set_b");
        dsBean2.setDesc("测试属性集b");
        dsBean2.setCreate(true);
        dataSets.add(dsBean2);
        mappingInputBean.setDataSets(dataSets);

        return mappingInputBean;
    }

    private FileStructBean getFileStructBean() {
        FileStructBean fileStructBean = new FileStructBean();
        fileStructBean.setSingleFileName("test.sh");
        Map<String, String> props = new HashMap<>();
        props.put("nan", "STRING");
        props.put("simple", "STRING");
        fileStructBean.setStruct(props);

        return fileStructBean;
    }

    private ObjectMappingInputBean getCreateObjectMapping() {
        ObjectMappingInputBean mappingInputBean = new ObjectMappingInputBean();
        mappingInputBean.setClean(false);
        mappingInputBean.setCreate(true);
        mappingInputBean.setObjectTypeId("file_data_import_" + DateUtil.getCurrentDateReadable());
        mappingInputBean.setObjectTypeName("测试文件导入自动创建对象类型");
        mappingInputBean.setSingleFileName("src_file_name.shp");

        List<DataSetMappingInputBean> dataSets = new ArrayList<>();
        DataSetMappingInputBean dsBean1 = new DataSetMappingInputBean();
        // dsBean1.setId("#142:26");
        dsBean1.setName("file_data_import_dataSet_" + DateUtil.getCurrentDateReadable());
        dsBean1.setDesc("测试文件导入自动创建属性集");

        dataSets.add(dsBean1);
        mappingInputBean.setDataSets(dataSets);

        return mappingInputBean;
    }


}