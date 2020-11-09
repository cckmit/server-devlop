package com.glodon.cim.serivce;

import com.glodon.pcop.cim.common.common.CallBackBean;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


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
