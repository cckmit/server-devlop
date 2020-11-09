package com.glodon.pcop.cimsvc.service.v2.engine;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.RelationshipMappingFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.RelationshipMappingVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.RelatedIntsanceQueryInputBean;
import com.glodon.pcop.cimsvc.model.RelationshipQueryInputBean;
import com.glodon.pcop.cimsvc.service.RelationService;
import com.google.gson.Gson;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;


public class RelationshipMappingServiceTest {

    private static Gson gson = new Gson();

    // @BeforeClass
    public static void init() {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "yuanjk";
    }

    //    @Test
    public void getRelationshipsByObjectTypeName() throws CimDataEngineRuntimeException {
        RelationService relationService = new RelationService();
        List<RelationshipMappingVO> relationshipMappingVOList = relationService.getRelationshipsByObjectTypeName("1", "relationship_source_obj_test", "SOURCE");
        System.out.println("relationship size: " + relationshipMappingVOList.size());
        if (relationshipMappingVOList != null) {
            for (RelationshipMappingVO relationshipMappingVO : relationshipMappingVOList) {
                System.out.println("relationshipId=" + relationshipMappingVO.getRelationshipId());
            }
        }
    }

    //    @Test
    public void getInfoObjectTypeRelationshipLinkMappings() throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            List<RelationshipMappingVO> relationshipMappingVOList = RelationshipMappingFeatures.getInfoObjectTypeRelationshipLinkMappings(cds, "1", "relationship_source_obj_test", RelationshipMappingFeatures.RelationshipRole.SOURCE);

            System.out.println("relationship size: " + relationshipMappingVOList.size());
            if (relationshipMappingVOList != null) {
                for (RelationshipMappingVO relationshipMappingVO : relationshipMappingVOList) {
                    System.out.println("relationshipId=" + relationshipMappingVO.getRelationshipId());
                }
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

    }

    //    @Test
    public void getRelatedInstanceByRid() throws DataServiceModelRuntimeException, EntityNotFoundException {
        String infoObjectTypeName = "GLD_City";
        String instanceId = "#1042:0";

        RelationService relationService = new RelationService();
        RelatedIntsanceQueryInputBean queryInputBean = new RelatedIntsanceQueryInputBean();
        queryInputBean.setRelationTypeName("BUSINESS_BUILDIN_RELATIONTYPE_SpaceContainedBy");
        queryInputBean.setStartPage(1);
        queryInputBean.setEndPage(2);
        queryInputBean.setPageSize(10);

        System.out.println(relationService.getRelatedInstanceByRid("1", infoObjectTypeName, instanceId, queryInputBean));
    }

    // @Test
    public void getRelatedInstanceByRidAndObjectTypeId() throws DataServiceModelRuntimeException, EntityNotFoundException {
        String infoObjectTypeName = "GLD_City";
        String instanceId = "#1041:0";
        String relationType = "BUSINESS_BUILDIN_RELATIONTYPE_SpaceContainedBy";

        RelationService relationService = new RelationService();
        RelationshipQueryInputBean queryInputBean = new RelationshipQueryInputBean();
        // queryInputBean.setRelationTypeName("BUSINESS_BUILDIN_RELATIONTYPE_SpaceContainedBy");
        // queryInputBean.setRelationDirection(RelationDirection.TWO_WAY);
        // queryInputBean.setRelatedObjectTypeId("GLD_County");
        // queryInputBean.setRelatedObjectTypeId("GLD_County_s");
        queryInputBean.setStartPage(1);
        queryInputBean.setEndPage(2);
        queryInputBean.setPageSize(10);

        System.out.println(gson.toJson(relationService.getRelatedInstanceByRid("1", infoObjectTypeName, instanceId, relationType, queryInputBean)));
    }

    // @Test
    public void getObjectTypeRelatedInstance() throws DataServiceModelRuntimeException, EntityNotFoundException {
        // String infoObjectTypeName = "GLD_City";
        String infoObjectTypeName = "GLD_County";
        // String instanceId = "#1041:0";
        String relationType = "BUSINESS_BUILDIN_RELATIONTYPE_SpaceContainedBy";

        RelationService relationService = new RelationService();
        RelationshipQueryInputBean queryInputBean = new RelationshipQueryInputBean();
        // queryInputBean.setRelationTypeName("BUSINESS_BUILDIN_RELATIONTYPE_SpaceContainedBy");
        // queryInputBean.setRelationDirection(RelationDirection.TWO_WAY);
        // queryInputBean.setRelatedObjectTypeId("GLD_County");
        // queryInputBean.setRelatedObjectTypeId("GLD_County_s");
        queryInputBean.setStartPage(1);
        queryInputBean.setEndPage(2);
        queryInputBean.setPageSize(10);

        System.out.println(gson.toJson(relationService.getObjectTypeRelatedInstance("1", infoObjectTypeName, relationType, queryInputBean)));

    }

}