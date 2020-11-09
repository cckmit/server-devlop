package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Ignore
public class TreeDisplayNameExportServiceTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void contentExport() throws IOException {
        String fileName = "G:\\tmp\\tree_name.xlsx";
        String sheetName = "first sheet";

        // FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        NodeInfoBean nodeInfoBean1 = new NodeInfoBean();
        nodeInfoBean1.setNAME("第一级分类（1）");
        nodeInfoBean1.setLevel(0);

        NodeInfoBean nodeInfoBean11 = new NodeInfoBean();
        nodeInfoBean11.setNAME("第二级分类（11）");
        nodeInfoBean11.setLevel(1);

        NodeInfoBean nodeInfoBean111 = new NodeInfoBean();
        nodeInfoBean111.setNAME("第三级分类（111）");
        nodeInfoBean111.setLevel(2);
        List<NodeInfoBean> nodeInfoBeanList11 = new ArrayList<>();
        nodeInfoBeanList11.add(nodeInfoBean111);
        nodeInfoBean11.setChildNodes(nodeInfoBeanList11);

        NodeInfoBean nodeInfoBean12 = new NodeInfoBean();
        nodeInfoBean12.setNAME("第二级分类（12）");
        nodeInfoBean12.setLevel(1);

        NodeInfoBean nodeInfoBean13 = new NodeInfoBean();
        nodeInfoBean13.setNAME("第二级分类（13）");
        nodeInfoBean13.setLevel(1);
        List<NodeInfoBean> nodeInfoBeanList1 = new ArrayList<>();
        nodeInfoBeanList1.add(nodeInfoBean11);
        nodeInfoBeanList1.add(nodeInfoBean12);
        nodeInfoBeanList1.add(nodeInfoBean13);
        nodeInfoBean1.setChildNodes(nodeInfoBeanList1);


        NodeInfoBean nodeInfoBean2 = new NodeInfoBean();
        nodeInfoBean2.setNAME("第一级分类（2）");
        nodeInfoBean2.setLevel(0);

        NodeInfoBean nodeInfoBean21 = new NodeInfoBean();
        nodeInfoBean21.setNAME("第二级分类（21）");
        nodeInfoBean21.setLevel(1);

        NodeInfoBean nodeInfoBean22 = new NodeInfoBean();
        nodeInfoBean22.setNAME("第二级分类（22）");
        nodeInfoBean22.setLevel(1);
        List<NodeInfoBean> nodeInfoBeanList2 = new ArrayList<>();
        nodeInfoBeanList2.add(nodeInfoBean21);
        nodeInfoBeanList2.add(nodeInfoBean22);
        nodeInfoBean2.setChildNodes(nodeInfoBeanList2);

        NodeInfoBean nodeInfoBean3 = new NodeInfoBean();
        nodeInfoBean3.setNAME("第一级分类（3）");
        nodeInfoBean3.setLevel(0);
        List<NodeInfoBean> nodeInfoBeanList = new ArrayList<>();
        nodeInfoBeanList.add(nodeInfoBean1);
        nodeInfoBeanList.add(nodeInfoBean2);
        nodeInfoBeanList.add(nodeInfoBean3);
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            TreeDisplayNameExportService.contentExport(fileOutputStream, sheetName, nodeInfoBeanList);
        }

    }

    @Test
    public void shiftRows() {
        try (Workbook wb = new XSSFWorkbook()) {   //or new HSSFWorkbook();
            Sheet sheet = wb.createSheet("Sheet1");

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue(1);

            Row row2 = sheet.createRow(4);
            row2.createCell(1).setCellValue(2);

            Row row3 = sheet.createRow(5);
            row3.createCell(2).setCellValue(3);

            Row row4 = sheet.createRow(6);
            row4.createCell(3).setCellValue(4);

            Row row5 = sheet.createRow(9);
            row5.createCell(4).setCellValue(5);

            // Shift rows 6 - 11 on the spreadsheet to the top (rows 0 - 5)
            // sheet.shiftRows(5, 10, -4);

            try (FileOutputStream fileOut = new FileOutputStream("G:\\tmp\\no-shiftRows.xlsx")) {
                wb.write(fileOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}