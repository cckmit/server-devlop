package com.glodon.pcop.cimsvc.service.v2.export;

import com.glodon.pcop.cim.common.model.excel.PropertyInputBean;
import com.glodon.pcop.cim.common.model.excel.PropertyValueMappingInputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.*;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.config.properties.MimeTypeConfig;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.excel.ExcelExportInputBean;
import com.glodon.pcop.cimsvc.model.v2.InstancesQueryInput;
import com.glodon.pcop.cimsvc.service.MinioService;
import com.glodon.pcop.cimsvc.util.DateUtil;
import com.glodon.pcop.cimsvc.util.condition.QueryConditionParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

@Service
public class ExcelExportService {
    private static Logger log = LoggerFactory.getLogger(ExcelExportService.class);
    @Autowired
    private MimeTypeConfig mimeTypeConfig;

    public void exportInstancesAsExcel(String tenantId, ExcelExportInputBean exportInput, HttpServletResponse response)
            throws EntityNotFoundException, InputErrorException {
        String fileName = DateUtil.getCurrentDateReadable() + ".xls";
        String objectTypeId = exportInput.getObjectTypeId();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
            if (infoObjectDef == null) {
                throw new EntityNotFoundException("info object def not found");
            }
            List<InfoObject> infoObjectList = queryInstances(cds, objectTypeId, infoObjectDef, exportInput.getQueryInput());

            List<PropertyInputBean> properties = exportInput.getProperties();
            if (CollectionUtils.isEmpty(properties)) {
                properties = mergeAllProperties(infoObjectDef);
            }

            Map<String, Map<String, String>> valueMapping = processValueMapping(exportInput.getValueMapping());

            Workbook wb = exportInstances(objectTypeId, cds, infoObjectList, properties, valueMapping);

            try (OutputStream os = response.getOutputStream()) {
                response.setContentType(mimeTypeConfig.getMimeType("xls"));
                response.setHeader(MinioService.CONTENT_DISPOSITION,
                        "attachment;filename=" + URLEncoder.encode(fileName, MinioService.UTF8));
                wb.write(os);
                response.setStatus(HttpStatus.SC_OK);
            } catch (Exception e) {
                response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                log.error("write excel to response failed", e);
            } finally {
                if (wb != null) {
                    try {
                        wb.close();
                    } catch (IOException e) {
                        log.error("close workbook failed", e);
                    }
                }
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    public List<InfoObject> queryInstances(CimDataSpace cds, String objectTypeId, InfoObjectDef infoObjectDef,
                                           InstancesQueryInput queryInput) throws InputErrorException {
        InfoObjectRetrieveResult infoObjectRetrieveResult;
        if (queryInput != null) {
            ExploreParameters exploreParameters = QueryConditionParser.parserQueryInput(cds, objectTypeId, queryInput, false);
            infoObjectRetrieveResult = infoObjectDef.getObjects(exploreParameters);
        } else {
            infoObjectRetrieveResult = infoObjectDef.getObjects(null);
        }
        return infoObjectRetrieveResult.getInfoObjects();
    }

    public Map<String, Map<String, String>> processValueMapping(List<PropertyValueMappingInputBean> inputBeanList) {
        Map<String, Map<String, String>> valueMapping = new HashMap<>();
        if (CollectionUtils.isNotEmpty(inputBeanList)) {
            for (PropertyValueMappingInputBean inputBean : inputBeanList) {
                valueMapping.put(inputBean.getName(), inputBean.getValueMapping());
            }
        }
        return valueMapping;
    }

    public Workbook exportInstances(String objectTypeId, CimDataSpace cds, List<InfoObject> infoObjectList,
                                    List<PropertyInputBean> propertyInput,
                                    Map<String, Map<String, String>> valueMapping) {
        int columnSize = propertyInput.size();
        Workbook wb = new HSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle dateCellStyle = wb.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));

        Sheet sheet = wb.createSheet(objectTypeId);
        int rowNum = 0;
        Row oneRow = sheet.createRow(rowNum);
        for (int i = 0; i < columnSize; i++) {
            PropertyInputBean inputBean = propertyInput.get(i);
            Cell cell = oneRow.createCell(i);
            cell.setCellValue(inputBean.getDesc());
        }

        if (CollectionUtils.isNotEmpty(infoObjectList)) {
            rowNum++;
            for (InfoObject infoObject : infoObjectList) {
                oneRow = sheet.createRow(rowNum);
                Fact fact;
                try {
                    fact = cds.getFactById(infoObject.getObjectInstanceRID());
                    if (fact != null) {
                        for (int i = 0; i < columnSize; i++) {
                            Cell cell = oneRow.createCell(i);
                            String key = propertyInput.get(i).getName();
                            if (fact.hasProperty(key) && fact.getProperty(key) != null) {
                                Object value = fact.getProperty(key).getPropertyValue();
                                //字典转换功能
                                if (valueMapping.containsKey(key)) {
                                    Map<String, String> mapping = valueMapping.get(key);
                                    if (mapping.containsKey(value)) {
                                        value = mapping.get(value);
                                    }
                                }
                                log.debug("value class type: [{}]", value.getClass());
                                if (value instanceof Date) {
                                    cell.setCellValue((Date) value);
                                    cell.setCellStyle(dateCellStyle);
                                } else if (value instanceof Boolean) {
                                    cell.setCellValue((Boolean) value);
                                } else {
                                    cell.setCellValue(value.toString());
                                }
                            }
                        }
                    }
                } catch (CimDataEngineRuntimeException e) {
                    e.printStackTrace();
                }
                rowNum++;
                if (rowNum >= 65536) {
                    log.info("the max row number of one sheet is 65536, others is omitted");
                    break;
                }
            }
        }

        return wb;
    }

    public List<PropertyInputBean> mergeAllProperties(InfoObjectDef infoObjectDef) {
        DatasetDef baseDatasetDef = infoObjectDef.getBaseDatasetDef();

        List<PropertyTypeDef> basePropertyTypeDefs = baseDatasetDef.getPropertyTypeDefs();
        List<PropertyTypeDef> generalPropertyTypeDefs = infoObjectDef.getPropertyTypeDefsOfGeneralDatasets();

        List<PropertyInputBean> allProperties = new ArrayList<>();
        for (PropertyTypeDef typeDef : basePropertyTypeDefs) {
            PropertyInputBean inputBean = new PropertyInputBean();
            inputBean.setName(typeDef.getPropertyTypeName());
            inputBean.setDesc(typeDef.getPropertyTypeDesc());
            allProperties.add(inputBean);
        }

        if (generalPropertyTypeDefs != null) {
            for (PropertyTypeDef typeDef : generalPropertyTypeDefs) {
                PropertyInputBean inputBean = new PropertyInputBean();
                inputBean.setName(typeDef.getPropertyTypeName());
                inputBean.setDesc(typeDef.getPropertyTypeDesc());
                allProperties.add(inputBean);
            }
        }

        return allProperties;
    }

    public void setMimeTypeConfig(MimeTypeConfig mimeTypeConfig) {
        this.mimeTypeConfig = mimeTypeConfig;
    }
}
