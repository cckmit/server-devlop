package com.glodon.pcop.spacialimportsvc.schedule;

import com.bimface.sdk.BimfaceClient;
import com.bimface.sdk.config.Endpoint;
import com.glodon.pcop.cim.common.model.bim.BimFileUploadTranslateBean;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore
public class BimfaceStatusTest {

    private static MinioClient minioClient;
    private static BimfaceClient bimfaceClient;

    private static BimfaceStatus bimfaceStatus;

    @BeforeClass
    public static void init() throws InvalidPortException, InvalidEndpointException {
        String minioUrl = "http://10.129.57.108:9000/";
        String userName = "pcop";
        String pwd = "gldcim.123";
        minioClient = new MinioClient(minioUrl, userName, pwd);
        BimfaceStatus.setMinioClient(minioClient);

        String appKey = "NNs0cDVULMeZOITxuWFacaghYCIZysAF";
        String appSecret = "d2TpUdkQCkGOiU5nEG1GCpthAo4QDts3";
        String apiHost = "https://api.bimface.com";
        String fileHost = "https://file.bimface.com";
        bimfaceClient = new BimfaceClient(appKey, appSecret, new Endpoint(apiHost, fileHost), null);
        BimfaceStatus.setBimfaceClient(bimfaceClient);

        bimfaceStatus = new BimfaceStatus();
    }

    @Test
    public void bimfaceTranslateStatus() {
    }

    @Test
    public void uploadAndTranslate() {
        String bucket = "cimbucket";
        String fileName = "410569ea-98c7-49c7-ac0e-7c32756c7699__YF-06-0310-八标段轻量化.rvt";
        String srcFileName = "YF-06-0310-八标段轻量化.rvt";
        String objId = "aaa";
        String tenantId = "3";
        BimFileUploadTranslateBean fileUploadTranslateBean = new BimFileUploadTranslateBean(bucket, fileName, objId,
                srcFileName, tenantId);
        bimfaceStatus.uploadAndTranslate(fileUploadTranslateBean);
    }

}