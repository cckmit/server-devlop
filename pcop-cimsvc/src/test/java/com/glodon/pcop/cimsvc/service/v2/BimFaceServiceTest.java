package com.glodon.pcop.cimsvc.service.v2;

import com.bimface.sdk.BimfaceClient;
import com.bimface.sdk.exception.BimfaceException;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.common.util.ZipFileUtil;
import com.glodon.pcop.cim.common.util.bimface.BimFaceUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.google.gson.Gson;
import io.minio.MinioClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Ignore
public class BimFaceServiceTest {
    private static Logger log = LoggerFactory.getLogger(BimFaceServiceTest.class);

    private static MinioClient minioClient;
    private static BimfaceClient bimfaceClient;
    private static ExecutorService executorService;

    private static CimDataSpace cds;

    @Before
    public void setUp() throws Exception {
        minioClient = new MinioClient("http://10.129.57.108:9000/", "pcop", "gldcim.123");
        bimfaceClient = new BimfaceClient("NNs0cDVULMeZOITxuWFacaghYCIZysAF", "d2TpUdkQCkGOiU5nEG1GCpthAo4QDts3");
        executorService = executorService = Executors.newSingleThreadExecutor();
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
    }

    @Test
    public void getViewToken() throws BimfaceException {
        String fileName = "Convention-Center_AR_B01-y0126.rvt";
        String bucket = "bimface";
        String dbName = "pcopcim";
        String ihFactId = "#1563:0";
        String tenantId = "2";
        Long fileId = 1531896286020288L;
        log.info("==start get bim face view token...");
        try {
            BimFaceService bimFaceService = new BimFaceService();
            String viewToken = bimFaceService.getViewTokenByFileId(bimfaceClient, tenantId, fileId);
            log.info("view toke of {}: {}", fileId, viewToken);
            log.info("==finish get bim face view token...");
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void getViewTokenByIntegretedId() {
        Long integratedId = 1587089728704256L;
        try {
            System.out.println("===" + (new Gson().toJson(bimfaceClient.getIntegrate(integratedId))));
        } catch (BimfaceException e) {
            e.printStackTrace();
            System.out.println("error");
        }

    }

    @Test
    public void dataBagExtract() throws IOException {
        String fileName = "906f9a27e262360420a3ad1e69572059";
        Path tmpFile = Files.createTempFile(fileName, ".zip");
        // System.out.println("tmp file path: " + tmpFile.toString());
        // System.out.println("tmp file name: " + tmpFile.getFileName());

        tmpFile = Paths.get("C:\\Users\\yuanjk\\AppData\\Local\\Temp" +
                "\\906f9a27e262360420a3ad1e69572059586802054432680980.zip");

        ZipFileUtil.unzip(tmpFile.toString(), tmpFile.getParent().toString());
    }

    @Test
    public void dataBagUpload() throws IOException {
        String zipFileName = "G:\\tmp\\906f9a27e262360420a3ad1e69572059586802054432680980.zip";

        FileSystem fs = FileSystems.newFileSystem(Paths.get(zipFileName), null);

        Files.walkFileTree(fs.getPath("/"), new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws FileNotFoundException {
                System.out.println("file full name: " + file);
                // String objName = file.toString().replaceFirst("/", "");
                try {
                    // System.out.println("file absulated name: " + fs.getPath(file.toString()));
                    minioClient.putObject("tmp", file.toString(), fs.provider().newInputStream(file),
                            URLConnection.guessContentTypeFromName(file.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return FileVisitResult.CONTINUE;
            }
        });

    }

    @Test
    public void unzipMinioFile() {
        String url = "http://10.129.57.108:7332/unzipbfp";
        String body = "{\"filepath\": \"pcop-bimpkgs/tt/9dbb380c04054fa4ed87a4cb3223db2d.zip\"}";
        // String body = "{\"filepath\": \"aaa.zip\"}";
        try {
            BimFaceService.unzipMinioFile(url, body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void uploadFile() {
        // String fileName = "YF-06-0310-八标段轻量化.rvt";
        // String filePath = "G:\\data\\bim\\YF-06-0310-八标段轻量化.rvt";
        String fileName = "大礼路道路模型-7-8标段&青礼路道路模型00.bmv";
        String filePath = "G:\\data\\bim\\大礼路道路模型-7-8标段&青礼路道路模型00.bmv";
        System.out.println("upload result: " + BimFaceUtil.bimUpload(bimfaceClient, fileName, filePath));
    }


}