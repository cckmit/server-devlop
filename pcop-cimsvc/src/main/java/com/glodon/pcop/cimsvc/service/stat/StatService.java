package com.glodon.pcop.cimsvc.service.stat;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceCache.CimCacheManager;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataAnalysis.InformationStatisticer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataAnalysis.StatFunctionItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataAnalysis.StatParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;

import com.glodon.pcop.cimsvc.model.QueryConditionsBean;
import com.glodon.pcop.cimsvc.model.stat.StatParameterBean;
import com.glodon.pcop.cimsvc.util.QueryConditionsUtil;
import com.google.gson.Gson;

import org.ehcache.Cache;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class StatService {
    private String cacheName = "CIM_STAT_SVC_CACHE";

    public List<Map<String, Object>> stat(StatParameterBean[] statParameterList, String tenantId) {
        Gson gson = new Gson();
        String cacheKey = gson.toJson(statParameterList) + tenantId;

        List<Map<String, Object>> resList = new ArrayList<>();
//        Cache<String, ArrayList> cache = CacheUtil.getOrCreateCache(cacheName, String.class, ArrayList.class, 180l);
        Cache<String, ArrayList> cache = CimCacheManager.getOrCreateCache(cacheName, String.class, ArrayList.class,
                180L);
        if (cache != null) {
            if (cache.containsKey(cacheKey)) {
                return cache.get(cacheKey);
            }
        }

        for (int i = 0; i < statParameterList.length; i++) {

        }

        List<String> list = new ArrayList<>();
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        try {
            InformationStatisticer informationStat = cds.getInformationStatisticser();

            resList = informationStat.batchStatInheritFactByTenantId((change(cds, statParameterList)), tenantId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }


        cache.put(cacheKey, (ArrayList) resList);
        return resList;
    }

    public List<Map<String, Object>> stat(CimDataSpace cds, StatParameterBean[] statParameterList, String tenantId) {
        List<Map<String, Object>> resList = new ArrayList<>();
        InformationStatisticer informationStat = cds.getInformationStatisticser();
        resList = informationStat.batchStatInheritFactByTenantId(change(cds, statParameterList), tenantId);
        return resList;
    }

    private List<StatParameters> change(CimDataSpace cds, StatParameterBean[] statParameters) {
        List<StatParameters> list = new ArrayList<>();
        for (int i = 0; i < statParameters.length; i++) {
            list.add(change(cds, statParameters[i]));
        }
        return list;
    }



    private StatParameters change(CimDataSpace cds, StatParameterBean statParameter) {
        StatParameters statParameters = new StatParameters();
        statParameters.setType(statParameter.getCim_object_type());

        List<QueryConditionsBean> queryConditionsBeanList = statParameter.getConditions();

        if (queryConditionsBeanList != null && queryConditionsBeanList.size() > 0) {
            for (int i = 0; i < queryConditionsBeanList.size(); i++) {
                FilteringItem filteringItem = QueryConditionsUtil.parseQueryCondition(statParameters.getType(),
                        queryConditionsBeanList.get(i), false);
                if (i == 0) {
                    statParameters.setDefaultFilteringItem(filteringItem);
                } else {
                    statParameters.addFilteringItem(filteringItem, queryConditionsBeanList.get(i).getFilterLogical());
                }
            }
        }

        List<StatFunctionItem> list = new ArrayList<>();

        if (statParameter.get_sign_() != null) {
            if (!statParameter.get_sign_().isEmpty()) {
                StatFunctionItem signFuncItem = new StatFunctionItem();
                signFuncItem.setFunctionType(StatFunctionItem.FunctionType.SIGN);
                signFuncItem.setAttribute(statParameter.get_sign_());
                signFuncItem.setAlias("_sign_");
                list.add(signFuncItem);
            }
        } else if (statParameter.getStatItem() != null) {
            if (!statParameter.getStatItem().isEmpty()) {
                StatFunctionItem signFuncItem = new StatFunctionItem();
                signFuncItem.setFunctionType(StatFunctionItem.FunctionType.SIGN);
                signFuncItem.setAttribute(statParameter.getStatItem());
                signFuncItem.setAlias("_sign_");
                list.add(signFuncItem);
            }
        }

        StatFunctionItem statFunctionItem = new StatFunctionItem();
        if (statParameter.getStatType().toLowerCase().equals("count")) {
            statFunctionItem.setFunctionType(StatFunctionItem.FunctionType.COUNT);
            statFunctionItem.setAttribute(statParameter.getProperty());
            statFunctionItem.setAlias(statParameter.getStatPro());
        } else if (statParameter.getStatType().toLowerCase().equals("sum")) {
            statFunctionItem.setFunctionType(StatFunctionItem.FunctionType.SUM);
            statFunctionItem.setAttribute(statParameter.getProperty());
            statFunctionItem.setAlias(statParameter.getStatPro());
        }
        list.add(statFunctionItem);
        statParameters.setFunctionItemList(list);
        statParameters.setGroupAttributes(statParameter.getGroupAttributes());

        return statParameters;

    }
}