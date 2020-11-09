package com.glodon.pcop.cimstatsvc.sql;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cimstatsvc.model.StatParameter;

public class SqlStatBatch {
    public static  String getStatExpression(CimDataSpace cimDataSpace,StatParameter[] statParameters, String tenantId)  {
        StringBuffer querySQLStringBuffer = new StringBuffer();
        StringBuffer queryDefParamString = new StringBuffer();

        querySQLStringBuffer.append("SELECT EXPAND( $" + statParameters.length+" ) \n");
        querySQLStringBuffer.append(" \n Let ");
        queryDefParamString.append("$" + statParameters.length + " = UNIONALL( ");
        for (int i = 0; i < statParameters.length; i++) {

            querySQLStringBuffer.append("$" + i + "  =(" + StatSqlBuilder.getStatSqlByParameter(cimDataSpace,statParameters[i],tenantId) + "),");
            queryDefParamString.append("$" + i);
            if (i < statParameters.length - 1) {
                queryDefParamString.append(",");
            }
        }
        queryDefParamString.append(")");
        querySQLStringBuffer.append(queryDefParamString);
        return querySQLStringBuffer.toString();
    }
}
