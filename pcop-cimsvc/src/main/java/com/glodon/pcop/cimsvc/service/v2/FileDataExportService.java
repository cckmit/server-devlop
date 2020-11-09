package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.common.model.excel.PropertyInputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.PropertyTypeDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.config.properties.MimeTypeConfig;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.service.MinioService;
import com.glodon.pcop.cimsvc.util.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FileDataExportService {
    private static Logger log = LoggerFactory.getLogger(FileDataExportService.class);
    @Autowired
    private MimeTypeConfig mimeTypeConfig;

    public void exportInstancesAsExcel(String tenantId, String objectTypeId, List<PropertyInputBean> properties,
                                       HttpServletResponse response) throws EntityNotFoundException {
        String fileName = DateUtil.getCurrentDateReadable() + ".xls";

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
            if (infoObjectDef == null) {
                throw new EntityNotFoundException("info object def not found");
            }
            // List<PropertyInputBean> properties = exportInputBean.getProperties();
            if (CollectionUtils.isEmpty(properties)) {
                properties = mergeAllProperties(infoObjectDef);
            }
            Workbook wb = exportInstances(objectTypeId, cds, infoObjectDef, properties);

            try (OutputStream os = response.getOutputStream()) {
                response.setContentType(mimeTypeConfig.getMimeType("xls"));
                // response.setHeader("Content-Type", "application/vnd.ms-excel");
                // response.setHeader(MinioService.CONTENT_DISPOSITION,
                // "inline;filename=" + URLEncoder.encode(fileName, MinioService.UTF8));
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

    public Workbook exportInstances(String objectTypeId, CimDataSpace cds, InfoObjectDef infoObjectDef,
                                    List<PropertyInputBean> propertyInput) {
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
        InfoObjectRetrieveResult retrieveResult = infoObjectDef.getObjects(null);
        List<InfoObject> infoObjectList = retrieveResult.getInfoObjects();
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

}
