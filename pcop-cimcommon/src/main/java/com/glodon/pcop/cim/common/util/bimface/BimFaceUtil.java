package com.glodon.pcop.cim.common.util.bimface;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bimface.sdk.BimfaceClient;
import com.bimface.sdk.bean.GeneralResponse;
import com.bimface.sdk.bean.request.translate.TranslateSource;
import com.bimface.sdk.bean.response.FileBean;
import com.bimface.sdk.config.Endpoint;
import com.bimface.sdk.exception.BimfaceException;
import com.bimface.sdk.http.HttpHeaders;
import com.bimface.sdk.http.HttpUtils;
import com.bimface.sdk.service.AccessTokenService;
import com.bimface.sdk.service.TranslateService;
import com.bimface.sdk.utils.AssertUtils;
import com.glodon.pcop.cim.common.model.bim.TranslateRequestBody;
import com.glodon.pcop.cim.common.model.bim.TranslateResponseBean;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BimFaceUtil {
    private static Logger log = LoggerFactory.getLogger(BimFaceUtil.class);

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static String callBackUrl = ""; //转换完成后回调地址

    public static BimfaceClient getBimfaceClient(String appKey, String appSecret) {
        return new BimfaceClient(appKey, appSecret);
    }

    public static BimfaceClient getBimfaceClient(String appKey, String appSecret, String apiHost, String fileHost) {
        Endpoint endpoint = new Endpoint(apiHost, fileHost);
        return new BimfaceClient(appKey, appSecret, endpoint, null);
    }

    public static FileBean bimUpload(BimfaceClient bimfaceClient, String fileName, String filePath) {
        log.info("start to upload: {}", dateFormat.format(new Date()));
        FileBean fileBean = null;
        File file = new File(filePath);
        if (file.exists()) {
            // FileInputStream inputStream = null;
            try (FileInputStream inputStream = new FileInputStream(file)) {
                // inputStream = new FileInputStream(file);
                fileBean = bimfaceClient.upload(fileName, file.length(), inputStream);
                log.info("respond file bean: {}", JSON.toJSONString(fileBean));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (BimfaceException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.error("file not exists: {}", filePath);
        }
        log.info("finish to upload: {}", dateFormat.format(new Date()));

        return fileBean;
    }

    public static FileBean bimUpload(BimfaceClient bimfaceClient, String fileName, Long length,
                                     InputStream inputStream) {
        log.info("start to upload: {}", dateFormat.format(new Date()));
        FileBean fileBean = null;
        try {
            fileBean = bimfaceClient.upload(fileName, length, inputStream);
            log.info("respond file bean: {}", JSON.toJSONString(fileBean));
        } catch (BimfaceException e) {
            e.printStackTrace();
        }
        log.info("finish to upload: {}", dateFormat.format(new Date()));

        return fileBean;
    }

    public static TranslateResponseBean bimTranslate(BimfaceClient bimfaceClient, Long fileId) {
        AssertUtils.assertParameterNotNull(fileId, "fileId");
        TranslateResponseBean translateBean = null;
        try {
            TranslateRequestBody requestBody = new TranslateRequestBody();
            requestBody.setCallback(callBackUrl);
            requestBody.setPriority(2);

            JSONObject jsonObject = new JSONObject();
            // jsonObject.put("texture", true);
            // jsonObject.put("visilbiltyByVoxel", true);
            requestBody.setConfig(jsonObject);

            TranslateSource translateSource = new TranslateSource();
            translateSource.setFileId(fileId);
            translateSource.setCompressed(false);
            requestBody.setSource(translateSource);

            // translateBean = bimfaceClient.translate(requestBody);
            translateBean = bimTranslate(bimfaceClient, requestBody);
            log.info("translate response bean: {}", JSON.toJSONString(translateBean));
        } catch (BimfaceException e) {
            log.error("invoke bim translation failed");
            e.printStackTrace();
        }

        return translateBean;
    }

    public static TranslateResponseBean bimTranslate(BimfaceClient bimfaceClient, Long fileId, JSONObject jsonObject) {
        AssertUtils.assertParameterNotNull(fileId, "fileId");
        TranslateResponseBean translateBean = null;
        try {
            TranslateRequestBody requestBody = new TranslateRequestBody();
            requestBody.setCallback(callBackUrl);
            requestBody.setPriority(2);

            // JSONObject jsonObject = new JSONObject();
            // jsonObject.put("texture", true);
            // jsonObject.put("visilbiltyByVoxel", true);
            requestBody.setConfig(jsonObject);
            log.info("bimface translate configuration: {}", jsonObject);

            TranslateSource translateSource = new TranslateSource();
            translateSource.setFileId(fileId);
            translateSource.setCompressed(false);
            requestBody.setSource(translateSource);

            // translateBean = bimfaceClient.translate(requestBody);
            translateBean = bimTranslate(bimfaceClient, requestBody);
            log.info("translate response bean: {}", JSON.toJSONString(translateBean));
        } catch (BimfaceException e) {
            log.error("invoke bim translation failed");
            e.printStackTrace();
        }

        return translateBean;
    }

    public static String getFileViewToken(BimfaceClient bimfaceClient, Long fileId) throws BimfaceException {
        String viewToken;
        viewToken = bimfaceClient.getViewTokenByFileId(fileId);
        log.info("view token: {}", viewToken);
        return viewToken;
    }

    public static String getAccessToken(BimfaceClient bimfaceClient) throws BimfaceException {
        AccessTokenService accessTokenService = bimfaceClient.getAccessTokenService();
        log.info("view token: {}", accessTokenService);
        return accessTokenService + "";
    }

    private static TranslateResponseBean bimTranslate(BimfaceClient bimfaceClient, TranslateRequestBody request) throws BimfaceException {
        AssertUtils.assertParameterNotNull(request, "TranslateRequest");
        AssertUtils.assertParameterNotNull(request.getSource(), "source");
        AssertUtils.assertParameterNotNull(request.getSource().getFileId(), "fileId");

        TranslateService translateService = bimfaceClient.getTranslateService();

        HttpHeaders headers = new HttpHeaders();
        headers.addOAuth2Header(translateService.getAccessToken());
        Response response = translateService.getServiceClient().put(translateService.getApiHost() + "/translate",
                request, headers);
        return HttpUtils.response(response, new TypeReference<GeneralResponse<TranslateResponseBean>>() {
        });
    }


}
