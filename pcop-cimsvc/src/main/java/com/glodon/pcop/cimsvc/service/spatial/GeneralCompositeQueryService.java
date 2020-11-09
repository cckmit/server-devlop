package com.glodon.pcop.cimsvc.service.spatial;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.gis.GisResponseFeature;
import com.glodon.pcop.cim.common.model.gis.GisSpatialQueryResponseBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cimsvc.exception.GisServerErrorException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.gis.GeneralCompositeInstancesQueryInput;
import com.glodon.pcop.cimsvc.model.gis.GeneralSpatialQueryConditionInputBean;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import com.glodon.pcop.cimsvc.model.v2.CompositeInstancesQueryInput;
import com.glodon.pcop.cimsvc.model.v2.GisSpatialQueryConditionInputBean;
import com.glodon.pcop.cimsvc.model.v2.InstancesQueryInput;
import com.glodon.pcop.cimsvc.model.v2.SingleInstancesQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.SingleQueryOutput;
import com.glodon.pcop.cimsvc.service.v2.InstancesService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GeneralCompositeQueryService {
    private static final Logger log = LoggerFactory.getLogger(GeneralCompositeQueryService.class);

    @Value("${gis-host.spatial-query.circle-buffer}")
    private String circleBufferUrl;

    @Value("${gis-host.spatial-query.polygon-buffer}")
    private String polygonBuffer;

    @Autowired
    private InstancesService instancesService;

    public List<SingleQueryOutput> compositeQueryGis(String tenantId,
                                                     GeneralCompositeInstancesQueryInput queryConditions) throws GisServerErrorException, InputErrorException {
        Assert.notEmpty(queryConditions.getObjectTypeIds(), "object type ids are mandatory");
        List<SingleQueryOutput> queryOutputs = new ArrayList<>();

        // GisSpatialQueryResponseBean responseBean = null;
        Map<String, Set<String>> bufferQueryResponseId = new HashMap<>();
        // GisSpatialQueryConditionInputBean gisSpatialQuery = queryConditions.getSpatialCondition();

        List<GeneralSpatialQueryConditionInputBean> spatialQueryBeans = queryConditions.getSpatialConditions();

        if (spatialQueryBeans != null && spatialQueryBeans.size() > 0) {

            for (GeneralSpatialQueryConditionInputBean gisSpatialQuery : spatialQueryBeans) {
                GisSpatialQueryResponseBean responseBean = gisBufferQuery(gisSpatialQuery.getQueryTypeEnum(),
                        gisSpatialQuery.getBoundary(), gisSpatialQuery.getOutput());
                if (responseBean != null && responseBean.getCount() > 0) {
                    for (GisResponseFeature feature : responseBean.getFeatures()) {
                        if (StringUtils.isNotBlank(feature.getFeatureClassName()) && StringUtils.isNotBlank(feature.getObjectId())) {
                            if (bufferQueryResponseId.containsKey(feature.getFeatureClassName())) {
                                bufferQueryResponseId.get(feature.getFeatureClassName()).add(feature.getObjectId());
                            } else {
                                Set<String> tmpSet = new HashSet<>();
                                tmpSet.add(feature.getObjectId());
                                bufferQueryResponseId.put(feature.getFeatureClassName(), tmpSet);
                            }
                        }
                    }
                }
            }
        } else {
            log.info("no gis buffer query");
        }
        log.debug("buffer query result: [{}]", JSON.toJSONString(bufferQueryResponseId));
        //TODO tempal query

        if (bufferQueryResponseId.size() == 0) {
            log.info("gis buffer query response blank");
            return queryOutputs;
        } else {
            for (String objId : queryConditions.getObjectTypeIds()) {
                queryOutputs.add(objectInstanceQuery(tenantId, objId, bufferQueryResponseId, queryConditions));
            }
        }

        return queryOutputs;
    }

    public SingleQueryOutput objectInstanceQuery(String tenantId, String objectTypeId,
                                                 Map<String, Set<String>> bufferQueryResponseId,
                                                 GeneralCompositeInstancesQueryInput queryConditions) throws InputErrorException {
        SingleQueryOutput queryOutput = new SingleQueryOutput();
        InstancesQueryInput generalQueryInput = new InstancesQueryInput();
        generalQueryInput.setConditions(queryConditions.getGeneralConditions());
        generalQueryInput.setPageIndex(0);
        generalQueryInput.setPageSize(Integer.MAX_VALUE);
        generalQueryInput.setSortAttributes(queryConditions.getSortAttributes());
        generalQueryInput.setSortingLogic(queryConditions.getSortingLogic());

        SingleQueryOutput tmpQueryOutput = instancesService.queryInstanceSingle(tenantId, objectTypeId, null,
                generalQueryInput);
        if (tmpQueryOutput != null && tmpQueryOutput.getTotalCount() > 0) {
            Set<String> ids = bufferQueryResponseId.get(objectTypeId);
            List<SingleInstancesQueryOutput> tmpSingleInstancesQueryOutputs = new ArrayList<>();
            for (SingleInstancesQueryOutput singleInstances : tmpQueryOutput.getInstances()) {
                // log.debug("instance data: {}", singleInstances.getInstanceData());
                Map<String, Object> baseInfo =
                        singleInstances.getInstanceData().get(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME);
                if (ids.contains(baseInfo.get(CimConstants.ID_PROPERTY_TYPE_NAME))) {
                    tmpSingleInstancesQueryOutputs.add(singleInstances);
                }
            }
            queryOutput.setTotalCount((long) tmpSingleInstancesQueryOutputs.size());
            queryOutput.setInstances(tmpSingleInstancesQueryOutputs);
        }
        return queryOutput;
    }

    public GisSpatialQueryResponseBean gisBufferQuery(GeneralSpatialQueryConditionInputBean.GisQueryTypeEnum gisQueryTypeEnum, String boundary, String output) throws GisServerErrorException {
        GisSpatialQueryResponseBean responseFeature = new GisSpatialQueryResponseBean();

        OkHttpClient client = new OkHttpClient();
        StringBuffer sb = new StringBuffer();
        switch (gisQueryTypeEnum) {
            case CIRCLE:
                sb.append(circleBufferUrl)
                        .append("?boundary=")
                        .append(boundary)
                        .append("&output=")
                        .append(output);
                break;
            case POLYGON:
                sb.append(polygonBuffer)
                        .append("?boundary=")
                        .append(boundary)
                        .append("&output=")
                        .append(output);
                break;
            default:
                log.error("unsupport gis query type: [{}]", gisQueryTypeEnum);
        }
        log.info("gis buffer query url: [{}]", sb.toString());
        Request request = new Request.Builder()
                .url(sb.toString())
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseContent = response.body().string();
                log.debug("gis buffer query response content: [{}]", responseContent);
                responseFeature = JSON.parseObject(responseContent, GisSpatialQueryResponseBean.class);
            } else {
                log.error("gis buffer query failed");
                throw new GisServerErrorException(EnumWrapper.CodeAndMsg.E05050001);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseFeature;
    }

    public void setCircleBufferUrl(String circleBufferUrl) {
        this.circleBufferUrl = circleBufferUrl;
    }

    public void setPolygonBuffer(String polygonBuffer) {
        this.polygonBuffer = polygonBuffer;
    }
}
