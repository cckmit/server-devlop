package com.glodon.pcop.cimsvc.service.export;

import com.glodon.pcop.cim.common.model.excel.PropertyInputBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.*;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectRetrieveResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.export.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.terracotta.exception.EntityNotFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DataExportService {
    private static Logger log = LoggerFactory.getLogger(DataExportService.class);


    public void exportInstancesAsExcel(String tenantId,ExportModel requestBodyModel, HttpServletResponse response) throws EntityNotFoundException, UnsupportedEncodingException {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd ");
        String fileName = formatter.format(currentTime);
        String projectId = requestBodyModel.getProjectId();

        CimDataSpace cds = null;
        try {
            //pcop-cim
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            //查询核心
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            //获取关联对象
            List<RelevanceObject> objectTypes=requestBodyModel.getObjectTypes();
            Workbook wb = exportInstances(modelCore,projectId,objectTypes);

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            String newFileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            try (OutputStream os = response.getOutputStream()) {
                response.setHeader("Content-Disposition", "attachment;filename=\""+ new String( fileName.getBytes( "gb2312" ), "ISO8859-1" )+ ".xls" + "\"");

                // response.setHeader(MinioService.CONTENT_DISPOSITION,
                // "inline;filename=" + URLEncoder.encode(fileName, MinioService.UTF8));
                wb.write(os);
                response.setStatus(HttpStatus.SC_OK);
            } catch (Exception e) {
                response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                log.error("write excel to response failed", e);
            } finally {
                if (wb != null) {
                    try {
                        wb.close();
                    } catch (IOException e) {
                        log.error("close workbook failed", e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    public List<PropertyInputBean> mergeAllProperties(InfoObjectDef infoObjectDef) {
        //获取基础定义
        DatasetDef baseDatasetDef = infoObjectDef.getBaseDatasetDef();
        //基础定义属性
        List<PropertyTypeDef> basePropertyTypeDefs = baseDatasetDef.getPropertyTypeDefs();
        //获取属性
        List<PropertyTypeDef> generalPropertyTypeDefs = infoObjectDef.getPropertyTypeDefsOfGeneralDatasets();

        List<PropertyInputBean> allProperties = new ArrayList<>();
        for (PropertyTypeDef typeDef : basePropertyTypeDefs) {
            PropertyInputBean inputBean = new PropertyInputBean();
            inputBean.setName(typeDef.getPropertyTypeName());
            inputBean.setDesc(typeDef.getPropertyTypeDesc());
            allProperties.add(inputBean);
        }

        if (generalPropertyTypeDefs != null) {
            for (PropertyTypeDef typeDef : generalPropertyTypeDefs) {
                PropertyInputBean inputBean = new PropertyInputBean();
                inputBean.setName(typeDef.getPropertyTypeName());
                inputBean.setDesc(typeDef.getPropertyTypeDesc());
                allProperties.add(inputBean);
            }
        }

        return allProperties;
    }

    public Workbook exportInstances(CIMModelCore targetCIMModelCore ,String projectId, List<RelevanceObject> relevanceObjects) {
        //对象类型定义集合
        List<InfoObjectDef> infoObjectDefs=new LinkedList<>();
        for (RelevanceObject relevanceObject:relevanceObjects) {
            InfoObjectDef infoObjectDef = targetCIMModelCore.getInfoObjectDef(relevanceObject.getObjectTypeId());
            if (infoObjectDef == null) {
                continue;
            }
            infoObjectDefs.add(infoObjectDef);
        }

        //List<PropertyInputBean> propertyInput
        //Workbook  设置样式
        Workbook wb = new HSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle dateCellStyle = wb.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
      //遍历集合 分别获取infoObjectDef  关联对象 RelevanceObject
        for (int m=0;m<relevanceObjects.size();m++) {
        //获取关联对象id
        String objectTypeId=relevanceObjects.get(m).getObjectTypeId();
        //获取对象定义
        InfoObjectDef infoObjectDef=infoObjectDefs.get(m);
        //获取关联属性  属性可以传多个 可以不传 不传默认查询所有属性

        List<Field> fields=relevanceObjects.get(m).getFields();
        //字典可以多个 可以不传 不传就没有
        List<Dict> dicts= relevanceObjects.get(m).getDicts();

        //日期格式转换  如果要转换指定格式日期必须传
        List<DateFormat> dateFormatList= relevanceObjects.get(m).getDateFormatList();

        //判断relevanceObjects 是否携带了属性 若没有则查询全部字段
        if(fields==null||fields.size()==0){
            fields= new ArrayList<>();
            //获取所有属性
            List<PropertyInputBean> properties = mergeAllProperties(infoObjectDef);
            //去除ObjectId 和ObjectName 字段
            for (PropertyInputBean propertyInputBean:properties) {
                //如果包含这两个字段 啥也不做 不包含就存储
                if("ObjectId".equals(propertyInputBean.getDesc())||"ObjectName".equals(propertyInputBean.getDesc())){

                }else{
                    Field field=new Field();
                    field.setFieldName(propertyInputBean.getName());
                    field.setDesc(propertyInputBean.getDesc());
                    fields.add(field);
                }

            }
        }
        //当前关联对象属性个数
       int columnSize = fields.size();
       //sheet命名
       Sheet sheet = wb.createSheet(infoObjectDef.getObjectTypeDesc());
       //设置首行 名称样式
       int rowNum = 0;
       Row oneRow = sheet.createRow(rowNum);
            for (int i = 0; i < columnSize; i++) {
                Field inputBean = fields.get(i);
                Cell cell = oneRow.createCell(i);
                String desc=inputBean.getDesc();
                cell.setCellValue(inputBean.getDesc());
            }

            List<String> realMeans=new LinkedList<>();
            for (Field field:fields) {
                realMeans.add(field.getFieldName());
            }

            //要根据关联对象id返回 关联对象返回object有关 的数据
            Map param= new HashMap();
            if(!"".equals(projectId)){
                param.put("projectId",projectId);
            }
            List<Map<String, Object>> dataList=queryToCim(infoObjectDef,param);

           //遍历数据
            if (CollectionUtils.isNotEmpty(dataList)) {
                //dataList数据要处理把字典值转义

                for (Map infoObject : dataList) {
                  List<Map<String, Object>> dictList=new ArrayList<>();
                  //获取数据的key
                  Set<String> params=  infoObject.keySet();
                  //获取每一个key
                    for (String param1:params) {
                        //和field 的fieldName 进行匹配 成功则获取field的字典值
                        for (Dict dict:dicts) {
                            if(param1.equals(dict.getFieldName())){
                                if(dict.getDictName()!=null){
                                GlobalDimensionType globalDimensionType = targetCIMModelCore.getGlobalDimensionTypes().getGlobalDimensionType(dict.getDictName());
                                if(globalDimensionType != null){
                                    //根据字典定义查询出字典值
                                    dictList=globalDimensionType.listItems();
                                }
                              //获取字段值 字段值不能为空
                              Object value= infoObject.get(param1);
                                if(value!=null){
                                    //判断类型 通常只有数字 字符串类型
                                    for (Map dictObj:dictList) {
                                        //相等时  dict key =001 value=类
                                       if(dictObj.get("key").equals(value)||value==dictObj.get("key")){
                                           infoObject.put(param1,dictObj.get("value"));
                                       }
                                }
                                }
                            }}
                        }
                    }
                }
                //判断是否进行日期格式化操作
                if (dateFormatList != null && dateFormatList.size() > 0) {
                for (Map infoObject : dataList) {
                        //日期格式化操作
                        //获取数据的key
                        Set<String> params=  infoObject.keySet();
                        //获取每一个key
                        for (String param1:params) {
                            for (DateFormat dateFormat : dateFormatList) {
                                if (param1.equals(dateFormat.getFieldName())) {
                                    //获取字段值 字段值不能为空  日期有可能是date 类型  或者string类型
                                    Object date = infoObject.get(param1);
                                    //date类型
                                    if (date instanceof Date) {
                                        infoObject.put(param1, formatDateTime(dateFormat.getFormat(), (Date) date));
                                    }
                                    //string类型
                                    else if (date instanceof String) {
                                        infoObject.put(param1,formatDate(dateFormat.getFormat(), (String) date));
                                    }

                                }
                            }
                        }
                    }
                }
                for (Map infoObject : dataList) {
                    rowNum++;
                    //如果有数据就创建新的行
                    oneRow = sheet.createRow(rowNum);
                    for (int i = 0; i < realMeans.size(); i++) {
                        Cell cell = oneRow.createCell(i);
                            String key= realMeans.get(i);
                            Object value = infoObject.get(realMeans.get(i));
                            if(value==null){
                                value="";
                            }
                            //把值放到cell里
                            if (value instanceof Date) {
                                cell.setCellValue((Date) value);
                                cell.setCellStyle(dateCellStyle);
                            } else if (value instanceof Boolean) {
                                cell.setCellValue((Boolean) value);
                            } else {
                                cell.setCellValue(value.toString());
                            }
                    }
                    if (rowNum >= 65536) {
                        log.info("the max row number of one sheet is 65536, others is omitted");
                        break;
                    }
                }
            }
        }
        return wb;
    }

    public static List<Map<String, Object>> queryToCim(InfoObjectDef  infoObjectDef, Map<String, Object> map) {
        ExploreParameters ep = new ExploreParameters();
        List<EqualFilteringItem> list = new ArrayList<>();
        for (String key : map.keySet()) {
            list.add(new EqualFilteringItem(key, map.get(key)));
        }
        for (int index = 0; index < list.size(); index++) {
            if (index == 0) {
                ep.setDefaultFilteringItem(list.get(index));
                continue;
            }
            ep.addFilteringItem(list.get(index), ExploreParameters.FilteringLogic.AND);
        }
        ep.setStartPage(0);
        ep.setPageSize(10000);
        InfoObjectRetrieveResult infoObjectRetrieveResult = infoObjectDef.getObjects(ep);
        List<InfoObject> infoObjectList = infoObjectRetrieveResult.getInfoObjects();

        List<Map<String, Object>> result = new ArrayList<>();
        if (infoObjectList != null && infoObjectList.size() > 0) {
            for (InfoObject infoObject : infoObjectList) {
                Map<String, Object> objectValuesMap = new HashMap<>();
                Map tmpMap = null;
                try {
                    tmpMap = infoObject.getObjectPropertiesByDatasets();
                    for (Object key : tmpMap.keySet()) {
                        objectValuesMap.putAll((Map<String, Object>) tmpMap.get(key));
                    }
                } catch (DataServiceModelRuntimeException e) {
                    e.printStackTrace();
                }
                result.add(objectValuesMap);
            }
        }
        return result;
    }

    public static String formatDateTime(String  format,Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.format(date.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    public static final String formatDate(String format, String strDate) {
        SimpleDateFormat df = null;
        Date date = null;
        df = new SimpleDateFormat(format);
        try {
            date = df.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDateTime(format,date);
    }

    public static final String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";
        if (aDate != null){
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }
        return (returnValue);
    }


}
