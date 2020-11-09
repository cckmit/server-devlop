package com.glodon.pcop.cimsvc.service.aecore;

import com.glodon.pcop.cim.common.common.CallBackBean;
import com.glodon.pcop.cim.common.util.SHA256Utils;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.controller.dataGraph.DataGraphController;
import com.glodon.pcop.cimsvc.model.output.RelationShipData;
import jline.internal.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author tangd-a
 * @date 2020/7/22 15:37
 */
@Service
public class DataBaseSpaceService {

	private static final Logger log = LoggerFactory.getLogger(DataBaseSpaceService.class);

	public boolean init(CallBackBean callBackBean) {
		boolean flag = false;
		String spaceName = callBackBean.getAppCode();
		CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(spaceName, null);
		if (!cimModelCore.isCimSpaceExist()) {
			cimModelCore.initCimSpace();
			flag = true;
		}else {
			log.info("spaceName:{} is exist",spaceName);
		}
		return flag;
	}
}
