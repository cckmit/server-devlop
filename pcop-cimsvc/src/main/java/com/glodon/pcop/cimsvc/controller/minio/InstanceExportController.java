package com.glodon.pcop.cimsvc.controller.minio;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.excel.PropertyInputBean;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.excel.ExcelExportInputBean;
import com.glodon.pcop.cimsvc.service.v2.FileDataExportService;
import com.glodon.pcop.cimsvc.service.v2.export.CSVExportService;
import com.glodon.pcop.cimsvc.service.v2.export.ExcelExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "文件数据导入")
@Controller
@ResponseBody
public class InstanceExportController {
    private static Logger log = LoggerFactory.getLogger(InstanceExportController.class);

    @Autowired
    private FileDataExportService exportService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private CSVExportService csvExportService;

    @ApiOperation(value = "实例数据导出", notes = "单类型实例数据已Excel格式导出")
    @RequestMapping(value = "/export/excel", method = RequestMethod.GET, produces = "application/vnd.ms-excel")
    @ResponseBody
    public void exportInstanceDataAsExcel(@RequestParam String objectTypeId,
                                          @RequestParam(required = false) String properties,
                                          @RequestHeader(name = "PCOP-USERID") String userId,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId,
                                          HttpServletResponse response) throws EntityNotFoundException {
        log.info("exportInstanceDataAsExcel(objectTypeId={}, properties={})", objectTypeId, properties);
        List<PropertyInputBean> propertyBeans = new ArrayList<>();
        if (StringUtils.isNotBlank(properties)) {
            propertyBeans = JSON.parseArray(properties, PropertyInputBean.class);
        }
        exportService.exportInstancesAsExcel(tenantId, objectTypeId, propertyBeans, response);
    }

    @ApiOperation(value = "实例数据导出", notes = "单类型实例数据已Excel格式导出")
    @RequestMapping(value = "/export/excel", method = RequestMethod.POST, produces = "application/vnd.ms-excel")
    @ResponseBody
    public void exportInstanceDataAsExcelPost(@RequestParam String objectTypeId,
                                              @RequestBody(required = false) List<PropertyInputBean> properties,
                                              @RequestHeader(name = "PCOP-USERID") String userId,
                                              @RequestHeader(name = "PCOP-TENANTID") String tenantId,
                                              HttpServletResponse response) throws EntityNotFoundException {
        log.info("exportInstanceDataAsExcelPost(objectTypeId={}, properties={})", objectTypeId, JSON.toJSON(properties));

        exportService.exportInstancesAsExcel(tenantId, objectTypeId, properties, response);
    }

    @ApiOperation(value = "实例数据导出V2", notes = "单类型实例数据以Excel格式导出V2")
    @RequestMapping(value = "/v2/export/excel", method = RequestMethod.POST, produces = "application/vnd.ms-excel")
    @ResponseBody
    public void exportInstanceDataAsExcelPostV2(@RequestBody(required = true) ExcelExportInputBean exportInput,
                                                @RequestHeader(name = "PCOP-USERID") String userId,
                                                @RequestHeader(name = "PCOP-TENANTID") String tenantId,
                                                HttpServletResponse response)
            throws EntityNotFoundException, InputErrorException {
        log.info("exportInstanceDataAsExcelPostV2(exportInput={})", exportInput);
        excelExportService.exportInstancesAsExcel(tenantId, exportInput, response);
    }

    @ApiOperation(value = "实例数据导出CSV", notes = "单类型实例数据已CSV格式导出")
    @RequestMapping(value = "/export/data.csv", method = RequestMethod.GET, produces = "text/csv")
    @CrossOrigin("*")
    public void exportInstanceDataAsCSV(@RequestParam String objectTypeId,
                                        @RequestParam(required = false) String properties,
                                        @RequestParam(name = "PCOP-TENANTID") String tenantId,
                                        HttpServletResponse response) throws EntityNotFoundException, IOException {
        log.info("exportInstanceDataAsExcel(objectTypeId={}, properties={})", objectTypeId, properties);
        List<PropertyInputBean> propertyBeans = new ArrayList<>();
//        if (StringUtils.isNotBlank(properties)) {
//            propertyBeans = JSON.parseArray(properties, PropertyInputBean.class);
//        }
        csvExportService.exportInstancesAsCSV(tenantId, objectTypeId, properties, response);
    }

}
