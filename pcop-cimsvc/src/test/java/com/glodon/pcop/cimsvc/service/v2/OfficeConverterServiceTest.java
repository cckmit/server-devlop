package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cimsvc.PcopCimsvcApplication;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {PcopCimsvcApplication.class})
@ActiveProfiles("local") //指定使用bootstrap-dev.yml
public class OfficeConverterServiceTest {

    @Autowired
    private OfficeConverterService converterService;

    private static MinioClient minioClient = null;

    @BeforeClass
    public static void init() throws InvalidPortException, InvalidEndpointException {
        String url = "http://10.129.57.108:9000/";
        String userName = "pcop";
        String pwd = "gldcim.123";

        minioClient = new MinioClient(url, userName, pwd);
        // converterService = new OfficeConverterService();
    }

    @Test
    public void officeToPdfConverter() throws IOException {
        // String inputFileName = "G:\\data\\excel\\安置房一期竣工目录及bim对照表.xlsx";
        String inputFileName = "G:\\data\\doc\\福州数据中国会展中心项目设计交底会会议纪要(1).doc";
        // InputStream inputStream = new FileInputStream(inputFileName);
        // OutputStream outputStream = new FileOutputStream(inputFileName.substring(inputFileName.lastIndexOf('.')) +
        //         ".pdf");
        // try (InputStream inputStream = new FileInputStream(inputFileName);
        //      OutputStream outputStream = new FileOutputStream(inputFileName.substring(0,
        //              inputFileName.lastIndexOf('.')) + ".pdf")) {
        // converterService.officeToPdfConverter(inputStream, outputStream, inputFileName);
        converterService.officeToPdfConverter(new File(inputFileName), new File(inputFileName.substring(0,
                inputFileName.lastIndexOf('.')) + ".pdf"));
        // }
    }

    @Test
    public void minioFileConverter() {
        String bucket = "cimbucket";
        String objectName = "3f047b0b-83ca-4fc7-bbbd-6f10773f3c78__00项目监管系统需求文档（人员管理- 管理人员考勤统计）-PRD-V1.1_20180726.doc";
        String outputFileName = "3f047b0b-83ca-4fc7-bbbd-6f10773f3c78__00项目监管系统需求文档（人员管理- 管理人员考勤统计）-PRD-V1.1_20180726.pdf";

        File file = converterService.minioFileConverter(minioClient, bucket, objectName, outputFileName);
        System.out.println("pdf file path: " + file.getAbsolutePath());
    }

}