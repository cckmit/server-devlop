package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Dimension;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.IndustryType;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.input.TreeNodeInputBean;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

@Ignore
public class IndustryTypesServiceTest {

    private static CimDataSpace cds;
    private static IndustryTypesService typesService;
    private static Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";

        typesService = new IndustryTypesService();
    }

    // @After
    public void tearDown() throws Exception {
        if (cds != null) {
            cds.closeSpace();
        }
    }

    // @Test
    public void namesAvailable() {
        Long stDate = System.currentTimeMillis();
        cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, "2");
        IndustryType industryType = modelCore.getIndustryType("#34:35");
        System.out.println("child nodes names of " + industryType.getIndustryTypeRID() + ": \n" + gson.toJson(typesService.getAllChildNodeNamesByType(industryType, cds, "2", TreeNodeInputBean.TreeNodeTypeEnum.INDUSTRY)));
        System.out.println("used time: " + (System.currentTimeMillis() - stDate));
    }

    // @Test
    public void getAllChildNodeNames() throws CimDataEngineRuntimeException {
        String industryRid = "#33:43";
        cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        Dimension dimension = cds.getDimensionById(industryRid);
        List<Relation> relationList = dimension.getAllSpecifiedRelations("CIM_BUILDIN_RELATIONTYPE_BELONGSTOTENANT", RelationDirection.FROM);

        if (relationList != null && relationList.size() > 0) {
            for (Relation relation : relationList) {
                Relationable fromRelationable = relation.getFromRelationable();
                Relationable toRelationable = relation.getToRelationable();
                System.out.println("from node: " + fromRelationable.getId());
                System.out.println("to node: " + toRelationable.getId());
            }
        }
    }

    @Test
    public void fileMetadataParser() {

    }

}