package com.glodon.pcop.cimsvc.controller.normal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimapi.exception.MinioClientException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.v2.mapping.FileMappingInputBean;
import com.glodon.pcop.cimsvc.model.v2.mapping.FileMappingOutputBean;
import com.glodon.pcop.cimsvc.model.v2.mapping.FileStructBean;
import com.glodon.pcop.cimsvc.service.v2.FileDataExportService;
import com.glodon.pcop.cimsvc.service.v2.FileDataImportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "文件数据导入")
@RestController
public class FileDataImportController {
    private static Logger log = LoggerFactory.getLogger(FileDataImportController.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileDataImportService dataImportService;

    @Autowired
    private FileDataExportService exportService;

    @ApiOperation(value = "解析文件结构", notes = "解析文件结构", response = FileStructBean.class, responseContainer = "list")
    @RequestMapping(value = "/import/struct", method = RequestMethod.GET)
    public ReturnInfo getFileStructure(@RequestParam(name = "fileContentType") String fileContentType,
                                       @RequestParam(name = "fileDataId") String fileDataId,
                                       @RequestHeader(name = "PCOP-USERID") String userId,
                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws MinioClientException, CimDataEngineRuntimeException, CimDataEngineInfoExploreException, InputErrorException {
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                dataImportService.getStruct(tenantId, fileDataId, fileContentType));
        return ri;
    }

    @ApiOperation(value = "导入文件数据", notes = "导入文件数据", response = FileMappingOutputBean.class, responseContainer =
            "list")
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ReturnInfo importData(@RequestBody FileMappingInputBean mapping,
                                 @RequestHeader(name = "PCOP-USERID") String userId,
                                 @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws CimDataEngineInfoExploreException, MinioClientException, CimDataEngineRuntimeException, InputErrorException {
        log.info("importData(mapping={})", mapping);
        List<FileMappingOutputBean> result = dataImportService.startImportTask(tenantId, mapping);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                result);
        return ri;
    }

}
