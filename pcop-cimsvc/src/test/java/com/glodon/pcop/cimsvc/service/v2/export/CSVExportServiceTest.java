package com.glodon.pcop.cimsvc.service.v2.export;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.excel.PropertyInputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.service.MinioService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CSVExportServiceTest {

    private static String spaceName = "pcopcim";
    private static String tenantId = "1";
    CSVExportService service = new CSVExportService();

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = spaceName;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void exportInstancesAsCSV() {
    }

    @Test
    public void exportInstances() throws IOException {
        CimDataSpace cds = null;
        try {
            String tenantId = "1";
            String objectTypeId = "china_building_sample_0424";
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);

            InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
            List<InfoObject> infoObjectList = infoObjectDef.getObjects(null).getInfoObjects();

            String str = "[{\"desc\":\"ID\",\"name\":\"ID\"},{\"desc\":\"NAME\",\"name\":\"name\"},{\"desc\":\"节点类型\"," +
                    "\"name\":\"nodeType\"},{\"desc\":\"创建时间\",\"name\":\"createTime\"},{\"desc\":\"层级\"," +
                    "\"name\":\"level\"},{\"desc\":\"显示顺序\",\"name\":\"idx\"},{\"desc\":\"更新时间\",\"name\":\"updateTime\"}" +
                    ",{\"desc\":\"CIM_GeographicInformation\",\"name\":\"CIM_GeographicInformation\"}]";

//            List<PropertyInputBean> properties = JSON.parseArray(str, PropertyInputBean.class);


            Map<String, String> properties = new HashMap<>();
            properties.put("ID", "ID");
            properties.put("name", "名称");
            properties.put("osm_id", "元素ID");
            properties.put("CIM_GeographicInformation", "空间信息");

            System.out.println("properties: " + JSON.toJSONString(properties));
            String exportFilePath = "G:\\tmp\\csv_test\\export_test_china_building_sample_0424-v2.csv";
            try (CSVPrinter printer = new CSVPrinter(new FileWriter(exportFilePath), CSVFormat.EXCEL)) {
                service.exportInstances(printer, cds, infoObjectList, properties);
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

    @Test
    public void setMimeTypeConfig() {
    }


    @Test
    public void jsonList() {
        String[] pros = {"a", "b", "c"};

        System.out.println(JSON.toJSONString(pros));

//        String proStr = "[\"a1\",\"b2\",\"c3\"]";
//        String proStr = "[]";
        String proStr = "";
        pros = JSON.parseObject(proStr, String[].class);

        System.out.println("Size="+pros.length);
        System.out.println("2nd element="+pros[1]);
    }
}