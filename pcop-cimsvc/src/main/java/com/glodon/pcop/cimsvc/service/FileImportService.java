package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.ShpFileParserException;
import com.glodon.pcop.cimsvc.model.FileStructureBean;
import com.glodon.pcop.cimsvc.service.client.SpacialServiceInterface;
import com.glodon.pcop.cimsvc.util.DateUtil;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yuanjk
 */
@Service
public class FileImportService {

    private static Logger log = LoggerFactory.getLogger(FileImportService.class);

    @Value("${my.temp-file.path:/root/temp/}")
    private String tempPath;

    @Autowired
    private MinioService minioService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SpacialServiceInterface spacialService;

    public FileStructureBean shpFile(MultipartFile file)
            throws IllegalStateException, IOException, ShpFileParserException {
        String originalName = file.getOriginalFilename();
        String localRootPath = getFilePath();
        String minioObjectName = getStoredFileName(originalName);
        String localFilePath = localRootPath + File.separator + minioObjectName;
        // 存到本地
        file.transferTo(new File(localFilePath));
        // 存到minio
        minioService.fileUpload(minioObjectName, localFilePath);
        // 文件解析
        FileStructureBean fsBean;
        // 获取shp文件路径
        ReturnInfo ri = spacialService.fileParserShp(minioObjectName);
        log.error("shp file parser result: return code = {}", ri.getCode().toString());
        if (!ri.getCode().toString().endsWith("200")) {
            throw new ShpFileParserException(EnumWrapper.CodeAndMsg.E05060001);
        } else {
            Gson gson = new Gson();
            log.info("Spacial sevice return: {}", ri.getData());
            fsBean = gson.fromJson(ri.getData().toString(), FileStructureBean.class);
        }
        // 清空临时文件夹
//		File tmpFile = new File(LocalTempPath);
//		for (File f : tmpFile.listFiles()) {
//			deleteFile(f);
//		}
        return fsBean;
    }

    /**
     * 上传文件在本地的存放路径
     *
     * @return
     */
    private String getFilePath() {
        File directory = new File(tempPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory.getAbsolutePath();
    }

    /**
     * 在原文件名称加上时间
     *
     * @param fileName
     * @return
     */
    private String getStoredFileName(String fileName) {
        String fn;
        if (fileName.contains(".") && (fileName.length() > fileName.lastIndexOf("."))) {
            fn = fileName.substring(0, fileName.lastIndexOf(".")) + "-" + DateUtil.getCurrentDateReadable()
                    + fileName.substring(fileName.lastIndexOf("."));
        } else {
            fn = fileName + "-" + DateUtil.getCurrentDateReadable();
        }
        return fn;
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
     * 上传图像文件到cim库
     *
     * @param file
     * @return
     */
    public boolean imageUpload2Cim(String instanceId, String propertyId, MultipartFile file) throws IOException, CimDataEngineRuntimeException {
        int length = (int) file.getSize();
        byte[] data = new byte[length];
        InputStream inputStream = file.getInputStream();
        inputStream.read(data, 0, length);

        Fact fact = InfoObjectFeatures.addOrUpdateObjectPropertyById(CimConstants.defauleSpaceName, instanceId, propertyId, data);
        if (fact != null) {
            return true;
        } else {
            return false;
        }
    }

    public FileStructureBean fileUploadAndParser(String fileType, MultipartFile file) throws ShpFileParserException, IOException {
        FileStructureBean fileStructureBean = null;
        EnumWrapper.IMPORT_FILE_TYPE ft = EnumWrapper.IMPORT_FILE_TYPE.valueOf(fileType);
        switch (ft) {
            case SHP:
                fileStructureBean = shpFile(file);
                break;
            case XLS:
                fileStructureBean = excelFile(file);
                break;
            case OBJ:
                break;
            case RVT:
                break;
            default:
                break;
        }
        return fileStructureBean;
    }

    /**
     * excel文件结构解析
     *
     * @param file
     * @return
     */
    public FileStructureBean excelFile(MultipartFile file) throws IOException {
        FileStructureBean fileStructureBean = new FileStructureBean();
        String originalName = file.getOriginalFilename();
        String localRootPath = getFilePath();
        String minioObjectName = getStoredFileName(originalName);
        String localFilePath = localRootPath + File.separator + minioObjectName;
        // 存到本地
        file.transferTo(new File(localFilePath));
        // 存到minio
        minioService.fileUpload(minioObjectName, localFilePath);
        fileStructureBean.setFileId(minioObjectName);
        File localFile = new File(localFilePath);
        if (localFile.exists()) {
            FileInputStream inputStream = new FileInputStream(localFile);
            excelParser(inputStream, file.getOriginalFilename(), fileStructureBean);
        }
        return fileStructureBean;
    }

    /**
     * excel文件结构解析
     *
     * @param inputStream
     * @param fileName
     * @param fileStructureBean
     * @throws IOException
     */
    public void excelParser(InputStream inputStream, String fileName, FileStructureBean fileStructureBean) throws IOException {
        Workbook wb = WorkbookFactory.create(inputStream);
        DataFormatter formatter = new DataFormatter();
        Sheet firstSheet = wb.getSheetAt(0);
        if (firstSheet != null) {
            log.info("File name={}, First sheet name={},  Total row count={}", fileName, firstSheet.getSheetName(), firstSheet.getLastRowNum());
            fileStructureBean.setTotalCount(firstSheet.getLastRowNum());
            Row firstRow = firstSheet.getRow(0);
            Map<String, String> fieldTypeMap = new HashMap<>();
            if (firstRow != null) {
                log.info("File name={}, First sheet name={},  Total row count={}, Total column count={}", fileName, firstSheet.getSheetName(), firstSheet.getLastRowNum(), firstRow.getLastCellNum());
                for (int i = 0; i < firstRow.getLastCellNum(); i++) {
                    Cell cell = firstRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    fieldTypeMap.put(formatter.formatCellValue(cell), "STRING");
                }
            }
            log.info("Excel structure: {}", fieldTypeMap);
            fileStructureBean.setStructure(fieldTypeMap);
        } else {
            log.error("Excel file name={}, first sheet not found", fileName);
        }
    }



}
