package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import io.minio.MinioClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jodconverter.DocumentConverter;
import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.jodconverter.document.DocumentFormat;
import org.jodconverter.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

@Service
public class OfficeConverterService {
    private static Logger log = LoggerFactory.getLogger(OfficeConverterService.class);

    @Autowired
    private DocumentConverter converter;

    public void officeToPdfConverter(InputStream inputStream, OutputStream outputStream, String inputFileName) {
        final DocumentFormat targetFormat = DefaultDocumentFormatRegistry.getFormatByExtension("pdf");
        try {
            converter.convert(inputStream)
                    .as(DefaultDocumentFormatRegistry.getFormatByExtension(FilenameUtils.getExtension(inputFileName)))
                    .to(outputStream)
                    .as(targetFormat)
                    .execute();
        } catch (OfficeException e) {
            log.error("office to pdf failed", e);
        }
    }

    public void officeToPdfConverter(File srcFile, File desFile) {
        final DocumentFormat targetFormat = DefaultDocumentFormatRegistry.getFormatByExtension("pdf");
        try {
            converter.convert(srcFile)
                    .to(desFile)
                    .execute();
        } catch (OfficeException e) {
            log.error("office to pdf failed", e);
        }
    }

    public File minioFileConverter(MinioClient minioClient, String bucket, String objectName, String outputFileName) {
        File srcFile = null;
        File descFile = null;
        CimDataSpace cds = null;
        try {
            if (objectName.contains("/")) {
                srcFile = FileUtils.getFile(FileUtils.getTempDirectory(), objectName.replace('/', '_'));
            } else {
                srcFile = FileUtils.getFile(FileUtils.getTempDirectory(), objectName);
            }
            log.info("source file path: {}", srcFile.getAbsolutePath());
            // FileUtils.forceDelete(srcFile);
            Files.deleteIfExists(srcFile.toPath());
            try (InputStream srcInputStream = minioClient.getObject(bucket, objectName);
                 OutputStream os = new FileOutputStream(srcFile)) {
                IOUtils.copy(srcInputStream, os);
            }
            if (outputFileName.contains("/")) {
                descFile = FileUtils.getFile(FileUtils.getTempDirectory(), outputFileName.replace('/', '_'));
            } else {
                descFile = FileUtils.getFile(FileUtils.getTempDirectory(), outputFileName);
            }
            log.info("destination file path: {}", descFile.getAbsolutePath());
            // FileUtils.forceDelete(descFile);
            Files.deleteIfExists(descFile.toPath());
            officeToPdfConverter(srcFile, descFile);

            //upload file to minio
            minioClient.putObject(bucket, outputFileName, descFile.getAbsolutePath());
            //update preview info
            // cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            // DocConverterTask.updatePreviewStatus(cds, outputFileName,
            //         CimConstants.BaseFileInfoKeys.BaseFileObjectTypeName, objectName);
        } catch (Exception e) {
            log.error("minio file converter failed", e);
        }
        return descFile;
    }
}

