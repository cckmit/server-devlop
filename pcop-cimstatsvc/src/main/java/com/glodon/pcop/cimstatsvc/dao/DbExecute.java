package com.glodon.pcop.cimstatsvc.dao;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureauImpl.OrientDBCimDataSpaceImpl;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CommonTag;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CommonTags;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.CommonTagsDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.sun.org.apache.bcel.internal.generic.NEW;

import javax.swing.text.html.HTML;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbExecute {

    public static String dbName = "AttendanceManagementCase";


    public static List<String> executeQuery(String sql) {
        System.out.println(sql);
        List list = new ArrayList();
        CimDataSpace cimDataSpace = CimDataEngineComponentFactory.connectInfoDiscoverSpace(dbName);
        try {
            OrientDBCimDataSpaceImpl orientDBCimDataSpaceImpl = (OrientDBCimDataSpaceImpl) cimDataSpace;
            OResultSet resultSet = orientDBCimDataSpaceImpl.getGraph().getRawGraph().query(sql);
            while (resultSet.hasNext()) {
                OResult r = resultSet.next();
                list.add(r.toJSON().toString());
            }
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cimDataSpace != null) {
                cimDataSpace.closeSpace();
            }
        }
        return list;
    }

    public static List<String> executeQuery(CimDataSpace cimDataSpace, String sql) {
        System.out.println(sql);
        List list = new ArrayList();
        OrientDBCimDataSpaceImpl orientDBCimDataSpaceImpl = (OrientDBCimDataSpaceImpl) cimDataSpace;
        OResultSet resultSet = orientDBCimDataSpaceImpl.getGraph().getRawGraph().query(sql);
        while (resultSet.hasNext()) {
            OResult r = resultSet.next();
            list.add(r.toJSON().toString());
        }
        resultSet.close();
        return list;
    }

    public static CommonTag getCommonTag(CimDataSpace cimDataSpace, String tagName,String tenantId){
        CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(dbName, tenantId);
        modelCore.setCimDataSpace(cimDataSpace);
        CommonTags commonTags  = modelCore.getCommonTags();
        return commonTags.getTag(tagName);
    }

    public static CommonTag getCommonTag( String tagName,String tenantId){
        CommonTags commonTags  = new CommonTagsDSImpl(dbName,tenantId);
        return commonTags.getTag(tagName);
    }
}
