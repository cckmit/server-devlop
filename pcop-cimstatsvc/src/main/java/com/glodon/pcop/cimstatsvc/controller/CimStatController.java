package com.glodon.pcop.cimstatsvc.controller;


import com.glodon.pcop.cim.common.common.ReturnInfo;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceCache.CimCacheManager;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cimstatsvc.dao.DbExecute;
import com.glodon.pcop.cimstatsvc.model.CommonTagData;
import com.glodon.pcop.cimstatsvc.service.CimStatService;
import com.glodon.pcop.cimstatsvc.service.CommonTagStatService;
import com.glodon.pcop.cimstatsvc.model.StatParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.glodon.pcop.cimstatsvc.dao.DbExecute.executeQuery;

@Api(value = "/test", tags = "cim统计结果")
@RestController
@Component
public class CimStatController {
    private static String  cacheName = "CIM_STAT_SVC_CACHE_TAG";
    @Autowired
    private CimStatService cimStatService;

    @Autowired
    private CommonTagStatService commonTagStatService;

    @ApiOperation(value = "旧版统计接口", notes = "简单统计")
    @PostMapping("/infoObjectStat")

    public ReturnInfo simpleCountOld(@RequestBody StatParameter[] statParameterList,
                                     @RequestHeader(name = "PCOP-USERID") String creator,
                                     @RequestHeader(name = "PCOP-TENANTID") String tenantId) {

        List<Map<String, Object>> ss = cimStatService.stat(statParameterList, tenantId);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), ss);


        return ri;
    }

    @ApiOperation(value = "统计接口", notes = "简单统计")
    @PostMapping("/instancesStat")

    public ReturnInfo instancesStat(@RequestBody StatParameter[] statParameters,
                                    @RequestHeader(name = "PCOP-USERID") String creator,
                                    @RequestHeader(name = "PCOP-TENANTID") String tenantId) {

        List<Map<String, Object>> ss = cimStatService.stat(statParameters, tenantId);

        //记录查询的结果
        Map<String, Boolean> signMap = new HashMap<>();
        for (int i = 0; i < statParameters.length; i++) {
            signMap.put(statParameters[i].getStat_item(), false);
        }

        Map<String, String> propertyMap = new HashMap<>();
        for (int i = 0; i < statParameters.length; i++) {
            propertyMap.put(statParameters[i].getStat_item(), statParameters[i].getProperty());
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

    public ReturnInfo commonTagStat(@RequestBody StatParameter[] statParameters,
                                    @RequestHeader(name = "PCOP-USERID") String creator,
                                    @RequestHeader(name = "PCOP-TENANTID") String tenantId,
                                    @PathVariable("tagName") String tagName) {
        List<StatParameter> statParameterList = new ArrayList<>();
        for (int i = 0; i < statParameters.length; i++) {
            statParameterList.add(statParameters[i]);
        }

        CommonTagData ss = new CommonTagData();

        String  cacheKey = JSONArray.fromObject(statParameterList).toString()+tenantId+tagName;
        Cache<String, CommonTagData> cache = CimCacheManager.getOrCreateCache(cacheName, String.class, CommonTagData.class);
        if (cache != null) {
            if (cache.containsKey(cacheKey)) {
                ss =  cache.get(cacheKey);
                ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), ss);
                return ri;
            }
        }
         CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(DbExecute.dbName);
        try {
            ss = commonTagStatService.commonTagStat(cds, statParameterList, tenantId, tagName);
            cache.put(cacheKey,ss);
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
