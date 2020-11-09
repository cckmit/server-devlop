package com.glodon.pcop.cimsvc.service;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.StandardTreeNode;
import com.glodon.pcop.cimsvc.service.tree.TreeService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@Ignore
public class StandFoldersServiceTest {

    private static StandFoldersService standFoldersService;

    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";

        standFoldersService = new StandFoldersService();
        standFoldersService.setTreeService(new TreeService());
    }

    @Test
    public void getExcelAbsolutePath() {
    }

    @Test
    public void setExcelAbsolutePath() {
    }

    @Test
    public void addStandFolders() {
    }

    @Test
    public void addIndustryTypeDataSet() {
    }

    @Test
    public void addRootIndustryType() {
    }

    @Test
    public void addRootIndustryType1() {
        StandardTreeNode childTreeNode = new StandardTreeNode();
        childTreeNode.setTitle("标准目录-子01");

        StandardTreeNode treeNode = new StandardTreeNode();
        treeNode.setTitle("标准目录-父01");
        treeNode.setChildren(Arrays.asList(childTreeNode));

        String tenantId = "3";
        String userId = "1";
        String treeDefId = "DATA_MANAGER_CONTENT_TREE";
        String parentNodeRid = "#5624:186";

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            modelCore.setCimDataSpace(cds);

            Fact parentNodeFact = cds.getFactById(parentNodeRid);

            List<String> rs = new ArrayList<>();
            standFoldersService.addIndustruyNode(modelCore, tenantId, userId, treeDefId, parentNodeFact, treeNode, rs);
            System.out.println("add result: " + JSON.toJSONString(rs));
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

}