package com.glodon.pcop.cim.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@Ignore
public class ExcelFileReaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void oneSheetContent() throws IOException {
//        String fileName = "G:\\data\\bim\\max数据发布测试样例\\xinhancheng_jichang.xlsx";
//        String fileName = "G:\\data\\excel\\zipStructure\\fuzhou_zlf02.xlsx";
        String fileName = "G:\\data\\excel\\zipStructure\\shuzizhognguoshinei.xlsx";
        InputStream inputStream = new FileInputStream(fileName);

        int idx = 0;
        List<Map<Integer, String>> sheetContent = ExcelFileReader.oneSheetContent(inputStream, idx, -1, -1);
        for (Map<Integer, String> rd : sheetContent) {
            System.out.println("column number=" + rd.size() + ", content= " + JSON.toJSONString(rd,
                    SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty));
        }

    }
}