package com.glodon.pcop.cimsvc.util;

import com.glodon.pcop.cimsvc.model.StandardTreeNode;
import com.google.gson.Gson;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class ExcelImportUtilTest {

    @Test
    public void analyzeExcel() {
    }

    @Test
    public void getWorkBook() {
    }

    @Test
    public void buildTree() {
        ExcelImportUtil excelImportUtil = new ExcelImportUtil();
        String fileName = "excel" + File.separator + "demo.xlsx";
        excelImportUtil.analyzeExcel(fileName, ExcelImportUtil.DEFAULT);
        List<StandardTreeNode> treeNodes = excelImportUtil.buildTree();
        System.out.println(new Gson().toJson(treeNodes));
    }

    @Test
    public void buildChildNodes() {
    }

    @Test
    public void getChildNodes() {
    }

    @Test
    public void rootNode() {
    }

    @Test
    public void getRootNodes() {
    }
}