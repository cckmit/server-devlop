package com.glodon.pcop.cimsvc.util;

import com.glodon.pcop.cimsvc.model.StandardTreeNode;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangd-a
 * @date 2019/4/11 10:40
 */
public class ExcelImportUtil {
    private static Logger log = LoggerFactory.getLogger(ExcelImportUtil.class);
    private final static String XLS = "xls";
    private final static String XLSX = "xlsx";
    public final static String EXT = "ext";
    public final static String DEFAULT = "default";
    public final static String LEVEL = "level";
    List<StandardTreeNode> nodes = new ArrayList<StandardTreeNode>();

    public void analyzeExcel(String fileName, String str) {//NOSONAR
        File tempFile = null;
        try {
            if (EXT.equals(str)) {
                tempFile = new File(fileName);
            } else if (DEFAULT.equals(str)) {
                Resource resource = new ClassPathResource(fileName);
                //前缀
                String prefix = fileName.substring(0, fileName.lastIndexOf("."));
                //后缀
                String suffix = fileName.substring(fileName.lastIndexOf("."));
                InputStream inputStream = resource.getInputStream();
                tempFile = File.createTempFile(prefix, suffix);
                FileUtils.copyInputStreamToFile(inputStream, tempFile);
            } else {
                log.error("error excel extension: {}", str);
            }
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            Workbook workbook = getWorkBook(tempFile);
            if (workbook != null) {
                for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                    //获得当前sheet工作表
                    Sheet sheet = workbook.getSheetAt(sheetNum);
                    if (sheet == null) {
                        continue;
                    }
                    //获得当前sheet的开始行
                    int firstRowNum = sheet.getFirstRowNum();
                    //获得当前sheet的结束行
                    int lastRowNum = sheet.getLastRowNum();
                    //拿到最后一列
                    int lastCellNum = sheet.getRow(0).getLastCellNum();

                    //循环除了第一行的所有行
                    for (int rowNum = 2; rowNum <= lastRowNum; rowNum++) {
                        //获得当前行
                        Row row = sheet.getRow(rowNum);
                        Map<String, String> map = new HashMap<>();
                        buildTopologyMap(row, 0, map);
                        list.add(map);
                    }
                }
                Map<String, String> resultMap = new LinkedHashMap<>();
                for (int index = 0; index < list.size(); index++) {
                    Map<String, String> map = list.get(index);
                    String parentSign = recursive(list, index, new Integer(map.get(LEVEL)));
                    resultMap.put(map.get("sign"), parentSign);
                }


                for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                    String key = entry.getKey();
                    String pid = entry.getValue();
                    StandardTreeNode node = new StandardTreeNode();
                    node.setKey(key);
                    node.setParentId(pid);
                    for (Map<String, String> map : list) {
                        if (key.equals(map.get("sign"))) {
                            String name = map.get("name");
                            node.setTitle(name);
                        }

                    }
                    nodes.add(node);
                }

                workbook.close();

            }


        } catch (Exception e) {
            log.error("Excel file not found");
            e.printStackTrace();
        }

    }

    public static Workbook getWorkBook(File file) {
        //获得文件名
        String fileName = file.getName();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        // InputStream is = null;
        try (InputStream is = new FileInputStream(file)) {
            //获取excel文件的io流
            // is = new FileInputStream(file);
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (fileName.endsWith(XLS)) {
                //2003
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(XLSX)) {
                //2007
                workbook = new XSSFWorkbook(is);
            } else {
                log.error("not support excel extension: {}", fileName);
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return workbook;
    }

    private static String getCellStringValue(Cell cell, boolean isDouble) throws Exception {
        String value = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    value = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    if (isDouble) {
                        value = new BigDecimal(new Double(cell.getNumericCellValue()).toString()).toString();
                    } else {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cell.getDateCellValue());
                        } else {
                            value = new BigDecimal(new Double(cell.getNumericCellValue()).toString()).intValue() + "";
                        }
                    }

                    break;
                case BOOLEAN:
                    value = "" + cell.getBooleanCellValue();
                    break;
                case BLANK:
                    value = "";
                    break;
                case ERROR:
                    throw new Exception("不能辨析CELL_TYPE_ERROR");
                case FORMULA:
                    value = cell.getCellFormula();
                    break;
                default:
                    log.debug("no action");
            }
        } else {
            value = "";
        }
        return value.trim();
    }

    private static void buildTopologyMap(Row row, Integer startColumn, Map<String, String> map) throws Exception {
        for (int index = 0; index < 6; index++) {
            Cell cell = row.getCell(startColumn + index);
            String cellValue = getCellStringValue(cell, false);
            if (cellValue != null && !"".equals(cellValue)) {
                map.put("name", cellValue);
                map.put(LEVEL, (index + 1) + "");
                map.put("sign", cell.getRowIndex() + "");
                break;
            }
        }
    }

    private String recursive(List<Map<String, String>> list, int index, Integer level) {
        if (index == -1) {
            return "-1";
        } else {
            Map<String, String> map = list.get(index);
            Integer findLevel = new Integer(map.get(LEVEL));
            if (level == (findLevel + 1)) {
                return map.get("sign");
            } else {
                return recursive(list, index -
                        1, level);
            }
        }
    }

    public List<StandardTreeNode> buildTree() {
        List<StandardTreeNode> treeNodes = new ArrayList<StandardTreeNode>();
        List<StandardTreeNode> rootNodes = getRootNodes();
        for (StandardTreeNode rootNode : rootNodes) {
            buildChildNodes(rootNode);
            treeNodes.add(rootNode);
        }
        return treeNodes;
    }

    /**
     * 递归子节点
     *
     * @param node
     */
    public void buildChildNodes(StandardTreeNode node) {
        List<StandardTreeNode> children = getChildNodes(node);
        if (!children.isEmpty()) {
            for (StandardTreeNode child : children) {
                buildChildNodes(child);
            }
            node.setChildren(children);
        }
    }

    /**
     * 获取父节点下所有的子节点
     *
     * @param pnode
     * @return List<StandardTreeNode>
     */
    public List<StandardTreeNode> getChildNodes(StandardTreeNode pnode) {
        List<StandardTreeNode> childNodes = new ArrayList<StandardTreeNode>();
        for (StandardTreeNode n : nodes) {
            if (pnode.getKey().equals(n.getParentId())) {
                childNodes.add(n);
            }
        }
        return childNodes;
    }

    /**
     * 判断是否为根节点
     *
     * @param node
     * @return boolean
     */
    public boolean rootNode(StandardTreeNode node) {
        boolean isRootNode = true;
        for (StandardTreeNode n : nodes) {
            if (node.getParentId().equals(n.getKey())) {
                isRootNode = false;
                break;
            }
        }
        return isRootNode;
    }

    /**
     * 获取集合中所有的根节点
     */
    public List<StandardTreeNode> getRootNodes() {
        List<StandardTreeNode> rootNodes = new ArrayList<StandardTreeNode>();
        for (StandardTreeNode n : nodes) {
            if (rootNode(n)) {
                rootNodes.add(n);
            }
        }
        return rootNodes;
    }
}
