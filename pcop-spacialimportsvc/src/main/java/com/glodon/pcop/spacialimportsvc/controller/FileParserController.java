package com.glodon.pcop.spacialimportsvc.controller;

import com.glodon.pcop.spacialimportsvc.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.glodon.pcop.cim.common.common.ReturnInfo;
import com.glodon.pcop.cim.common.model.FileStructureBean;
import com.glodon.pcop.cim.common.util.EnumWrapper.CodeAndMsg;
import com.glodon.pcop.spacialimportsvc.service.ShpFileParserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author yuanjk(yuanjk @ glodon.com), 2018-07-23 14:00:24
 */
@Api("数据导入")
@RestController
@RequestMapping("/")
public class FileParserController {
    static Logger log = LoggerFactory.getLogger(FileParserController.class);

    @Autowired
    private ShpFileParserService shpParser;

    @ApiOperation(value = "解析shp文件结构", notes = "解析Minio objectName指定shp文件的文件结构", response = FileStructureBean.class)
    @RequestMapping(value = "/fileParserShp/{objectName}", method = RequestMethod.GET)
    public ReturnInfo fileParserShp(@PathVariable("objectName") String objectName) {
        log.info("fileParserShp(objectName={})", objectName);

        CodeAndMsg code = CodeAndMsg.E17000200;
        FileStructureBean fsb = new FileStructureBean();
        try {
            if (ConfigProperties.shpParserVersion.equals("v1")) {
                log.info("===old shp parser is used");
                fsb = shpParser.getShpFileStructureV1(objectName);
            } else {
                log.info("===new shp parser is used");
                fsb = shpParser.getShpFileStructure(objectName);
            }
        } catch (Exception e) {
            log.error("解析文件结构失败：objectName={}", objectName);
            code = CodeAndMsg.E17060001;
            e.printStackTrace();
        }
        ReturnInfo ri = new ReturnInfo(code, code.getMsg(), fsb);
        return ri;
    }

}
