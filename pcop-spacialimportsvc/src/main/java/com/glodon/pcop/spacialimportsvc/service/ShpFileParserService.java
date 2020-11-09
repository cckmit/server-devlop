package com.glodon.pcop.spacialimportsvc.service;

import com.glodon.pcop.cim.common.model.FileStructureBean;
import com.glodon.pcop.cim.common.service.MinioService;
import com.glodon.pcop.spacialimportsvc.config.OrientdbPropertyConfig;
import com.glodon.pcop.spacialimportsvc.util.ImportCommonUtil;
import com.glodon.pcop.spacialimportsvc.util.ShpFileUtil;
import com.glodon.pcop.spacialimportsvc.util.ZipFileUtil;
import com.glodon.sde.fileParser.CreateFileParser;
import com.google.gson.Gson;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author yuanjk
 */
@Service
public class ShpFileParserService {
    private static Logger log = LoggerFactory.getLogger(ShpFileParserService.class);
    public static final String UNZIP_TEMP = "unzip_temp";

    @Value("${my.temp-file.path:/root/temp/}")
    private String tempPath;

    @Value("${my.minio.url}")
    private String url;

    @Value("${my.minio.user-name}")
    private String userName;

    @Value("${my.minio.password}")
    private String pwd;

    @Value("${my.minio.bucket}")
    private String bucket;

    /**
     * 解析shp文件结构
     *
     * @param objectName 文件名称
     * @return
     * @throws IllegalStateException
     * @throws IOException
     * @throws InvalidEndpointException
     * @throws InvalidPortException
     */
    public FileStructureBean getShpFileStructureV1(String objectName) throws IllegalStateException, IOException, InvalidEndpointException, InvalidPortException {
        String localRootPath = getWorkPath();
        String LocalTempPath = localRootPath + File.separator + "unzip_tmp";
        String zipFilePath = getZipFilePath(objectName);
        // zip文件解压
        ZipFileUtil.unzip(zipFilePath, LocalTempPath);
        // 获取shp文件路径
        String shpPath = getShpFilePath(LocalTempPath);
        log.info("localTempPath={}, shpPath={}", LocalTempPath, shpPath);
        // 文件解析
        FileStructureBean fsBean;
        fsBean = getShpFileStruct(objectName, shpPath);
        // 清空临时文件夹
        File tmpFile = new File(LocalTempPath);
        for (File f : tmpFile.listFiles()) {
            deleteFile(f);
        }
        return fsBean;
    }

    /**
     * 上传文件在本地的存放路径
     *
     * @return
     */
    private String getWorkPath() {
        if (!tempPath.endsWith(File.separator)) {
            tempPath = tempPath + File.separator;
        }
        File directory = new File(tempPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory.getAbsolutePath() + File.separator;
    }

    public String getShpFilePath(String dirPath) {
        File file = new File(dirPath);
        for (File outFile : file.listFiles()) {
            if (outFile.isDirectory()) {
                for (File inFile : outFile.listFiles()) {
                    if (inFile.getName().endsWith(".shp")) {
                        return inFile.getAbsolutePath();
                    }
                }
            }
        }
        return null;
    }

    public FileStructureBean getShpFileStruct(String fileId, String filePath) {
        log.info("Start shp file parser...");
        System.load("/usr/local/lib/libjni.so");
        CreateFileParser parser = new CreateFileParser();
        log.info("加载shp文件：{}", filePath);
        parser.Load(filePath);
        Long count = parser.getRecordCount();
        log.info("file record count {}", count);
        Map<String, String> structMap = parser.getStruct();
        log.info("file record struct {}", structMap);
        FileStructureBean fsBean = new FileStructureBean(fileId, count, structMap);
        return fsBean;
    }

    public static boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }
        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {
            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }
        return dirFile.delete();
    }

    /**
     * 返回shp文件路径，若不存在则从minio下载
     *
     * @param fileId
     * @return
     * @throws InvalidEndpointException
     * @throws InvalidPortException
     */
    private String getZipFilePath(String fileId) throws InvalidEndpointException, InvalidPortException {
        String shp = getWorkPath() + fileId;

        File file = new File(shp);
        log.info("本地zip文件{}", shp);
        if (!file.exists()) {
            log.info("Minio client connection paramters url={}, userName={}, pass_word={}, bucket={}", url, userName, pwd, bucket);
            MinioService minioService = new MinioService(new MinioClient(url, userName, pwd), bucket);
            minioService.fileDownload(fileId, shp);
        }
        return shp;
    }

    public FileStructureBean getShpFileStructure(String fileId) throws InvalidPortException, InvalidEndpointException, IOException {
        String tempPath = OrientdbPropertyConfig.getPropertyValue(OrientdbPropertyConfig.TEMP_FILE_PATH);
        String LocalTempPath;
        if (tempPath.endsWith(File.separator)) {
            LocalTempPath = tempPath + UNZIP_TEMP;
        } else {
            LocalTempPath = tempPath + File.separator + UNZIP_TEMP;
        }
        String shpFile = ImportCommonUtil.getShpFilePath(fileId, LocalTempPath);
        log.info("Local shp file path: {}", shpFile);
        FileStructureBean fileStructureBean = ShpFileUtil.getShpStructure(fileId, shpFile);
        log.info("shp file structure: {}", (new Gson()).toJson(fileStructureBean));
        File tmpFile = new File(LocalTempPath);
        for (File f : tmpFile.listFiles()) {
            deleteFile(f);
        }
        return fileStructureBean;
    }

}
