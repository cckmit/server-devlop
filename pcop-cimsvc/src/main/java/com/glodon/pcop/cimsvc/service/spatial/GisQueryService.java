package com.glodon.pcop.cimsvc.service.spatial;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cimsvc.exception.GisServerErrorException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import com.glodon.pcop.cimsvc.model.v2.InstancesQueryInput;
import com.glodon.pcop.cimsvc.model.v2.SingleQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.gis.SpatialAnalysisQueryInput;
import com.glodon.pcop.cimsvc.service.v2.InstancesService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class GisQueryService {
    private static final Logger log = LoggerFactory.getLogger(GisQueryService.class);

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static final int RES_CODE_VAL = 0;
    private static final String RES_CODE = "res_code";
    private static final String MSG = "msg";
    private static final String DATA = "data";
    private static final String CIM_ID = "cimid";
    private static final String OBJECT_ID_KEY = "feature_class";

    @Value("${gis-host.spatial-query.query}")
    private String queryUrl;

    @Autowired
    private InstancesService instancesService;


    public Map<String, SingleQueryOutput> compositeQueryGis(String tenantId, SpatialAnalysisQueryInput queryConditions)
            throws InputErrorException, GisServerErrorException {
        Assert.notEmpty(queryConditions.getObjectTypeIds(), "object type ids are mandatory");
        Map<String, SingleQueryOutput> queryOutputs = new HashMap<>();
        String cimIdKey = CIM_ID;
        if (StringUtils.isNotBlank(queryConditions.getCimIdKey())) {
            cimIdKey = queryConditions.getCimIdKey().trim();
        }

        String objectIdKey = OBJECT_ID_KEY;
        if (StringUtils.isNotBlank(queryConditions.getObjectIdKey())) {
            objectIdKey = queryConditions.getObjectIdKey().trim();
        }

        Map<String, Set<String>> cimIdsMap = new HashMap<>();
        if (StringUtils.isNotBlank(queryConditions.getSpatialCondition().getQuerySql())) {
            String responseStr = spatialQuery(queryConditions.getSpatialCondition().getQuerySql());
            log.info("gis query result: [{}]", responseStr);
            JSONObject resJsb = JSON.parseObject(responseStr);
            if (resJsb.getInteger(RES_CODE) == RES_CODE_VAL) {
                JSONArray dataArr = resJsb.getJSONArray(DATA);
                if (dataArr != null && dataArr.size() > 0) {
                    for (int i = 0; i < dataArr.size(); i++) {
                        JSONObject jsb = dataArr.getJSONObject(i);
                        if (jsb.containsKey(objectIdKey) && jsb.containsKey(cimIdKey)) {
                            String featureClassId = jsb.getString(objectIdKey);
                            String cimId = jsb.getString(cimIdKey);
                            if (cimId == null) {
                                continue;
                            }
                            if (cimIdsMap.containsKey(featureClassId)) {
                                cimIdsMap.get(featureClassId).add(cimId);
                            } else {
                                Set<String> cimIdsTmp = new HashSet<>();
                                cimIdsTmp.add(cimId);
                                cimIdsMap.put(featureClassId, cimIdsTmp);
                            }
                        } else {
                            log.error("gis query response not contain featureClass or cimId: [{}]", jsb);
                        }
                    }
                }
            } else {
                log.error("gis query response error: [{}]", responseStr);
            }
//            log.info("cim ids map: [{}]", JSON.toJSONString(cimIdsMap));
        } else {
            log.info("no gis query condition");
        }
        log.info("buffer query result: [{}]", JSON.toJSONString(cimIdsMap));

        if (CollectionUtils.isEmpty(cimIdsMap)) {
            log.info("gis buffer query response empty");
            return queryOutputs;
        } else {
            for (String objectId : queryConditions.getObjectTypeIds()) {
                if (cimIdsMap.containsKey(objectId)) {
                    SingleQueryOutput queryOutput = objectInstanceQuery(tenantId, objectId, cimIdsMap.get(objectId),
                            queryConditions.getGeneralConditions());
                    queryOutputs.put(objectId, queryOutput);
                }
            }
        }

        return queryOutputs;
    }

    public SingleQueryOutput objectInstanceQuery(String tenantId, String objectTypeId, Set<String> cimIds,
                                                 List<CommonQueryConditionsBean> inputGeneralConds)
            throws InputErrorException {

        List<CommonQueryConditionsBean> generalConds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cimIds)) {
            CommonQueryConditionsBean commonQueryCond = new CommonQueryConditionsBean();
            commonQueryCond.setPropertyName("ID");
            commonQueryCond.setFilterType("InValueFilteringItem");
            commonQueryCond.setFilterLogical(ExploreParameters.FilteringLogic.AND);
            commonQueryCond.setListParam(new ArrayList<>(cimIds));
            generalConds.add(commonQueryCond);
        }
        if (!CollectionUtils.isEmpty(inputGeneralConds)) {
            generalConds.addAll(inputGeneralConds);
        }

        InstancesQueryInput generalQueryInput = new InstancesQueryInput();
        generalQueryInput.setConditions(generalConds);
        generalQueryInput.setPageIndex(0);
        generalQueryInput.setPageSize(Integer.MAX_VALUE);

        return instancesService.queryInstanceSingle(tenantId, objectTypeId, null, generalQueryInput);
    }

    public String spatialQuery(String json)
            throws GisServerErrorException {
        log.info("query input: [{}]", json);
        try {
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            RequestBody body = RequestBody.create(MEDIA_TYPE, json);
            Request request = new Request.Builder()
                    .url(queryUrl)
                    .post(body)
                    .build();
            log.info("gis query: [{}]", request.toString());
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    throw new GisServerErrorException(EnumWrapper.CodeAndMsg.E05050001);
                }
            }
        } catch (Exception e) {
            log.error("gis query faild", e);
            throw new GisServerErrorException(EnumWrapper.CodeAndMsg.E05050001);
        }
    }


    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }

    public void setInstancesService(InstancesService instancesService) {
        this.instancesService = instancesService;
    }
}
