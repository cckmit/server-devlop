package com.glodon.pcop.cimsvc.controller.stat;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.glodon.pcop.cim.common.common.ReturnInfo;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataAnalysis.StatParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureauImpl.OrientDBCimDataSpaceImpl;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.stat.*;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import com.glodon.pcop.cimsvc.repository.MatchResult;
import com.glodon.pcop.cimsvc.service.stat.BusinessStatService;
import com.glodon.pcop.cimsvc.service.stat.SimpleStatService;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.glodon.pcop.cim.engine.dataServiceFeature.feature.GlobalDimensionFeatures.listGlobalDimensionItems;
import static com.glodon.pcop.cimsvc.util.condition.QueryConditionParser.parserStatConditions;

@Api(tags = "统计功能")
@RestController
@RequestMapping(value = "/stat")
public class SimpleStatController {
    private static final Logger log = LoggerFactory.getLogger(SimpleStatController.class);
    @Autowired
    private SimpleStatService simpleStatService;


    @Autowired
    private BusinessStatService businessStatService;

    @ApiOperation(value = "对象类型单个属性计数", notes = "简单计数")
    @PostMapping("/objectTypeId/{objectTypeId}/property/{propertyName}/count")
    public ReturnInfo simplePropertyCount(@RequestHeader(name = "PCOP-USERID") String creator,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId,
                                          @PathVariable String objectTypeId,
                                          @PathVariable String propertyName) {
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        List<Map<String, Object>> ss = new ArrayList<>();
        try {
            ss = simpleStatService.statCount(cds, tenantId, objectTypeId, propertyName);
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


    @ApiOperation(value = "对象类型单个属性求和", notes = "简单求和")
    @PostMapping("/objectTypeId/{objectTypeId}/property/{propertyName}/sum")
    public ReturnInfo simplePropertySum(@RequestHeader(name = "PCOP-USERID") String creator,
                                        @RequestHeader(name = "PCOP-TENANTID") String tenantId,
                                        @PathVariable String objectTypeId,
                                        @PathVariable String propertyName) {
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        List<Map<String, Object>> ss = new ArrayList<>();
        try {
            ss = simpleStatService.statSum(cds, tenantId, objectTypeId, propertyName);
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

    @ApiOperation(value = "对象类型单个属性计数", notes = "简单计数")
    @PostMapping("/objectTypeId/{objectTypeId}/property/{propertyName}/countByDic")
    public ReturnInfo propertyCount(@RequestHeader(name = "PCOP-USERID") String creator,
                                    @RequestHeader(name = "PCOP-TENANTID") String tenantId,
                                    @PathVariable String objectTypeId,
                                    @PathVariable String propertyName,
                                    @RequestBody DimensionTypeBean dimensionTypeBean) {

        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        List<Map<String, Object>> ss = new ArrayList<>();
        try {
            ss = simpleStatService.statCountByDic(cds, tenantId, dimensionTypeBean, objectTypeId, propertyName);
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


    @ApiOperation(value = "对象类型单个属性求和", notes = "简单求和")
    @PostMapping("/objectTypeId/{objectTypeId}/property/{propertyName}/sumByDic")
    public ReturnInfo propertySum(@RequestHeader(name = "PCOP-USERID") String creator,
                                  @RequestHeader(name = "PCOP-TENANTID") String tenantId,
                                  @PathVariable String objectTypeId,
                                  @PathVariable String propertyName,
                                  @RequestBody DimensionTypeBean dimensionTypeBean) {
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        List<Map<String, Object>> ss = new ArrayList<>();
        try {
            ss = simpleStatService.statSumByDic(cds, tenantId, dimensionTypeBean, objectTypeId, propertyName);
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

    //基于字典的统计
    @ApiOperation(value = "对象类型按字典划分个数", notes = "简单计数")
    @PostMapping("/objectTypeId/{objectTypeId}/countByDic")
    public ReturnInfo simplePropertyCount(@RequestBody StatCondtionBean dicBean,
                                          @RequestHeader(name = "PCOP-USERID") String userId,
                                          @PathVariable("objectTypeId") String objectTypeId,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        StatVO tagNodeStatVO = new StatVO();
        try {
            CIMModelCore targetCIMModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            targetCIMModelCore.setCimDataSpace(cds);
            StatParameters statParameter = new StatParameters();
            List<CommonQueryConditionsBean> queryConditionsBeanList = dicBean.getConditions();
            DimensionTypeBean dimensionTypeBean = dicBean.getDimension();
            statParameter = parserStatConditions(cds, objectTypeId, queryConditionsBeanList, statParameter);
            InfoObjectDef infoObjectDef = targetCIMModelCore.getInfoObjectDef(objectTypeId);

            tagNodeStatVO = simpleStatService.infoObjectWithDicStat(cds, infoObjectDef, dimensionTypeBean, statParameter, tenantId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                tagNodeStatVO);
        return ri;
    }


    public static List<Map<String, Object>> executeQuery(CimDataSpace cimDataSpace, String sql) {
        System.out.println(sql);
        List<Map<String, Object>> list = new ArrayList();
        OrientDBCimDataSpaceImpl orientDBCimDataSpaceImpl = (OrientDBCimDataSpaceImpl) cimDataSpace;
        OResultSet resultSet = orientDBCimDataSpaceImpl.getGraph().getRawGraph().query(sql);
        while (resultSet.hasNext()) {
            Map<String, Object> map = new HashMap<>();
            OResult r = resultSet.next();
            for (String key : r.getPropertyNames()) {
                map.put(key, r.getProperty(key));
            }
            list.add(map);
        }
        resultSet.close();
        return list;
    }

    @ApiOperation(value = "多对象通用分组查询", notes = "简单版,以后会升级但返回结果不会变")
    @PostMapping("/multiObjectGeneralGroupQueryMatch")
    public com.glodon.pcop.cimapi.common.ReturnInfo multiObjectGeneralGroupQueryMatch(@RequestBody String match,
                                                                                      @RequestParam String groupByName,
                                                                                      @RequestParam String containName,
                                                                                      @RequestHeader(name = "PCOP-USERID") String creator,
                                                                                      @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws JsonProcessingException {
        log.info("multiObjectGeneralGroupQueryMatch(userId={},tenantId={} ,match={},groupByName={},containName={})", creator,tenantId,match,groupByName,containName);
        List<MatchResult> matchResults = businessStatService.multiObjectGeneralGroupQueryMatch(match, groupByName, containName);
        com.glodon.pcop.cimapi.common.ReturnInfo ri = new com.glodon.pcop.cimapi.common.ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), matchResults);
        return ri;
    }


    @ApiOperation(value = "对象类型时间区间字典统计", notes = "对象类型时间区间字典统计")
    @PostMapping("/objectTypeId/{objectTypeId}/interval/countByDic")
    public ReturnInfo simplePropertyCount(@RequestBody IntervalWithDimensionBean intervalWithDimensionStatVO,
                                          @RequestHeader(name = "PCOP-USERID") String userId,
                                          @PathVariable("objectTypeId") String objectTypeId,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId) {

        DimensionTypeBean dimensionTypeBean = intervalWithDimensionStatVO.getDimension();
        String format = intervalWithDimensionStatVO.getFormat().toUpperCase();
        String propertyName = intervalWithDimensionStatVO.getPropertyName();
        int times = intervalWithDimensionStatVO.getTimes();
        String sql = "SELECT count(ID) as value," + dimensionTypeBean.getDimensionTypeProperty() + "  as dic, " + propertyName.toString() + ".format(\"" + getFormat(format) + "\") as interval " +
                "FROM `GLD_IH_FACT_" + objectTypeId +
                "` WHERE \"" + tenantId + "\"in OUTE(\"GLD_RELATION_CIM_BUILDIN_RELATIONTYPE_BELONGSTOTENANT\").INV().CIM_BUILDIN_TENANT_ID " +
                "GROUP BY dic , interval ";
        CimDataSpace cds = null;
        List<Map<String, Object>> statDatalist = new ArrayList<>();
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            statDatalist = executeQuery(cds, sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        //获取关联的字典
        List<Map<String, Object>> dicDataList = listGlobalDimensionItems(CimConstants.defauleSpaceName, dimensionTypeBean.getDimensionTypeName());
        List<String> dicKeys = new ArrayList<>();
        List<String> legend = new ArrayList<>();
        HashMap<Object, Object> dicKey2Value = new HashMap<>();
        for (int i = 0; i < dicDataList.size(); i++) {
            String key = dicDataList.get(i).get("key").toString();
            String value = dicDataList.get(i).get("value").toString();
            dicKeys.add(key);
            legend.add(value);
            dicKey2Value.put(key,value);
        }

        //拆分时间序列
        List<String> timeSeries = getTimeSeries(new Date(), times, format);
        Collections.reverse(timeSeries);

        //填充数据
        HashMap<String, HashMap<String, Object>> map = new HashMap<>();
        for (int i = 0; i < dicKeys.size(); i++) {
            HashMap<String, Object> item = new HashMap<>();
            for (int j = 0; j < timeSeries.size(); j++) {
                item.put(timeSeries.get(j), 0);
            }
            map.put(dicKeys.get(i), item);
        }

        //替换真实数据
        for (int i = 0; i < statDatalist.size(); i++) {
            Map<String, Object> item = statDatalist.get(i);
            Object dickey = item.get("dic");
            Object interval = item.get("interval");
            Object value = item.get("value");
            if (dickey != null && interval != null) {
                if (map.get(dickey) != null) {
                    if (map.get(dickey).get(interval) != null) {
                        map.get(dickey).put(interval.toString(), value);
                    }
                }
            }
        }

        //生成数据格式
         List<HashMap<String, Object>> series = new ArrayList<>();
        for (int i = 0; i < dicKeys.size(); i++) {
            List<Object> data = new ArrayList<>();
            for (int j = 0; j < timeSeries.size(); j++) {
                data.add(map.get(dicKeys.get(i)).get(timeSeries.get(j)));
            }
            HashMap<String, Object> item = new HashMap<>();
            item.put("name",dicKey2Value.get(dicKeys.get(i)).toString());
            item.put("data",data);
            series.add(item);
        }


        //返回结果
        HashMap<String, Object> result = new HashMap<>();
        result.put("xAxis", timeSeries);
        result.put("legend", legend);
        result.put("series", series);

        ReturnInfo returnInfo = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), result);
        return returnInfo;

    }


    public static String getFormat(String dateType) {
        String fmtStr = null;
        int CalendarType = Calendar.DAY_OF_MONTH;
        int timeNum = 3600 * 24;
        switch (dateType) {
            case "HOUR":
                fmtStr = "yyyy-MM-dd HH";
                break;
            case "DAY":
                fmtStr = "yyyy-MM-dd";
                break;
            case "MONTH":
                fmtStr = "yyyy-MM";
                break;
            case "YEAR":
                fmtStr = "yyyy";
                break;
            default:
                fmtStr = "yyyy-MM-dd";
                break;
        }
        return fmtStr;
    }


    public static List<String> getTimeSeries(Date startDate, int times, String dateType) {
        String fmtStr = null;
        int CalendarType = Calendar.DAY_OF_MONTH;
        int timeNum = 3600 * 24;
        switch (dateType) {
            case "HOUR":
                fmtStr = "yyyy-MM-dd HH";
                CalendarType = Calendar.HOUR_OF_DAY;
                timeNum = 3600;
                break;
            case "DAY":
                fmtStr = "yyyy-MM-dd";
                CalendarType = Calendar.DAY_OF_MONTH;
                timeNum = 3600 * 24;
                break;
            case "MONTH":
                fmtStr = "yyyy-MM";
                CalendarType = Calendar.MONTH;
                timeNum = 3600 * 24 * 30;
                break;
            case "YEAR":
                fmtStr = "yyyy";
                CalendarType = Calendar.YEAR;
                timeNum = 3600 * 24 * 365;
                break;
            default:
                fmtStr = "yyyy-MM-dd";
                CalendarType = Calendar.DAY_OF_MONTH;
                break;
        }

        List<String> list = new ArrayList<>();
        Calendar startCal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(fmtStr);//此处修改日期格式
        startCal.setTime(startDate);

        //得到两个日期相差的天数
        for (int i = 0; i < times; i++) {
            list.add(sdf.format(startCal.getTime()));
            startCal.add(CalendarType, -1);//此处修改时间单位,   小时/日/星期/月  等等
        }
        return list;
    }
}