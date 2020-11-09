package com.glodon.pcop.cimsvc.service.v2;

import com.bimface.sdk.BimfaceClient;
import com.bimface.sdk.config.Endpoint;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import io.minio.MinioClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Ignore
public class BimFaceUploadTaskTest {
    private static Logger log = LoggerFactory.getLogger(BimFaceUploadTaskTest.class);

    private static MinioClient minioClient;
    private static BimfaceClient bimfaceClient;
    private static ExecutorService executorService;

    private static CimDataSpace cds;

    @Before
    public void setUp() throws Exception {
        minioClient = new MinioClient("http://10.129.57.108:9000/", "pcop", "gldcim.123");
        //bimface
        bimfaceClient = new BimfaceClient("NNs0cDVULMeZOITxuWFacaghYCIZysAF", "d2TpUdkQCkGOiU5nEG1GCpthAo4QDts3");
        //pcmp
        String apiHost = "http://pcmp.glodon.com/api";
        String fileHost = "http://pcmp.glodon.com/files";
        Endpoint endpoint = new Endpoint(apiHost, fileHost);
        // bimfaceClient = new BimfaceClient("MshEi3cLeM8rs8Jg51K4wTpjTQBZw1S6", "RmGLg83nJAAw86jznzZogf7kCV7TDyUo", endpoint, null);
        executorService = executorService = Executors.newSingleThreadExecutor();
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
    }

    @Test
    public void bimFaceUploadAndTranslate() {
        // String fileName = "Convention-Center_AR_B01-y0126.rvt";
        String fileName = "02197474-9a85-4dd6-8506-d2d0503ea2c4__大礼路道路模型-7-8标段&青礼路道路模型00.bmv";
        String bucket = "cimbucket";
        String dbName = "pcopcim";
        String ihFactId = "#1563:0";
        log.info("==start bim face task...");
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(dbName);
            BimFaceUploadTask uploadTask = new BimFaceUploadTask(minioClient, bimfaceClient, fileName, bucket, cds, ihFactId);
            // executorService.submit(uploadTask);
            uploadTask.uploadAndTranslate();
            log.info("==finish bim face task");
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }


    @After
    public void tearDown() throws Exception {
    }
}