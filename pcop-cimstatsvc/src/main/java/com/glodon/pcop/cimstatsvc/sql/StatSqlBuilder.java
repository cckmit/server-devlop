package com.glodon.pcop.cimstatsvc.sql;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Dimension;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.CimDataEngineConstant;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cimstatsvc.dao.DbExecute;
import com.glodon.pcop.cimstatsvc.model.QueryConditionsBean;
import com.glodon.pcop.cimstatsvc.model.StatParameter;
import com.glodon.pcop.cimstatsvc.util.QueryConditionsUtil;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

import static com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil.getCimTenantDimensionById;
import static com.glodon.pcop.cimstatsvc.util.QueryConditionsUtil.getPropertyTypeByName;

public class StatSqlBuilder {
    public static String getStatSqlByParameter(CimDataSpace cimDataSpace, StatParameter statParameter,String tenantId) {
        StringBuffer querySQLStringBuffer = new StringBuffer();
        String objectTypeId = statParameter.getCim_object_type();
        String orientTypeClass = CimDataEngineConstant.CLASSPERFIX_IHFACT + objectTypeId;
        GroupParameters groupParameters = new GroupParameters(statParameter.getGroupAttributes());



        //select
        querySQLStringBuffer.append("SELECT ");
        if (statParameter.get_sign_() != null){
            if (!statParameter.get_sign_().isEmpty()) {
                querySQLStringBuffer.append("\"" + statParameter.get_sign_() + "\"");
                querySQLStringBuffer.append(" as ");
                querySQLStringBuffer.append("_sign_");
                querySQLStringBuffer.append(",");
            }

        }
        if (statParameter.getStat_item() != null) {
            if (!statParameter.getStat_item().isEmpty()) {
                querySQLStringBuffer.append("\"" + statParameter.getStat_item() + "\"");
                querySQLStringBuffer.append(" as ");
                querySQLStringBuffer.append("_sign_");
                querySQLStringBuffer.append(",");
            }
        }
        if (groupParameters.getGroupString() != null) {
            if (!groupParameters.getGroupString().isEmpty()) {
                querySQLStringBuffer.append(groupParameters.getGroupString());
                querySQLStringBuffer.append(",");
            }
        }
        String statType = statParameter.getStatType();
        if (statType != null) {
            if (statType.toLowerCase().equals("count")) {
                querySQLStringBuffer.append("COUNT(");
                querySQLStringBuffer.append(statParameter.getProperty());
                querySQLStringBuffer.append(")  as ");
                querySQLStringBuffer.append(statParameter.getProperty());
                querySQLStringBuffer.append("Count");
            } else if (statType.toLowerCase().equals("sum")) {
                querySQLStringBuffer.append("SUM(");
                PropertyTypeVO propertyTypeVO =  getPropertyTypeByName(cimDataSpace, objectTypeId, statParameter.getProperty());
                if(propertyTypeVO != null){
                    if(propertyTypeVO.getPropertyFieldDataClassify().toLowerCase().equals("string")) {
                        querySQLStringBuffer.append(statParameter.getProperty());
                        querySQLStringBuffer.append(".asFloat()"); //字符串转成浮点型进行计算
                    }else{
                        querySQLStringBuffer.append(statParameter.getProperty());
                    }
                }else{
                    querySQLStringBuffer.append(statParameter.getProperty());
                }
                querySQLStringBuffer.append(")  as ");
                querySQLStringBuffer.append(statParameter.getProperty());
                querySQLStringBuffer.append("Sum");
            }
        }

        //form
        querySQLStringBuffer.append(" FROM ");
//        querySQLStringBuffer.append(CimDataEngineConstant.CLASSPERFIX_IHFACT + objectTypeId);
//        querySQLStringBuffer.append("` ");

        ////////////////处理tantId
//        StringBuffer relationSelectSQL = new StringBuffer();
//        relationSelectSQL.append("\"");
//        relationSelectSQL.append("GLD_RELATION_CIM_BUILDIN_RELATIONTYPE_BELONGSTOTENANT");
//        relationSelectSQL.append("\"");
//        String relationFilter = "both" + "(" + relationSelectSQL.toString() + ")";
//
//        CimDataSpace cimDataSpace= CimDataEngineComponentFactory.connectInfoDiscoverSpace(DbExecute.dbName);
//        StringBuffer sourceRelationableIdsArray = new StringBuffer();
//        try {
//            Dimension dimension = getCimTenantDimensionById(cimDataSpace, tenantId);
//            sourceRelationableIdsArray.append("[");
//            sourceRelationableIdsArray.append(dimension.getId());
//            sourceRelationableIdsArray.append("]");
//        } catch (CimDataEngineRuntimeException e) {
//            e.printStackTrace();
//        } catch (CimDataEngineInfoExploreException e) {
//            e.printStackTrace();
//        }finally {
//            if(cimDataSpace != null){
//                cimDataSpace.closeSpace();
//            }
//        }
//
//
//        String traverseSQL = "  (TRAVERSE " + relationFilter + " FROM " + sourceRelationableIdsArray.toString() + " WHILE $depth <= 1" + " STRATEGY BREADTH_FIRST) WHERE @class = \"" + orientTypeClass + "\"";
//        querySQLStringBuffer.append(traverseSQL);

        querySQLStringBuffer.append("`" + orientTypeClass + "`");
        //处理tanlent
        querySQLStringBuffer.append(" WHERE \"" +tenantId +"\" in OUTE(\"GLD_RELATION_CIM_BUILDIN_RELATIONTYPE_BELONGSTOTENANT\").INV().CIM_BUILDIN_TENANT_ID " );
        ///////////////////////////////////


        ExploreParameters ep = new ExploreParameters();
        boolean setDeflaut = true;
        boolean hasCondition = false;

        if (statParameter.getConditions() != null) {
            for (QueryConditionsBean bean : statParameter.getConditions()) {
                FilteringItem item = QueryConditionsUtil.parseQueryCondition(cimDataSpace,objectTypeId, bean);
                if (item != null) {
                    if (setDeflaut) {
                        ep.setDefaultFilteringItem(item);
                        setDeflaut = false;
                        hasCondition = true;
                    } else {
                        ep.addFilteringItem(item, bean.getFilterLogical());
                    }
                } else {
//                log.error("Query condition parser failed");
                }
            }
        }
        if (hasCondition) {
//            querySQLStringBuffer.append(" WHERE ");
            querySQLStringBuffer.append(" AND ");
            querySQLStringBuffer.append(ep.getDefaultFilteringItem().getFilteringLogic());

            List<FilteringItem> andFilteringItemsList = ep.getAndFilteringItemsList();
            for (FilteringItem andFilteringItem : andFilteringItemsList) {
                querySQLStringBuffer.append(" AND ");
                querySQLStringBuffer.append(andFilteringItem.getFilteringLogic());
            }
            List<FilteringItem> orFilteringItemsList = ep.getOrFilteringItemsList();
            for (FilteringItem orFilteringItem : orFilteringItemsList) {
                querySQLStringBuffer.append(" OR ");
                querySQLStringBuffer.append(orFilteringItem.getFilteringLogic());
            }
        }
       //处理tanlent

        //in OUTE("GLD_RELATION_CIM_BUILDIN_RELATIONTYPE_BELONGSTOTENANT").INV().CIM_BUILDIN_TENANT_ID


        //group by
        if (groupParameters.getGroupString() != null) {
            if (!groupParameters.getGroupString().isEmpty()) {
                querySQLStringBuffer.append(groupParameters.getGroupExpression());
            }
        }
        return querySQLStringBuffer.toString();
    }
}
