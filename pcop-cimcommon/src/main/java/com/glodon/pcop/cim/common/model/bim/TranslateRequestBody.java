package com.glodon.pcop.cim.common.model.bim;

import com.alibaba.fastjson.JSONObject;
import com.bimface.sdk.bean.request.translate.TranslateRequest;
import com.bimface.sdk.bean.request.translate.TranslateSource;

public class TranslateRequestBody extends TranslateRequest {
    private JSONObject config;

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public static void main(String[] args) {
        String callback = "http:\\pcop.glodon.com";
        Long fileId = 123321L;

        TranslateRequestBody translateRequestBody = new TranslateRequestBody();
        TranslateSource translateSource = new TranslateSource();
        translateSource.setFileId(fileId);
        translateSource.setCompressed(false);
        translateRequestBody.setSource(translateSource);
        translateRequestBody.setCallback(callback);
        translateRequestBody.setPriority(2);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("texture", true);
        translateRequestBody.setConfig(jsonObject);

        System.out.println("request body 1: " + JSONObject.toJSONString(translateRequestBody));


        TranslateRequest fileTranslateRequest = new TranslateRequest();
        fileTranslateRequest.setCallback(callback);
        TranslateSource source = new TranslateSource();
        source.setFileId(fileId);
        fileTranslateRequest.setSource(source);
        System.out.println("request body 2: " + JSONObject.toJSONString(fileTranslateRequest));
    }

}
