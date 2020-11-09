package com.glodon.pcop.cimsvc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.glodon.pcop.cim.common.constant.FileImportTypeEnum;
import com.glodon.pcop.cim.common.util.ExcelFileReader;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@Ignore
public class MinioServiceTest {

    private static MinioClient minioClient = null;

    @BeforeClass
    public static void init() throws InvalidPortException, InvalidEndpointException {
        String url = "http://10.129.57.108:9000/";
        String userName = "pcop";
        String pwd = "gldcim.123";

        minioClient = new MinioClient(url, userName, pwd);
    }

    @Test
    public void getShareUrl() {
        try {
            minioClient.makeBucket("yuanjk/test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fileUpload() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            InsufficientDataException, InvalidArgumentException, InternalException, NoResponseException,
            InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        String bucketName = "tmp";
        String objectName = "906f9a27e262360420a3ad1e69572059/jssdk/Bimface@3.4.47/zh_CN.js";
        // String objectName = "index.html";
        // String fileName = "C:\\Users\\yuanjk\\Downloads\\9dbb380c04054fa4ed87a4cb3223db2d
        // \\aeb47a8f6e28ecf96b1251edcb82f383\\data\\spaces.json";
        String fileName = "G:\\tmp\\906f9a27e262360420a3ad1e69572059\\jssdk\\Bimface@3.4.47\\zh_CN.js";

        minioClient.putObject(bucketName, objectName, fileName);
    }

    @Test
    public void createBucket() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            InsufficientDataException, ErrorResponseException, NoResponseException, InvalidBucketNameException,
            XmlPullParserException, InternalException, RegionConflictException {
        minioClient.makeBucket("yuanjk/test");
    }

    @Test
    public void objectStatInfo() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException,
            XmlPullParserException, ErrorResponseException {
        String bucketName = "temp";
        String objectName = "2018年财务报销指南.pdf";

        ObjectStat objectStat = minioClient.statObject(bucketName, objectName);
        System.out.println("minio object stats info: " + objectStat);
    }

    @Test
    public void listObjects() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            XmlPullParserException {
        String bucketName = "test";
        try {
            // Check whether 'mybucket' exists or not.
            boolean found = minioClient.bucketExists(bucketName);
            if (found) {
                // List objects from 'my-bucketname'
                Iterable<Result<Item>> myObjects = minioClient.listObjects(bucketName);
                for (Result<Item> result : myObjects) {
                    Item item = result.get();
                    System.out.println(item.lastModified() + ", " + item.size() + ", " + item.objectName());
                }
            } else {
                System.out.println("mybucket does not exist");
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }

    @Test
    public void firstRowAsHeader() throws IOException {
        String fileName = "D:\\tmp\\导入文件标准.xlsx";
        InputStream inputStream = new FileInputStream(fileName);

        int idx = 1;
        List<Map<Integer, String>> sheetContent = ExcelFileReader.oneSheetContent(inputStream, idx, 0, -1);
        for (Map<Integer, String> rd : sheetContent) {
            System.out.println("column number=" + rd.size() + ", content= " + JSON.toJSONString(rd,
                    SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty));
        }

        MinioService minioService = new MinioService();

        System.out.println("result: " + JSON.toJSONString(minioService.firstRowAsHeader(sheetContent)));

    }

    @Test
    public void zipFileStructure() {
        MinioService minioService = new MinioService();
        minioService.setMinioClient(minioClient);

        String bucket = "cimbucket";
        String fileName = "487da588-17b7-464f-aad8-1d518068a8ff__max结构测试.zip";

        System.out.println("zip file structure: " + JSON.toJSONString(minioService.zipFileStructure(bucket, fileName,
                FileImportTypeEnum.MAX)));

    }

    @Test
    public void miniTypeFromExtension() throws IOException {
        String[] fileName = {"PLAN_KG_GHYD.zip", "xinhancheng_jichang.xlsx", "test.txt", "10" +
                "-3_Terracotta_Server_Administration.pdf", "临空经济区0614最终2 - 副本.pptx", "server.bat", "Employee.xls"};
        for (String fn : fileName) {
            System.out.println("fileName=" + fn + ", content type=" + Files.probeContentType(Paths.get(fn)));
        }
    }


    @Test
    public void isExistsTest() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
            InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidArgumentException {
        String bucket = "tmp";
        String fileName = "abc";

        // ObjectStat objectStat = minioClient.statObject(bucket, fileName);
        InputStream inputStream = minioClient.getObject(bucket, fileName);

    }

}