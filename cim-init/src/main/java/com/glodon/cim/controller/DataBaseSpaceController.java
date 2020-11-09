package com.glodon.cim.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.cim.serivce.DataBaseSpaceService;
import com.glodon.pcop.cim.common.common.CallBackBean;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.common.util.SHA256Utils;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.concurrent.*;

/**
 * 数据库图空间
 *
 * @author tangd-a
 * @date 2020/7/22 15:35
 */

@Api(tags = "图空间")
@RestController
@RequestMapping(value = "/dataBaseSpace")
public class DataBaseSpaceController {

	private static final Logger log = LoggerFactory.getLogger(DataBaseSpaceController.class);

	@Autowired
	private DataBaseSpaceService dataBaseSpaceService;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${my.signKey}")
	private String signKey;


	@ApiOperation(value = "初始化创建图空间", notes = "初始化创建图空间", response = boolean.class)
	@RequestMapping(path = "/init", method = RequestMethod.POST)
	public ReturnInfo init(@RequestBody CallBackBean callBackBean) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		log.info("init(callBackBean={})", objectMapper.writeValueAsString(callBackBean));
		String message = SHA256Utils.sha256_HMAC(MessageFormat.format("appCode={0}&appKey={1}&appName={2}&contactEmail={3}&contactPhone={4}&resourceId={5}&signKey={6}&timestamp={7}&userId={8}", callBackBean.getAppCode(), callBackBean.getAppkey(), callBackBean.getAppName(),
				callBackBean.getContactEmail(), callBackBean.getContactPhone(), callBackBean.getResourceId(),
				signKey, String.valueOf(callBackBean.getTimestamp()), callBackBean.getUserId()), signKey);
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		if (StringUtils.equals(callBackBean.getSignature(), message)) {
			executorService.submit(() -> dataBaseSpaceService.init(callBackBean));
			return new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200);
		} else {
			return new ReturnInfo(EnumWrapper.CodeAndMsg.E05080004);
		}

	}


}
