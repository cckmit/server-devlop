package com.glodon.pcop.cimsvc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.DataSetMapping;
import com.glodon.pcop.cim.common.model.FileImportTaskInputBean;
import com.glodon.pcop.cim.common.model.PropertyMapping;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters.FilteringLogic;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.BetweenFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.GreaterThanEqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.LessThanEqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.SimilarFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DataLoadStatisticsVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTransferVO;
import com.glodon.pcop.cimapi.model.InstanceQueryInputBean;
import com.glodon.pcop.cimapi.model.InstanceQueryOutput;
import com.glodon.pcop.cimsvc.exception.PropertyMappingException;
import com.glodon.pcop.cimsvc.model.DataSetMappingGis;
import com.glodon.pcop.cimsvc.model.FileImportMappingBean;
import com.glodon.pcop.cimsvc.model.InputInstancesBean;
import com.glodon.pcop.cimsvc.model.InstanceBean;
import com.glodon.pcop.cimsvc.model.PropMappingGis;
import com.glodon.pcop.cimsvc.model.PropertyMappingBeanGis;
import com.glodon.pcop.cimsvc.model.ShpFileDownloaJobMessageBean;
import com.glodon.pcop.cimsvc.util.DateUtil;
import com.glodon.pcop.core.models.IdResult;
import com.glodon.pcop.jobapi.JobResponse;
import com.glodon.pcop.jobapi.dto.JobPropsDTO;
import com.glodon.pcop.jobclt.client.JobInfoClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yuanjk(yuanjk @ glodon.com), 2018-07-24 10:33:13
 */
@Service
public class OldInstanceService {
    static Logger log = LoggerFactory.getLogger(OldInstanceService.class);

    private static final String TYPE_CODE = "typeCode={}";

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JobInfoClient jobInfoClient;

    private static Gson gson = new Gson();

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${my.mq.importCode}")
    private String importTypeCode = "pcopDataImport";
    @Value("${my.mq.downloadCode}")
    private String downloadTypeCode = "pcopDataDownload";
    @Value("${my.mq.importJobName}")
    private String importJobNamePrefix = "SHP_DATA_IMPORT";
    @Value("${my.mq.downloadJobName}")
    private String downloadJobNamePrefix = "SHP_FILE_DOWNLOAD";

    /**
     * 手动输入实例
     *
     * @param instanceData
     * @return
     */
    public Boolean importManually(InputInstancesBean instanceData) {
        DataLoadStatisticsVO rt = null;
        List<Map<String, InfoObjectTransferVO>> list = new ArrayList<>();
        String objectTypeId = "";
        if (instanceData != null) {
            for (InstanceBean ib : instanceData.getInstances()) {
                if (StringUtils.isNotBlank(objectTypeId) && !objectTypeId.equals(ib.getObjectTypeId())) {
                    rt = InfoObjectFeatures.loadInfoObjectDataByPropertyTypeId(CimConstants.defauleSpaceName, objectTypeId,
                            list);
                    log.info("rt.getOperationSummary()={}, rt.getFailItemsCount()={}, rt.getSuccessItemsCount()={}",
                            rt.getOperationSummary(), rt.getFailItemsCount(), rt.getSuccessItemsCount());
                    list = new ArrayList<>();
                }
                objectTypeId = ib.getObjectTypeId();
                Map<String, InfoObjectTransferVO> tmpMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : ib.getPropertyValue().entrySet()) {
                    InfoObjectTransferVO info = new InfoObjectTransferVO(entry.getValue());
                    tmpMap.put(entry.getKey(), info);
                }
//				log.info("import instance: {}", tmpMap);
                list.add(tmpMap);
            }
        }
        rt = InfoObjectFeatures.loadInfoObjectDataByPropertyTypeId(CimConstants.defauleSpaceName, objectTypeId, list);
        log.info("rt.getOperationSummary()={}, rt.getFailItemsCount()={}, rt.getSuccessItemsCount()={}", rt.getOperationSummary(), rt.getFailItemsCount(), rt.getSuccessItemsCount());

        boolean flag = false;
        if (rt != null) {
            flag = true;
        }
        return flag;
    }

    /**
     * 根据输入条件查询实例数据
     *
     * @param conditions
     * @return
     */
    public InstanceQueryOutput queryInstanceData(InstanceQueryInputBean conditions) { //NOSONAR
        InstanceQueryOutput instanceQueryOutput = new InstanceQueryOutput();
        if (StringUtils.isNotBlank(conditions.getObjectTypeId())) {
            ExploreParameters ep = new ExploreParameters();
            ep.setType(conditions.getObjectTypeId());
            ep.setResultNumber(1000000000);
            // 构造查询条件
            Map<String, String> map = conditions.getConditions();
            boolean flag = true;
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    FilteringItem filter = null;
                    if (entry.getValue().contains("#")) {
                        String[] dts = entry.getValue().split("#", 2);
                        if (entry.getValue().startsWith("#")) {
                            filter = new LessThanEqualFilteringItem(entry.getKey(), Long.valueOf(dts[1]));
                        } else if (entry.getValue().endsWith("#")) {
                            filter = new GreaterThanEqualFilteringItem(entry.getKey(), Long.valueOf(dts[0]));
                        } else if (dts.length == 2) {
                            filter = new BetweenFilteringItem(entry.getKey(), Long.valueOf(dts[0]), Long.valueOf(dts[1]));
                        } else {
                            log.error("not support");
                        }
                    } else if (entry.getValue().startsWith("%")) {
                        filter = new SimilarFilteringItem(entry.getKey(), entry.getValue().replaceFirst("%", ""), SimilarFilteringItem.MatchingType.Contain, false);
                    } else {
                        filter = new EqualFilteringItem(entry.getKey(), entry.getValue());
                    }
                    if (filter != null) {
                        if (flag) {
                            ep.setDefaultFilteringItem(filter);
                            flag = false;
                        } else {
                            ep.addFilteringItem(filter, FilteringLogic.AND);
                        }
                    } else {
                        log.info("查询条件输入错误，key={}, value={}", entry.getKey(), entry.getValue());
                    }
                }
            }
            // 查询总量
            List<Map<String, Object>> instances = InfoObjectFeatures.queryInfoObjectData(CimConstants.defauleSpaceName, conditions.getObjectTypeId(), ep);
            Long totalCount = (long) instances.size();
            ep.setPageSize(conditions.getPageSize());
            ep.setStartPage(conditions.getStartPage());
            ep.setEndPage(conditions.getEndPage());
            // 按照输入条件查询
            instances = InfoObjectFeatures.queryInfoObjectData(CimConstants.defauleSpaceName,
                    conditions.getObjectTypeId(), ep);
            instanceQueryOutput.setInstances(instances);
            instanceQueryOutput.setTotalCount(totalCount);
        }
        return instanceQueryOutput;
    }

    /**
     * 更新实例数据，每次更新一个实例
     *
     * @param instanceId
     * @param instance
     * @return
     */
    public Boolean updateInstanceData(String instanceId, InstanceBean instance) {
        boolean flag = InfoObjectFeatures.updateInfoObjectInstanceData(CimConstants.defauleSpaceName,
                instance.objectTypeId, instanceId, instance.getPropertyValue());
        return flag;
    }

    /**
     * 删除实例数据
     *
     * @param instanceId
     * @param objectTypeId
     * @return
     */
    public Boolean deleteInstanceData(String instanceId, String objectTypeId) {
        boolean flag = InfoObjectFeatures.removeInfoObjectInstance(CimConstants.defauleSpaceName, objectTypeId,
                instanceId);
        return flag;
    }

    /**
     * @param fileType
     * @param propertyMapping
     * @return
     * @throws PropertyMappingException
     */
    public String createTask(String fileType, PropertyMappingBeanGis propertyMapping) throws PropertyMappingException {//NOSONAR
        //ID属性必须匹配
        List<DataSetMappingGis> datasetMappingList = propertyMapping.getDataSetMapping();
        if (datasetMappingList != null) {
            for (DataSetMappingGis dataSetMapping : datasetMappingList) {
                List<PropMappingGis> propMappingList = dataSetMapping.getPropertyMapping();
                if (propMappingList != null) {
                    for (PropMappingGis propMapping : propMappingList) {
                        if (propMapping.getName().equals(CimConstants.ID_PROPERTY_TYPE_NAME) && StringUtils.isBlank(propMapping.getTargetName())) {
                            throw new PropertyMappingException(EnumWrapper.CodeAndMsg.E05040005);
                        }
                    }
                }

            }
        }

        sendShpDownloadMessage(fileType, propertyMapping);
        return sendFileDataImportMessage(fileType, propertyMapping);
    }


    /**
     * @param fileType
     * @param mappingBean
     * @return
     * @throws PropertyMappingException
     */
    public String createTask(String fileType, FileImportMappingBean mappingBean) throws PropertyMappingException {//NOSONAR
        PropertyMappingBeanGis propertyMapping = mappingBean.getPropertyMappingBean();
        //ID属性必须匹配
        if (propertyMapping != null) {
            List<DataSetMappingGis> datasetMappingList = propertyMapping.getDataSetMapping();
            if (datasetMappingList != null) {
                for (DataSetMappingGis dataSetMapping : datasetMappingList) {
                    List<PropMappingGis> propMappingList = dataSetMapping.getPropertyMapping();
                    if (propMappingList != null) {
                        for (PropMappingGis propMapping : propMappingList) {
                            if (propMapping.getName().equals(CimConstants.ID_PROPERTY_TYPE_NAME) && StringUtils.isBlank(propMapping.getTargetName())) {
                                log.error("File data import, ID property must be mapping,");
                                throw new PropertyMappingException(EnumWrapper.CodeAndMsg.E05040005);
                            }
                        }
                    }
                }
            }
        }

        sendShpDownloadMessage(fileType, propertyMapping);
        return sendFileDataImportMessage(fileType, mappingBean);
    }

    /**
     * 文件数据导入V3
     *
     * @param taskInputBean
     * @return
     * @throws JsonProcessingException
     */
    public String createTask(FileImportTaskInputBean taskInputBean) throws JsonProcessingException {
        if (taskInputBean == null || StringUtils.isBlank(taskInputBean.getFileName()) || StringUtils.isBlank(taskInputBean.getObjectTypeId())) {
            String msg = "file import failed, provided info is not enough";
            log.error(msg);
            return msg;
        }

        PropertyMappingBeanGis propertyMappingBeanGis = new PropertyMappingBeanGis();
        propertyMappingBeanGis.setFileId(taskInputBean.getFileName());
        propertyMappingBeanGis.setTypeId(taskInputBean.getObjectTypeId());
        List<DataSetMapping> dataSetMappings = taskInputBean.getDataSetMappings();
        if (dataSetMappings != null) {
            List<DataSetMappingGis> dataSetMappingGisList = new ArrayList<>();
            for (DataSetMapping dataSetMapping : dataSetMappings) {
                DataSetMappingGis dataSetMappingGis = new DataSetMappingGis();
                dataSetMappingGis.setDataSetId(dataSetMapping.getDataSetId());
                dataSetMappingGis.setDataSetName(dataSetMapping.getDataSetName());
                List<PropertyMapping> propertyMappings = dataSetMapping.getPropertyMappings();
                if (propertyMappings != null) {
                    List<PropMappingGis> propMappingGisList = new ArrayList<>();
                    for (PropertyMapping propertyMapping : propertyMappings) {
                        PropMappingGis propMappingGis = new PropMappingGis();
                        propMappingGis.setId(propertyMapping.getPropertyRid());
                        propMappingGis.setName(propertyMapping.getSourcePropertyName());
                        propMappingGis.setTargetName(propertyMapping.getTargetPropertyName());
                        propMappingGis.setTargetValue(propertyMapping.getPropertyValue());
                        propMappingGisList.add(propMappingGis);
                    }
                    dataSetMappingGis.setPropertyMapping(propMappingGisList);
                }
                dataSetMappingGisList.add(dataSetMappingGis);
            }
            propertyMappingBeanGis.setDataSetMapping(dataSetMappingGisList);
        }
        String fileType = taskInputBean.getFileType().toString();
        // sendShpDownloadMessage(fileType, propertyMappingBeanGis);
        return sendFileDataImportMessage(taskInputBean);
    }


    /**
     * 发送shp文件下载消息
     *
     * @param fileType
     * @param propertyMapping
     */
    private void sendShpDownloadMessage(String fileType, PropertyMappingBeanGis propertyMapping) {
        JobPropsDTO jobPropsDTO = new JobPropsDTO();
        // 下载任务消息
        ShpFileDownloaJobMessageBean jd = new ShpFileDownloaJobMessageBean();
        jd.setBucketId("pcop-cim");
        jd.setPropertyMapping(propertyMapping);
        // job参数
        jobPropsDTO.setJobName(downloadJobNamePrefix + "-" + DateUtil.getCurrentDateReadable());
        log.info(TYPE_CODE, downloadTypeCode);
        jobPropsDTO.setTypeCode(downloadTypeCode);
        jobPropsDTO.setParam(gson.toJson(jd));
        // 发送下载任务消息
        if (fileType.equals(EnumWrapper.IMPORT_FILE_TYPE.SHP.toString())) {
            log.info("Send shp file download messgae!");
            JobResponse<IdResult> jobResponse = jobInfoClient.add(jobPropsDTO);
            log.info("Download job message: {}", jobResponse.getCode());
        } else {
            log.info("No file download messge should be sent!");
        }
    }

    /**
     * 发送旧版文件数据导入消息
     *
     * @param fileType
     * @param propertyMapping
     * @return
     */
    private String sendFileDataImportMessage(String fileType, PropertyMappingBeanGis propertyMapping) {
        JobPropsDTO jobPropsDTO = new JobPropsDTO();
        // shp导入消息
        jobPropsDTO.setJobName(importJobNamePrefix + "-" + DateUtil.getCurrentDateReadable());
        log.info(TYPE_CODE, importTypeCode);
        jobPropsDTO.setTypeCode(importTypeCode);
        // 消息内容
        JsonObject jsonObject = gson.toJsonTree(propertyMapping).getAsJsonObject();
        jsonObject.addProperty("fileType", fileType);
        jobPropsDTO.setParam(jsonObject.toString());
        JobResponse<IdResult> jobResponse = jobInfoClient.add(jobPropsDTO);
        log.info("SHP file import response status code: {}, content: {}", jobResponse.getCode(), jobResponse.getData());
        return jobResponse.getData().toString();
    }

    /**
     * 发送新版文件数据导入消息
     *
     * @param fileType
     * @param mappingBean
     * @return
     */
    private String sendFileDataImportMessage(String fileType, FileImportMappingBean mappingBean) {
        JobPropsDTO jobPropsDTO = new JobPropsDTO();
        jobPropsDTO.setJobName(importJobNamePrefix + "-" + fileType + "-" + DateUtil.getCurrentDateReadable());
        log.info(TYPE_CODE, importTypeCode);
        jobPropsDTO.setTypeCode(importTypeCode);
        // 消息内容
        JsonObject jsb = gson.toJsonTree(mappingBean).getAsJsonObject();
        jsb.addProperty("fileType", fileType);
        jobPropsDTO.setParam(jsb.toString());
        JobResponse<IdResult> jobResponse = jobInfoClient.add(jobPropsDTO);
        log.info("SHP file import response status code: {}, content: {}", jobResponse.getCode(), jobResponse.getData());
        return jobResponse.getData().toString();
    }


    /**
     * 发送文件数据导入消息V3
     *
     * @param taskInputBean
     * @return
     * @throws JsonProcessingException
     */
    private String sendFileDataImportMessage(FileImportTaskInputBean taskInputBean) throws JsonProcessingException {
        JobPropsDTO jobPropsDTO = new JobPropsDTO();
        jobPropsDTO.setJobName(importJobNamePrefix + "-" + taskInputBean.getFileType() + "-" + DateUtil.getCurrentDateReadable());
        log.info(TYPE_CODE, importTypeCode);
        jobPropsDTO.setTypeCode(importTypeCode);
        // 消息内容
        if (objectMapper == null) {
            log.error("objectMapper is null");
        }
        if (taskInputBean == null) {
            log.error("taskInputBean is null");
        }
        // jobPropsDTO.setParam(objectMapper.writeValueAsString(taskInputBean));
        jobPropsDTO.setParam((new Gson()).toJson(taskInputBean));
        JobResponse<IdResult> jobResponse = jobInfoClient.add(jobPropsDTO);
        log.info("file data import response status code: {}, content: {}", jobResponse.getCode(), jobResponse.getData());
        return jobResponse.getData().toString();
    }

}
