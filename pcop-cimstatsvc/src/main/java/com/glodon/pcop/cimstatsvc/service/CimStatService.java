package com.glodon.pcop.cimstatsvc.service;

import com.glodon.pcop.cim.common.common.ReturnInfo;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceCache.CimCacheManager;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CommonTag;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CommonTags;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.CommonTagsDSImpl;
import com.glodon.pcop.cimstatsvc.dao.DbExecute;
import com.glodon.pcop.cimstatsvc.model.StatParameter;
import com.glodon.pcop.cimstatsvc.sql.SqlStatBatch;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.ehcache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import springfox.documentation.annotations.Cacheable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.glodon.pcop.cimstatsvc.dao.DbExecute.executeQuery;
import static jdk.nashorn.internal.runtime.GlobalFunctions.parseInt;

@Service

public class CimStatService {
     private  String  cacheName = "CIM_STAT_SVC_CACHE";

    public List<Map<String, Object>> stat(StatParameter[] statParameterList,  String tenantId) {

        String  cacheKey = JSONArray.fromObject(statParameterList).toString()+tenantId;

        List<Map<String, Object>> resList = new ArrayList<>();
//        Long expiredSeconds=new Long((long)(120+ parseInt(Math.random()*120,10))); //缓存2~3分钟1l
//        Cache<String,ArrayList> cache = CacheUtil.getOrCreateCache(cacheName, String.class,ArrayList.class,180l);
//        Cache<String,ArrayList> cache = CacheUtil.getOrCreateCache(cacheName, String.class,ArrayList.class,180l);
        Cache<String,ArrayList> cache = CimCacheManager.getOrCreateCache(cacheName, String.class,ArrayList.class, (long) (150+(int)(Math.random()*100)));
        if (cache != null) {
            if (cache.containsKey(cacheKey)) {
                  return  cache.get(cacheKey);
            }
        }

        List<String> list = new ArrayList<>();
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(DbExecute.dbName);
        try {
             list = executeQuery(cds,SqlStatBatch.getStatExpression(cds,statParameterList, tenantId));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cds != null) {
                cds.closeSpace();
            }
        }


        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = new JSONObject().fromObject(list.get(i));
            Map<String, Object> resMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof JSONNull) {
                    resMap.put(entry.getKey(), null);
                } else {
                    resMap.put(entry.getKey(), entry.getValue());
                }
            }
            resList.add(resMap);
        }

        try {
            if (cache != null) {
                if (resList != null) {
                    cache.put(cacheKey, (ArrayList) resList);
                }
            } else {
                System.out.println("cache is null");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return resList;
    }

    public List<Map<String, Object>> stat(CimDataSpace cimDataSpace, StatParameter[] statParameterList,  String tenantId) {
        List<Map<String, Object>> resList = new ArrayList<>();
        List<String> list =  executeQuery(cimDataSpace,SqlStatBatch.getStatExpression(cimDataSpace,statParameterList, tenantId));
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = new JSONObject().fromObject(list.get(i));
            Map<String, Object> resMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof JSONNull) {
                    resMap.put(entry.getKey(), null);
                } else {
                    resMap.put(entry.getKey(), entry.getValue());
                }
            }
            resList.add(resMap);
        }
        return resList;
    }



    private <T> List<List<T>> spliceArrays(List<T> datas, int splitSize) {
        if (datas == null || splitSize < 1) {
            return  null;
        }
        int totalSize = datas.size();
        int count = (totalSize % splitSize == 0) ?
                (totalSize / splitSize) : (totalSize/splitSize+1);

        List<List<T>> rows = new ArrayList<>();

        for (int i = 0; i < count;i++) {

            List<T> cols = datas.subList(i * splitSize,
                    (i == count - 1) ? totalSize : splitSize * (i + 1));
            rows.add(cols);
            System.out.println(cols);
        }
        return rows;
    }

}
