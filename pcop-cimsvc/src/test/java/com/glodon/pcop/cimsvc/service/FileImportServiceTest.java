package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cimsvc.model.FileStructureBean;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class FileImportServiceTest {

    @Test
    public void excelParser() throws IOException {
        FileImportService importService = new FileImportService();
        String fileName = "读取测试.xlsx";
//        File inputXmlFile = new File(this.getClass().getResource(fileName).getFile());
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/读取测试.xlsx");
        FileStructureBean fileStructureBean = new FileStructureBean();
        importService.excelParser(fileInputStream, fileName, fileStructureBean);

    }

}