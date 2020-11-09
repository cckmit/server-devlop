package com.glodon.pcop.cim.common.service;

import com.glodon.pcop.cim.common.model.FileUploadStatusBean;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.NoResponseException;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 自定义minio客户端.
 *
 * @author yuanjk
 */
public class MyMinioClient {
    private static Logger log = LoggerFactory.getLogger(MyMinioClient.class);

    /**
     * 上传文件到Minio.
     *
     * @param minioClient
     * @param inputStream
     * @param bucket
     * @param objectName
     * @param isOverwrite 是否可覆盖
     * @return
     */
    public static FileUploadStatusBean uploader(MinioClient minioClient, InputStream inputStream, String bucket,
                                                String objectName, boolean isOverwrite) {

        FileUploadStatusBean fileUploadStatusBean = new FileUploadStatusBean();
        fileUploadStatusBean.setObjectName(objectName);
        fileUploadStatusBean.setBucket(bucket);
        try {
            boolean bucketFound = minioClient.bucketExists(bucket);
            if (!bucketFound) {
                minioClient.makeBucket(bucket);
                log.info("Make new bucket: {}", bucket);
            }
            if (isOverwrite) {
                fileUploadStatusBean.setOverwrite(true);
            } else {
                boolean objectFound = objectExists(minioClient, bucket, objectName);
                if (objectFound) {
                    fileUploadStatusBean.setOverwrite(false);
                    fileUploadStatusBean.setSuccess(false);
                    fileUploadStatusBean.setMsg("bucket=" + bucket + ", objectName=" + objectName + " is already exists!");
                    return fileUploadStatusBean;
                }
            }

            String contentType = URLConnection.guessContentTypeFromName(objectName);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            minioClient.putObject(bucket, objectName, inputStream, contentType);
            fileUploadStatusBean.setSuccess(true);
            fileUploadStatusBean.setMsg("Success");
        } catch (Exception e) {
            fileUploadStatusBean.setSuccess(false);
            e.printStackTrace();
            fileUploadStatusBean.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return fileUploadStatusBean;
    }

    /**
     * 文件是否已存在.
     *
     * @param minioClient
     * @param bucket
     * @param objectName
     * @return
     * @throws XmlPullParserException
     * @throws InvalidBucketNameException
     * @throws NoSuchAlgorithmException
     * @throws InsufficientDataException
     * @throws IOException
     * @throws InvalidKeyException
     * @throws NoResponseException
     * @throws ErrorResponseException
     * @throws InternalException
     */
    private static boolean objectExists(MinioClient minioClient, String bucket, String objectName)
            throws XmlPullParserException, InvalidBucketNameException, NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, NoResponseException, ErrorResponseException, InternalException {
        Iterable<Result<Item>> myObjects = minioClient.listObjects(bucket);
        for (Result<Item> result : myObjects) {
//            log.info("object name: {}", result.get().objectName());
            Item item = result.get();
            if (item.objectName().equals(objectName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Minio文件下载
     *
     * @param minioClient
     * @param bucket
     * @param objectName
     * @return
     */
    public static InputStream downloader(MinioClient minioClient, String bucket, String objectName) {
        try {
            // boolean objectFound = objectExists(minioClient, bucket, objectName);
            boolean objectFound = true;
            if (objectFound) {
                return minioClient.getObject(bucket, objectName);
            } else {
                log.info("bucket={}, objectName={} not exists", bucket, objectName);
            }
        } catch (Exception e) {
            log.error("Minio file download failed, bucket=" + bucket + ", objectName=" + objectName, e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件元数据
     *
     * @param minioClient
     * @param bucket
     * @param objectName
     * @return
     */
    public static ObjectStat statObject(MinioClient minioClient, String bucket, String objectName) {
        ObjectStat objectStat = null;
        try {
            boolean objectFound = objectExists(minioClient, bucket, objectName);
            if (objectFound) {
                objectStat = minioClient.statObject(bucket, objectName);
            } else {
                log.info("bucket={}, objectName={} not exists", bucket, objectName);
            }
        } catch (Exception e) {
            log.error("get minio file statistic info failed", e);
        }
        return objectStat;
    }


    public static void main(String[] args) {
        MinioClient minioClient = null;
        try {
            minioClient = new MinioClient("http://localhost:9000", "minio", "miniominio");
        } catch (InvalidEndpointException e) {
            e.printStackTrace();
        } catch (InvalidPortException e) {
            e.printStackTrace();
        }
//        File file = new File("C:\\Users\\yuanj\\Pictures\\Saved Pictures\\firfox browser.png");
        File file = new File("C:\\Users\\yuanj\\Pictures\\Saved Pictures\\timgEWF2I3G4.jpg");
        // FileInputStream inputStream = null;
        InputStream inputStream = null;
        try {
            // fileInputStream = new FileInputStream(file);
            inputStream = Files.newInputStream(file.toPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUploadStatusBean fileUploadStatusBean = uploader(minioClient, inputStream, "test", "firfox.png", true);
        System.out.println("isSuccess=" + fileUploadStatusBean.isSuccess());
        System.out.println("isOverride=" + fileUploadStatusBean.isOverwrite());
        System.out.println("objectName=" + fileUploadStatusBean.getObjectName());
        System.out.println("message=" + fileUploadStatusBean.getMsg());
    }
}
