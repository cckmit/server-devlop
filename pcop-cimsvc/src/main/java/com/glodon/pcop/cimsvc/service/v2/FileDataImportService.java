package com.glodon.pcop.cimsvc.service.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.constant.FileImportTypeEnum;
import com.glodon.pcop.cim.common.constant.JobStatusConst;
import com.glodon.pcop.cim.common.exception.ShpStructGisException;
import com.glodon.pcop.cim.common.model.PropertyMappingInputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.FileContentTypes;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.*;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimapi.exception.MinioClientException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.v2.GisFileStructOutputBean;
import com.glodon.pcop.cimsvc.model.v2.mapping.*;
import com.glodon.pcop.cimsvc.service.MinioService;
import com.glodon.pcop.cimsvc.util.DateUtil;
import com.glodon.pcop.cimsvc.util.PinyinUtils;
import com.glodon.pcop.core.models.IdResult;
import com.glodon.pcop.jobapi.JobResponse;
import com.glodon.pcop.jobapi.dto.JobParmDTO;
import com.glodon.pcop.jobapi.dto.JobPropsDTO;
import com.glodon.pcop.jobapi.type.JobStatusEnum;
import com.glodon.pcop.jobclt.client.JobInfoClient;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileDataImportService {
    private static Logger log = LoggerFactory.getLogger(FileDataImportService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MinioService minioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExcelParserUtil excelParserUtil;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JobInfoClient jobInfoClient;

    public static Map<String, String> GisPropertyTypeMapping;

    public static Set<String> spacialFileContentTypes;

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Value("${my.mq.importJobName}")
    private String importJobNamePrefix = "SHP_DATA_IMPORT";
    @Value("${my.mq.analyticStorageCode}")
    private String analyticStorageCode = "pcopAnalyticStorage";

    private static final String TASK_ID = "task_id";
    private static final String ZIP_FILE_URL = "zipFileUrl";
    private static final String TARGET_FILE_NAME = "targetFileName";
    private static final String OBJECT_TYPE_ID = "objectTypeId";
    // private static final String ZIP_FILE_URL = "zipFileUrl";

    static {
        GisPropertyTypeMapping = new HashMap<>();
        GisPropertyTypeMapping.put("int", "INT");
        GisPropertyTypeMapping.put("float", "FLOAT");
        GisPropertyTypeMapping.put("double", "DOUBLE");
        GisPropertyTypeMapping.put("char", "CHAR");
        GisPropertyTypeMapping.put("string", "STRING");
        GisPropertyTypeMapping.put("long", "LONG");
        GisPropertyTypeMapping.put("geometry", "STRING");

        spacialFileContentTypes = new HashSet<>();
        spacialFileContentTypes.add(FileContentTypes.SHP);
        spacialFileContentTypes.add(FileContentTypes.GDB);
        spacialFileContentTypes.add(FileContentTypes.MDB);
    }

    /**
     * 解析文件结构请求地址
     */
    public static String getStructUrl = "http://10.0.197.53:7202/glodon/3DGISServer/parser/getStruct?zipFileUrl" +
            "={zipFileUrl}";

    /**
     * 通知开始导入请求地址
     */
    // public static String getRecordUrl = "http://10.0.197
    // .53:7202/glodon/3DGISServer/parser/getRecord?zipFileUrl={zipFileUrl}&targetFileName={targetFileName}&taskId
    // ={taskId}";
    public static String getRecordUrl = "http://10.0.197.53:7202/glodon/3DGISServer/parser/getRecord";

    /**
     * 分类型获取文件结构
     *
     * @param tenantId
     * @param fileDataId
     * @param fileContentType
     * @return
     * @throws MinioClientException
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     */
    public List<FileStructBean> getStruct(String tenantId, String fileDataId, String fileContentType)
            throws MinioClientException, CimDataEngineRuntimeException, CimDataEngineInfoExploreException,
            InputErrorException {//NOSONAR
        List<FileStructBean> structBeanList = new ArrayList<>();

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            Map<String, String> fileMap = getFileStoreStruct(cds, fileDataId);
            if (fileMap != null && fileMap.size() > 0) {
                String bucketName = fileMap.get(CimConstants.BaseFileInfoKeys.BUCKET_NAME);
                String minioObjectName = fileMap.get(CimConstants.BaseFileInfoKeys.MINIO_OBJECT_NAME);
                if (spacialFileContentTypes.contains(fileContentType.trim().toUpperCase())) {
                    log.info("空间数据文件解析");
                    structBeanList = getSpacialFileStruct(bucketName, minioObjectName);
                } else if (fileContentType.trim().toUpperCase().equals(
                        FileContentTypes.XLS) || fileContentType.trim().toUpperCase().equals(FileContentTypes.XLSX)) {
                    log.info("excel 文件结构解析");
                    structBeanList = getExcelFileStruct(bucketName, minioObjectName);
                } else {
                    //测试
                    FileStructBean structBean = new FileStructBean();
                    structBean.setSingleFileName("test");
                    structBeanList.add(structBean);
                    log.error("not support file content type currently: {}", fileContentType);
                }

            } else {
                log.error("file of {} not found in {}", fileDataId,
                        CimConstants.BaseFileInfoKeys.BaseFileObjectTypeName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return structBeanList;
    }

    /**
     * 解析空间数据文件结构
     *
     * @param bucketName
     * @param minioObjectName
     * @return
     * @throws MinioClientException
     */
    public List<FileStructBean> getSpacialFileStruct(String bucketName, String minioObjectName)
            throws MinioClientException {//NOSONAR
        List<FileStructBean> structBeanList = new ArrayList<>();
        String shareUrl = minioService.presignedGetObject(bucketName, minioObjectName, 0);
        log.info("share url of bucketName={}, minioObjectName={}: {}", bucketName, minioObjectName, shareUrl);
        // String structUrl = "http://10.0.197.53:7202/glodon/3DGISServer/parser/getStruct?zipFileUrl={zipFileUrl}";
        // Map<String, String> params = new HashMap<>();
        // params.put(ZIP_FILE_URL, shareUrl);
        // ResponseEntity<String> responseEntity = restTemplate.getForEntity(getStructUrl, String.class, params);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getStructUrl).queryParam(ZIP_FILE_URL,
                shareUrl);
        log.info("get shp file struct uri: [{}]", builder.toUriString());
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(builder.build().toUri(), String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            try {
                log.info("fiel structure response: {}", responseEntity.getBody());
                List<GisFileStructOutputBean> metdataBeans = objectMapper.readValue(responseEntity.getBody(),
                        new TypeReference<List<GisFileStructOutputBean>>() {
                        });
                Assert.notEmpty(metdataBeans, "GIS server error: get shp file struct");
                for (GisFileStructOutputBean bean : metdataBeans) {
                    FileStructBean structBean = new FileStructBean();
                    structBean.setSingleFileName(bean.getFileName());
                    List<Map<String, String>> fieldCollection = bean.getFieldCollection();
                    Map<String, String> struct = new HashMap<>();
                    if (fieldCollection != null && fieldCollection.size() > 0) {
                        for (Map<String, String> field : fieldCollection) {
                            for (Map.Entry<String, String> entry : field.entrySet()) {
                                if (StringUtils.isBlank(
                                        GisPropertyTypeMapping.get(entry.getValue().trim().toLowerCase()))) {
                                    struct.put(entry.getKey(),
                                            GisPropertyTypeMapping.get(entry.getValue().trim().toLowerCase()));
                                } else {
                                    struct.put(entry.getKey(), GisPropertyTypeMapping.get("string"));
                                }
                            }
                        }
                    }
                    structBean.setStruct(struct);
                    structBean.setGeoType(bean.getGeoType());

                    structBeanList.add(structBean);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.error("get file struct failed: {}", responseEntity.getStatusCode());
            throw new ShpStructGisException("GIS server error: get shp file struct");
        }
        return structBeanList;
    }


    /**
     * 解析Excel数据文件结构
     *
     * @param bucketName
     * @param minioObjectName
     * @return
     * @throws IOException
     */
    private List<FileStructBean> getExcelFileStruct(String bucketName, String minioObjectName) throws IOException {
        List<FileStructBean> structBeanList;
        InputStream inputStream = null;
        try {
            inputStream = minioService.minioDownloader(bucketName, minioObjectName);
            structBeanList = ExcelParserUtil.excelParser(inputStream, minioObjectName);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return structBeanList;
    }

    /**
     * 开始文件数据导入任务
     *
     * @param tenantId
     * @param mappingInputBean
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     * @throws MinioClientException
     */
    public List<FileMappingOutputBean> startImportTask(String tenantId, FileMappingInputBean mappingInputBean)
            throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException, MinioClientException,
            InputErrorException {//NOSONAR
        List<FileMappingOutputBean> fileMappingOutputBeanList = new ArrayList<>();
        log.info("file content type: {}", mappingInputBean.getFileContentType());
        String fileContentType = mappingInputBean.getFileContentType().trim().toUpperCase();
        String fileDataId = mappingInputBean.getFileDataId();
        List<ObjectMappingInputBean> fileMapping = mappingInputBean.getFileMapping();
        List<FileStructBean> structBeanList = getStruct(tenantId, fileDataId, fileContentType);
        Map<String, FileStructBean> structBeanMap = new HashMap<>();
        Map<String, List<PropertyMappingInputBean>> propertiesMapping = new HashMap<>();
        Map<String, String> objectTypeIdMap = new HashMap<>();
        Map<String, Boolean> isUpdateMap = new HashMap<>();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            InfoObjectDef infoObjectDef =
                    modelCore.getInfoObjectDef(CimConstants.FileImportProperties.DATA_IMPORT_MAPPING_INFO_TYPE_NAME);
            for (FileStructBean structBean : structBeanList) {
                structBeanMap.put(structBean.getSingleFileName(), structBean);
            }
            if (fileMapping != null && fileMapping.size() > 0) {
                Map<String, String> excelNameTaskMap = new HashMap<>();
                for (ObjectMappingInputBean objectMappingInputBean : fileMapping) {
                    try {
                        String singleFileName = objectMappingInputBean.getSingleFileName();
                        if (StringUtils.isNotBlank(singleFileName)) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put(JobStatusConst.PCOP_ANALYTIC_STORAGE_ID, fileDataId);
                            map.put(JobStatusConst.PCOP_ANALYTIC_STORAGE_FILENAME, singleFileName);
                            map.put(JobStatusConst.PCOP_ANALYTIC_STORAGE_JOBTYPE,
                                    JobStatusConst.PcopAnalyticStorageJobTypeEnum.PARSE_IMPORT.toString());
                            String json = objectMapper.writeValueAsString(map);

                            String taskId = sendStartTaskMq(json) + "";
                            // String taskId = "123456123456";
                            if (StringUtils.isNotBlank(taskId)) {
                                sendTaskStatusMq(taskId, JobStatusEnum.PROCESSING.getCode());
                            }

                            List<PropertyMappingInputBean> tmpPropertiesMapping = parserObjectMappingV2(
                                    fileDataId, taskId, objectMappingInputBean, structBeanMap.get(singleFileName),
                                    infoObjectDef, modelCore, cds, tenantId);
                            cds.flushUncommitedData();
                            if (spacialFileContentTypes.contains(fileContentType)) {
                                fileMappingOutputBeanList.add(sendStartTaskToGis(taskId, singleFileName, fileDataId,
                                        cds, objectMappingInputBean.getObjectTypeId()));
                            } else if (fileContentType.equals(FileContentTypes.XLS) || fileContentType.equals(
                                    FileContentTypes.XLSX)) {
                                FileMappingOutputBean mappingOutputBean = new FileMappingOutputBean(singleFileName,
                                        taskId);
                                fileMappingOutputBeanList.add(mappingOutputBean);
                                excelNameTaskMap.put(singleFileName, taskId);
                                List<PropertyMappingInputBean> propertyMappingInputBeanList = new ArrayList<>(
                                        tmpPropertiesMapping);
                                //merge all properties mapping
//                                if (objectMappingInputBean.getDataSets() != null && objectMappingInputBean.getDataSets().size() > 0) {
//                                    for (DataSetMappingInputBean dsMappingInput : objectMappingInputBean.getDataSets()) {
//                                        List<PropertyMappingInputBean> mappingInputBeans = dsMappingInput.getProperties();
//                                        if (mappingInputBeans != null) {
//                                            propertyMappingInputBeanList.addAll(mappingInputBeans);
//                                        }
//                                    }
//                                } else {
//                                    log.debug("no dataset mapping input");
//                                }
                                objectTypeIdMap.put(singleFileName, objectMappingInputBean.getObjectTypeId());
                                isUpdateMap.put(singleFileName, objectMappingInputBean.getUpdate());
                                propertiesMapping.put(singleFileName, propertyMappingInputBeanList);
                            } else {
                                log.error("not support file format current: {}", fileContentType);
                            }
                        }
                    } catch (Exception e) {
                        log.error("import task start failed: {}", objectMappingInputBean.getObjectTypeName());
                        log.error("import task start failed cause: ", e);
                    }
                }
                if (excelNameTaskMap.size() > 0) {
                    log.info("start send excel data to kafka task: {}", excelNameTaskMap);
                    Map<String, String> fileMap = getFileStoreStruct(cds, fileDataId);
                    InputStream inputStream =
                            minioService.minioDownloader(fileMap.get(CimConstants.BaseFileInfoKeys.BUCKET_NAME),
                                    fileMap.get(CimConstants.BaseFileInfoKeys.MINIO_OBJECT_NAME));
                    ExcelData2KafkaTask data2KafkaTask = new ExcelData2KafkaTask(tenantId, isUpdateMap, kafkaTemplate
                            , inputStream, excelNameTaskMap, objectTypeIdMap, propertiesMapping);
                    executorService.submit(data2KafkaTask);
                } else {
                    log.info("not excel file parser");
                }
            } else {
                log.error("file import failed, no object type info is support");
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return fileMappingOutputBeanList;
    }

    /**
     * 发送开始任务消息到MQ
     *
     * @param json
     * @return
     */
    public Long sendStartTaskMq(String json) {
        JobPropsDTO jobPropsDTO = new JobPropsDTO();
        // shp导入消息
        jobPropsDTO.setJobName(importJobNamePrefix + "-" + DateUtil.getCurrentDateReadable());
        log.info("typeCode={}", analyticStorageCode);
        jobPropsDTO.setTypeCode(analyticStorageCode);
        // 消息内容
        jobPropsDTO.setParam(json);
        JobResponse<IdResult> jobResponse = jobInfoClient.add(jobPropsDTO);
        log.info("SHP file import response status code: {}, content: {}", jobResponse.getCode(), jobResponse.getData());
        return jobResponse.getData().getId();
    }


    /**
     * 发送更改任务消息到MQ--更改文件任务状态
     *
     * @return
     */
    public void sendTaskStatusMq(String taskId, String status) {
        JobParmDTO jobParmDTO = new JobParmDTO();
        jobParmDTO.setStatus(status);
        jobParmDTO.setDate(DateUtil.getCurrentDate());
        log.info("更改文件任务状态开始{}", DateUtil.getCurrentDate());
        jobInfoClient.updateStatus(Long.valueOf(taskId), jobParmDTO);
    }

    /**
     * 发送开始任务到GIS
     *
     * @param taskId
     * @param singleFileName
     * @param fileDateId
     * @return
     * @throws MinioClientException
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     */
    public FileMappingOutputBean sendStartTaskToGis(String taskId, String singleFileName, String fileDateId,
                                                    CimDataSpace cds, String objectTypeId)
            throws MinioClientException, CimDataEngineRuntimeException, CimDataEngineInfoExploreException,
            InputErrorException {
        Map<String, String> fileMap = getFileStoreStruct(cds, fileDateId);
        String shareUrl = minioService.presignedGetObject(fileMap.get(CimConstants.BaseFileInfoKeys.BUCKET_NAME),
                fileMap.get(CimConstants.BaseFileInfoKeys.MINIO_OBJECT_NAME), 0);
        log.info("share url of bucketName={}, minioObjectName={}: {}",
                fileMap.get(CimConstants.BaseFileInfoKeys.BUCKET_NAME),
                CimConstants.BaseFileInfoKeys.MINIO_OBJECT_NAME, shareUrl);

        // Map<String, String> parameters = new HashMap<>();
        // parameters.put("taskId", taskId);
        // parameters.put("zipFileUrl", shareUrl);
        // parameters.put("targetFileName", singleFileName);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getRecordUrl)
                .queryParam(ZIP_FILE_URL, shareUrl)
                .queryParam(TARGET_FILE_NAME, singleFileName)
                .queryParam(TASK_ID, taskId)
                .queryParam(OBJECT_TYPE_ID, objectTypeId);

        log.info("get record: {}", builder.build().toUri());

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(builder.build().toUri(), String.class);
        FileMappingOutputBean fileMappingOutputBean = new FileMappingOutputBean(singleFileName);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String respondeBody = responseEntity.getBody();
            log.info("get fiel record response: {}", respondeBody);
            if (respondeBody.trim().toUpperCase().equals("OK")) {
                fileMappingOutputBean = new FileMappingOutputBean(singleFileName, taskId);
            } else {
                fileMappingOutputBean.setMessage(respondeBody);
            }
        } else {
            log.error("get file record failed: {}", responseEntity.getStatusCode());
        }
        return fileMappingOutputBean;
    }

    /**
     * 查询文件元数据
     *
     * @param fileDataId
     * @return
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     */
    public Map<String, String> getFileStoreStruct(CimDataSpace cds, String fileDataId)
            throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException, InputErrorException {
        Map<String, String> map = new HashMap<>();

        FilteringItem filteringItem = new EqualFilteringItem(CimConstants.BaseFileInfoKeys.FILE_DATA_ID, fileDataId);
        ExploreParameters exploreParameters = new ExploreParameters();
        exploreParameters.setType(CimConstants.BaseFileInfoKeys.BaseFileObjectTypeName);
        exploreParameters.setDefaultFilteringItem(filteringItem);
        InformationExplorer informationExplorer = cds.getInformationExplorer();
        List<Fact> factList = informationExplorer.discoverInheritFacts(exploreParameters);

        if (factList != null && factList.size() > 0) {
            //ID唯一，数据只有一条
            Fact fact = factList.get(0);
            if (fact.hasProperty(CimConstants.BaseFileInfoKeys.MINIO_OBJECT_NAME) && fact.hasProperty(
                    CimConstants.BaseFileInfoKeys.BUCKET_NAME)) {
                String bucketName =
                        fact.getProperty(CimConstants.BaseFileInfoKeys.BUCKET_NAME).getPropertyValue().toString();
                String minioObjectName =
                        fact.getProperty(CimConstants.BaseFileInfoKeys.MINIO_OBJECT_NAME).getPropertyValue().toString();
                map.put(CimConstants.BaseFileInfoKeys.BUCKET_NAME, bucketName);
                map.put(CimConstants.BaseFileInfoKeys.MINIO_OBJECT_NAME, minioObjectName);
            }
        } else {
            String msg = String.format("file not found, fileDataId [%s]", fileDataId);
            log.error(msg);
            throw new InputErrorException(msg);
        }
        return map;
    }

    /**
     * 解析单个文件的Mapping信息
     *
     * @param fileDataId
     * @param taskId
     * @param objectMapping
     * @param fileStructBean
     * @param fileImportInfoObjectDef
     * @throws DataServiceUserException
     * @throws JsonProcessingException
     */
    public void parserObjectMapping(String fileDataId, String taskId, ObjectMappingInputBean objectMapping,
                                    FileStructBean fileStructBean, InfoObjectDef fileImportInfoObjectDef,
                                    CIMModelCore modelCore, CimDataSpace cds, String tenantId)
            throws DataServiceUserException, JsonProcessingException, CimDataEngineInfoExploreException,
            DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        List<PropertyMappingInputBean> propertyMappings = new ArrayList<>();
        String singleFileName = objectMapping.getSingleFileName();

        Map<String, Object> generalInfo = new HashMap<>();
        if (objectMapping.getCreate()) {
            //create object type and data set
            log.info("根据文件结构创建相应的定义");
            addObjectMapping(propertyMappings, objectMapping, generalInfo, modelCore, fileStructBean, cds, tenantId);
        } else if (objectMapping.getDataSets() != null && (objectMapping.getDataSets().get(
                0).getProperties() == null || objectMapping.getDataSets().get(0).getProperties().size() < 1)) {
            // mapping automatically
            log.info("根据指定的对象类型ID和属性集，自动匹配");
            automaticallyMapping(propertyMappings, objectMapping, objectMapping.getDataSets().get(0).getName(),
                    generalInfo, modelCore, fileStructBean);
        } else {
            log.info("用户匹配");
            mappingManually(propertyMappings, objectMapping, objectMapping.getDataSets().get(0), generalInfo);
        }

        //save property mpping info to db
        Map<String, Object> baseInfo = new HashMap<>();
        baseInfo.put(CimConstants.BaseDataSetKeys.NAME, taskId);

        generalInfo.put(CimConstants.FileImportProperties.TASK_ID, taskId);
        generalInfo.put(CimConstants.FileImportProperties.TENANT_ID, tenantId);
        generalInfo.put(CimConstants.FileImportProperties.FILE_DATA_ID, fileDataId);
        generalInfo.put(CimConstants.FileImportProperties.SINGLE_FILE_NAME, singleFileName);
        generalInfo.put(CimConstants.FileImportProperties.PROPERTIES_MAPPING,
                objectMapper.writeValueAsString(propertyMappings));
        generalInfo.put(CimConstants.FileImportProperties.CREATE_TIME, new Date());
        generalInfo.put(CimConstants.FileImportProperties.UPDATE_TIME, new Date());

        InfoObjectValue objectValue = new InfoObjectValue();
        objectValue.setBaseDatasetPropertiesValue(baseInfo);
        objectValue.setGeneralDatasetsPropertiesValue(generalInfo);

        fileImportInfoObjectDef.newObject(objectValue, false);
    }

    public List<PropertyMappingInputBean> parserObjectMappingV2(String fileDataId, String taskId,
                                                                ObjectMappingInputBean objectMapping,
                                                                FileStructBean fileStructBean,
                                                                InfoObjectDef fileImportInfoObjectDef,
                                                                CIMModelCore modelCore, CimDataSpace cds,
                                                                String tenantId)
            throws DataServiceUserException, JsonProcessingException, CimDataEngineInfoExploreException,
            DataServiceModelRuntimeException, CimDataEngineRuntimeException {//NOSONAR
        List<PropertyMappingInputBean> propertyMappings = new ArrayList<>();
        String singleFileName = objectMapping.getSingleFileName();

        Map<String, Object> generalInfo = new HashMap<>();
        if (objectMapping.getCreate()) {
            //create object type and data set
            log.info("根据文件结构创建相应的定义：对象类型，属性集和属性");
            addObjectMapping(propertyMappings, objectMapping, generalInfo, modelCore, fileStructBean, cds, tenantId);
        } else if (objectMapping.getDataSets() == null || objectMapping.getDataSets().size() < 1) {
            // mapping all exists data sets automatically
            log.info("根据指定的对象类型ID，自动匹配所有属性");
            automaticallyMapping(propertyMappings, objectMapping, null, generalInfo, modelCore, fileStructBean);
        } else {
            log.info("混合匹配模式");
            // InfoObjectDef infoObjectDef;
            InfoObjectDefs infoObjectDefs = modelCore.getInfoObjectDefs();
            InfoObjectDef infoObjectDef = infoObjectDefs.getInfoObjectDef(objectMapping.getObjectTypeId());
            if (objectMapping.getClean()) {
                log.warn("object should be clean: [{}]", objectMapping.getObjectTypeId());
//                cds.getInheritFactType(objectMapping.getObjectTypeId()).removeContainedFacts();

                ExploreParameters ep = new ExploreParameters();
                ep.setStartPage(1);
                ep.setEndPage(2);
                ep.setPageSize(Integer.MAX_VALUE);
                InfoObjectRetrieveResult retrieveResult = infoObjectDef.getObjects(ep);

                if (retrieveResult != null && !CollectionUtils.isEmpty(retrieveResult.getInfoObjects())) {
                    log.info("===delete object size: [{}]", retrieveResult.getInfoObjects().size());
                    for (InfoObject object : retrieveResult.getInfoObjects()) {
                        cds.removeFact(object.getObjectInstanceRID());
                    }
                } else {
                    log.info("===delete object size: [0]");
                }
            }


            List<DataSetMappingInputBean> dataSetMappingInputBeans = objectMapping.getDataSets();
            boolean isCreateDataSet = true;
            for (DataSetMappingInputBean dsMapping : dataSetMappingInputBeans) {
                if (dsMapping.getCreate()) {
                    log.info("新增属性集：name={}, id={}", dsMapping.getName(), dsMapping.getId());
                    if (isCreateDataSet) {
                        if (StringUtils.isNotBlank(dsMapping.getName()) && StringUtils.isNotBlank(
                                dsMapping.getDesc())) {
                            isCreateDataSet = false;
                            addDataSetMapping(propertyMappings, objectMapping, dsMapping, generalInfo, modelCore,
                                    fileStructBean);
                        } else {
                            log.info("add data set: data set name and desc are mandatory");
                        }
                    } else {
                        log.info("only one data set should be created");
                    }
                } else {
                    log.info("用户已匹配属性集：name={}, id={}", dsMapping.getName(), dsMapping.getId());
                    mappingManually(propertyMappings, objectMapping, dsMapping, generalInfo);
                }
            }
        }
        //save property mpping info to db
//        Map<String, Object> baseInfo = new HashMap<>();
//        baseInfo.put(CimConstants.BaseDataSetKeys.NAME, taskId);
//
//        generalInfo.put(CimConstants.FileImportProperties.TASK_ID, taskId);
//        generalInfo.put(CimConstants.FileImportProperties.TENANT_ID, tenantId);
//        generalInfo.put(CimConstants.FileImportProperties.FILE_DATA_ID, fileDataId);
//        generalInfo.put(CimConstants.FileImportProperties.SINGLE_FILE_NAME, singleFileName);
//        generalInfo.put(CimConstants.FileImportProperties.PROPERTIES_MAPPING,
//                objectMapper.writeValueAsString(propertyMappings));
//        generalInfo.put(CimConstants.FileImportProperties.CREATE_TIME, new Date());
//        generalInfo.put(CimConstants.FileImportProperties.UPDATE_TIME, new Date());
//
//        InfoObjectValue objectValue = new InfoObjectValue();
//        objectValue.setBaseDatasetPropertiesValue(baseInfo);
//        objectValue.setGeneralDatasetsPropertiesValue(generalInfo);
//
//        fileImportInfoObjectDef.newObject(objectValue, false);
        return propertyMappings;
    }


    /**
     * 根据对象类型已定义属性自动匹配：若属性集已指定，则匹配该属性集；若属性集未指定，则匹配所有属性集
     *
     * @param propertyMappings
     * @param objectMapping
     * @param dataSetName
     * @param generalInfo
     * @param modelCore
     * @param fileStructBean
     * @throws JsonProcessingException
     */
    private void automaticallyMapping(List<PropertyMappingInputBean> propertyMappings,
                                      ObjectMappingInputBean objectMapping, String dataSetName,
                                      Map<String, Object> generalInfo, CIMModelCore modelCore,
                                      FileStructBean fileStructBean) throws JsonProcessingException {//NOSONAR
        InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectMapping.getObjectTypeId());
        if (infoObjectDef != null) {
            // DataSetMappingInputBean dsMapping = objectMapping.getDataSets().get(0);
            generalInfo.put(CimConstants.FileImportProperties.OBJECT_TYPE_ID, objectMapping.getObjectTypeId());
            StringBuffer dsIds = new StringBuffer();
            StringBuffer dsNames = new StringBuffer();
            // generalInfo.put(CimConstants.FileImportProperties.DataSetId, dsMapping.getId());
            // generalInfo.put(CimConstants.FileImportProperties.DataSetName, dsMapping.getName());
            List<DatasetDef> datasetDefList = new ArrayList<>();
            if (StringUtils.isNotBlank(dataSetName)) {
                datasetDefList.add(infoObjectDef.getDatasetDef(dataSetName));
            } else {
                datasetDefList = infoObjectDef.getDatasetDefs();
            }
            if (datasetDefList != null && datasetDefList.size() > 0) {
                List<PropertyTypeDef> propertyTypeDefList = new ArrayList<>();
                for (DatasetDef datasetDef : datasetDefList) {
                    dsIds.append(datasetDef.getDatasetRID()).append(",");
                    dsNames.append(datasetDef.getDatasetDesc()).append(",");
                    propertyTypeDefList.addAll(datasetDef.getPropertyTypeDefs());
                }
                generalInfo.put(CimConstants.FileImportProperties.DATA_SET_ID, dsIds.toString());
                generalInfo.put(CimConstants.FileImportProperties.DATA_SET_NAME, dsNames.toString());
                log.info("file struct: {}", objectMapper.writeValueAsString(fileStructBean));
                log.info("property size: {}", propertyTypeDefList.size());
                if (propertyTypeDefList != null && propertyTypeDefList.size() > 0) {
                    Map<String, String> srcProps = fileStructBean.getStruct();
                    for (PropertyTypeDef propertyTypeDef : propertyTypeDefList) {
                        PropertyMappingInputBean propertyMappingInputBean = new PropertyMappingInputBean();
                        if (srcProps.containsKey(propertyTypeDef.getPropertyTypeName())) {
                            propertyMappingInputBean.setPropertyType(propertyTypeDef.getPropertyFieldDataClassify());
                            propertyMappingInputBean.setSrcPropertyName(propertyTypeDef.getPropertyTypeName());
                            propertyMappingInputBean.setDesPropertyName(propertyTypeDef.getPropertyTypeName());
                        } else if (srcProps.containsKey(propertyTypeDef.getPropertyTypeDesc())) {
                            propertyMappingInputBean.setPropertyType(propertyTypeDef.getPropertyFieldDataClassify());
                            propertyMappingInputBean.setSrcPropertyName(propertyTypeDef.getPropertyTypeDesc());
                            propertyMappingInputBean.setDesPropertyName(propertyTypeDef.getPropertyTypeName());
                        } else {
                            log.info("not mapping of property name={}", propertyTypeDef.getPropertyTypeName());
                        }
                        if (StringUtils.isNotBlank(
                                propertyMappingInputBean.getDesPropertyName()) && StringUtils.isNotBlank(
                                propertyMappingInputBean.getSrcPropertyName())) {
                            propertyMappings.add(propertyMappingInputBean);
                        }
                    }
                } else {
                    log.error("data set of objectTypeId={} is zero", objectMapping.getObjectTypeName());
                }
            } else {
                log.error("data set of object type ={} not found", objectMapping.getObjectTypeId());
            }
        } else {
            log.error("object type of {} is null", objectMapping.getObjectTypeId());
        }
    }

    /**
     * 用户匹配信息处理
     *
     * @param propertyMappings
     * @param objectMapping
     * @param generalInfo
     */
    private void mappingManually(List<PropertyMappingInputBean> propertyMappings,
                                 ObjectMappingInputBean objectMapping, DataSetMappingInputBean dsMapping, Map<String,
            Object> generalInfo) {
        // DataSetMappingInputBean dsMapping = objectMapping.getDataSets().get(0);
        generalInfo.put(CimConstants.FileImportProperties.OBJECT_TYPE_ID, objectMapping.getObjectTypeId());
        generalInfo.put(CimConstants.FileImportProperties.DATA_SET_ID, dsMapping.getId());
        generalInfo.put(CimConstants.FileImportProperties.DATA_SET_NAME, dsMapping.getName());
        List<PropertyMappingInputBean> propertyMappingInputBeans = dsMapping.getProperties();
        if (propertyMappingInputBeans != null || propertyMappingInputBeans.size() > 0) {
            for (PropertyMappingInputBean mappingInputBean : propertyMappingInputBeans) {
                if (StringUtils.isNotBlank(mappingInputBean.getSrcPropertyName()) && StringUtils.isNotBlank(
                        mappingInputBean.getDesPropertyName())) {
                    propertyMappings.add(mappingInputBean);
                } else {
                    log.info("invalid property mapping item");
                }
            }
        } else {
            log.error("no property mapping is provided");
        }
    }


    /**
     * 新增对象类型，属性集和属性
     *
     * @param propertyMappings
     * @param objectMapping
     * @param generalInfo
     * @param modelCore
     * @param fileStructBean
     * @param cds
     * @param tenantId
     * @return
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     * @throws DataServiceModelRuntimeException
     */
    private Boolean addObjectMapping(List<PropertyMappingInputBean> propertyMappings,
                                     ObjectMappingInputBean objectMapping, Map<String, Object> generalInfo,
                                     CIMModelCore modelCore, FileStructBean fileStructBean, CimDataSpace cds,
                                     String tenantId) throws CimDataEngineRuntimeException,
            CimDataEngineInfoExploreException, DataServiceModelRuntimeException {//NOSONAR
        Boolean flag = false;
        String objectTypeId = objectMapping.getObjectTypeId();
        // DataSetMappingInputBean dsMappingBean = dataSetMappingInputBeans.get(0);
        if (StringUtils.isBlank(objectTypeId)) {
            log.error("object type id is null, obejctTypeId={}", objectMapping.getObjectTypeId());
            return flag;
        }

        String dataSetName = "";
        List<DataSetMappingInputBean> dataSetMappingInputBeans = objectMapping.getDataSets();
        if (dataSetMappingInputBeans == null || dataSetMappingInputBeans.size() < 1) {
            log.info("data set mapping info is null");
        } else {
            DataSetMappingInputBean dsMappingBean = dataSetMappingInputBeans.get(0);
            if (StringUtils.isBlank(dsMappingBean.getName())) {
                log.info("data set mapping, data set name is empty");
            } else {
                dataSetName = dsMappingBean.getName();
            }
        }
        if (StringUtils.isBlank(dataSetName)) {
            log.info("default data set name is used");
            dataSetName = objectTypeId + "_DataSet";
        }

        Map<String, String> properties = fileStructBean.getStruct();
        if (properties == null || properties.size() < 1) {
            log.error("no property struct is get from file");
            return flag;
        }

        //新增对象模型定义
        //对象模型ID可用性检查
        InfoObjectDef infoObjectDef;
        InfoObjectDefs infoObjectDefs = modelCore.getInfoObjectDefs();
        Fact targetCimObjectTypeFact = InfoObjectFeatures.getInfoObjectTypeStatusRecord(cds, objectTypeId);
        if (targetCimObjectTypeFact != null) {
            // objectTypeId = objectTypeId + "__" + DateUtil.getCurrentDateReadable();
            infoObjectDef = infoObjectDefs.getInfoObjectDef(objectTypeId);
            if (infoObjectDef == null) {
                infoObjectDefs.shareInfoObjectDef(objectTypeId, tenantId);
                infoObjectDef = infoObjectDefs.getInfoObjectDef(objectTypeId);
                if (infoObjectDef == null) {
                    log.error("share info object type failed: [{}]", objectTypeId);
                }
            }
            if (objectMapping.getClean()) {
                log.warn("object should be clean: [{}]", objectTypeId);
//                cds.getInheritFactType(objectTypeId).removeContainedFacts();
                ExploreParameters ep = new ExploreParameters();
                ep.setStartPage(1);
                ep.setEndPage(2);
                ep.setPageSize(Integer.MAX_VALUE);
                InfoObjectRetrieveResult retrieveResult = infoObjectDef.getObjects(ep);

                if (retrieveResult != null && !CollectionUtils.isEmpty(retrieveResult.getInfoObjects())) {
                    log.info("===delete object size: [{}]", retrieveResult.getInfoObjects().size());
                    for (InfoObject object : retrieveResult.getInfoObjects()) {
                        cds.removeFact(object.getObjectInstanceRID());
                    }
                } else {
                    log.info("===delete object size: [0]");
                }
            }
        } else {
            InfoObjectTypeVO objectTypeVO = new InfoObjectTypeVO();
            objectTypeVO.setObjectId(objectTypeId);
            objectTypeVO.setObjectName(objectMapping.getObjectTypeName());
            objectTypeVO.setTenantId(tenantId);
            try {
                log.info("add new object type: {}", objectMapper.writeValueAsString(objectTypeVO));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            cds.flushUncommitedData();
            infoObjectDef = infoObjectDefs.addRootInfoObjectDef(objectTypeVO);
        }

        if (infoObjectDef != null) {
            //新增属性集定义
            DatasetVO datasetVO = new DatasetVO();
            // datasetVO.setDatasetName(dsMappingBean.getName());
            datasetVO.setDatasetName(dataSetName);
            datasetVO.setDatasetDesc(dataSetName);
            datasetVO.setDatasetClassify("通用属性集");
            datasetVO.setDatasetStructure(DatasetDef.DatasetStructure.SINGLE);
            datasetVO.setDataSetType(BusinessLogicConstant.DatasetType.INSTANCE);

            DatasetDefs datasetDefs = modelCore.getDatasetDefs();
            DatasetDef datasetDef = datasetDefs.addDatasetDef(datasetVO);

            if (datasetDef != null) {
                PropertyTypeDefs propertyTypeDefs = modelCore.getPropertyTypeDefs();
                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    PropertyTypeVO typeVO = new PropertyTypeVO();
                    // typeVO.setPropertyTypeName(entry.getKey());
                    log.info("structure: key={}, value={}", entry.getKey(), entry.getValue());
                    if (StringUtils.isBlank(
                            PinyinUtils.getPinYinWithoutSpecialChar(entry.getKey())) || StringUtils.isBlank(
                            entry.getKey()) || StringUtils.isBlank(entry.getValue())) {
                        continue;
                    }
                    typeVO.setPropertyTypeName(PinyinUtils.getPinYinWithoutSpecialChar(entry.getKey()));
                    typeVO.setPropertyTypeDesc(entry.getKey());
                    typeVO.setPropertyFieldDataClassify(entry.getValue());
                    log.info("add property type: {}", new Gson().toJson(typeVO));
                    PropertyTypeDef propertyTypeDef = propertyTypeDefs.addPropertyTypeDef(typeVO);

                    datasetDef.addPropertyTypeDef(propertyTypeDef);

                    PropertyMappingInputBean propertyMappingInputBean = new PropertyMappingInputBean();
                    propertyMappingInputBean.setPropertyType(entry.getValue());
                    // propertyMappingInputBean.setDesPropertyName(entry.getKey());
                    propertyMappingInputBean.setDesPropertyName(
                            PinyinUtils.getPinYinWithoutSpecialChar(entry.getKey()));
                    propertyMappingInputBean.setSrcPropertyName(entry.getKey());
                    propertyMappings.add(propertyMappingInputBean);
                }
                infoObjectDef.linkDatasetDef(datasetDef.getDatasetRID());
                generalInfo.put(CimConstants.FileImportProperties.OBJECT_TYPE_ID, objectTypeId);
                generalInfo.put(CimConstants.FileImportProperties.DATA_SET_ID, datasetDef.getDatasetRID());
                generalInfo.put(CimConstants.FileImportProperties.DATA_SET_NAME, dataSetName);
                flag = true;
            } else {
                log.error("add data set definition failed");
            }
        } else {
            log.error("object type of {} not found", objectTypeId);
        }

        return flag;
    }

    /**
     * 新增属性集和属性
     *
     * @param propertyMappings
     * @param objectMapping
     * @param dsMappingBean
     * @param generalInfo
     * @param modelCore
     * @param fileStructBean
     * @return
     * @throws DataServiceModelRuntimeException
     */
    private Boolean addDataSetMapping(List<PropertyMappingInputBean> propertyMappings,
                                      ObjectMappingInputBean objectMapping, DataSetMappingInputBean dsMappingBean,
                                      Map<String, Object> generalInfo, CIMModelCore modelCore,
                                      FileStructBean fileStructBean) throws DataServiceModelRuntimeException {//NOSONAR
        Boolean flag = false;
        String objectTypeId = objectMapping.getObjectTypeId();
        // DataSetMappingInputBean dsMappingBean = dataSetMappingInputBeans.get(0);
        if (StringUtils.isBlank(objectTypeId)) {
            log.error("object type id is null, obejctTypeId={}", objectMapping.getObjectTypeId());
            return flag;
        }

        if (StringUtils.isBlank(dsMappingBean.getName())) {
            log.error("data set mapping, data set name is empty");
            return flag;
        }

        Map<String, String> properties = fileStructBean.getStruct();
        if (properties == null || properties.size() < 1) {
            log.error("no property struct is get from file");
            return flag;
        }

        InfoObjectDefs infoObjectDefs = modelCore.getInfoObjectDefs();
        InfoObjectDef infoObjectDef = infoObjectDefs.getInfoObjectDef(objectTypeId);

        if (infoObjectDef != null) {
            //新增属性集定义
            DatasetVO datasetVO = new DatasetVO();
            datasetVO.setDatasetName(dsMappingBean.getName());
            datasetVO.setDatasetDesc(dsMappingBean.getName());
            datasetVO.setDatasetClassify("通用属性集");
            datasetVO.setDatasetStructure(DatasetDef.DatasetStructure.SINGLE);
            datasetVO.setDataSetType(BusinessLogicConstant.DatasetType.INSTANCE);

            DatasetDefs datasetDefs = modelCore.getDatasetDefs();
            DatasetDef datasetDef = datasetDefs.addDatasetDef(datasetVO);

            if (datasetDef != null) {
                PropertyTypeDefs propertyTypeDefs = modelCore.getPropertyTypeDefs();
                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    PropertyTypeVO typeVO = new PropertyTypeVO();
                    // typeVO.setPropertyTypeName(entry.getKey());
                    log.error("structure: key={}, value={}", entry.getKey(), entry.getValue());
                    if (StringUtils.isBlank(
                            PinyinUtils.getPinYinWithoutSpecialChar(entry.getKey())) || StringUtils.isBlank(
                            entry.getKey()) || StringUtils.isBlank(entry.getValue())) {
                        continue;
                    }
                    typeVO.setPropertyTypeName(PinyinUtils.getPinYinWithoutSpecialChar(entry.getKey()));
                    typeVO.setPropertyTypeDesc(entry.getKey());
                    typeVO.setPropertyFieldDataClassify(entry.getValue());
                    log.info("==== dataSet type vo: {}", new Gson().toJson(typeVO));
                    PropertyTypeDef propertyTypeDef = propertyTypeDefs.addPropertyTypeDef(typeVO);

                    datasetDef.addPropertyTypeDef(propertyTypeDef);

                    PropertyMappingInputBean propertyMappingInputBean = new PropertyMappingInputBean();
                    propertyMappingInputBean.setPropertyType(entry.getValue());
                    // propertyMappingInputBean.setDesPropertyName(entry.getKey());
                    propertyMappingInputBean.setDesPropertyName(
                            PinyinUtils.getPinYinWithoutSpecialChar(entry.getKey()));
                    propertyMappingInputBean.setSrcPropertyName(entry.getKey());
                    propertyMappings.add(propertyMappingInputBean);
                }
                infoObjectDef.linkDatasetDef(datasetDef.getDatasetRID());
                generalInfo.put(CimConstants.FileImportProperties.OBJECT_TYPE_ID, objectTypeId);
                generalInfo.put(CimConstants.FileImportProperties.DATA_SET_ID, datasetDef.getDatasetRID());
                generalInfo.put(CimConstants.FileImportProperties.DATA_SET_NAME, dsMappingBean.getName());
                flag = true;
            } else {
                log.error("add data set definition failed");
            }
        } else {
            log.error("object type of {} not found", objectTypeId);
        }

        return flag;
    }

    public List<FileStructBean> getStruct(String bucket, String fileName, FileImportTypeEnum fileImportTypeEnum) {
        switch (fileImportTypeEnum) {
            case SHP:
                try {
                    return getSpacialFileStruct(bucket, fileName);
                } catch (MinioClientException e) {
                    log.error("get shp file stucture failed", e);
                }
                break;
            case XLS:
                try {
                    return getExcelFileStruct(bucket, fileName);
                } catch (IOException e) {
                    log.error("get excel file stucture failed", e);
                }
                break;
            case XLSX:
                try {
                    return getExcelFileStruct(bucket, fileName);
                } catch (IOException e) {
                    log.error("get excel file stucture failed", e);
                }
                break;
            default:
                log.error("not support import file type [{}]currently", fileImportTypeEnum);
        }
        return null;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static void setGetStructUrl(String getStructUrl) {
        FileDataImportService.getStructUrl = getStructUrl;
    }

    public static void setGetRecordUrl(String getRecordUrl) {
        FileDataImportService.getRecordUrl = getRecordUrl;
    }
}
