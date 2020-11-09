package com.glodon.pcop.cimsvc.service.stat;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataAnalysis.InformationStatisticer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataAnalysis.StatFunctionItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataAnalysis.StatParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.GlobalDimensionFeatures;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cimsvc.model.stat.StatVO;
import com.glodon.pcop.cimsvc.model.stat.TagNodeStatVO;
import com.glodon.pcop.cimsvc.model.stat.DimensionTypeBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.glodon.pcop.cim.engine.dataServiceFeature.feature.GlobalDimensionFeatures.listGlobalDimensionItems;

@Service
public class SimpleStatService {


    public List<Map<String, Object>> statCount(CimDataSpace cds, String tenantId, String objectTypeId, String propertyName) {
        StatParameters statParameter = new StatParameters();
        statParameter.setType(objectTypeId);
        List<StatFunctionItem> statFunctionItems = new ArrayList<>();
        StatFunctionItem statFunctionItem = new StatFunctionItem();
        statFunctionItem.setFunctionType(StatFunctionItem.FunctionType.COUNT);
        statFunctionItem.setAttribute(propertyName);
        statFunctionItems.add(statFunctionItem);
        statParameter.setFunctionItemList(statFunctionItems);
        InformationStatisticer informationStat = cds.getInformationStatisticser();
        List<Map<String, Object>> statList = informationStat.statInheritFactByTenantId(statParameter, tenantId);
        List<Map<String, Object>> res = new ArrayList<>();
        Map<String, Object> obj = new HashMap<>();
        obj.put("value", statList.get(0).get(statFunctionItem.getFunctionAttribute()));
        obj.put("name", "总计");
        res.add(obj);
        return res;
    }


    public List<Map<String, Object>> statSum(CimDataSpace cds, String tenantId, String objectTypeId, String propertyName) {
        StatParameters statParameter = new StatParameters();
        statParameter.setType(objectTypeId);
        List<StatFunctionItem> statFunctionItems = new ArrayList<>();
        StatFunctionItem statFunctionItem = new StatFunctionItem();
        statFunctionItem.setFunctionType(StatFunctionItem.FunctionType.SUM);
        statFunctionItem.setAttribute(propertyName);
        statFunctionItems.add(statFunctionItem);
        statParameter.setFunctionItemList(statFunctionItems);
        InformationStatisticer informationStat = cds.getInformationStatisticser();
        List<Map<String, Object>> statList = informationStat.statInheritFactByTenantId(statParameter, tenantId);

        List<Map<String, Object>> res = new ArrayList<>();
        Map<String, Object> obj = new HashMap<>();
        obj.put("value", statList.get(0).get(statFunctionItem.getFunctionAttribute()));
        obj.put("name", "总和");
        res.add(obj);

        return res;

    }


    public List<Map<String, Object>> statCountByDic(CimDataSpace cds, String tenantId, DimensionTypeBean dimensionTypeBean, String objectTypeId, String propertyName) {

        StatParameters statParameter = new StatParameters();
        statParameter.setType(objectTypeId);
        List<String> groupPro = new ArrayList<>();
        groupPro.add(dimensionTypeBean.getDimensionTypeProperty());
        statParameter.setGroupAttributes(groupPro);
        List<StatFunctionItem> statFunctionItems = new ArrayList<>();
        StatFunctionItem statFunctionItem = new StatFunctionItem();
        statFunctionItem.setFunctionType(StatFunctionItem.FunctionType.COUNT);
        statFunctionItem.setAttribute(propertyName);
        statFunctionItems.add(statFunctionItem);
        statParameter.setFunctionItemList(statFunctionItems);
        InformationStatisticer informationStat = cds.getInformationStatisticser();
        List<Map<String, Object>> statList = informationStat.statInheritFactByTenantId(statParameter, tenantId);
        List<Map<String, Object>> list = listGlobalDimensionItems(CimConstants.defauleSpaceName, dimensionTypeBean.getDimensionTypeName());
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();

        for (int i = 0; i < statList.size(); i++) {
            map2.put(statList.get(i).get(dimensionTypeBean.getDimensionTypeProperty()).toString(), statList.get(i).get(statFunctionItem.getFunctionAttribute()));
        }

        for (int i = 0; i < list.size(); i++) {
            if (map2.get(list.get(i).get("key").toString()) == null) {
                map.put(list.get(i).get("value").toString(), 0);
            } else {
                map.put(list.get(i).get("value").toString(), map2.get(list.get(i).get("key").toString()));
            }
        }
        List<Map<String, Object>> res = new ArrayList<>();
        for (String key : map.keySet()) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("value", map.get(key));
            obj.put("name", key);
            res.add(obj);
        }

        return res;

    }

    public List<Map<String, Object>> statSumByDic(CimDataSpace cds, String tenantId, DimensionTypeBean dimensionTypeBean, String objectTypeId, String propertyName) {

        StatParameters statParameter = new StatParameters();
        statParameter.setType(objectTypeId);
        List<String> groupPro = new ArrayList<>();
        groupPro.add(dimensionTypeBean.getDimensionTypeProperty());
        statParameter.setGroupAttributes(groupPro);
        List<StatFunctionItem> statFunctionItems = new ArrayList<>();
        StatFunctionItem statFunctionItem = new StatFunctionItem();
        statFunctionItem.setFunctionType(StatFunctionItem.FunctionType.SUM);
        statFunctionItem.setAttribute(propertyName);
        statFunctionItems.add(statFunctionItem);
        statParameter.setFunctionItemList(statFunctionItems);
        InformationStatisticer informationStat = cds.getInformationStatisticser();
        List<Map<String, Object>> statList = informationStat.statInheritFactByTenantId(statParameter, tenantId);
        List<Map<String, Object>> list = listGlobalDimensionItems(CimConstants.defauleSpaceName, dimensionTypeBean.getDimensionTypeName());
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();

        for (int i = 0; i < statList.size(); i++) {
            map2.put(statList.get(i).get(dimensionTypeBean.getDimensionTypeProperty()).toString(), statList.get(i).get(statFunctionItem.getFunctionAttribute()));
        }

        for (int i = 0; i < list.size(); i++) {
            if (map2.get(list.get(i).get("key").toString()) == null) {
                map.put(list.get(i).get("value").toString(), 0);
            } else {
                map.put(list.get(i).get("value").toString(), map2.get(list.get(i).get("key").toString()));
            }
        }

        List<Map<String, Object>> res = new ArrayList<>();
        for (String key : map.keySet()) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("value", map.get(key));
            obj.put("name", key);
            res.add(obj);
        }

        return res;
    }

    //计算单个对象根据字典统计的个数
    public StatVO infoObjectWithDicStat(CimDataSpace cds, InfoObjectDef infoObjectDef, DimensionTypeBean dimensionTypeBean, StatParameters statParameter, String tenantId) {
        String property = dimensionTypeBean.getDimensionTypeProperty();
        String group = dimensionTypeBean.getDimensionTypeProperty();
        StatVO  tagNodeStatVO = new StatVO();
        tagNodeStatVO.setName(infoObjectDef.getObjectTypeName());
        tagNodeStatVO.setShowName(infoObjectDef.getObjectTypeDesc());
        tagNodeStatVO.setStatus(false);
        statParameter.setType(infoObjectDef.getObjectTypeName());
        List<String> groupPro = new ArrayList<>();
        groupPro.add(group);
        statParameter.setGroupAttributes(groupPro);
        List<StatFunctionItem> statFunctionItems = new ArrayList<>();
        StatFunctionItem statFunctionItem = new StatFunctionItem();
        statFunctionItem.setFunctionType(StatFunctionItem.FunctionType.COUNT);
        statFunctionItem.setAttribute(property);
        statFunctionItems.add(statFunctionItem);
        statParameter.setFunctionItemList(statFunctionItems);
        InformationStatisticer informationStat = cds.getInformationStatisticser();
        List<Map<String, Object>> statList = informationStat.statInheritFactByTenantId(statParameter, tenantId);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = listGlobalDimensionItems(cds,  dimensionTypeBean.getDimensionTypeName());
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        for (int i = 0; i < statList.size(); i++) {
            map2.put(statList.get(i).get(dimensionTypeBean.getDimensionTypeProperty()).toString(), statList.get(i).get(statFunctionItem.getFunctionAttribute()));
        }
        for (int i = 0; i < list.size(); i++) {
            if (map2.get(list.get(i).get("key").toString()) == null) {
                map.put(list.get(i).get("value").toString(), 0);
            } else {
                map.put(list.get(i).get("value").toString(), map2.get(list.get(i).get("key").toString()));
            }
        }


        List<Map<String, Object>> res = new ArrayList<>();
        long totaLCount = 0l;
        for (String key : map.keySet()) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("value", map.get(key));
            if(map.get(key) != null) {
                    totaLCount = totaLCount + Long.parseLong(map.get(key).toString());
            }

            obj.put("name", key);
            res.add(obj);
        }
        tagNodeStatVO.setTotalCount(totaLCount);
        tagNodeStatVO.setResult(res);
        return tagNodeStatVO;
    }

}
