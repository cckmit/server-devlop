package com.glodon.pcop.cimsvc.controller.dataGraph;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.model.input.GraphObjectTypeInputBean;
import com.glodon.pcop.cimsvc.model.output.ConnectedOutputBean;
import com.glodon.pcop.cimsvc.model.output.RelationShipData;
import com.glodon.pcop.cimsvc.model.vo.InstanceRelationsVO;
import com.glodon.pcop.cimsvc.service.dataGraph.DataGraphService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author tangd-a
 * @date 2020/6/16 16:21
 */
@Api(tags = "主数据地图")
@RestController
@RequestMapping(value = "/dataGraph")
public class DataGraphController {

	private static final Logger log = LoggerFactory.getLogger(DataGraphController.class);


	@Autowired
	private DataGraphService dataGraphService;


	@ApiOperation(value = "对象类型关系图展示", notes = "对象类型关系图展示", response = ConnectedOutputBean.class)
	@RequestMapping(path = "/objectTypesRelationShipView", method = RequestMethod.POST)
	public ReturnInfo objectTypesRelationShpView(@RequestBody GraphObjectTypeInputBean conditions,
								   @RequestHeader(name = "PCOP-USERID", required = false) String userId,
								   @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) {
		log.info("objectTypesRelationShipView(conditions={})", conditions);
		RelationShipData data = dataGraphService.relationshipsData(userId, tenantId, conditions);
		ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, data);
		return ri;
	}


	@ApiOperation(value = "实例关系图展示", notes = "实例关系图展示", response = ConnectedOutputBean.class)
	@RequestMapping(path = "/instancesRelationShipView/{instanceRID}", method = RequestMethod.POST)
	public ReturnInfo instancesRelationShipView(@PathVariable String instanceRID,
												 @RequestHeader(name = "PCOP-USERID", required = false) String userId,
												 @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) {
		log.info("instancesRelationShipView(instanceRID={})", instanceRID);
		InstanceRelationsVO data = dataGraphService.loadInstanceRelationData(instanceRID);
		ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, data);
		return ri;
	}




}
