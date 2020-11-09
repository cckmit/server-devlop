package com.glodon.pcop.cimsvc.controller.deprecate;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.glodon.pcop.cimsvc.model.PropertyTypeBean;
import com.glodon.pcop.cimsvc.service.PropertyTypeService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author yuanjk(yuanjk@glodon.com), 2018-07-23 14:00:24
 *
 */
// @Api(tags = "v1--属性类型")
// @RestController
@RequestMapping("/abort")
public class PropertyTypeController {
	static Logger log = LoggerFactory.getLogger(PropertyTypeController.class);

	@Autowired
	private PropertyTypeService ptService;

	@ApiOperation(value = "所有属性类型", notes = "查询配置表中所有可用的属性类型", response = PropertyTypeBean.class, responseContainer = "List")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
			@ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header") })
	@RequestMapping(value = "/propertyType", method = RequestMethod.GET)
	public ReturnInfo getAllPropertyType(@RequestHeader(name = "PCOP-USERID") String creator,
										 @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
		log.info("getAllPropertyType()");
		ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
				ptService.getAllPropertieType(tenantId));
		return ri;
	}
}
