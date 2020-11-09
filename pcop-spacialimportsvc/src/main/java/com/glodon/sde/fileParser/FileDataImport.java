package com.glodon.sde.fileParser;

import com.glodon.pcop.cim.common.service.MinioService;
import com.glodon.pcop.cim.common.util.EnumWrapper.IMPORT_FILE_TYPE;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.DatasetFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.PropertyTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DataLoadStatisticsVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTransferVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.RelationshipMappingVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.RelationshipDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.spacialimportsvc.config.OrientdbPropertyConfig;
import com.glodon.pcop.spacialimportsvc.service.DataSetMapping;
import com.glodon.pcop.spacialimportsvc.service.ImportMappingBean;
import com.glodon.pcop.spacialimportsvc.service.PropMapping;
import com.glodon.pcop.spacialimportsvc.service.PropertyMappingBean;
import com.glodon.pcop.spacialimportsvc.util.ImportCimConstants;
import com.glodon.pcop.spacialimportsvc.util.ZipFileUtil;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDataImport {
    private static Logger log = LoggerFactory.getLogger(FileDataImport.class);

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
    private String getZipFilePath(String fileId) throws InvalidEndpointException, InvalidPortException, IOException {
        String tempPathStr = OrientdbPropertyConfig.getPropertyValue(OrientdbPropertyConfig.TEMP_FILE_PATH);
        String url = OrientdbPropertyConfig.getPropertyValue(OrientdbPropertyConfig.TEMP_MINIO_URL);
        String userName = OrientdbPropertyConfig.getPropertyValue(OrientdbPropertyConfig.TEMP_MINIO_USERNAME);
        String pwd = OrientdbPropertyConfig.getPropertyValue(OrientdbPropertyConfig.TEMP_MINIO_PASSWORD);
        String bucket = OrientdbPropertyConfig.getPropertyValue(OrientdbPropertyConfig.TEMP_MINIO_BUCKET);

        Path tempDir = Paths.get(tempPathStr);
        Files.createDirectories(tempDir);
        Path filePath = tempDir.resolve(fileId);
        String filePathStr = filePath.toAbsolutePath().toString();
        log.info("Local file {}", filePathStr);
        if (!Files.exists(filePath)) {
            log.info("Minio file download: url={}, userName={}, psw={}, bucket={}", url, userName, pwd, bucket);
            MinioService minioService = new MinioService(new MinioClient(url, userName, pwd), bucket);
            minioService.fileDownload(fileId, filePathStr);
        }
        return filePathStr;
    }

    public String getShpFilePath(String fileId) throws InvalidPortException, InvalidEndpointException, IOException {
        String tempPath = OrientdbPropertyConfig.getPropertyValue(OrientdbPropertyConfig.TEMP_FILE_PATH);
        String zipFile = getZipFilePath(fileId);
        String LocalTempPath = tempPath;
        if (tempPath.endsWith(File.separator)) {
            LocalTempPath = tempPath + "uplaod_temp";
        } else {
            LocalTempPath = tempPath + File.separator + "uplaod_temp";
        }
        log.info("unzip shp file {} to {}", zipFile, LocalTempPath);
        ZipFileUtil.unzip(zipFile, LocalTempPath);

        File file = new File(LocalTempPath);
        for (File outFile : file.listFiles()) {
            if (outFile.isDirectory()) {
                for (File inFile : outFile.listFiles()) {
                    if (inFile.getName().endsWith(".shp")) {
                        return inFile.getAbsolutePath();
                    }
                }
            }
        }
        throw new RuntimeException("shp file is not found");
    }

    /**
     * 导入shp文件数据到cim数据库
     *
     * @param pm
     */
    public void shpDataImport(PropertyMappingBean pm) {
        try {
            String filePath = getShpFilePath(pm.getFileId());
            log.info("Shp file local path: {}", filePath);
            System.load("/usr/local/lib/libjni.so");
            CreateFileParser parser = new CreateFileParser();
            parser.Load(filePath);
            Long count = parser.getRecordCount();
            log.info("Shp file record count {}", count);
            Map<String, String> structureMap = parser.getStruct();
            log.info("Shp file record structure {}", structureMap);
            List<PropMapping> propMappingList = getFlatPropertyMapping(pm);
            if (propMappingList == null || propMappingList.size() == 0) {
                log.info("Shp file no mapping import, create data set: {}", structureMap);
                createDataSet(pm, structureMap);
            }
            List<Map<String, InfoObjectTransferVO>> infoObjectDataList = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                try {
                    Map<String, Object> row_map = parser.getNextRow();
                    if (i < 30) {
                        log.info("Shp file content line {}: {}", i, row_map);
                    }
                    Map<String, InfoObjectTransferVO> nMap = new HashMap<String, InfoObjectTransferVO>();
                    for (Map.Entry<String, Object> entry : row_map.entrySet()) {
                        InfoObjectTransferVO iot = new InfoObjectTransferVO(entry.getValue());
                        nMap.put(entry.getKey(), iot);
                    }
                    infoObjectDataList.add(nMap);
                } catch (Exception e) {
                    log.error("Shp record cast failed：lineNumber={}", i);
                }
            }
            if (propMappingList == null || propMappingList.size() == 0) {
                log.info("Shp file no mapping import.");
                DataLoadStatisticsVO dataLoadStatisticsVO = InfoObjectFeatures.loadInfoObjectData(ImportCimConstants.defauleSpaceName, pm.getTypeId(), null,
                        infoObjectDataList);
                log.info("Shp file no mapping import result, count={}, fail={}, success={}", dataLoadStatisticsVO.getSuccessItemsCount(), dataLoadStatisticsVO.getFailItemsCount(), dataLoadStatisticsVO.getSuccessItemsCount());
            } else {
                log.info("Shp file mapping import.");
                Map<String, String> kkMap = new HashMap<>();
                for (PropMapping pp : propMappingList) {
                    kkMap.put(pp.getTargetName(), pp.getName());
                }
                log.info("Property mapping info: {}", kkMap);
                DataLoadStatisticsVO dataLoadStatisticsVO = InfoObjectFeatures.loadInfoObjectData(ImportCimConstants.defauleSpaceName, pm.getTypeId(), kkMap, infoObjectDataList);
                log.info("Shp file mapping import result, count={}, fail={}, success={}", dataLoadStatisticsVO.getSuccessItemsCount(), dataLoadStatisticsVO.getFailItemsCount(), dataLoadStatisticsVO.getSuccessItemsCount());
            }
            // 清空临时文件
            File tempFilePath = new File(filePath);
            deleteFile(tempFilePath.getParentFile());
        } catch (Exception e) {
            log.error("Shp file import failed: fileId={}", pm.getFileId());
            e.printStackTrace();
        }
    }

    public void createDataSet(PropertyMappingBean propertyMappingBean, Map<String, String> properties) {
        DatasetVO ds = new DatasetVO();
        ds.setDatasetName(propertyMappingBean.getTypeId() + "dataSet");
        ds.setDatasetClassify("shp");
        ds.setDatasetDesc(propertyMappingBean.getTypeId() + "dataSet");

        ds = DatasetFeatures.addDataset(ImportCimConstants.defauleSpaceName, ds);
        InfoObjectFeatures.linkInfoObjectTypeWithDataset(ImportCimConstants.defauleSpaceName, propertyMappingBean.getTypeId(), ds.getDatasetId());

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            PropertyTypeVO propertyTypeVO = new PropertyTypeVO();
            propertyTypeVO.setPropertyTypeName(entry.getKey());
            propertyTypeVO.setPropertyFieldDataClassify("STRING");
            propertyTypeVO.setPropertyTypeDesc(entry.getKey());

            propertyTypeVO = PropertyTypeFeatures.addPropertyType(ImportCimConstants.defauleSpaceName, propertyTypeVO);
            DatasetFeatures.addPropertyTypeLink(ImportCimConstants.defauleSpaceName, ds.getDatasetId(), propertyTypeVO.getPropertyTypeId());
        }
    }

    public List<PropMapping> getFlatPropertyMapping(PropertyMappingBean propertyMappingBean) {
        List<PropMapping> propMappingList = new ArrayList<>();

        List<DataSetMapping> dataSetMappingList = propertyMappingBean.getDataSetMapping();
        if (dataSetMappingList != null && dataSetMappingList.size() > 0) {
            for (DataSetMapping dataSetMapping : dataSetMappingList) {
                List<PropMapping> tmpPropMappingList = dataSetMapping.getPropertyMapping();
                if (tmpPropMappingList != null) {
                    propMappingList.addAll(tmpPropMappingList);
                }
            }
        }
        return propMappingList;
    }

    /**
     * 解析excel文件结构
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public Map<String, String> getExcelFileStructure(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("{} file not exists", filePath);
            return null;
        }
        FileInputStream inputStream = new FileInputStream(file);
        Workbook wb = WorkbookFactory.create(inputStream);
        DataFormatter formatter = new DataFormatter();
        Sheet firstSheet = wb.getSheetAt(0);
        Map<String, String> fieldTypeMap = new HashMap<>();
        if (firstSheet != null) {
            Row firstRow = firstSheet.getRow(0);
            if (firstRow != null) {
                for (int i = 0; i < firstRow.getLastCellNum(); i++) {
                    Cell cell = firstRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    fieldTypeMap.put(formatter.formatCellValue(cell), "STRING");
                }
            }
            log.info("Excel structure: {}", fieldTypeMap);
        }
        return fieldTypeMap;
    }

    private List<Map<String, Object>> readExcelContent(String filePath) throws IOException {
        List<Map<String, Object>> contents = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        FileInputStream inputStream = new FileInputStream(file);
        Workbook wb = WorkbookFactory.create(inputStream);
        DataFormatter formatter = new DataFormatter();
        Sheet firstSheet = wb.getSheetAt(0);

        List<String> headerList = new ArrayList<>();
        int columnNumber = firstSheet.getRow(0).getLastCellNum();
        Row firstRow = firstSheet.getRow(0);
        for (int i = 0; i < columnNumber; i++) {
            headerList.add(formatter.formatCellValue(firstRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)));
        }

        //暂时只导入第一个sheet
        if (firstSheet != null) {
            //从第二行开始导入，第一行为标题
            for (int i = 1; i <= firstSheet.getLastRowNum(); i++) {
                Row row = firstSheet.getRow(i);
                if (row != null) {
                    Map<String, Object> fieldTypeMap = new HashMap<>();
                    for (int n = 0; n < columnNumber; n++) {
                        Cell cell = row.getCell(n, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        fieldTypeMap.put(headerList.get(n), formatter.formatCellValue(cell));
                    }
                    contents.add(fieldTypeMap);
                }
            }
        }
        return contents;
    }

    public void excelDataImport(ImportMappingBean mappingBean) {//NOSONAR
        PropertyMappingBean propertyMapping = mappingBean.getPropertyMappingBean();
        if (propertyMapping != null) {
            try {
                String filePath = getZipFilePath(propertyMapping.getFileId());
                log.info("Excel file local path: {}", filePath);
                Map<String, String> structureMap = getExcelFileStructure(filePath);
                List<PropMapping> propMappingList = getFlatPropertyMapping(propertyMapping);
                if (propMappingList == null || propMappingList.size() == 0) {
                    log.info("No mapping import, create data set: {}", structureMap);
                    createDataSet(propertyMapping, structureMap);
                }
//                List<Map<String, InfoObjectTransferVO>> infoObjectDataList = new ArrayList<>();
                List<Map<String, Object>> contents = readExcelContent(filePath);
                RelationshipMappingVO relationshipMapping = mappingBean.getRelationshipMappingVO();
                RelationshipDef relationshipDef = null;
                if (relationshipMapping != null && relationshipMapping.getSourceInfoObjectType() != null && relationshipMapping.getTargetInfoObjectType() != null && relationshipMapping.getRelationTypeName() != null && relationshipMapping.getLinkLogic() != null) {
                    CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(ImportCimConstants.defauleSpaceName, "1");
                    relationshipDef = cimModelCore.createTemporaryRelationshipDef(relationshipMapping.getSourceInfoObjectType(), relationshipMapping.getTargetInfoObjectType(), relationshipMapping.getRelationTypeName(), relationshipMapping.getLinkLogic());
                }
                if (contents != null && contents.size() > 0) {
                    if (propMappingList == null || propMappingList.size() == 0) {
                        log.info("Excel file no mapping import!");
                        for (Map<String, Object> record : contents) {
                            Fact fact = InfoObjectFeatures.loadInfoObjectData(ImportCimConstants.defauleSpaceName, propertyMapping.getTypeId(), null, record);
                            if (fact != null && relationshipDef != null) {
                                boolean flag = relationshipDef.createRelationship(fact.getId(), null);
                                log.info("Add relationship of instanceId={}, result={}", fact.getId(), flag);
                            }
                        }
                    } else {
                        log.info("Excel file mapping import!");
                        Map<String, String> kkMap = new HashMap<>();
                        for (PropMapping pp : propMappingList) {
                            kkMap.put(pp.getTargetName(), pp.getName());
                        }
                        log.info("Property mapping info: {}", kkMap);
                        for (Map<String, Object> record : contents) {
                            Fact fact = InfoObjectFeatures.loadInfoObjectData(ImportCimConstants.defauleSpaceName, propertyMapping.getTypeId(), kkMap, record);
                            if (fact != null && relationshipDef != null) {
                                boolean flag = relationshipDef.createRelationship(fact.getId(), null);
                                log.info("Add relationship of instanceId={}, result={}", fact.getId(), flag);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Excel file import failed: fileId={}", propertyMapping.getFileId());
                e.printStackTrace();
            }
        }
    }

    public void fileImport(ImportMappingBean mappingBean) {
        String fileType = mappingBean.getFileType();
        PropertyMappingBean propertyMapping = mappingBean.getPropertyMappingBean();
        if (propertyMapping != null) {
            if (fileType.equals(IMPORT_FILE_TYPE.SHP.toString())) {
                shpDataImport(propertyMapping);
            } else if (fileType.equals(IMPORT_FILE_TYPE.XLS.toString())) {
                excelDataImport(mappingBean);
            } else {
                log.error("only shp and excel file format is support currently");
            }
        } else {
            log.error("File import failed, no import condition is provided.");
        }
    }

}
