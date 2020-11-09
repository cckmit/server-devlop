package com.glodon.pcop.spacialimportsvc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.RelationshipLinkLogicVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.RelationshipMappingVO;
import com.glodon.sde.fileParser.FileDataImport;
import com.google.gson.Gson;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CimServiceTest {

    static String mappingStr = "{\"propertyMappingBean\": {\"fileId\": \"building-20180930010638.zip\",\"typeId\": \"aa\",\"dataSetMapping\": [{\"dataSetId\": \"#137:0\",\"dataSetName\": \"CIM_BUILDIN_BASE_DATASET_NAME\",\"propertyMapping\": [{\"id\": \"#161:0\",\"name\": \"objecttypeID\",\"targetName\": \"barrier\"},{\"id\": \"#162:0\",\"name\": \"creator\",\"targetName\": \"landuse\"},{\"id\": \"#163:0\",\"name\": \"createtime\",\"targetName\": \"place\"},{\"id\": \"#164:0\",\"name\": \"updatetime\",\"targetName\": \"place\"},{\"id\": \"#165:0\",\"name\": \"x\",\"targetName\": \"historic\"},{\"id\": \"#166:0\",\"name\": \"y\",\"targetName\": \"historic\"},{\"id\": \"#167:0\",\"name\": \"z\",\"targetName\": \"osm_id\"},{\"id\": \"#168:0\",\"name\": \"angle\",\"targetName\": \"osm_id\"},{\"id\": \"#161:1\",\"name\": \"scaling\",\"targetName\": \"natural\"},{\"id\": \"#162:1\",\"name\": \"comment\",\"targetName\": \"natural\"}]}]},\"fileType\": \"SHP\"}";


    @Test
    public void shpImporttest() {
        Gson gson = new Gson();
        PropertyMappingBean mappingBean = gson.fromJson(mappingStr, PropertyMappingBean.class);
        System.out.println(gson.toJson(mappingStr));
    }

    public static void main(String[] args) {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();

        Gson gson = new Gson();
//        PropertyMappingBean mappingBean = gson.fromJson(mappingStr, PropertyMappingBean.class);
        ImportMappingBean importMappingBean = gson.fromJson(mappingStr, ImportMappingBean.class);

        System.out.println(gson.toJson(mappingStr));

        System.out.println("fileId=" + importMappingBean.getPropertyMappingBean().getFileId());

        FileDataImport fileDataImport = new FileDataImport();

        fileDataImport.fileImport(importMappingBean);
        System.out.println("import end!");

    }


    @Test
    public void gsonObject() throws IOException {
        String str = "{\"fileId\":\"building-20180930024745.zip\",\"typeId\":\"ffff\"}";
//        Gson gson = new Gson();
//
//        JsonElement jsonElement = gson.toJsonTree(str);
//
//        System.out.println(jsonElement.toString());
//
//        System.out.println(jsonElement.isJsonObject());
//
//        System.out.println(jsonElement.isJsonPrimitive());
//        System.out.println(jsonElement.getAsJsonPrimitive());
//

        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(str);

        System.out.println(node.has("fileId"));
        System.out.println(node.has("propertyMappingBean"));

    }

    @Test
    public void getExcelFileStructure() throws IOException {
        String filePath = "src/test/resources/relationship_source_file.xlsx";

        System.out.println();

        FileDataImport dataImport = new FileDataImport();
        System.out.println(dataImport.getExcelFileStructure(filePath));
    }


    // @Test
    public void excelImportTest() {
        //difine PropMapping list
        List<PropMapping> propertyMappingList = new ArrayList<>();
        String targetName = "ID";
        String name = "ID";
        PropMapping propMapping0 = new PropMapping();
        propMapping0.setTargetName(targetName);
        propMapping0.setName(name);

        targetName = "NAME";
        name = "NAME";
        PropMapping propMapping1 = new PropMapping();
        propMapping1.setTargetName(targetName);
        propMapping1.setName(name);

        targetName = "TYPE";
        name = "type";
        PropMapping propMapping2 = new PropMapping();
        propMapping2.setTargetName(targetName);
        propMapping2.setName(name);

        targetName = "TAG_ID";
        name = "tag_id";
        PropMapping propMapping3 = new PropMapping();
        propMapping3.setTargetName(targetName);
        propMapping3.setName(name);

        propertyMappingList.add(propMapping0);
        propertyMappingList.add(propMapping1);
        propertyMappingList.add(propMapping2);
        propertyMappingList.add(propMapping3);

        //difine DataSetMapping
        String dataSetName = "自定义属性集";
        DataSetMapping dataSetMapping = new DataSetMapping();
        dataSetMapping.setDataSetName(dataSetName);
        dataSetMapping.setPropertyMapping(propertyMappingList);

        List<DataSetMapping> dataSetMappingList = new ArrayList<>();
        dataSetMappingList.add(dataSetMapping);
        //difine PropertyMappingBean
        String fileId = "src/test/resources/relationship_source_file.xlsx";
        String objectTypeName = "relationship_source_object_a";
        PropertyMappingBean propertyMappingBean = new PropertyMappingBean();
        propertyMappingBean.setFileId(fileId);
        propertyMappingBean.setTypeId(objectTypeName);
        propertyMappingBean.setDataSetMapping(dataSetMappingList);

        //difine RelationshipLinkLogicVO
        String sourceProperty = "tag_id";
        String targetProperty = "ID";
        String linkLogic = "EQUAL";
        String compositeLogic = "DEFAULT";
        RelationshipLinkLogicVO linkLogicVO = new RelationshipLinkLogicVO();
        linkLogicVO.setCompositeLogic(compositeLogic);
        linkLogicVO.setLinkLogic(linkLogic);
        linkLogicVO.setSourceProperty(sourceProperty);
        linkLogicVO.setTargetProperty(targetProperty);

        List<RelationshipLinkLogicVO> linkLogicList = new ArrayList<>();
        linkLogicList.add(linkLogicVO);

        //define RelationshipMappingVO
        String sourceInfoObjectType = objectTypeName;
        String targetInfoObjectType = "relationship_target_object_a";
        String relationTypeName = "BUSINESS_BUILDIN_RELATIONTYPE_SpaceContainedBy";
        String relationTypeDesc = sourceInfoObjectType + "/" + targetInfoObjectType;
        String tenantId = "1";

        RelationshipMappingVO relationshipMappingVO = new RelationshipMappingVO();
        relationshipMappingVO.setRelationTypeName(relationTypeName);
        relationshipMappingVO.setRelationshipDesc(relationTypeDesc);
        relationshipMappingVO.setSourceInfoObjectType(sourceInfoObjectType);
        relationshipMappingVO.setTargetInfoObjectType(targetInfoObjectType);
        relationshipMappingVO.setTenantId(tenantId);
        relationshipMappingVO.setLinkLogic(linkLogicList);

        //define ImportMappingBean
        String fileType = "XLS";
        ImportMappingBean importMappingBean = new ImportMappingBean();
        importMappingBean.setFileType(fileType);
        importMappingBean.setRelationshipMappingVO(relationshipMappingVO);
        importMappingBean.setPropertyMappingBean(propertyMappingBean);

        FileDataImport dataImport = new FileDataImport();
        dataImport.excelDataImport(importMappingBean);

    }

}