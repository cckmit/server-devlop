package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cimsvc.PcopCimsvcApplication;
import com.glodon.pcop.cimsvc.service.MinioService;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {PcopCimsvcApplication.class})
@ActiveProfiles("dev") //指定使用bootstrap-dev.yml
public class DocConverterTaskTest {
    private static Logger log = LoggerFactory.getLogger(DocConverterTaskTest.class);

    @Autowired
    private MinioService minioService;


    @Autowired
    private OfficeConverterService converterService;

    @BeforeClass
    public static void init() {

    }

    @Test
    public void converter() throws InvalidPortException, InvalidEndpointException {
        String tenantId = "1";
        String objectTypeId = CimConstants.BaseFileInfoKeys.BaseFileObjectTypeName;
        String bucket = "pan-test";
        String fileName = "C35P19__aeadd738-e35c-4530-8bab-50fa579c393c__工程项目信息-excel导入数据.xlsx";

        DocConverterTask converterTask = new DocConverterTask(minioService.getMinioClient(), converterService,
                tenantId, objectTypeId, bucket, fileName);
        log.info("start to converter...");
        converterTask.converter();
        log.info("complete to converter!!!");
    }

    @Test
    public void updatePreviewStatus() {
    }
}