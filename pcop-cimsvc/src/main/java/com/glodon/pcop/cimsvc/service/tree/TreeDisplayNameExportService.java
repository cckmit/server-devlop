package com.glodon.pcop.cimsvc.service.tree;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.config.properties.MimeTypeConfig;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.glodon.pcop.cimsvc.service.MinioService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

@Service
public class TreeDisplayNameExportService {
    private static final Logger log = LoggerFactory.getLogger(TreeDisplayNameExportService.class);

    @Autowired
    private TreeServiceWithPermission treeService;

    @Autowired
    private MimeTypeConfig mimeTypeConfig;

    private static Integer rowNumber;

    public static void contentExport(OutputStream outputStream, String sheetName,
                                     List<NodeInfoBean> nodeInfoBeanList) throws IOException {
        Assert.notEmpty(nodeInfoBeanList, "node info is empty");
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet firstSheet = wb.createSheet(sheetName);
            int columnShift = nodeInfoBeanList.get(0).getLevel();
            // int startRowNum = 0;
            rowNumber = 0;
            for (NodeInfoBean nodeInfoBean : nodeInfoBeanList) {
                nodeExport(firstSheet, nodeInfoBean, rowNumber, columnShift);
            }
            log.debug("first row number [{}], last row number [{}]", firstSheet.getFirstRowNum(),
                    firstSheet.getLastRowNum());
            wb.write(outputStream);
        }
    }

    private static void nodeExport(Sheet sheet, NodeInfoBean nodeInfoBean, int currentRowNum, Integer columnShift) {
        Row tmpRow = sheet.createRow(currentRowNum);
        // int nextRowNum = currentRowNum + 1;
        log.debug("current row number: [{}], next row number: [{}], node name: [{}]", currentRowNum, rowNumber + 1,
                nodeInfoBean.getNAME());
        tmpRow.createCell(nodeInfoBean.getLevel() - columnShift).setCellValue(nodeInfoBean.getNAME());
        rowNumber += 1;
        if (CollectionUtils.isNotEmpty(nodeInfoBean.getChildNodes())) {
            for (NodeInfoBean childNodeInfoBean : nodeInfoBean.getChildNodes()) {
                nodeExport(sheet, childNodeInfoBean, rowNumber, columnShift);
            }
        }
    }

    public void treeNodesExport(HttpServletResponse response, String tenantId, String userId, String treeDefId,
                                NodeInfoBean parentNode, String treeName) throws IOException,
            CimDataEngineRuntimeException {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);

            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);
            InfoObjectDef treeObjectDef = modelCore.getInfoObjectDef(treeDefId);

            List<NodeInfoBean> nodeInfoBeanList;

            if (StringUtils.hasText(parentNode.getID())) {
                log.info("export child tree: [{}]", parentNode.getID());
                List<NodeInfoBean> childNodeInfoBeanList = treeService.listChildNodesRecursivelyWithFilter(userId,
                        null, treeObjectDef, treeDefId, parentNode, modelCore,
                        TreeServiceWithPermission.DEFAULT_MAX_LOOP_COUNT);
                parentNode.setChildNodes(childNodeInfoBeanList);
                nodeInfoBeanList = Arrays.asList(parentNode);
            } else {
                log.info("export the whole tree");
                nodeInfoBeanList = treeService.listRootNodesWithFilter(userId, null, treeDefId, modelCore);
                if (nodeInfoBeanList != null && nodeInfoBeanList.size() > 0) {
                    for (NodeInfoBean infoBean : nodeInfoBeanList) {
                        infoBean.setChildNodes(treeService.listChildNodesRecursivelyWithFilter(userId, null,
                                treeObjectDef, treeDefId, infoBean, modelCore,
                                TreeServiceWithPermission.DEFAULT_MAX_LOOP_COUNT));
                    }
                }
            }
            String fileName = getOutputFileName(treeName, parentNode);
            setResponseHeaders(response, fileName);
            log.debug("node list: [{}]", JSON.toJSONString(nodeInfoBeanList));
            contentExport(response.getOutputStream(), fileName.substring(0, fileName.lastIndexOf('.')),
                    nodeInfoBeanList);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    private void setResponseHeaders(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        String mimeType = mimeTypeConfig.getMimeType(fileName.substring(fileName.lastIndexOf('.')));
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        // set content attributes for the response
        response.setContentType(mimeType);
        response.setHeader(MinioService.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(fileName,
                MinioService.UTF8) + ";filename*=UTF-8''" + URLEncoder.encode(fileName, MinioService.UTF8));
    }

    private String getOutputFileName(String treeName, NodeInfoBean parentNode) {
        String fileName;
        if (StringUtils.hasText(parentNode.getID())) {
            fileName = parentNode.getNAME() + "目录内容";
        } else if (StringUtils.hasText(treeName)) {
            fileName = treeName + "树内容";
        } else {
            fileName = "ContentDisplayNames";
        }
        fileName += ".xlsx";
        return fileName;
    }


}
