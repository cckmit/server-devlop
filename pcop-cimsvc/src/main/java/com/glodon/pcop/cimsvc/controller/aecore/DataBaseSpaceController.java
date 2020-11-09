package com.glodon.pcop.cimsvc.controller.aecore;

import com.glodon.pcop.cim.common.common.CallBackBean;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.model.output.ConnectedOutputBean;
import com.glodon.pcop.cimsvc.service.aecore.DataBaseSpaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * 数据库图空间
 * @author tangd-a
 * @date 2020/7/22 15:35
 */

@Api(tags = "主数据地图")
@RestController
@RequestMapping(value = "/dataBaseSpace")
public class DataBaseSpaceController {

	private static final Logger log = LoggerFactory.getLogger(DataBaseSpaceController.class);

	@Autowired
	private DataBaseSpaceService dataBaseSpaceService;


	final String signKey = "gyPscHZoGVTGYfOu";


	@ApiOperation(value = "初始化创建图空间", notes = "初始化创建图空间", response = ConnectedOutputBean.class)
	@RequestMapping(path = "/init", method = RequestMethod.POST)
	public ReturnInfo init(@RequestBody CallBackBean callBackBean) {
		log.info("init(callBackBean={})", callBackBean);
		boolean flag = dataBaseSpaceService.init(callBackBean);
		ReturnInfo ri;
		if(flag){
			ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200);
		}else{
			ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200);
		}
		return ri;
	}







}
