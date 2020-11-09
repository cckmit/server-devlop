package com.glodon.pcop.spacialimportsvc.schedule;

import com.alibaba.fastjson.JSON;
import com.bimface.sdk.BimfaceClient;
import com.bimface.sdk.bean.response.FileBean;
import com.bimface.sdk.bean.response.TranslateBean;
import com.bimface.sdk.config.Endpoint;
import com.bimface.sdk.exception.BimfaceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.glodon.pcop.cim.common.constant.JobStatusConst;
import com.glodon.pcop.cim.common.model.bim.BimFileUploadTranslateBean;
import com.glodon.pcop.cim.common.model.bim.TranslateResponseBean;
import com.glodon.pcop.cim.common.service.MyMinioClient;
import com.glodon.pcop.cim.common.util.CimConstants.BaseFileInfoKeys;
import com.glodon.pcop.cim.common.util.bimface.BimFaceUtil;
import com.glodon.pcop.cim.common.util.bimface.BimfaceTranslateStatusEnum;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.jobapi.type.JobStatusEnum;
import com.glodon.pcop.spacialimportsvc.config.BimfaceTranslateConfig;
import com.glodon.pcop.spacialimportsvc.util.DateUtil;
import com.glodon.pcop.spacialimportsvc.util.ImportCimConstants;
import com.glodon.pcop.spacialimportsvc.util.JobClientUtil;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BimfaceStatus {
    private static final Logger log = LoggerFactory.getLogger(BimfaceStatus.class);

    @Value("${cim.bimface.client.appKey}")
    private String appKey;

    @Value("${cim.bimface.client.appSecret}")
    private String appSecret;

    @Value("${cim.bimface.client.apiHost}")
    private String apiHost;

    @Value("${cim.bimface.client.fileHost}")
    private String fileHost;

    @Value("${my.minio.url}")
    private String url;

    @Value("${my.minio.user-name}")
    private String userName;

    @Value("${my.minio.password}")
    private String pwd;

    private static BimfaceClient bimfaceClient;
    private static MinioClient minioClient;

    private static final String SUCCESS = "SUCCESS";

    @PostConstruct
    public void init() {
        bimfaceClient = new BimfaceClient(appKey, appSecret, new Endpoint(apiHost, fileHost), null);
        try {
            minioClient = new MinioClient(url, userName, pwd);
        } catch (Exception e) {
            log.error("minio client initiation failed", e);
        }
    }

    @Scheduled(fixedDelayString = "${cim.bimface.translate-status.interval}")
    public void bimfaceTranslateStatus() {
        log.info("===start to udpate bimface translate status===: {}", DateUtil.getCurrentDateReadable());
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(ImportCimConstants.defauleSpaceName);
            FilteringItem equalFilteringItem = new EqualFilteringItem(BaseFileInfoKeys.TRANSLATE_STATUS,
                    BimfaceTranslateStatusEnum.PROCESS.toString());

            ExploreParameters ep = new ExploreParameters();
            ep.setDefaultFilteringItem(equalFilteringItem);
            ep.setType(BaseFileInfoKeys.BaseFileObjectTypeName);

            InformationExplorer ie = cds.getInformationExplorer();
            List<Fact> factList = ie.discoverInheritFacts(ep);
            log.debug("[{}] file translate status need to be updated", factList.size());
            for (Fact fact : factList) {
                if (fact.hasProperty(BaseFileInfoKeys.FILE_ID)) {
                    updateTranslateStatus(fact);
                } else {
                    log.error("fact not has fileId: {}", fact.getId());
                }
            }
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        log.info("===complete udpate bimface translate status===: {}", DateUtil.getCurrentDateReadable());
    }

    private void updateTranslateStatus(Fact fact) {
        String fileId = fact.getProperty(BaseFileInfoKeys.FILE_ID).getPropertyValue().toString();
        String translateTaskId = null;
        if (fact.hasProperty(BaseFileInfoKeys.TRANSLATE_TASK_ID)) {
            translateTaskId = fact.getProperty(BaseFileInfoKeys.TRANSLATE_TASK_ID).getPropertyValue().toString();
        }
        try {
            TranslateBean translateBean = bimfaceClient.getTranslate(Long.valueOf(fileId));
            log.debug("translate status: {}", JSON.toJSONString(translateBean));
            String statusStr = translateBean.getStatus().trim().toUpperCase();
            String status = null;
            String jobStatus = null;
            if (statusStr.equals(BimfaceTranslateStatusEnum.SUCCESS.toString())) {
                status = BimfaceTranslateStatusEnum.SUCCESS.toString();
                jobStatus = JobStatusEnum.ENDED.getCode();
            } else if (statusStr.equals(BimfaceTranslateStatusEnum.FAILED.toString())) {
                status = BimfaceTranslateStatusEnum.FAILED.toString();
                jobStatus = JobStatusEnum.FAIL.getCode();
            } else {
                log.debug("translate of [{}] is [{}]", fileId, statusStr);
            }
            if (status != null) {
                fact.updateProperty(BaseFileInfoKeys.TRANSLATE_STATUS, status);
            }
            if (translateTaskId != null && jobStatus != null) {
                JobClientUtil.sendTaskStatusMq(Long.valueOf(translateTaskId), jobStatus, getTenantIdByFact(fact));
            }
        } catch (BimfaceException e) {
            log.error("request bimface translate status failed", e);
        } catch (CimDataEngineRuntimeException e) {
            log.error("udpate fact property failed", e);
        }
    }

    /**
     * 上传文件到bimface并启动转换
     *
     * @param fileUploadTranslateBean
     */
    public void uploadAndTranslate(BimFileUploadTranslateBean fileUploadTranslateBean) {
        String bucket = fileUploadTranslateBean.getBucket();
        String fileName = fileUploadTranslateBean.getFileName();
        String srcFileName = fileUploadTranslateBean.getSrcFileName();
        Assert.hasText(bucket, "bucket is mandatory");
        Assert.hasText(fileName, "file name is mandatory");
        try {
            ObjectStat objectStat = null;
            //minio list object delay
            for (int r = 0; r < 3; r++) {
                log.debug("===try to get file metdata from minio {} times", r + 1);
                try {
                    objectStat = minioClient.statObject(bucket, fileName);
                    if (objectStat != null) {
                        log.debug("---object stat response: {}", objectStat.name());
                        break;
                    } else {
                        log.error("file of {} not found", fileName);
                    }
                    Thread.sleep(3 * 1000);
                } catch (Exception e) {
                    log.debug("get minio object stat failed", e);
                }
            }

            // ObjectStat objectStat = MyMinioClient.statObject(minioClient, bucket, fileName);
            if (objectStat == null) {
                log.error("file not found: bucket [{}] fileName [{}]", bucket, fileName);
                return;
            }
            Long length = objectStat.length();
            InputStream inputStream = MyMinioClient.downloader(minioClient, bucket, fileName);
            FileBean fileBean;
            try {
                inputStream = MyMinioClient.downloader(minioClient, bucket, fileName);
                fileBean = bimfaceClient.upload(srcFileName, length, inputStream);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("close minio stream filed", e);
                }
            }
            log.debug("file upload response: {}", fileBean);
            if (fileBean != null && fileBean.getStatus().toUpperCase().equals(SUCCESS)) {
                // TranslateResponseBean responseBean = BimFaceUtil.bimTranslate(bimfaceClient, fileBean.getFileId());
                TranslateResponseBean responseBean = BimFaceUtil.bimTranslate(bimfaceClient, fileBean.getFileId(),
                        BimfaceTranslateConfig.jsonObject);
                log.debug("translate response: {}", JSON.toJSONString(responseBean));
                // if (responseBean != null && responseBean.getStatus().toUpperCase().equals(SUCCESS)) {
                if (responseBean != null) {
                    updateStatusAndSendMsg(fileUploadTranslateBean, fileBean.getFileId(), responseBean.getDatabagId());
                } else {
                    log.error("start bimface translate failed");
                }
            } else {
                log.error("bim face file upload failed");
            }
        } catch (Exception e) {
            log.error("upload or trasnlate to bimface failed", e);
        }
    }

    private void updateStatusAndSendMsg(BimFileUploadTranslateBean fileUploadTranslateBean,
                                        Long fileId, String dataBagId) throws JsonProcessingException {
        CimDataSpace cds = null;
        try {
            String minioFileName = fileUploadTranslateBean.getFileName();
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(ImportCimConstants.defauleSpaceName);
            FilteringItem firstItem = new EqualFilteringItem(BaseFileInfoKeys.BUCKET_NAME,
                    fileUploadTranslateBean.getBucket());
            FilteringItem secondItem = new EqualFilteringItem(BaseFileInfoKeys.MINIO_OBJECT_NAME, minioFileName);

            ExploreParameters ep = new ExploreParameters();
            ep.setType(BaseFileInfoKeys.BaseFileObjectTypeName);
            ep.setDefaultFilteringItem(firstItem);
            ep.addFilteringItem(secondItem, ExploreParameters.FilteringLogic.AND);

            List<Fact> factList = cds.getInformationExplorer().discoverInheritFacts(ep);
            Assert.notEmpty(factList, "base file info fact not found");

            log.info("translation of file [{}] is start", minioFileName);
            //send start job message
            Map<String, String> jobBody = new HashMap<>();
            jobBody.put(JobStatusConst.PCOP_ANALYTIC_STORAGE_ID, minioFileName);
            jobBody.put(JobStatusConst.PCOP_ANALYTIC_STORAGE_FILENAME,
                    fileUploadTranslateBean.getSrcFileName());
            jobBody.put(JobStatusConst.PCOP_ANALYTIC_STORAGE_JOBTYPE,
                    JobStatusConst.PcopAnalyticStorageJobTypeEnum.MODEL_TRANSLATE.toString());
            long taskId = JobClientUtil.addTask(JobStatusConst.BIM_MODEL_TRANSLATE,
                    JobStatusConst.PCOP_ANALYTIC_STORAGE_JOBNAME_PREFIX, fileUploadTranslateBean.getTenantId(),
                    jobBody);
            //update file fact status
            Map<String, Object> values = new HashMap<>();
            values.put(BaseFileInfoKeys.FILE_ID, fileId);
            values.put(BaseFileInfoKeys.DATA_BAG_ID, dataBagId);
            values.put(BaseFileInfoKeys.TRANSLATE_STATUS, BimfaceTranslateStatusEnum.PROCESS.toString());
            values.put(BaseFileInfoKeys.TRANSLATE_TASK_ID, taskId);
            for (Fact fact : factList) {
                fact.addNewOrUpdateProperties(values);
            }
            //update job status
            JobClientUtil.sendTaskStatusMq(taskId, JobStatusEnum.PROCESSING.toString(),
                    fileUploadTranslateBean.getTenantId());
        } catch (CimDataEngineInfoExploreException | CimDataEngineRuntimeException e) {
            log.error("query related base file info fact failed", e);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    public static void setBimfaceClient(BimfaceClient bimfaceClient) {
        BimfaceStatus.bimfaceClient = bimfaceClient;
    }

    public static void setMinioClient(MinioClient minioClient) {
        BimfaceStatus.minioClient = minioClient;
    }

    private static String getTenantIdByFact(Fact fact) throws CimDataEngineRuntimeException {
        String tennatId = null;
        List<Relation> relationList =
                fact.getAllSpecifiedRelations(BusinessLogicConstant.RELATION_TYPE_BELONGS_TO_TENANT,
                        RelationDirection.TWO_WAY);

        if (relationList != null && relationList.size() > 0) {
            Relation relation = relationList.get(0);
            Relationable relationable = relation.getToRelationable();
            if (relation.getId().equals(fact.getId())) {
                relationable = relation.getFromRelationable();
            }
            tennatId = relationable.getProperty("CIM_BUILDIN_TENANT_ID").getPropertyValue().toString();
        }
        return tennatId;
    }

}
