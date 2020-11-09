package com.glodon.pcop.cimsvc.service.tree;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Ignore
public class TreeServiceUtilTest {


    @Before
    public void setUp() throws Exception {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();
        CimConstants.defauleSpaceName = "pcopcim";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void relationableToNodeInfo() {
    }

    @Test
    public void nodeCopyRecursive01() {
        String treeDefId = "xinjianchangjingmuluyuanjk0919";
        String tenantId = "3";
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        try {
            NodeInfoBean nodeInfoBean = new NodeInfoBean();
            nodeInfoBean.setID("xinjianchangjingmuluyuanjk0919_568413f8-c420-4969-a23e-706d9f639859_1570860376054");
            nodeInfoBean.setNAME("BIM_MIXIN_dizhimoxing");
            nodeInfoBean.setNodeType(NodeInfoBean.NodeType.OBJECT);
            nodeInfoBean.setRefObjType("BIM_MIXIN_dizhimoxing");
            nodeInfoBean.setIdx(1570860376040D);
            nodeInfoBean.setCreator("6508264434020594292");
            nodeInfoBean.setUpdator("6508264434020594292");
            nodeInfoBean.setRid("#9341:0");
            List<Fact> nodeFactList = new ArrayList<>();
            TreeServiceUtil.nodeCopyRecursive(treeDefId, tenantId, null, nodeInfoBean, cds, nodeFactList);
        } finally {
            cds.closeSpace();
        }

    }

    @Test
    public void nodeCopyRecursive02() {
        String treeDefId = "xinjianchangjingmuluyuanjk0919";
        String tenantId = "3";
        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        try {
            NodeInfoBean nodeInfoBean = new NodeInfoBean();
            nodeInfoBean.setID("xinjianchangjingmuluyuanjk0919_d3fb7794-d795-491d-b799-1d07e6b88b3e_1568963358324");
            nodeInfoBean.setNAME("test01");
            nodeInfoBean.setNodeType(NodeInfoBean.NodeType.INDUSTRY);
            nodeInfoBean.setIdx(1568963358310D);
            nodeInfoBean.setCreator("6508264434020594292");
            nodeInfoBean.setUpdator("6508264434020594292");
            nodeInfoBean.setRid("#9338:1");
            List<Fact> nodeFactList = new ArrayList<>();
            TreeServiceUtil.nodeCopyRecursive(treeDefId, tenantId, null, nodeInfoBean, cds, nodeFactList);
        } finally {
            cds.closeSpace();
        }

    }
}