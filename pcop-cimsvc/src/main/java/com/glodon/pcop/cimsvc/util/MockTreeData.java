package com.glodon.pcop.cimsvc.util;

import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;

import java.util.ArrayList;
import java.util.List;

public class MockTreeData {

    public static List<NodeInfoBean> firstLevel() {
        List<NodeInfoBean> nodeInfoBeans = new ArrayList<>();

        NodeInfoBean infoBean1 = new NodeInfoBean();
        infoBean1.setID("11101");
        infoBean1.setNAME("永兴和北路管廊");
        infoBean1.setTreeDefId("bjdx_guanlangxinxi_tree");
        infoBean1.setNodeType(NodeInfoBean.NodeType.VIRTUNALNODE);
        infoBean1.setParentId("0");
        infoBean1.setRefObjType("UtilityTunnelSection");
        infoBean1.setRelationType("GLD_RELATION_BUSINESS_BUILDIN_RELATIONTYPE_SpaceContainedBy");
        infoBean1.setFilter("city=北京");
        nodeInfoBeans.add(infoBean1);

        NodeInfoBean infoBean2 = new NodeInfoBean();
        infoBean2.setID("11102");
        infoBean2.setNAME("定制Name101");
        infoBean2.setTreeDefId("bjdx_guanlangxinxi_tree");
        infoBean2.setNodeType(NodeInfoBean.NodeType.OBJECT);
        infoBean2.setParentId("0");
        nodeInfoBeans.add(infoBean2);

        NodeInfoBean infoBean3 = new NodeInfoBean();
        infoBean3.setID("11103");
        infoBean3.setNAME("文件1号");
        infoBean3.setTreeDefId("bjdx_guanlangxinxi_tree");
        infoBean3.setNodeType(NodeInfoBean.NodeType.FILE);
        infoBean3.setParentId("0");
        infoBean3.setRefObjType("BASE_FILE_METADATA_INFO");
        nodeInfoBeans.add(infoBean3);

        return nodeInfoBeans;
    }

    public static List<NodeInfoBean> firstLevelVirtualNodes() {
        List<NodeInfoBean> nodeInfoBeans = new ArrayList<>();

        NodeInfoBean infoBean1 = new NodeInfoBean();
        infoBean1.setID("12");
        infoBean1.setNAME("大里路");
        infoBean1.setNodeType(NodeInfoBean.NodeType.INSTANCE);
        infoBean1.setRefObjType("road");
        // infoBean1.setRefInstanceRid("#11:1");
        // infoBean1.setOriginalNodeId("11101");
        nodeInfoBeans.add(infoBean1);

        NodeInfoBean infoBean2 = new NodeInfoBean();
        infoBean2.setID("13");
        infoBean2.setNAME("清礼路");
        infoBean2.setNodeType(NodeInfoBean.NodeType.INSTANCE);
        infoBean2.setRefObjType("road");
        // infoBean1.setRefInstanceRid("#11:1");
        // infoBean1.setOriginalNodeId("11101");
        nodeInfoBeans.add(infoBean2);

        NodeInfoBean infoBean3 = new NodeInfoBean();
        infoBean3.setID("11");
        infoBean3.setNAME("永兴和北路");
        infoBean3.setNodeType(NodeInfoBean.NodeType.INSTANCE);
        infoBean3.setRefObjType("road");
        // infoBean1.setRefInstanceRid("#11:1");
        // infoBean1.setOriginalNodeId("11101");
        nodeInfoBeans.add(infoBean3);

        return nodeInfoBeans;
    }


}
