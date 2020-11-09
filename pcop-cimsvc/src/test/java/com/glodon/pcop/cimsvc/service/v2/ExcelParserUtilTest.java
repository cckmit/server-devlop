package com.glodon.pcop.cimsvc.service.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cimsvc.model.v2.mapping.FileStructBean;
import com.glodon.pcop.cimsvc.service.MinioService;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ExcelParserUtilTest {

    private static ExcelParserUtil parserUtil;

    @Before
    public void setUp() throws Exception {
        parserUtil = new ExcelParserUtil();
        ObjectMapper objectMapper = new ObjectMapper();
        // parserUtil.setObjectMapper(objectMapper);
        // MinioService
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void excelParser() throws IOException {
        String filePath = "G:\\tmp\\";
        String fileName = "控规总体指标.xlsx";
        FileInputStream fileInputStream = new FileInputStream(filePath + fileName);

        List<FileStructBean> fileStructBeanList = ExcelParserUtil.excelParser(fileInputStream, fileName);

        Gson gson = new Gson();
        System.out.println("file struct: \n" + gson.toJson(fileStructBeanList));
    }

    @Test
    public void sendDataToKafka() throws FileNotFoundException {
        String filePath = "G:\\tmp\\";
        String fileName = "控规总体指标.xlsx";
        FileInputStream fileInputStream = new FileInputStream(filePath + fileName);

        Map<String, String> sheetNames = new HashMap<>();
        sheetNames.put("planOverallQuota", "taskId-01");

        ExcelParserUtil.sendDataToKafka(null, fileInputStream, sheetNames);

        System.out.println("end...");
    }

}