package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.CimConstants.BaseFileInfoKeys;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DocConverterTask implements Runnable {
    private static Logger log = LoggerFactory.getLogger(DocConverterTask.class);

    private MinioClient minioClient;
    private OfficeConverterService converterService;
    private String tenantId;
    private String objectTypeId;
    private String bucket;
    private String fileName;


    public DocConverterTask(MinioClient minioClient, OfficeConverterService converterService, String tenantId,
                            String objectTypeId, String bucket, String fileName) {
        this.minioClient = minioClient;
        this.converterService = converterService;
        this.tenantId = tenantId;
        this.objectTypeId = objectTypeId;
        this.bucket = bucket;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        log.info("start to converter file...");
        converter();
        log.info("complete converter file!!!");
    }

    public void converter() {
        CimDataSpace cds = null;
        try {
            String outputFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".pdf";
            ObjectStat objectStat = minioClient.statObject(bucket, fileName);
            if (objectStat != null) {
                Long fileSize = objectStat.length();
                if (fileSize > CimConstants.MAX_OFFICE_FILE_SIZE) {
                    log.error("file is too large, cannot preview online");
                    return;
                }
            }
            //file converter
            InputStream inputStream = minioClient.getObject(bucket, fileName);
            Path tmpPath = Files.createTempFile(fileName.substring(0, fileName.lastIndexOf('.')), ".pdf");
            FileOutputStream fileOutputStream = new FileOutputStream(tmpPath.toFile());
            converterService.officeToPdfConverter(inputStream, fileOutputStream, outputFileName);
            fileOutputStream.close();
            inputStream.close();
            //upload file to minio
            minioClient.putObject(bucket, outputFileName, tmpPath.toString());
            //update preview info
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            updatePreviewStatus(cds, outputFileName, objectTypeId, fileName);
            Files.deleteIfExists(tmpPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    public static void updatePreviewStatus(CimDataSpace cds, String objectName, String objectTypeId, String fileName) throws CimDataEngineRuntimeException,
            CimDataEngineInfoExploreException {
        FilteringItem filteringItem = new EqualFilteringItem(BaseFileInfoKeys.MINIO_OBJECT_NAME,
                fileName);

        ExploreParameters ep = new ExploreParameters();
        ep.setType(objectTypeId);
        ep.setDefaultFilteringItem(filteringItem);

        List<Fact> factList = cds.getInformationExplorer().discoverInheritFacts(ep);
        if (CollectionUtils.isNotEmpty(factList)) {
            Fact firstFact = factList.get(0);
            if (firstFact.hasProperty(BaseFileInfoKeys.PREVIEW_TYPE)) {
                firstFact.updateProperty(BaseFileInfoKeys.PREVIEW_TYPE, "PDF");
            } else {
                firstFact.addProperty(BaseFileInfoKeys.PREVIEW_TYPE, "PDF");
            }

            if (firstFact.hasProperty(BaseFileInfoKeys.PREVIEW_PATH)) {
                firstFact.updateProperty(BaseFileInfoKeys.PREVIEW_PATH, objectName);
            } else {
                firstFact.addProperty(BaseFileInfoKeys.PREVIEW_PATH, objectName);
            }
        }
    }

}
