package com.glodon.pcop.cimsvc.controller.stat;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.common.ReturnInfo;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cimsvc.controller.spatial.SpatialController;
import com.glodon.pcop.cimsvc.model.stat.CommonTagOutput;
import com.glodon.pcop.cimsvc.model.stat.StatParamBean;
import com.glodon.pcop.cimsvc.model.stat.StatParameterBean;
import com.glodon.pcop.cimsvc.service.stat.StatService;
import com.glodon.pcop.cimsvc.service.stat.CommonTagStatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(tags = "统计功能")
@RestController
@RequestMapping(value = "/stat")

public class StatController {
    private static final Logger log = LoggerFactory.getLogger(StatController.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StatService cimStatService;

    @Autowired
    private CommonTagStatService commonTagStatService;

    @ApiOperation(value = "旧版统计接口", notes = "简单统计")
    @PostMapping("/infoObjectStat")

    public ReturnInfo simpleCountOld(@RequestBody StatParameterBean[] statParameterList,
                                     @RequestHeader(name = "PCOP-USERID") String creator,
                                     @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("{}", objectMapper.writeValueAsString(statParameterList));
        List<Map<String, Object>> ss = cimStatService.stat(statParameterList, tenantId);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), ss);


        return ri;
    }

    @ApiOperation(value = "统计接口", notes = "简单统计")
    @PostMapping("/instancesStat")

    public ReturnInfo instancesStat(@RequestBody StatParameterBean[] statParameters,
                                    @RequestHeader(name = "PCOP-USERID") String creator,
                                    @RequestHeader(name = "PCOP-TENANTID") String tenantId) {


        List<Map<String, Object>> ss = cimStatService.stat(statParameters, tenantId);

        //记录查询的结果
        Map<String, Boolean> signMap = new HashMap<>();
        for (int i = 0; i < statParameters.length; i++) {
            signMap.put(statParameters[i].getStatItem(), false);
        }

        Map<String, String> propertyMap = new HashMap<>();
        for (int i = 0; i < statParameters.length; i++) {
            propertyMap.put(statParameters[i].getStatItem(), statParameters[i].getProperty());
        }

        String propertyName = statParameters[0].getProperty();

        Boolean needSign = false;
        if (statParameters.length > 1) {
            needSign = true;
        }

        List<Map<String, Object>> outputList = new ArrayList<>();
        for (int i = 0; i < ss.size(); i++) {
            Map<String, Object> inputMap = ss.get(i);
            Map<String, Object> outputMap = new HashMap<>();
            List<Map<String, Object>> list = new ArrayList<>();
            if (inputMap.get("_sign_") != null) {
                propertyName = propertyMap.get(inputMap.get("_sign_"));
            }
            //遍历结果
            for (String key : inputMap.keySet()) {
                if (key.equals("_sign_") && needSign) {
                    Map<String, Object> propMap = new HashMap<>();
                    propMap.put("prop", "stat_item");
                    propMap.put("value", inputMap.get(key));
                    signMap.remove(inputMap.get(key));
                    signMap.put((String) inputMap.get(key), true);
                    list.add(propMap);
                } else if (key.contains(propertyName + "Count") || key.contains(propertyName + "Sum")) {
                    outputMap.put("value", inputMap.get(key));
                    continue;
                } else if (!key.equals("_sign_")) {
                    Map<String, Object> propMap = new HashMap<>();
                    propMap.put("prop", key);
                    propMap.put("value", inputMap.get(key));
                    list.add(propMap);
                }
            }
            outputMap.put("keys", list);
            outputList.add(outputMap);
        }

        if (needSign) {
            for (String key : signMap.keySet()) {
                if (!signMap.get(key)) {
                    Map<String, Object> outputMap = new HashMap<>();
                    List<Map<String, Object>> list = new ArrayList<>();
                    Map<String, Object> propMap = new HashMap<>();
                    propMap.put("prop", "stat_item");
                    propMap.put("value", key);
                    list.add(propMap);
                    outputMap.put("keys", list);
                    outputMap.put("value", 0);
                    outputList.add(outputMap);
                }
            }
        }

        if (outputList.size() == 0) {
            Map<String, Object> outputMap = new HashMap<>();
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> propMap = new HashMap<>();
            list.add(propMap);
            outputMap.put("keys", list);
            outputMap.put("value", 0);
            outputList.add(outputMap);
        }

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), outputList);
        return ri;
    }

    @ApiOperation(value = "统计接口", notes = "简单统计")

    @RequestMapping(value = "/commonTagStat/{tagName}", method = RequestMethod.POST)

    public ReturnInfo commonTagStat(@RequestBody StatParameterBean[] statParameters,
                                    @RequestHeader(name = "PCOP-USERID") String creator,
                                    @RequestHeader(name = "PCOP-TENANTID") String tenantId,
                                    @PathVariable("tagName") String tagName) {
        List<StatParameterBean> statParameterList = new ArrayList<>();
        for (int i = 0; i < statParameters.length; i++) {
            statParameterList.add((StatParameterBean)statParameters[i]);
        }
        CommonTagOutput ss = new CommonTagOutput();
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        try {
            ss = commonTagStatService.commonTagStat(cds, statParameterList, tenantId, tagName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), ss);
        return ri;
    }

}
