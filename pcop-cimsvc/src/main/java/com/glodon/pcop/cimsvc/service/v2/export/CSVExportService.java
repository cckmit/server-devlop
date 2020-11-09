package com.glodon.pcop.cimsvc.service.v2.export;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.*;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.config.properties.MimeTypeConfig;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.service.MinioService;
import com.glodon.pcop.cimsvc.util.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

@Service
public class CSVExportService {
    private static Logger log = LoggerFactory.getLogger(CSVExportService.class);
    @Autowired
    private MimeTypeConfig mimeTypeConfig;

    public void exportInstancesAsCSV(String tenantId, String objectTypeId, String propertiesStr, HttpServletResponse response)
            throws EntityNotFoundException, IOException {
        String fileName = DateUtil.getCurrentDateReadable() + ".csv";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
            if (infoObjectDef == null) {
                throw new EntityNotFoundException("info object def not found");
            }
            InfoObjectRetrieveResult retrieveResult = infoObjectDef.getObjects(null);
            List<InfoObject> infoObjectList = retrieveResult.getInfoObjects();

            Map<String, String> allProperties = mergeAllProperties(infoObjectDef);
            Map<String, String> exportProperties = new HashMap<>();
            if (StringUtils.isNotBlank(propertiesStr)) {
//                String[] keys = JSON.parseObject(propertiesStr, String[].class);
                String[] keys = propertiesStr.split(",");
                for (String k : keys) {
                    exportProperties.put(k, allProperties.get(k.trim()));
                }
            } else {
                exportProperties = allProperties;
            }

            try (PrintStream ps = new PrintStream(response.getOutputStream(), true, "UTF-8")) {
                CSVPrinter csvPrinter = new CSVPrinter(ps, CSVFormat.EXCEL);
                response.setContentType(mimeTypeConfig.getMimeType("csv"));
                response.setCharacterEncoding(MinioService.UTF8);
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"");
                response.setStatus(HttpStatus.SC_OK);
                exportInstances(csvPrinter, cds, infoObjectList, exportProperties);
            } catch (Exception e) {
                response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                log.error("write excel to response failed", e);
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }


    public void exportInstances(CSVPrinter csvPrinter, CimDataSpace cds, List<InfoObject> infoObjectList,
                                Map<String, String> exportProperties) throws IOException {
        int columnSize = exportProperties.size();
        List<String> keys = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        for (Map.Entry<String, String> entry : exportProperties.entrySet()) {
            keys.add(entry.getKey());
            headers.add(entry.getValue());
        }
        csvPrinter.printRecord(headers);
        List<List<Object>> instanceData = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(infoObjectList)) {
            for (InfoObject infoObject : infoObjectList) {
                Fact fact;
                try {
                    fact = cds.getFactById(infoObject.getObjectInstanceRID());
                    if (fact != null) {
                        List<Object> oneRecord = new LinkedList<>();
                        for (int i = 0; i < columnSize; i++) {
                            String key = keys.get(i);
                            if (fact.hasProperty(key) && fact.getProperty(key) != null) {
                                Object value = fact.getProperty(key).getPropertyValue();
                                oneRecord.add(value);
                            } else {
                                oneRecord.add(null);
                            }
                        }
                        instanceData.add(oneRecord);
                    }
                } catch (CimDataEngineRuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        csvPrinter.printRecords(instanceData);
    }

    public Map<String, String> mergeAllProperties(InfoObjectDef infoObjectDef) {
        DatasetDef baseDatasetDef = infoObjectDef.getBaseDatasetDef();

        List<PropertyTypeDef> basePropertyTypeDefs = baseDatasetDef.getPropertyTypeDefs();
        List<PropertyTypeDef> generalPropertyTypeDefs = infoObjectDef.getPropertyTypeDefsOfGeneralDatasets();

        Map<String, String> allProperties = new HashMap<>();
        for (PropertyTypeDef typeDef : basePropertyTypeDefs) {
            allProperties.put(typeDef.getPropertyTypeName(), typeDef.getPropertyTypeDesc());
        }

        if (generalPropertyTypeDefs != null) {
            for (PropertyTypeDef typeDef : generalPropertyTypeDefs) {
                allProperties.put(typeDef.getPropertyTypeName(), typeDef.getPropertyTypeDesc());
            }
        }

        return allProperties;
    }

    public void setMimeTypeConfig(MimeTypeConfig mimeTypeConfig) {
        this.mimeTypeConfig = mimeTypeConfig;
    }
}
