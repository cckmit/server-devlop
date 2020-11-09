package com.glodon.pcop.cimsvc.service.v2;

import com.bimface.sdk.BimfaceClient;
import com.bimface.sdk.bean.response.FileBean;
import com.glodon.pcop.cim.common.service.MyMinioClient;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.bimface.BimFaceUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.common.model.bim.TranslateResponseBean;
import com.google.gson.Gson;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * 上传bim文件到bim face，启动文件转化，更新文件id到cim
 */
public class BimFaceUploadTask implements Runnable {
    private static Logger log = LoggerFactory.getLogger(BimFaceUploadTask.class);

    // public static String FILE_ID = "fileId";
    private String fileName;
    private String bucket;
    // private String dbName;
    private String ihFactId;
    private MinioClient minioClient;
    private BimfaceClient bimfaceClient;
    private CimDataSpace cds;

    public BimFaceUploadTask(MinioClient minioClient, BimfaceClient bimfaceClient, String fileName, String bucket, CimDataSpace cimDataSpace, String ihFactId) {
        this.bimfaceClient = bimfaceClient;
        this.minioClient = minioClient;
        this.fileName = fileName;
        this.bucket = bucket;
        this.cds = cimDataSpace;
        this.ihFactId = ihFactId;
    }

    @Override
    public void run() {//NOSONAR
        InputStream inputStream = MyMinioClient.downloader(minioClient, bucket, fileName);
        // CimDataSpace cds = null;
        try {
            if (inputStream != null) {
                ObjectStat objectStat = minioClient.statObject(bucket, fileName);
                FileBean fileBean = BimFaceUtil.bimUpload(bimfaceClient, fileName, objectStat.length(), inputStream);
                if (fileBean != null) {
                    // cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(dbName);
                    Fact fact = cds.getFactById(ihFactId);
                    if (fact != null) {
                        if (fact.hasProperty(CimConstants.BaseFileInfoKeys.FILE_ID)) {
                            fact.updateProperty(CimConstants.BaseFileInfoKeys.FILE_ID, fileBean.getFileId());
                        } else {
                            fact.addProperty(CimConstants.BaseFileInfoKeys.FILE_ID, fileBean.getFileId());
                        }
                        TranslateResponseBean responseBean = BimFaceUtil.bimTranslate(bimfaceClient, fileBean.getFileId());
                        if (responseBean != null) {
                            if (fact.hasProperty(CimConstants.BaseFileInfoKeys.DATA_BAG_ID)) {
                                fact.updateProperty(CimConstants.BaseFileInfoKeys.DATA_BAG_ID, responseBean.getDatabagId());
                            } else {
                                fact.addProperty(CimConstants.BaseFileInfoKeys.DATA_BAG_ID, responseBean.getDatabagId());
                            }
                        } else {
                            log.error("translate response is null");
                        }
                    } else {
                        log.error("fact of {} not found");
                    }
                } else {
                    log.error("bim face file upload failed");
                }
            }
        } catch (Exception e) {
            log.error("bim file upload failed", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    public void uploadAndTranslate() {
        InputStream inputStream = MyMinioClient.downloader(minioClient, bucket, fileName);
        // CimDataSpace cds = null;
        try {
            if (inputStream != null) {
                ObjectStat objectStat = minioClient.statObject(bucket, fileName);
                log.info("start to upload {} to bimface", fileName);
                FileBean fileBean = BimFaceUtil.bimUpload(bimfaceClient, fileName, objectStat.length(), inputStream);
                log.info("finish to upload {} to bimface", fileName);
                if (fileBean != null) {
                    // cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(dbName);
                    // Fact fact = cds.getFactById(ihFactId);
                    // if (fact != null) {
                    //     if (fact.hasProperty(CimConstants.BaseFileInfoKeys.FILE_ID)) {
                    //         fact.updateProperty(CimConstants.BaseFileInfoKeys.FILE_ID, fileBean.getFileId());
                    //     } else {
                    //         fact.addProperty(CimConstants.BaseFileInfoKeys.FILE_ID, fileBean.getFileId());
                    //     }
                    // } else {
                    //     log.error("fact of {} not found", ihFactId);
                    // }
                    TranslateResponseBean responseBean = BimFaceUtil.bimTranslate(bimfaceClient, fileBean.getFileId());
                    log.info("response bean: " + new Gson().toJson(responseBean));
                } else {
                    log.error("bim face file upload failed");
                }
            } else {
                log.error("get minio file failed");
            }
        } catch (Exception e) {
            log.error("bim file upload failed", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

}
