package com.glodon.pcop.cimsvc.util.tree;

import com.glodon.pcop.cim.common.model.entity.PropertyEntity;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.PropertyTypeRestrictFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeRestrictVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.PropertyTypeDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.PropertyTypeDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.optionalInit.TreeNodeInfoDataSetDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 青岛城市部件
 */
public class QingdaoCityComponentsTreeTypeInitiation {
    private static Logger log = LoggerFactory.getLogger(QingdaoCityComponentsTreeTypeInitiation.class);

    private static String tenantId = "2";
    private static String qingDaoCityComponentTreeObjectTypeName = "QingDaoCityComponentTree";
    private static String qingDaoCityComponentTreeObjectTypeDesc = "青岛城市部件树";
    private static String qingDaoCityComponentTreeBaseDataSet = "QingDaoCityComponentTreeDataSet";
    private static String qingDaoCityComponentTreeBaseDataSetDesc = "青岛城市部件属性集";


    public static InfoObjectDef addInheritFactType(CIMModelCore cimModelCore, CimDataSpace cimDataSpace) throws DataServiceModelRuntimeException {
        if (cimDataSpace.hasInheritFactType(qingDaoCityComponentTreeObjectTypeName)) {
            log.info("inherit fact type of {} is already exists", qingDaoCityComponentTreeObjectTypeName);
            return null;
        }

        InfoObjectTypeVO objectTypeVO = new InfoObjectTypeVO();
        objectTypeVO.setObjectTypeName(qingDaoCityComponentTreeObjectTypeName);
        objectTypeVO.setObjectTypeDesc(qingDaoCityComponentTreeObjectTypeDesc);
        objectTypeVO.setDisabled(false);

        InfoObjectDefs infoObjectDefs = cimModelCore.getInfoObjectDefs();
        return infoObjectDefs.addRootInfoObjectDef(objectTypeVO);
    }

    public static DatasetDef addMetaDataDataSet(CimDataSpace cimDataSpace, String cimDataSpaceName) throws DataServiceModelRuntimeException, CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        ExploreParameters exploreParameters = new ExploreParameters();
        exploreParameters.setType(BusinessLogicConstant.DATASET_FACT_TYPE_NAME);
        FilteringItem filteringItem = new EqualFilteringItem(BusinessLogicConstant.DATASET_FACT_FIELDNAME_NAME,
                qingDaoCityComponentTreeBaseDataSet);
        exploreParameters.setDefaultFilteringItem(filteringItem);

        InformationExplorer informationExplorer = cimDataSpace.getInformationExplorer();
        List<Fact> facts = informationExplorer.discoverFacts(exploreParameters);

        if (facts != null && facts.size() > 0) {
            log.info("data set of {} is already exists", qingDaoCityComponentTreeBaseDataSet);
            return null;
        }

        List<PropertyTypeVO> linkedProperties = new ArrayList<>();

        PropertyTypeVO pro1 = new PropertyTypeVO();
        pro1.setPropertyTypeName("dataName");
        pro1.setPropertyTypeDesc("数据名称");
        pro1.setPropertyFieldDataClassify(PropertyEntity.DataTypes.STRING.toString());

        PropertyTypeRestrictVO restrictVO1 = new PropertyTypeRestrictVO();
        restrictVO1.setNull(false);
        restrictVO1.setPrimaryKey(false);
        restrictVO1.setDefaultValue("-");
        pro1.setRestrictVO(restrictVO1);
        linkedProperties.add(pro1);

        PropertyTypeVO pro2 = new PropertyTypeVO();
        pro2.setPropertyTypeName("comment");
        pro2.setPropertyTypeDesc("备注");
        pro2.setPropertyFieldDataClassify(PropertyEntity.DataTypes.STRING.toString());

        PropertyTypeRestrictVO restrictBean2 = new PropertyTypeRestrictVO();
        restrictBean2.setNull(false);
        restrictBean2.setPrimaryKey(false);
        restrictBean2.setDefaultValue("-");
        pro2.setRestrictVO(restrictBean2);
        linkedProperties.add(pro2);

        PropertyTypeVO pro3 = new PropertyTypeVO();
        pro3.setPropertyTypeName("nodeType");
        pro3.setPropertyTypeDesc("节点类型");
        pro3.setPropertyFieldDataClassify(PropertyEntity.DataTypes.STRING.toString());

        PropertyTypeRestrictVO restrictBean3 = new PropertyTypeRestrictVO();
        restrictBean3.setNull(false);
        restrictBean3.setPrimaryKey(false);
        restrictBean3.setDefaultValue("-");
        pro3.setRestrictVO(restrictBean3);
        linkedProperties.add(pro3);

        PropertyTypeVO pro4 = new PropertyTypeVO();
        pro4.setPropertyTypeName("submissionUnit");
        pro4.setPropertyTypeDesc("提交单位");
        pro4.setPropertyFieldDataClassify(PropertyEntity.DataTypes.STRING.toString());

        PropertyTypeRestrictVO restrictBean4 = new PropertyTypeRestrictVO();
        restrictBean4.setNull(false);
        restrictBean4.setPrimaryKey(false);
        restrictBean4.setDefaultValue("-");
        pro4.setRestrictVO(restrictBean4);
        linkedProperties.add(pro4);

        PropertyTypeVO pro5 = new PropertyTypeVO();
        pro5.setPropertyTypeName("createTime");
        pro5.setPropertyTypeDesc("创建时间");
        pro5.setPropertyFieldDataClassify(PropertyEntity.DataTypes.DATE.toString());

        PropertyTypeRestrictVO restrictBean5 = new PropertyTypeRestrictVO();
        restrictBean5.setNull(false);
        restrictBean5.setPrimaryKey(false);
        restrictBean5.setDefaultValue("-");
        pro5.setRestrictVO(restrictBean5);
        linkedProperties.add(pro5);

        PropertyTypeVO pro6 = new PropertyTypeVO();
        pro6.setPropertyTypeName("updateTime");
        pro6.setPropertyTypeDesc("更新时间");
        pro6.setPropertyFieldDataClassify(PropertyEntity.DataTypes.DATE.toString());

        PropertyTypeRestrictVO restrictBean6 = new PropertyTypeRestrictVO();
        restrictBean6.setNull(false);
        restrictBean6.setPrimaryKey(false);
        restrictBean6.setDefaultValue("-");
        pro6.setRestrictVO(restrictBean6);
        linkedProperties.add(pro6);

        PropertyTypeVO pro7 = new PropertyTypeVO();
        pro7.setPropertyTypeName("creator");
        pro7.setPropertyTypeDesc("创建人ID");
        pro7.setPropertyFieldDataClassify(PropertyEntity.DataTypes.STRING.toString());

        PropertyTypeRestrictVO restrictBean7 = new PropertyTypeRestrictVO();
        restrictBean7.setNull(false);
        restrictBean7.setPrimaryKey(false);
        restrictBean7.setDefaultValue("-");
        pro7.setRestrictVO(restrictBean7);
        linkedProperties.add(pro7);

        PropertyTypeVO pro8 = new PropertyTypeVO();
        pro8.setPropertyTypeName("updator");
        pro8.setPropertyTypeDesc("更新人ID");
        pro8.setPropertyFieldDataClassify(PropertyEntity.DataTypes.STRING.toString());

        PropertyTypeRestrictVO restrictBean8 = new PropertyTypeRestrictVO();
        restrictBean8.setNull(false);
        restrictBean8.setPrimaryKey(false);
        restrictBean8.setDefaultValue("-");
        pro8.setRestrictVO(restrictBean8);
        linkedProperties.add(pro8);

        PropertyTypeVO pro9 = new PropertyTypeVO();
        pro9.setPropertyTypeName("contact");
        pro9.setPropertyTypeDesc("联系人");
        pro9.setPropertyFieldDataClassify(PropertyEntity.DataTypes.STRING.toString());

        PropertyTypeRestrictVO restrictBean9 = new PropertyTypeRestrictVO();
        restrictBean9.setNull(false);
        restrictBean9.setPrimaryKey(false);
        restrictBean9.setDefaultValue("-");
        pro9.setRestrictVO(restrictBean9);
        linkedProperties.add(pro9);

        PropertyTypeVO pro10 = new PropertyTypeVO();
        pro10.setPropertyTypeName("contactNumber");
        pro10.setPropertyTypeDesc("联系电话");
        pro10.setPropertyFieldDataClassify(PropertyEntity.DataTypes.STRING.toString());

        PropertyTypeRestrictVO restrictBean10 = new PropertyTypeRestrictVO();
        restrictBean10.setNull(false);
        restrictBean10.setPrimaryKey(false);
        restrictBean10.setDefaultValue("-");
        pro10.setRestrictVO(restrictBean10);
        linkedProperties.add(pro10);

        PropertyTypeVO pro11 = new PropertyTypeVO();
        pro11.setPropertyTypeName("srcFileName");
        pro11.setPropertyTypeDesc("源文件名称");
        pro11.setPropertyFieldDataClassify(PropertyEntity.DataTypes.STRING.toString());

        PropertyTypeRestrictVO restrictBean11 = new PropertyTypeRestrictVO();
        restrictBean11.setNull(false);
        restrictBean11.setPrimaryKey(false);
        restrictBean11.setDefaultValue("-");
        pro11.setRestrictVO(restrictBean11);
        linkedProperties.add(pro11);

        PropertyTypeVO pro12 = new PropertyTypeVO();
        pro12.setPropertyTypeName("fileType");
        pro12.setPropertyTypeDesc("源文件类型");
        pro12.setPropertyFieldDataClassify(PropertyEntity.DataTypes.STRING.toString());

        PropertyTypeRestrictVO restrictBean12 = new PropertyTypeRestrictVO();
        restrictBean12.setNull(false);
        restrictBean12.setPrimaryKey(false);
        restrictBean12.setDefaultValue("-");
        pro12.setRestrictVO(restrictBean12);
        linkedProperties.add(pro12);

        DatasetVO datasetVO = new DatasetVO();
        datasetVO.setDatasetName(qingDaoCityComponentTreeBaseDataSet);
        datasetVO.setDatasetDesc(qingDaoCityComponentTreeBaseDataSetDesc);
        datasetVO.setDatasetStructure(DatasetDef.DatasetStructure.SINGLE);
        datasetVO.setDataSetType(BusinessLogicConstant.DatasetType.INSTANCE);
        datasetVO.setDatasetClassify("通用属性集");
        datasetVO.setInheritDataset(false);
        datasetVO.setHasDescendant(false);

        datasetVO.setLinkedPropertyTypes(linkedProperties);

        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(cimDataSpaceName, tenantId);
        //新增属性集定义
        DatasetDefs datasetDefs = cimModelCore.getDatasetDefs();
        DatasetDef datasetDef = datasetDefs.addDatasetDef(datasetVO);
        // DatasetDef datasetDef = datasetDefs.getDatasetDef("#140:58");

        if (datasetVO.getLinkedPropertyTypes() != null) {
            //新增属性集关联的属性
            PropertyTypeDefs propertyTypeDefs = cimModelCore.getPropertyTypeDefs();
            for (PropertyTypeVO propertyTypeVO : datasetVO.getLinkedPropertyTypes()) {
                PropertyTypeDef tmpPropertyTypeDef = propertyTypeDefs.addPropertyTypeDef(propertyTypeVO);
                if (tmpPropertyTypeDef != null) {
                    //属性与属性集关联
                    datasetDef.addPropertyTypeDef(tmpPropertyTypeDef);
                    //新增属性关联的限制条件
                    if (propertyTypeVO.getRestrictVO() != null) {
                        PropertyTypeRestrictVO restrictVO = propertyTypeVO.getRestrictVO();
                        restrictVO.setDatasetId(datasetDef.getDatasetRID());
                        restrictVO.setPropertyTypeId(tmpPropertyTypeDef.getPropertyTypeRID());
                        PropertyTypeRestrictFeatures.addRestrictToDataSet(cimDataSpaceName,
                                datasetDef.getDatasetRID(), restrictVO);
                    }
                } else {
                    log.error("property add failed: {}", propertyTypeVO.getPropertyTypeName());
                }
            }
        } else {
            log.error("data set add failed: {}", datasetVO.getDatasetName());
        }
        return datasetDef;
    }

    public static void linkDataSetWithObjectType(CIMModelCore cimModelCore, String dataSetRid) throws DataServiceModelRuntimeException {
        InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(qingDaoCityComponentTreeObjectTypeName);
        if (infoObjectDef != null) {
            List<DatasetDef> datasetDefs = infoObjectDef.getDatasetDefs();
            Set<String> dataSetRids = new HashSet<>();
            if (datasetDefs != null && datasetDefs.size() > 0) {
                for (DatasetDef def : datasetDefs) {
                    dataSetRids.add(def.getDatasetRID());
                }
            }
            if (dataSetRids.contains(dataSetRid)) {
                log.info("object type of {} is already link with data set of {}",
                        qingDaoCityComponentTreeObjectTypeName,
                        qingDaoCityComponentTreeBaseDataSet);
            } else {
                infoObjectDef.linkDatasetDef(dataSetRid);
            }
        } else {
            log.error("object type of {} not found", qingDaoCityComponentTreeObjectTypeName);
        }
    }

    public static InfoObjectDef dataMagagerTreeInitiation(String cimSpaceName) {
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(cimSpaceName, tenantId);
        CimDataSpace cimDataSpace = null;
        InfoObjectDef infoObjectDef = null;
        try {
            cimDataSpace = CimDataEngineComponentFactory.connectInfoDiscoverSpace(cimSpaceName);
            modelCore.setCimDataSpace(cimDataSpace);
            infoObjectDef = addInheritFactType(modelCore, cimDataSpace);
            //只有树基本属性集
            // DatasetDef datasetDef = addMetaDataDataSet(cimDataSpace, cimSpaceName);
            // if (datasetDef != null) {
            //     linkDataSetWithObjectType(modelCore, datasetDef.getDatasetRID());
            // } else {
            //     log.error("data set add failed");
            // }
            if (infoObjectDef != null) {
                String datasetRid = TreeNodeInfoDataSetDefinition.addMetaDataDataSet(cimDataSpace, cimSpaceName,
                        tenantId);
                linkDataSetWithObjectType(modelCore, datasetRid);
            } else {
                log.error("object type of {} add failed", qingDaoCityComponentTreeObjectTypeName);
            }
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cimDataSpace != null) {
                cimDataSpace.closeSpace();
            }
        }
        return infoObjectDef;
    }

    public static void main(String[] args) {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        dataMagagerTreeInitiation("pcopcim");
        // CimDataSpace cds = null;
        // String dbName = "pcopcim";
        // try {
        //     cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(dbName);
        //     addMetaDataDataSet(cds, dbName);
        // } catch (CimDataEngineInfoExploreException e) {
        //     e.printStackTrace();
        // } catch (DataServiceModelRuntimeException e) {
        //     e.printStackTrace();
        // } catch (CimDataEngineRuntimeException e) {
        //     e.printStackTrace();
        // } finally {
        //     if (cds != null) {
        //         cds.closeSpace();
        //     }
        // }
    }


}
