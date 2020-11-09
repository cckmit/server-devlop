package com.glodon.pcop.cimsvc.service.v2;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.excel.PropertyInputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class FileDataExportServiceTest {
    private static FileDataExportService exportService;

    @Before
    public void setUp() throws Exception {
        exportService = new FileDataExportService();
        CimConstants.defauleSpaceName = "pcopcim";
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void exportInstancesAsExcel() {
    }

    @Test
    public void mergeAllProperties() {
    }

    @Test
    public void exportInstances() {
        CimDataSpace cds = null;
        try {
            String tenantId = "3";
            String objectTypeId = "xinjianchangjingmuluyuanjk0919";
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);

            InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);

            String str = "[{\"desc\":\"ID\",\"name\":\"ID\"},{\"desc\":\"NAME\",\"name\":\"NAME\"},{\"desc\":\"节点类型\"," +
                    "\"name\":\"nodeType\"},{\"desc\":\"创建时间\",\"name\":\"createTime\"},{\"desc\":\"层级\"," +
                    "\"name\":\"level\"},{\"desc\":\"显示顺序\",\"name\":\"idx\"},{\"desc\":\"更新时间\",\"name\":\"updateTime\"}]";

            List<PropertyInputBean> properties = JSON.parseArray(str, PropertyInputBean.class);
            // List<PropertyInputBean> properties = exportService.mergeAllProperties(infoObjectDef);
            System.out.println("properties: " + JSON.toJSONString(properties));

            String exportFilePath = "G:\\tmp\\excel_test\\export_test.xls";

            Workbook wb = exportService.exportInstances(objectTypeId, cds, infoObjectDef, properties);
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
    public void jsonDes() {
        String str = "[{\"desc\":\"ID\",\"name\":\"ID\"},{\"desc\":\"NAME\",\"name\":\"NAME\"},{\"desc\":\"节点类型\"," +
                "\"name\":\"nodeType\"},{\"desc\":\"创建时间\",\"name\":\"createTime\"},{\"desc\":\"层级\"," +
                "\"name\":\"level\"},{\"desc\":\"显示顺序\",\"name\":\"idx\"},{\"desc\":\"更新时间\",\"name\":\"updateTime\"}]";

        List<PropertyInputBean> properties = JSON.parseArray(str, PropertyInputBean.class);

        System.out.println("property size: " + properties.size());
    }

}