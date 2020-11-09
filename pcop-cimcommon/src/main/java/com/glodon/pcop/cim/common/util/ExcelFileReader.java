package com.glodon.pcop.cim.common.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelFileReader {
    private static final Logger log = LoggerFactory.getLogger(ExcelFileReader.class);

    public static void main(String[] args) {
        try (InputStream inp = new FileInputStream("C:\\Users\\yuanjk\\Desktop\\读取测试.xlsx")) {
            Workbook wb = WorkbookFactory.create(inp);
            DataFormatter formatter = new DataFormatter();
            Sheet sheet1 = wb.getSheetAt(0);
            System.out.println("Row numbers: " + sheet1.getLastRowNum());
            for (Row row : sheet1) {
                System.out.println("Column numbers: " + row.getLastCellNum());
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    CellReference cellRef = new CellReference(row.getRowNum(), i);
                    System.out.print(cellRef.formatAsString());
                    System.out.print(" - ");
                    String text = formatter.formatCellValue(cell);
                    System.out.println(text);

                }
                break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param inputStream
     * @param idx         sheet indx: start from 0
     * @param stRec       row indx start from 0
     * @param endRec
     * @return
     * @throws IOException
     */
    public static List<Map<Integer, String>> oneSheetContent(InputStream inputStream, Integer idx, Integer stRec,
                                                             Integer endRec) throws IOException {
        List<Map<Integer, String>> rowValueList = new ArrayList<>();

        Workbook wb = WorkbookFactory.create(inputStream);
        if (idx < 0 || idx >= wb.getNumberOfSheets()) {
            log.error("Excel sheet idx must between {} and {}", 0, wb.getNumberOfSheets());
            return rowValueList;
        }
        DataFormatter formatter = new DataFormatter();
        Sheet sheet = wb.getSheetAt(idx);

        if (stRec > endRec) {
            log.error("start record number must less than end record number");
            return rowValueList;
        }

        if (stRec < 0) {
            stRec = 0;
        }

        if (endRec < 0 || endRec > sheet.getLastRowNum()) {
            endRec = sheet.getLastRowNum() + 1;
        }

        for (int rdIdx = stRec; rdIdx < endRec; rdIdx++) {
            Row row = sheet.getRow(rdIdx);
            Map<Integer, String> oneRowValue = new HashMap<>();
            for (Cell cell : row) {
                oneRowValue.put(cell.getColumnIndex(), formatter.formatCellValue(cell));
            }
            rowValueList.add(oneRowValue);
        }
        return rowValueList;
    }

}
