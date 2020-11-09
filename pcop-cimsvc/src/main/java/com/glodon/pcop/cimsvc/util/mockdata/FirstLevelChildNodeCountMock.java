package com.glodon.pcop.cimsvc.util.mockdata;

import com.glodon.pcop.cimsvc.model.tree.ChildNodeCountBean;

import java.util.ArrayList;
import java.util.List;

public class FirstLevelChildNodeCountMock {

    public static List<ChildNodeCountBean> childNodeCount() {
        List<ChildNodeCountBean> countBeanList = new ArrayList<>();

        ChildNodeCountBean bean1 = new ChildNodeCountBean("id-001", "基础地理", 567, 0);
        ChildNodeCountBean bean2 = new ChildNodeCountBean("id-002", "规划研究", 1521, 0);
        ChildNodeCountBean bean3 = new ChildNodeCountBean("id-003", "工程项目", 1099, 0);

        countBeanList.add(bean1);
        countBeanList.add(bean2);
        countBeanList.add(bean3);

        return countBeanList;
    }

}
