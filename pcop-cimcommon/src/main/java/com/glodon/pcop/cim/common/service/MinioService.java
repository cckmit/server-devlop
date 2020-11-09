package com.glodon.pcop.cim.common.service;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuanjk
 */
//@Component
public class MinioService {
    private static Logger log = LoggerFactory.getLogger(MinioService.class);

    private String bucket;

    private MinioClient minioClient = null;

    public MinioService() {
        super();
    }

    public MinioService(MinioClient minioClient, String bucket) {
        super();
        this.bucket = bucket;
        this.minioClient = minioClient;
    }

    /**
     * 上传本地文件到minio
     *
     * @param objectName 文件对象名称
     * @param filePath   本地文件存放路径
     */
    public void fileUpload(String objectName, String filePath) {
        try {
            minioClient.statObject(bucket, objectName);
        } catch (Exception e) {
            log.error("bucket={}, objectName={}不存在", bucket, objectName);
            // e.printStackTrace();
        }

        try {
            minioClient.putObject(bucket, objectName, filePath);
        } catch (Exception e) {
            log.error("上传文件失败：{}", filePath);
            e.printStackTrace();
        }
    }

    /**
     * minio文件下载
     *
     * @param objectName 文件对象名称
     * @param filePath   下载文件存放路径
     */
    @SuppressWarnings("Duplicates")
    public void fileDownload(String objectName, String filePath) {
        ObjectStat objectStat;
        try {
            objectStat = minioClient.statObject(bucket, objectName);
            minioClient.getObject(bucket, objectName, filePath);
        } catch (Exception e) {
            log.error("bucket={}, objectName={} 不存在，文件下载失败", bucket, objectName);
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String fileName = "relationship_source_file-20181107023347.xlsx";
        String filePath = "G:\\tmp\\";
        MinioService minioService;
        try {
            minioService = new MinioService(new MinioClient("http://10.129.57.108:9000/", "pcop", "pcoppcop"), "pcop-cim");
            minioService.fileDownload(fileName, filePath + fileName);
        } catch (InvalidEndpointException | InvalidPortException e) {
            e.printStackTrace();
        }
    }

}
