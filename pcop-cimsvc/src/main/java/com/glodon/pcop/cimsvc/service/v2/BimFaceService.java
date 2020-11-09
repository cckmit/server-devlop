package com.glodon.pcop.cimsvc.service.v2;

import com.bimface.sdk.BimfaceClient;
import com.bimface.sdk.bean.request.integrate.IntegrateRequest;
import com.bimface.sdk.bean.request.integrate.IntegrateSource;
import com.bimface.sdk.bean.response.AccessTokenBean;
import com.bimface.sdk.bean.response.FileBean;
import com.bimface.sdk.bean.response.IntegrateBean;
import com.bimface.sdk.bean.response.TranslateBean;
import com.bimface.sdk.exception.BimfaceException;
import com.bimface.sdk.service.AccessTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.model.bim.BimFileUploadTranslateBean;
import com.glodon.pcop.cim.common.model.bim.BimfaceIntegrateInputBean;
import com.glodon.pcop.cim.common.model.bim.BimfaceIntegrateSourceBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.BaseFileInfoKeys;
import com.glodon.pcop.cim.common.util.bimface.BimFaceUtil;
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
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.InfoObjectDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.InfoObjectDefDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.BimfaceProcessingException;
import com.glodon.pcop.cimsvc.exception.BimfaceResponseErrorException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.v2.SingleInstancesQueryOutput;
import com.glodon.pcop.cimsvc.model.v2.SingleQueryOutput;
import com.glodon.pcop.cimsvc.service.MinioService;
import com.glodon.pcop.cimsvc.service.OrientDBCommonUtil;
import com.glodon.pcop.cimsvc.service.bim.BimOutputBean;
import com.glodon.pcop.cim.common.model.bim.TranslateResponseBean;
import com.glodon.pcop.cimsvc.service.kafka.SendMessageUtil;
import com.google.gson.Gson;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class BimFaceService {
    private static Logger log = LoggerFactory.getLogger(BimFaceService.class);

    @Value("${my.bimface.appKey}")
    private String appKey;

    @Value("${my.bimface.appSecret}")
    private String appSecret;

    @Value("${my.bimface.apiHost}")
    private String apiHost;

    @Value("${my.bimface.fileHost}")
    private String fileHost;

    @Value("${my.bimface.bimfacePkgsvc}")
    private String bimfacePkgsvc;

    @Autowired
    private SendMessageUtil sendMessageUtil;

    private static String viewToken = "viewToken";
    private static String viewTokenTime = "viewTokenTime";

    private static final String SUCCESS = "success";
    private static final String PROCESSING = "processing";
    private static final String UNKNOWN_BIMFACE_STATUS = "unknown bimface status: {}";

    private static ExecutorService executorService;

    @Autowired
    private MinioService minioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        executorService = Executors.newCachedThreadPool();//NOSONAR
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }

    public String getViewTokenByFileId(BimfaceClient bimfaceClient, String tenantId, Long fileId) throws
                                                                                                  BimfaceException {
        //NOSONAR
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        String availableViewToken = "";
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            modelCore.setCimDataSpace(cds);
            InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(BaseFileInfoKeys.BaseFileObjectTypeName);
            if (infoObjectDef != null) {
                ExploreParameters ep = new ExploreParameters();
                ep.setType(BaseFileInfoKeys.BaseFileObjectTypeName);
                FilteringItem filteringItem = new EqualFilteringItem(BaseFileInfoKeys.FILE_ID, fileId);
                ep.setDefaultFilteringItem(filteringItem);

                InformationExplorer ip = cds.getInformationExplorer();
                List<Fact> factList = ip.discoverInheritFacts(ep);
                if (factList != null && factList.size() > 0) {
                    //fileId is unique
                    GregorianCalendar mailCalendar = new GregorianCalendar();
                    mailCalendar.add(Calendar.HOUR_OF_DAY, -12);
                    Fact fact = factList.get(0);
                    if (fact.hasProperty(viewToken)) {
                        availableViewToken = fact.getProperty(viewToken).getPropertyValue().toString();
                        if (fact.hasProperty(viewTokenTime)) {
                            Date viewTokenDate = (Date) fact.getProperty(viewTokenTime).getPropertyValue();
                            if (mailCalendar.before(viewTokenDate)) {
                                fact.updateProperty(viewTokenTime, new Date());
                                return availableViewToken;
                            }
                        }
                    }
                    availableViewToken = BimFaceUtil.getFileViewToken(bimfaceClient, fileId);
                    if (fact.hasProperty(viewToken)) {
                        fact.updateProperty(viewToken, availableViewToken);
                    } else {
                        fact.addProperty(viewToken, availableViewToken);
                    }
                    if (fact.hasProperty(viewTokenTime)) {
                        fact.updateProperty(viewTokenTime, new Date());
                    } else {
                        fact.addProperty(viewTokenTime, new Date());
                    }
                } else {
                    log.error("inherit fact of factType={}, fileDataId={} not found",
                            BaseFileInfoKeys.BaseFileObjectTypeName, fileId);
                }
            } else {
                log.info("object type of {} not found", BaseFileInfoKeys.BaseFileObjectTypeName);
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
        return availableViewToken;
    }

    public void uploadTask(String fileName, String bucket, String dbName, String ihFactId) {
        try {
            MinioClient minioClient = minioService.getMinioClient();
            // BimfaceClient bimfaceClient = BimFaceUtil.getBimfaceClient(appKey, appSecret);
            BimfaceClient bimfaceClient = BimFaceUtil.getBimfaceClient(appKey, appSecret, apiHost, fileHost);
            CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            BimFaceUploadTask uploadTask = new BimFaceUploadTask(minioClient, bimfaceClient, fileName, bucket, cds,
                    ihFactId);
            executorService.submit(uploadTask);
        } catch (InvalidPortException e) {
            e.printStackTrace();
        } catch (InvalidEndpointException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件到bimface，并启动转换
     *
     * @param multipartFile
     * @return
     */
    public BimOutputBean uploadAndTranslate(MultipartFile multipartFile) {
        BimOutputBean outputBean = new BimOutputBean();
        try {
            BimfaceClient bimfaceClient = BimFaceUtil.getBimfaceClient(appKey, appSecret, apiHost, fileHost);
            String fileName = multipartFile.getOriginalFilename();
            if (fileName.contains(File.separator)) {
                fileName = fileName.substring(fileName.lastIndexOf(File.separator + 1));
            }
            FileBean fileBean = BimFaceUtil.bimUpload(bimfaceClient, fileName, multipartFile.getSize(),
                    multipartFile.getInputStream());
            outputBean.setFileId(fileBean.getFileId());
            outputBean.setName(fileBean.getName());
            if (fileBean != null) {
                TranslateResponseBean responseBean = BimFaceUtil.bimTranslate(bimfaceClient, fileBean.getFileId());
                if (responseBean != null) {
                    outputBean.setDatabagId(responseBean.getDatabagId());
                } else {
                    log.error("translate response is null");
                }
            } else {
                log.error("bim face file upload failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputBean;
    }

    /**
     * 获取bimface的viewtoken
     *
     * @param fileId
     * @return
     */
    public String getViewToken(Long fileId) throws BimfaceProcessingException {
        String availableViewToken = null;
        BimfaceClient bimfaceClient = BimFaceUtil.getBimfaceClient(appKey, appSecret, apiHost, fileHost);
        try {
            TranslateBean translateBean = bimfaceClient.getTranslate(fileId);
            if (translateBean.getStatus().equals(SUCCESS)) {
                availableViewToken = BimFaceUtil.getFileViewToken(bimfaceClient, fileId);
            } else if (translateBean.getStatus().equals(PROCESSING)) {
                throw new BimfaceProcessingException();
            } else {
                log.error(UNKNOWN_BIMFACE_STATUS, translateBean.getStatus());
            }
        } catch (BimfaceException e) {
            e.printStackTrace();
        }
        return availableViewToken;
    }

    public String getViewToken(BimfaceIdType idType, Long bimFaceId) throws BimfaceProcessingException {
        String availableViewToken = null;
        BimfaceClient bimfaceClient = BimFaceUtil.getBimfaceClient(appKey, appSecret, apiHost, fileHost);
        try {
            switch (idType) {
                case INTEGRATEDID:
                    IntegrateBean integrateBean = bimfaceClient.getIntegrate(bimFaceId);
                    if (integrateBean.getStatus().equals(SUCCESS)) {
                        availableViewToken = bimfaceClient.getViewTokenByIntegrateId(bimFaceId);
                    } else if (integrateBean.getStatus().equals(PROCESSING)) {
                        throw new BimfaceProcessingException();
                    } else {
                        log.error(UNKNOWN_BIMFACE_STATUS, integrateBean.getStatus());
                    }
                    break;
                default:
                    TranslateBean translateBean = bimfaceClient.getTranslate(bimFaceId);
                    if (translateBean.getStatus().equals(SUCCESS)) {
                        availableViewToken = BimFaceUtil.getFileViewToken(bimfaceClient, bimFaceId);
                    } else if (translateBean.getStatus().equals(PROCESSING)) {
                        throw new BimfaceProcessingException();
                    } else {
                        log.error(UNKNOWN_BIMFACE_STATUS, translateBean.getStatus());
                    }
                    break;
            }
        } catch (BimfaceException e) {
            e.printStackTrace();
        }
        return availableViewToken;
    }

    /**
     * bim离线数据包部署
     *
     * @param bucketName
     * @param bimFile
     */
    public String dataBagUpload(String filePath, String bucketName, MultipartFile bimFile) {
        String storePath = null;
        try {
            String bucket = "pcop-bimpkgs";
            if (StringUtils.isNotBlank(bucketName)) {
                bucket = bucketName;
            }
            // String storePath = filePath + '/' + bimFile.getOriginalFilename();
            storePath = bucket + '/' + filePath;
            log.debug("minio store path: {}", storePath);
            MinioClient minioClient = minioService.getMinioClient();
            minioClient.putObject(bucket, filePath, bimFile.getInputStream(),
                    URLConnection.guessContentTypeFromName(bimFile.getOriginalFilename()));
            Map<String, String> parms = new HashMap<>();
            parms.put("filepath", storePath);
            log.debug("bimface pkgsvc: {}", bimfacePkgsvc);
            log.debug("unzip parmeters: {}", objectMapper.valueToTree(parms));
            if (unzipMinioFile(bimfacePkgsvc, new Gson().toJson(parms))) {
                storePath = storePath.substring(0, storePath.lastIndexOf('.'));
            } else {
                storePath = "";
            }
        } catch (Exception e) {
            log.error("upload databag to minio failed", e);
            e.printStackTrace();
        }
        return storePath;
    }


    public static boolean unzipMinioFile(String url, String body) throws IOException {
        boolean flag = false;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(url);
            StringEntity reqEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
            reqEntity.setChunked(true);
            httppost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                int status = response.getStatusLine().getStatusCode();
                if ((200 <= status) && (status < 300)) {
                    flag = true;
                } else {
                    log.error("status line: {}", response.getStatusLine().toString());
                    log.error("content: ", EntityUtils.toString(response.getEntity()));
                }
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        return flag;
    }

    /**
     * 获取BIMFace构件信息
     *
     * @param objectTypeId 图层名: BIMItem
     * @param instance     fileId_elementId
     * @return 返回instance结构中增加字段: bimprops 为json字符串
     */
    public SingleQueryOutput queryAttributes(String objectTypeId, String instance, String tenantId) throws
                                                                                                    DataServiceModelRuntimeException,
                                                                                                    EntityNotFoundException,
                                                                                                    BimfaceException {
        SingleQueryOutput resultQueryOutput = new SingleQueryOutput();
        InfoObjectDef infoObjectDef = OrientDBCommonUtil.getInfoObjectDef(tenantId, objectTypeId);
        //获取bimface客户端
        BimfaceClient bimfaceClient = BimFaceUtil.getBimfaceClient(appKey, appSecret);
        //获取access服务
        AccessTokenService accessTokenService = bimfaceClient.getAccessTokenService();
        //获取Access token
        AccessTokenBean accessTokenBean = accessTokenService.get();
        String accessToken = accessTokenBean.getToken();
        String[] strArr = StringUtils.split(instance, "_");
        String fileId = strArr[0];
        String elementId = strArr[1];
        String url = "https://api.bimface.com/data/v2/files/{fileId}/elements/{elementId}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("", accessToken);
        HttpEntity entity = new HttpEntity(headers);
        //访问bimface 获取构件属性 接口  返回结果
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class, fileId, elementId);
        infoObjectDef.addOrUpdateDefinitionProperty("bimprops", response);
        ExploreParameters parameters = new ExploreParameters();
        InfoObjectRetrieveResult result = infoObjectDef.getObjects(parameters);
        List<InfoObject> infoObjectList = result.getInfoObjects();
        log.info("Query instance count {}", infoObjectList.size());
        List<SingleInstancesQueryOutput> queryResults = new ArrayList<>();
        Long totalCount = 0L;
        if (infoObjectList != null && infoObjectList.size() > 0) {
            for (InfoObject infoObject : infoObjectList) {
                SingleInstancesQueryOutput queryOutput = new SingleInstancesQueryOutput();
                queryOutput.setInstanceRid(infoObject.getObjectInstanceRID());
                Map<String, Map<String, Object>> tmpMap = infoObject.getObjectPropertiesByDatasets();
                if (tmpMap == null) {
                    tmpMap = new HashMap<>();
                    tmpMap.put(BusinessLogicConstant.COMMON_DATASET_NAME_PROPERTY_NAME, infoObject.getInfo());
                    queryOutput.setInstanceData(tmpMap);
                    queryResults.add(queryOutput);
                }
                totalCount = ((InfoObjectDefDSImpl) infoObjectDef).countObjects(parameters);
            }
            resultQueryOutput.setTotalCount(totalCount);
            resultQueryOutput.setInstances(queryResults);
        }
        return resultQueryOutput;
    }

    public IntegrateBean fileIntegrate(BimfaceIntegrateInputBean integrateInput) throws BimfaceResponseErrorException {
        IntegrateBean responseBean = null;
        try {
            IntegrateRequest request = new IntegrateRequest();
            request.setName(integrateInput.getName());
            List<IntegrateSource> sources = new ArrayList<>();
            request.setSources(sources);
            for (BimfaceIntegrateSourceBean sourceBean : integrateInput.getSources()) {
                IntegrateSource file = new IntegrateSource();
                file.setFileId(sourceBean.getFileId());
                file.setFloor(sourceBean.getFloor());
                file.setFloorSort(sourceBean.getFloorSort());
                file.setSpecialty(sourceBean.getSpecialty());
                file.setSpecialtySort(sourceBean.getSpecialtySort());
                sources.add(file);
            }

            BimfaceClient bimfaceClient = BimFaceUtil.getBimfaceClient(appKey, appSecret, apiHost, fileHost);
            responseBean = bimfaceClient.integrate(request);
        } catch (BimfaceException e) {
            log.error("bimface integrate failed", e);
            throw new BimfaceResponseErrorException();
        }
        return responseBean;
    }

    public boolean sendMessageUploadAndTranslate(BimFileUploadTranslateBean translateBean) {
        sendMessageUtil.sendMessage(translateBean);
        return true;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getApiHost() {
        return apiHost;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public String getFileHost() {
        return fileHost;
    }

    public void setFileHost(String fileHost) {
        this.fileHost = fileHost;
    }

    public enum BimfaceIdType {
        FILEID, INTEGRATEDID;
    }

}
