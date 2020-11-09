package com.glodon.pcop.cimsvc.util;

import com.glodon.pcop.cim.common.util.OrientdbConfigUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.config.PropertyHandler;
import com.glodon.pcop.cimsvc.model.ObjectTypeBean;
import com.glodon.pcop.cimsvc.model.PropertyBean;
import com.glodon.pcop.cimsvc.model.PropertySetBean;
import com.glodon.pcop.cimsvc.service.ObjectTypeService;

import java.util.ArrayList;
import java.util.List;

public class AddObjectUtil {

    private static final String STRING = "STRING";
    private static final String INSTANCE_RID = "#896:0";
    private static final String DEFAULT_DATASET_TYPE = "通用属性集";

    public static void main(String[] args) {
        PropertyHandler.map = OrientdbConfigUtil.getParameters();

        ObjectTypeService objectTypeService = new ObjectTypeService();
        ObjectTypeBean typeBean = addLandblockObjAndData();
        objectTypeService.addObjectType(typeBean, "1");
        typeBean = addBinhaiBuildingsObjAndData();
        objectTypeService.addObjectType(typeBean, "1");
    }

    public static ObjectTypeBean addLandblockObjAndData() {
        List<PropertyBean> beanList0 = new ArrayList<>();
        PropertyBean bean0 = new PropertyBean();
        bean0.setIsNull(true);
        bean0.setName("规划用地");
        bean0.setAlias("规划用地");
        bean0.setTypeName(STRING);
        bean0.setTypeId(INSTANCE_RID);

        PropertyBean bean1 = new PropertyBean();
        bean1.setIsNull(true);
        bean1.setName("面积率");
        bean1.setAlias("面积率");
        bean1.setTypeName(STRING);
        bean1.setTypeId(INSTANCE_RID);

        PropertyBean bean2 = new PropertyBean();
        bean2.setIsNull(true);
        bean2.setName("建筑密度");
        bean2.setAlias("建筑密度");
        bean2.setTypeName(STRING);
        bean2.setTypeId(INSTANCE_RID);

        PropertyBean bean3 = new PropertyBean();
        bean3.setIsNull(true);
        bean3.setName("绿地率");
        bean3.setAlias("绿地率");
        bean3.setTypeName(STRING);
        bean3.setTypeId(INSTANCE_RID);

        PropertyBean bean4 = new PropertyBean();
        bean4.setIsNull(true);
        bean4.setName("地块编号");
        bean4.setAlias("地块编号");
        bean4.setTypeName(STRING);
        bean4.setTypeId(INSTANCE_RID);

        PropertyBean bean5 = new PropertyBean();
        bean5.setIsNull(true);
        bean5.setName("用地性质代码");
        bean5.setAlias("用地性质代码");
        bean5.setTypeName(STRING);
        bean5.setTypeId(INSTANCE_RID);

        PropertyBean bean6 = new PropertyBean();
        bean6.setIsNull(true);
        bean6.setName("用地性质");
        bean6.setAlias("用地性质");
        bean6.setTypeName(STRING);
        bean6.setTypeId(INSTANCE_RID);

        PropertyBean bean7 = new PropertyBean();
        bean7.setIsNull(true);
        bean7.setName("用地面积");
        bean7.setAlias("用地面积");
        bean7.setTypeName(STRING);
        bean7.setTypeId(INSTANCE_RID);

        PropertyBean bean8 = new PropertyBean();
        bean8.setIsNull(true);
        bean8.setName("容积率");
        bean8.setAlias("容积率");
        bean8.setTypeName(STRING);
        bean8.setTypeId(INSTANCE_RID);

        beanList0.add(bean0);
        beanList0.add(bean1);
        beanList0.add(bean2);
        beanList0.add(bean3);
        beanList0.add(bean4);
        beanList0.add(bean5);
        beanList0.add(bean6);
        beanList0.add(bean7);
        beanList0.add(bean8);

        PropertySetBean setBean0 = new PropertySetBean();
        setBean0.setName("规划");
        setBean0.setPropertySetTypeId(DEFAULT_DATASET_TYPE);
        setBean0.setProperties(beanList0);

        //第二个属性集
        List<PropertyBean> beanList1 = new ArrayList<>();
        PropertyBean bean10 = new PropertyBean();
        bean10.setIsNull(true);
        bean10.setName("建造");
        bean10.setAlias("建造");
        bean10.setTypeName(STRING);
        bean10.setTypeId(INSTANCE_RID);

        beanList1.add(bean10);

        PropertySetBean setBean1 = new PropertySetBean();
        setBean1.setName("建造");
        setBean1.setPropertySetTypeId(DEFAULT_DATASET_TYPE);
        setBean1.setProperties(beanList1);

        //第三个属性集
        List<PropertyBean> beanList2 = new ArrayList<>();
        PropertyBean bean20 = new PropertyBean();
        bean20.setIsNull(true);
        bean20.setName("管理");
        bean20.setAlias("管理");
        bean20.setTypeName(STRING);
        bean20.setTypeId(INSTANCE_RID);

        beanList2.add(bean20);

        PropertySetBean setBean2 = new PropertySetBean();
        setBean2.setName("管理");
        setBean2.setPropertySetTypeId(DEFAULT_DATASET_TYPE);
        setBean2.setProperties(beanList2);


        List<PropertySetBean> propertySetBeanList = new ArrayList<>();
        propertySetBeanList.add(setBean0);
        propertySetBeanList.add(setBean1);
        propertySetBeanList.add(setBean2);

        ObjectTypeBean objectTypeBean = new ObjectTypeBean();
        objectTypeBean.setTypeName("LandBlock");
        objectTypeBean.setIndustryTypeId("#34:0");
        objectTypeBean.setPropertySet(propertySetBeanList);

        return objectTypeBean;
    }

    public static ObjectTypeBean addBinhaiBuildingsObjAndData() {
        List<PropertyBean> beanList0 = new ArrayList<>();
        PropertyBean bean0 = new PropertyBean();
        bean0.setIsNull(true);
        bean0.setName("规划用地");
        bean0.setAlias("规划用地");
        bean0.setTypeName(STRING);
        bean0.setTypeId(INSTANCE_RID);

        PropertyBean bean1 = new PropertyBean();
        bean1.setIsNull(true);
        bean1.setName("建筑面积");
        bean1.setAlias("建筑面积");
        bean1.setTypeName(STRING);
        bean1.setTypeId(INSTANCE_RID);

        PropertyBean bean4 = new PropertyBean();
        bean4.setIsNull(true);
        bean4.setName("建筑高度");
        bean4.setAlias("建筑高度");
        bean4.setTypeName(STRING);
        bean4.setTypeId(INSTANCE_RID);

        PropertyBean bean2 = new PropertyBean();
        bean2.setIsNull(true);
        bean2.setName("建筑密度");
        bean2.setAlias("建筑密度");
        bean2.setTypeName(STRING);
        bean2.setTypeId(INSTANCE_RID);

        PropertyBean bean3 = new PropertyBean();
        bean3.setIsNull(true);
        bean3.setName("绿地率");
        bean3.setAlias("绿地率");
        bean3.setTypeName(STRING);
        bean3.setTypeId(INSTANCE_RID);

        beanList0.add(bean0);
        beanList0.add(bean1);
        beanList0.add(bean2);
        beanList0.add(bean3);
        beanList0.add(bean4);

        PropertySetBean setBean0 = new PropertySetBean();
        setBean0.setName("规划");
        setBean0.setPropertySetTypeId(DEFAULT_DATASET_TYPE);
        setBean0.setProperties(beanList0);

        //第二个属性集
        List<PropertyBean> beanList1 = new ArrayList<>();

        PropertyBean bean5 = new PropertyBean();
        bean5.setIsNull(true);
        bean5.setName("总建筑面积");
        bean5.setAlias("总建筑面积");
        bean5.setTypeName(STRING);
        bean5.setTypeId(INSTANCE_RID);

        PropertyBean bean6 = new PropertyBean();
        bean6.setIsNull(true);
        bean6.setName("本月耗电量");
        bean6.setAlias("本月耗电量");
        bean6.setTypeName(STRING);
        bean6.setTypeId(INSTANCE_RID);

        PropertyBean bean7 = new PropertyBean();
        bean7.setIsNull(true);
        bean7.setName("本月耗水量");
        bean7.setAlias("本月耗水量");
        bean7.setTypeName(STRING);
        bean7.setTypeId(INSTANCE_RID);

        PropertyBean bean8 = new PropertyBean();
        bean8.setIsNull(true);
        bean8.setName("温度");
        bean8.setAlias("温度");
        bean8.setTypeName(STRING);
        bean8.setTypeId(INSTANCE_RID);

        PropertyBean bean10 = new PropertyBean();
        bean10.setIsNull(true);
        bean10.setName("湿度");
        bean10.setAlias("湿度");
        bean10.setTypeName(STRING);
        bean10.setTypeId(INSTANCE_RID);

        beanList1.add(bean5);
        beanList1.add(bean6);
        beanList1.add(bean7);
        beanList1.add(bean8);
        beanList1.add(bean10);

        PropertySetBean setBean1 = new PropertySetBean();
        setBean1.setName("建造");
        setBean1.setPropertySetTypeId(DEFAULT_DATASET_TYPE);
        setBean1.setProperties(beanList1);

        //第三个属性集
        List<PropertyBean> beanList2 = new ArrayList<>();
        PropertyBean bean20 = new PropertyBean();
        bean20.setIsNull(true);
        bean20.setName("管理");
        bean20.setAlias("管理");
        bean20.setTypeName(STRING);
        bean20.setTypeId(INSTANCE_RID);

        beanList2.add(bean20);

        PropertySetBean setBean2 = new PropertySetBean();
        setBean2.setName("管理");
        setBean2.setPropertySetTypeId(DEFAULT_DATASET_TYPE);
        setBean2.setProperties(beanList2);


        List<PropertySetBean> propertySetBeanList = new ArrayList<>();
        propertySetBeanList.add(setBean0);
        propertySetBeanList.add(setBean1);
        propertySetBeanList.add(setBean2);

        ObjectTypeBean objectTypeBean = new ObjectTypeBean();
        objectTypeBean.setTypeName("Binhai_Buildings");
        objectTypeBean.setIndustryTypeId("#33:0");
        objectTypeBean.setPropertySet(propertySetBeanList);

        return objectTypeBean;
    }


}
