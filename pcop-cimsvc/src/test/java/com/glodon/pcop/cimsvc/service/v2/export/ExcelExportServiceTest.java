package com.glodon.pcop.cimsvc.service.v2.export;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.excel.PropertyInputBean;
import com.glodon.pcop.cim.common.model.excel.PropertyValueMappingInputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.config.properties.MimeTypeConfig;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.excel.ExcelExportInputBean;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelExportServiceTest {

    private static String spaceName = "pcopcim";
    private static String tenantId = "1";
    ExcelExportService service = new ExcelExportService();

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = spaceName;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void exportInstancesAsExcel() throws InputErrorException, EntityNotFoundException {
        MimeTypeConfig mtc = new MimeTypeConfig();
        service.setMimeTypeConfig(mtc);
        MockHttpServletResponse response = new MockHttpServletResponse();
        String exportFilePath = "G:\\tmp\\excel_test\\export_test_v3.xls";
        try (FileOutputStream os = new FileOutputStream(exportFilePath)) {
            ExcelExportInputBean inputBean = parserExcelExportInput();
            service.exportInstancesAsExcel(tenantId, inputBean, response);
            os.write(response.getContentAsByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void queryInstances() {
    }

    @Test
    public void processValueMapping() {
        valueMapping();
    }

    @Test
    public void exportInstances() {
        String exportFilePath = "G:\\tmp\\excel_test\\export_test.xls";
        CimDataSpace cds = null;
        try {
            String objectTypeId = "projectV1";
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(spaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(spaceName, tenantId);
            InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
            List<InfoObject> infoObjectList = infoObjectDef.getObjects(null).getInfoObjects();
            Map<String, Map<String, String>> valueMappings = valueMapping();
            List<PropertyInputBean> properties = properties();

            Workbook wb = service.exportInstances(objectTypeId, cds, infoObjectList, properties, valueMappings);

            try (FileOutputStream os = new FileOutputStream(exportFilePath)) {
                wb.write(os);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    wb.close();
                } catch (IOException e) {
                    System.out.println("workbook close failed!");
                    e.printStackTrace();
                }
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void mergeAllProperties() {
    }

    private Map<String, Map<String, String>> valueMapping() {
        PropertyValueMappingInputBean inputBean1 = new PropertyValueMappingInputBean();
        inputBean1.setName("projectStatus");
        inputBean1.setDesc("项目状态");
        Map<String, String> mapping1 = new HashMap<>();
        mapping1.put("1", "成功");
        mapping1.put("2", "进行中");
        mapping1.put("3", "失败");
        mapping1.put("4", "未知状态");
        inputBean1.setValueMapping(mapping1);

        PropertyValueMappingInputBean inputBean2 = new PropertyValueMappingInputBean();
        inputBean2.setName("projectType");
        inputBean2.setDesc("工程类型");
        Map<String, String> mapping2 = new HashMap<>();
        mapping2.put("001", "类型1");
        mapping2.put("002", "类型2");
        inputBean2.setValueMapping(mapping2);

        List<PropertyValueMappingInputBean> inputBeanList = new ArrayList<>();
        inputBeanList.add(inputBean1);
        inputBeanList.add(inputBean2);
        Map<String, Map<String, String>> valueMapping = service.processValueMapping(inputBeanList);
        System.out.println(valueMapping);
        return valueMapping;
    }


    private List<PropertyInputBean> properties() {
        String str = "[{\"desc\":\"ID\",\"name\":\"ID\"},{\"desc\":\"NAME\",\"name\":\"NAME\"},{\"desc\":\"项目状态\"," +
                "\"name\":\"projectStatus\"},{\"desc\":\"工程类型\",\"name\":\"projectType\"}]";

        List<PropertyInputBean> properties = JSON.parseArray(str, PropertyInputBean.class);
        return properties;
    }


    private ExcelExportInputBean parserExcelExportInput() {
//        String str = "{\"object_type_id\":\"projectV1\",\"properties\":[{\"desc\":\"ID\",\"name\":\"ID\"},
//        {\"desc\":\"NAME\",\"name\":\"NAME\"},{\"desc\":\"项目状态\",\"name\":\"projectStatus\"},
//        {\"desc\":\"工程类型\",\"name\":\"projectType\"}],\"query_input\":{\"page_index\":0,\"page_size\":50},
//        \"value_mapping\":[{\"desc\":\"项目状态\",\"name\":\"projectStatus\",\"value_mapping\":
//        {\"1\":\"成功\",\"2\":\"进行中\",\"3\":\"失败\",\"4\":\"未知状态\"}},
//        {\"desc\":\"工程类型\",\"name\":\"projectType\",\"value_mapping\":
//        {\"001\":\"类型1\",\"002\":\"类型2\"}}]}";
        String str = "{\"object_type_id\":\"projectV1\",\"query_input\":{\"page_index\":0,\"page_size\":50}," +
                "\"value_mapping\":[{\"desc\":\"项目状态\",\"name\":\"projectStatus\",\"value_mapping\":" +
                "{\"1\":\"成功\",\"2\":\"进行中\",\"3\":\"失败\",\"4\":\"未知状态\"}},{\"desc\":\"工程类型\",\"name\"" +
                ":\"projectType\",\"value_mapping\":{\"001\":\"类型1\",\"002\":\"类型2\"}}]}";

        ExcelExportInputBean exportInputBean = JSON.parseObject(str, ExcelExportInputBean.class);
        return exportInputBean;
    }
}