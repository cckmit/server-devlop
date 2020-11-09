package com.glodon.pcop.cimsvc.controller.deprecate;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.model.definition.DefinitionsBean;
import com.glodon.pcop.cimsvc.service.DefinitionsBatchService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// @Api(tags = "其他服务")
// @RestController
@RequestMapping(path = "/abort")
public class DefinitionBatchController {
    private static Logger log = LoggerFactory.getLogger(DefinitionBatchController.class);

    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "批量导入定义", notes = "批量导入定义，行业分类，对象，属性集，属性定义", response = boolean.class)
    @RequestMapping(value = "/definitions", method = RequestMethod.POST)
    public ReturnInfo importDefinitionsBatch(@RequestBody DefinitionsBean definitions,
                                             @RequestHeader(name = "PCOP-USERID") String userId,
                                             @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("importDefinitionsBatch(definitions={})", objectMapper.writeValueAsString(definitions));
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200,
                DefinitionsBatchService.addDefinitionsBatch(tenantId, userId, definitions));
        return ri;
    }

}
