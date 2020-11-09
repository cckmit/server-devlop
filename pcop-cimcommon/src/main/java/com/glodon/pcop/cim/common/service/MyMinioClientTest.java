package com.glodon.pcop.cim.common.service;

import com.alibaba.fastjson.JSON;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Ignore
public class MyMinioClientTest {

    private static MinioClient minioClient = null;

    @Before
    public void setUp() throws Exception {
        String url = "http://10.129.57.108:9000/";
        String userName = "pcop";
        String pwd = "gldcim.123";

        minioClient = new MinioClient(url, userName, pwd);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void uploader() throws FileNotFoundException {
        String bucket = "yuanjk-test";
        String objectName = "tt/dd1/test.html";

        String fileName = "G:\\tmp\\Untitled-1.html";

        MyMinioClient.uploader(minioClient, new FileInputStream(fileName), bucket, objectName, false);

    }

    @Test
    public void downloader() {
    }

    @Test
    public void statObject() {
        String bucket = "cimbucket";
        String fileName = "tt.txt";
        // ObjectStat objectStat = minioClient.statObject(bucket, fileName);
        // System.out.println("stat object: " + JSON.toJSONString(objectStat));
        ObjectStat objectStat = MyMinioClient.statObject(minioClient, bucket, fileName);
        System.out.println("stat object name: " + objectStat.name());
        System.out.println("stat object: " + JSON.toJSONString(objectStat));
    }
}