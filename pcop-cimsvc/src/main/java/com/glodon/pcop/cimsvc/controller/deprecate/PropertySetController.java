package com.glodon.pcop.cimsvc.controller.deprecate;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.glodon.pcop.cimsvc.model.PropertySetBean;
import com.glodon.pcop.cimsvc.model.PropertySetTypeBean;
import com.glodon.pcop.cimsvc.service.PropertySetService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author yuanjk(yuanjk@glodon.com), 2018-07-23 14:00:24
 *
 */
// @Api(tags = "v1--属性集")
// @RestController
@RequestMapping("/abort")
public class PropertySetController {
	static Logger log = LoggerFactory.getLogger(PropertySetController.class);

	@Autowired
	private PropertySetService psService;

	@ApiOperation(value = "对象所有属性集", notes = "获取指定对象的所有属性集（包括继承的），若未提供objectTypeId则返回默认base属性集", response = PropertySetBean.class, responseContainer = "List")
	@RequestMapping(value = "/propertySet", method = RequestMethod.GET)
	@Deprecated
	public ReturnInfo getObjectTypePeopertySets(@RequestParam(required = false) String objectTypeId,
			@RequestHeader(name = "PCOP-USERID") String creator,
			@RequestHeader(name = "PCOP-TENANTID") String tenantId) {
		log.info("getObjectTypePeopertySets(objectTypeId={})", objectTypeId);
		ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
				psService.getPropertySets(objectTypeId));
		return ri;
	}

	@ApiOperation(value = "默认属性集", notes = "获取默认base属性集", response = PropertySetBean.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
			@ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header") })
	@RequestMapping(value = "/defaultPropertySet", method = RequestMethod.GET)
	public ReturnInfo getDefaultPeopertySets(@RequestHeader(name = "PCOP-USERID") String creator,
			@RequestHeader(name = "PCOP-TENANTID") String tenantId) {
		log.info("getDefaultPeopertySets()");
		ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
				psService.getDefaultBasePropertySet());
		return ri;
	}

	@ApiOperation(value = "对象所有属性集", notes = "获取指定对象的所有属性集（包括继承的），若未提供objectTypeId则返回默认base属性集", response = PropertySetBean.class, responseContainer = "List")
	@RequestMapping(value = "/{objectTypeId}/propertySet", method = RequestMethod.GET)
	public ReturnInfo getObjectTypeAllPeopertySets(@PathVariable String objectTypeId,
			@RequestHeader(name = "PCOP-USERID") String creator,
			@RequestHeader(name = "PCOP-TENANTID") String tenantId) {
		log.info("getObjectTypePeopertySets(objectTypeId={})", objectTypeId);
		ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), psService.getAllPropertySets(objectTypeId));
		return ri;
	}

	@ApiOperation(value = "属性集类型", notes = "获取已经配置的所有属性集类型", response = PropertySetTypeBean.class, responseContainer = "List")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
			@ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header") })
	@RequestMapping(value = "/propertySetTypes", method = RequestMethod.GET)
	public ReturnInfo getAllPropertySetType(@RequestHeader(name = "PCOP-USERID") String creator,
			@RequestHeader(name = "PCOP-TENANTID") String tenantId) {
		log.info("getAllPropertySetType(userId={}, tenantId={})", creator, tenantId);
		ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
				psService.getAllPropertySetTypes(tenantId));
		return ri;
	}


	@ApiOperation(value = "删除属性集", notes = "根据属性集ID，删除指定的属性集", response = Boolean.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "propertySetId", value = "属性集ID", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
			@ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header") })
	@RequestMapping(value = "/propertySet/{propertySetId}", method = RequestMethod.DELETE)
	public ReturnInfo deletePeopertySet(@PathVariable String propertySetId,
												@RequestHeader(name = "PCOP-USERID") String creator,
												@RequestHeader(name = "PCOP-TENANTID") String tenantId) {
		log.info("deletePeopertySet(propertySetId={})", propertySetId);
		ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), psService.deletePropertySet(propertySetId));
		return ri;
	}


	@ApiOperation(value = "属性集的属性", notes = "根据属性集ID，返回该属性集及其所有属性", response = DatasetVO.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "dataSetId", value = "属性集ID", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
			@ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header") })
	@RequestMapping(value = "/dataSet/{dataSetId}", method = RequestMethod.GET)
	public ReturnInfo getDataSet(@PathVariable String dataSetId,
										@RequestHeader(name = "PCOP-USERID") String creator,
										@RequestHeader(name = "PCOP-TENANTID") String tenantId) throws EntityNotFoundException {
		log.info("getDataSet(dataSetId={})", dataSetId);
		ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), psService.getDataSetAndProperty(dataSetId));
		return ri;
	}


}
