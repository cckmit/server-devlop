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
public class FuZhouCityComponentsTreeTypeInitiation {
    private static Logger log = LoggerFactory.getLogger(FuZhouCityComponentsTreeTypeInitiation.class);

    private static String tenantId = "1";
    private static String fuZhouCityComponentTreeObjectTypeName = "FuZhouCityComponentTree";
    private static String fuZhouCityComponentTreeObjectTypeDesc = "福州城市部件树";
    // private static String fuZhouCityComponentTreeBaseDataSet = "FuZhouCityComponentTreeDataSet";
    // private static String fuZhouCityComponentTreeBaseDataSetDesc = "福州城市部件属性集";


    public static InfoObjectDef addInheritFactType(CIMModelCore cimModelCore, CimDataSpace cimDataSpace) throws DataServiceModelRuntimeException {
        if (cimDataSpace.hasInheritFactType(fuZhouCityComponentTreeObjectTypeName)) {
            log.info("inherit fact type of {} is already exists", fuZhouCityComponentTreeObjectTypeName);
            return null;
        }

        InfoObjectTypeVO objectTypeVO = new InfoObjectTypeVO();
        objectTypeVO.setObjectTypeName(fuZhouCityComponentTreeObjectTypeName);
        objectTypeVO.setObjectTypeDesc(fuZhouCityComponentTreeObjectTypeDesc);
        objectTypeVO.setDisabled(false);

        InfoObjectDefs infoObjectDefs = cimModelCore.getInfoObjectDefs();
        return infoObjectDefs.addRootInfoObjectDef(objectTypeVO);
    }


    public static void linkDataSetWithObjectType(CIMModelCore cimModelCore, String dataSetRid) throws DataServiceModelRuntimeException {
        InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(fuZhouCityComponentTreeObjectTypeName);
        if (infoObjectDef != null) {
            List<DatasetDef> datasetDefs = infoObjectDef.getDatasetDefs();
            Set<String> dataSetRids = new HashSet<>();
            if (datasetDefs != null && datasetDefs.size() > 0) {
                for (DatasetDef def : datasetDefs) {
                    dataSetRids.add(def.getDatasetRID());
                }
            }
            if (dataSetRids.contains(dataSetRid)) {
                log.info("object type of [{}] is already link with data set of [{}]",
                        fuZhouCityComponentTreeObjectTypeName, dataSetRid);
            } else {
                infoObjectDef.linkDatasetDef(dataSetRid);
            }
        } else {
            log.error("object type of {} not found", fuZhouCityComponentTreeObjectTypeName);
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
            if (infoObjectDef != null) {
                String datasetRid = TreeNodeInfoDataSetDefinition.addMetaDataDataSet(cimDataSpace, cimSpaceName,
                        tenantId);
                linkDataSetWithObjectType(modelCore, datasetRid);
            } else {
                log.error("object type of {} add failed", fuZhouCityComponentTreeObjectTypeName);
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
    }


}
