package com.glodon.pcop.cimsvc.service.tag;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.entity.ObjectTypeEntity;
import com.glodon.pcop.cim.common.model.tag.CommonTagAddInputBean;
import com.glodon.pcop.cim.common.model.tag.CommonTagBaseInputBean;
import com.glodon.pcop.cim.common.model.tag.CommonTagOutputBean;
import com.glodon.pcop.cim.common.model.tag.CommonTagTrasnlater;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Dimension;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineDataMartException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.exception.DataServiceFeatureRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceFeature.extension.universalDimension.CommonUtil;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.CommonTagVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.*;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.UniversalDimensionAttachInfo;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimapi.exception.DataEngineException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.output.InstancesByTagOutputBean;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@Ignore
public class CommonTagServiceTest {

    private static String tennatId = "1";
    private static String userId = "1";
    private static CommonTagService commonTagServic;

    @Before
    public void setUp()
            throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";
        commonTagServic = new CommonTagService();
    }

    @Test
    public void addCommonTag()
            throws DataServiceModelRuntimeException {
        CommonTagAddInputBean tagAddInput = new CommonTagAddInputBean();
        tagAddInput.setTagName("tag_name_yuanjk_test_1101");
        tagAddInput.setTagDesc("标签名称-测试-yuanjk-1101");

        System.out.println(
                "add tag: " + JSON.toJSONString(commonTagServic.addCommonTag(tennatId, userId, tagAddInput)));
    }

    @Test
    public void updateCommonTag()
            throws DataServiceModelRuntimeException, EntityNotFoundException {
        String tagName = "tag_name_yuanjk_test_1101";
        CommonTagBaseInputBean tagAddInput = new CommonTagBaseInputBean();
        tagAddInput.setTagName("tag_name_yuanjk_test_1101");
        tagAddInput.setTagDesc("标签名称-测试-yuanjk-1101-更新");

        System.out.println(
                "update tag: " + JSON.toJSONString(
                        commonTagServic.updateCommonTag(tennatId, userId, tagName, tagAddInput)));

    }

    @Test
    public void deleteCommonTag()
            throws EntityNotFoundException {
        String tagName = "tag_name_yuanjk_test_1101";

        System.out.println(
                "delete tag: " + JSON.toJSONString(commonTagServic.deleteCommonTag(tennatId, userId, tagName)));
    }

    @Test
    public void getCommonTag()
            throws EntityNotFoundException {
        String tagName = "cityComponentMonitoring";

        System.out.println(
                "get by tagName: " + JSON.toJSONString(commonTagServic.getCommonTag(tennatId, userId, tagName)));
    }

    @Test
    public void getParentCommonTag() {
    }

    @Test
    public void getChildCommonTags() {
    }

    @Test
    public void getCommonTagsByDataSetRid()
            throws CimDataEngineRuntimeException, DataEngineException, CimDataEngineDataMartException,
            DataServiceModelRuntimeException {
        String dataSetRid = "#137:335";
        String relationType = "DATASET_COMMONTAG";

        attachTagsToDataSet(relationType, dataSetRid);

        System.out.println(
                "get by dataset rid: " + JSON.toJSONString(
                        commonTagServic.getCommonTagsByDataSetRid(tennatId, userId, dataSetRid, relationType,
                                RelationDirection.TWO_WAY)));
    }

    private void attachTagsToDataSet(String relationType, String dataSetRid)
            throws CimDataEngineDataMartException, DataServiceModelRuntimeException {
        UniversalDimensionAttachInfo attachInfo = new UniversalDimensionAttachInfo(relationType, RelationDirection.TO,
                null);

        String dataSetName = "tag_name_yuanjk_test_1101_";
        String dataSetDesc = "标签名称-测试-yuanjk-1101_";

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tennatId);

            if (!cds.hasRelationType(relationType)) {
                cds.addRelationType(relationType);
            }

            modelCore.setCimDataSpace(cds);
            CommonTags commonTags = modelCore.getCommonTags();
            for (int i = 3; i < 6; i++) {
                CommonTagVO tagVO = new CommonTagVO();
                tagVO.setTagName(dataSetName + i);
                tagVO.setTagDesc(dataSetDesc + i);

                CommonTag commonTag = commonTags.addTag(tagVO);

                commonTag.attachDatasetDef(attachInfo, dataSetRid);
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }


    @Test
    public void getCommonTagsByObjectType()
            throws CimDataEngineRuntimeException, DataEngineException, CimDataEngineInfoExploreException,
            DataServiceModelRuntimeException, CimDataEngineDataMartException, DataServiceFeatureRuntimeException,
            EntityNotFoundException {
        String objectTypeId = "tag_test_object_type_1101";
        String relationType = "DATASET_COMMONTAG";

        attachTagsToObjectType(relationType, objectTypeId);

        System.out.println(
                "get by dataset rid: " + JSON.toJSONString(
                        commonTagServic.getCommonTagsByObjectType(tennatId, userId, objectTypeId, relationType,
                                RelationDirection.TWO_WAY)));
    }

    private void attachTagsToObjectType(String relationType, String objectTypeId)
            throws CimDataEngineDataMartException, DataServiceModelRuntimeException, CimDataEngineRuntimeException,
            CimDataEngineInfoExploreException, DataServiceFeatureRuntimeException {
        RelationDirection relationDirection = RelationDirection.TO;

        String dataSetName = "tag_name_yuanjk_test_1101_";
        String dataSetDesc = "标签名称-测试-yuanjk-1101_";

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tennatId);
            modelCore.setCimDataSpace(cds);

            if (!cds.hasRelationType(relationType)) {
                cds.addRelationType(relationType);
            }

            FilteringItem equalFilter = new EqualFilteringItem("infoObjectTypeName", objectTypeId);
            ExploreParameters ep = new ExploreParameters();
            ep.setDefaultFilteringItem(equalFilter);
            ep.setType(BusinessLogicConstant.INFO_OBJECT_TYPE_STATUS_FACT_TYPE_NAME);

            List<Fact> factList = cds.getInformationExplorer().discoverFacts(ep);
            Fact objectTypeStatuFact = factList.get(0);
            CommonTags commonTags = modelCore.getCommonTags();
            for (int i = 6; i < 9; i++) {
                CommonTagVO tagVO = new CommonTagVO();
                tagVO.setTagName(dataSetName + i);
                tagVO.setTagDesc(dataSetDesc + i);

                CommonTag commonTag = commonTags.addTag(tagVO);
                Relation resultRelation = CommonUtil.attachToRelationable(cds, commonTag.getTagRID(), relationType,
                        relationDirection, null, objectTypeStatuFact);
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    @Test
    public void getObjectTypesByCommonTag()
            throws DataServiceModelRuntimeException, CimDataEngineDataMartException, EntityNotFoundException {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tennatId);
            modelCore.setCimDataSpace(cds);

//            String tagName = addCommonTagAndAttachObjectTypes(cds, modelCore);
            String tagName = "tag_name_yuanjk_test_1583998917125";

            List<ObjectTypeEntity> objectTypeEntityList = commonTagServic.getObjectTypesByCommonTag(tennatId, "1",
                    tagName, "test_tag_object_relation_type", RelationDirection.FROM);
            System.out.println("object types: [" + JSON.toJSONString(objectTypeEntityList) + "]");
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }

    public String addCommonTagAndAttachObjectTypes(CimDataSpace cds, CIMModelCore modelCore)
            throws DataServiceModelRuntimeException, CimDataEngineDataMartException {
        CommonTagAddInputBean tagAddInput = new CommonTagAddInputBean();
        long currentTimeMillis = System.currentTimeMillis();
        tagAddInput.setTagName("tag_name_yuanjk_test_" + currentTimeMillis);
        tagAddInput.setTagDesc("标签名称-测试-yuanjk_" + currentTimeMillis);

        if (!cds.hasDimensionType(BusinessLogicConstant.CIM_TAG_DIMENSION_TYPE_NAME)) {
            cds.addDimensionType(BusinessLogicConstant.CIM_TAG_DIMENSION_TYPE_NAME);
        }
        CommonTags commonTags = modelCore.getCommonTags();
        CommonTag commonTag = commonTags.addTag(CommonTagTrasnlater.updateInputBeanToVo(tagAddInput));
        CommonTagOutputBean tagOutputBean = CommonTagTrasnlater.addOutputVoToBean(commonTag);
        System.out.println("add tag: " + JSON.toJSONString(tagOutputBean));

        String relationType = "test_tag_object_relation_type";
        RelationDirection direction = RelationDirection.FROM;
        UniversalDimensionAttachInfo dimensionAttachInfo = new UniversalDimensionAttachInfo(relationType, direction,
                null);

        if (!cds.hasRelationType(relationType)) {
            cds.addRelationType(relationType);
        }
        String objectTypeIdPrefix = "test_tag_object_type_id_";
        String objectTypeNamePrefix = "标签关联对象类型测试-";
        InfoObjectDefs infoObjectDefs = modelCore.getInfoObjectDefs();
        for (int i = 0; i < 3; i++) {
            InfoObjectTypeVO typeVO = new InfoObjectTypeVO();
            typeVO.setObjectTypeName(objectTypeIdPrefix + i);
            typeVO.setObjectTypeDesc(objectTypeNamePrefix + i);
            InfoObjectDef infoObjectDef = infoObjectDefs.addRootInfoObjectDef(typeVO);
            System.out.println("object type: " + JSON.toJSON(typeVO));
            infoObjectDef.attachCommonTag(dimensionAttachInfo, commonTag.getTagRID());
        }
        return commonTag.getTagName();
    }


    @Test
    public void getInstanceByCommonTag()
            throws CimDataEngineRuntimeException, CimDataEngineDataMartException, EntityNotFoundException {
        String tagName = "tag_name_yuanjk_test_1583998917125";
        String relationType = "test_tag_instance_relation_type";
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tennatId);
            modelCore.setCimDataSpace(cds);

//            attachTagToInstances(cds, modelCore, tagName);
            List<InstancesByTagOutputBean> instancesByTagOutputBeanList = commonTagServic.getInstanceByCommonTag(
                    tennatId, "", tagName, relationType, RelationDirection.TO);

            System.out.println("instances: " + JSON.toJSONString(instancesByTagOutputBeanList));
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    public void attachTagToInstances(CimDataSpace cds, CIMModelCore modelCore, String tagName)
            throws CimDataEngineRuntimeException, CimDataEngineDataMartException, EntityNotFoundException {

        CommonTagOutputBean tagOutputBean = commonTagServic.getCommonTag(tennatId, "", tennatId);

        Dimension tagDimension = cds.getDimensionById(tagOutputBean.getId());

        String relationType = "test_tag_instance_relation_type";
        if (!cds.hasRelationType(relationType)) {
            cds.addRelationType(relationType);
        }
        String objectTypeId = "china_points_0225";

        InfoObjectDef infoObjectDef = modelCore.getInfoObjectDef(objectTypeId);
        ExploreParameters ep = new ExploreParameters();
        ep.setStartPage(1);
        ep.setEndPage(2);
        ep.setPageSize(6);

        List<InfoObject> infoObjectList = infoObjectDef.getObjects(ep).getInfoObjects();
        for (InfoObject infoObject : infoObjectList) {
            String instanceRid = infoObject.getObjectInstanceRID();
            System.out.println("instance rid: " + instanceRid);
            tagDimension.addFromRelation(cds.getFactById(instanceRid), relationType);
        }
    }
}