package com.glodon.pcop.cimsvc.util;

import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Dimension;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.InheritFactType;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.InfoObjectFeatures;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class TenantUtil {
    private static final Logger log = LoggerFactory.getLogger(TenantUtil.class);

    private static String tenantId = "1";
    private static String dataSpaceName = "pcopcim";
    // private static String dataSpaceName = "test";
    // private static String dataSpaceName = "yuanjk";

    /**
     * 将指定对象类型下的实例与租户关联起来
     *
     * @param cimDataSpace
     * @param objectTypeId
     * @throws CimDataEngineRuntimeException
     * @throws CimDataEngineInfoExploreException
     */
    public static void linkAllFactWithTenanByObjectType(CimDataSpace cimDataSpace, String objectTypeId) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        Dimension cimTenantDimension = CommonOperationUtil.getCimTenantDimensionById(cimDataSpace, tenantId);

        if (cimDataSpace.hasInheritFactType(objectTypeId)) {
            ExploreParameters exploreParameters = new ExploreParameters();
            exploreParameters.setType(objectTypeId);

            InformationExplorer informationExplorer = cimDataSpace.getInformationExplorer();
            List<Fact> factList = informationExplorer.discoverInheritFacts(exploreParameters);
            if (factList != null && factList.size() > 0) {
                for (Fact fact : factList) {
                    // List<Relation> relationList = fact.getAllSpecifiedRelations(BusinessLogicConstant
                    // .RELATION_TYPE_BELONGS_TO_TENANT, RelationDirection.FROM);
                    boolean isInValidTenant = CommonOperationUtil.isTenantContainsData(tenantId, fact);
                    // if (relationList != null && relationList.size() > 0) {
                    if (isInValidTenant) {
                        log.info("{} is linked to {}", fact.getId(), tenantId);
                        // CommonOperationUtil.addToBelongingTenant(cimTenantDimension, fact);
                    } else {
                        log.info("{} is alone", fact.getId());
                        CommonOperationUtil.addToBelongingTenant(cimDataSpace, cimTenantDimension, fact);
                    }
                }
            }
        } else {
            log.info("inheritent object type of {} not found", objectTypeId);
        }
    }

    public static void listAllSubObject(CimDataSpace cimDataSpace, String objectTypeId) {
        if (cimDataSpace.hasInheritFactType(objectTypeId)) {
            InheritFactType inheritFactType = cimDataSpace.getInheritFactType(objectTypeId);
            List<InheritFactType> inheritFactTypeList = inheritFactType.getDescendantFactTypes();
            if (inheritFactTypeList != null && inheritFactTypeList.size() > 0) {
                for (InheritFactType factType : inheritFactTypeList) {
                    log.info("sub object type of {}: {}", objectTypeId, factType.getTypeName());
                }
            } else {
                log.info("{} does not has sub object type", objectTypeId);
            }
        } else {
            log.error("inherit type of {} not found", objectTypeId);
        }
    }


    public static void linkObjectTypeToTenant(CimDataSpace cds, String objectTypeId, String tenantId) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        Fact targetCimObjectTypeFact = InfoObjectFeatures.getInfoObjectTypeStatusRecord(cds, objectTypeId);
        Dimension cimTenantDimension = CommonOperationUtil.getCimTenantDimensionById(cds, tenantId);
        if (targetCimObjectTypeFact != null) {
            boolean isInValidTenant = CommonOperationUtil.isTenantContainsData(tenantId, targetCimObjectTypeFact);
            if (isInValidTenant) {
                log.info("fact type of {} has linked to tenant of {}", targetCimObjectTypeFact.getId(), tenantId);
            } else {
                CommonOperationUtil.addToBelongingTenant(cds, cimTenantDimension, targetCimObjectTypeFact);
                log.info("link fact type of {} to tenant of {}", targetCimObjectTypeFact.getId(), tenantId);
            }
        } else {
            log.error("object type status of {} not found", objectTypeId);
        }
    }

    public static void unlinkObjectTypeToTenant(CimDataSpace cds, String objectTypeId, String tenantId) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {//NOSONAR
        Fact targetCimObjectTypeFact = InfoObjectFeatures.getInfoObjectTypeStatusRecord(cds, objectTypeId);
        if (targetCimObjectTypeFact != null) {
            boolean isInValidTenant = CommonOperationUtil.isTenantContainsData(tenantId, targetCimObjectTypeFact);
            if (isInValidTenant) {
                List<Relation> relationList =
                        targetCimObjectTypeFact.getAllSpecifiedRelations(BusinessLogicConstant.RELATION_TYPE_BELONGS_TO_TENANT, RelationDirection.FROM);
                if (relationList != null && relationList.size() > 0) {
                    for (Relation relation : relationList) {
                        Dimension tenantDimension = (Dimension) relation.getToRelationable();
                        if (tenantDimension != null && tenantDimension.getType().equals(BusinessLogicConstant.CIM_TENANT_DIMENSION_TYPE_NAME)) {
                            if (tenantDimension.getProperty("CIM_BUILDIN_TENANT_ID").getPropertyValue().toString().equals(tenantId)) {
                                cds.removeRelation(relation.getId());
                                log.info("relation of {} is removed", relation.getId());
                            } else {
                                log.info("relation of {} should bot be removed", relation.getId());
                            }
                        } else {
                            log.error("relation to node type error, {}", tenantDimension.getType());
                        }
                    }
                }
            } else {
                log.info("fact type of {} is not link to tenant of {}", targetCimObjectTypeFact.getId(), tenantId);
            }
        } else {
            log.error("object type status of {} not found", objectTypeId);
        }
    }

    public static void linkFactToTenant(CimDataSpace cds, List<String> rids) throws CimDataEngineRuntimeException,
            CimDataEngineInfoExploreException {
        Dimension cimTenantDimension = CommonOperationUtil.getCimTenantDimensionById(cds, tenantId);
        for (String rid : rids) {
            Fact fact;
            try {
                fact = cds.getFactById(rid);
                boolean isInValidTenant = CommonOperationUtil.isTenantContainsData(tenantId, fact);
                if (isInValidTenant) {
                    log.info("{} is already linked to {}", fact.getId(), tenantId);
                } else {
                    log.info("{} is alone", fact.getId());
                    CommonOperationUtil.addToBelongingTenant(cds, cimTenantDimension, fact);
                }
            } catch (CimDataEngineRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(dataSpaceName);
            // String[] objectTypes = {"qualityProblemSum", "qualityProblemByDutyperson",
            // "qualityProblemBySubconstrator"
            //         , "qualityProblemTrendByType", "qualityProblemTrend", "qualityProblem", "problemRectification",
            //         "problemReview", "qualityProblemType", "safetyProblemSum", "safetyProblemByDutyperson",
            //         "safetyProblemBySubconstrator", "safetyProblemTrendByType", "safetyProblemTrend", "safetyProblem"
            //         , "safetyPoblemRectification", "safetyProblemReview", "safetyProblemType", "towerCraneDynamic",
            //         "towerCraneStatic", "productionMap", "envInfo", "weather", "alarmMsg"};
            //
            // for (String obj : objectTypes) {
            //     linkAllFactWithTenanByObjectType(cds, obj);
            // }

            List<String> rids = Arrays.asList("#138:21", "#143:5", "#139:21", "#140:21", "#140:22", "#143:18", "#144" +
                    ":19", "#144:20");
            linkFactToTenant(cds, rids);
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

}
