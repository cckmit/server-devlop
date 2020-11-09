package com.glodon.pcop.cimstatsvc.sql;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationType;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.CimDataEngineConstant;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;


public class SQLBuilder  {
    private final static Logger logger = LogManager.getLogger(SQLBuilder.class);

    public enum SQLType{
        FETCH,
        COUNT,
        STAT
    }

    public static String buildQuerySQL(InformationType informationType, ExploreParameters exploreParameters, SQLType sqlType)throws CimDataEngineInfoExploreException {
        String type=exploreParameters.getType();
        String orientTypeClass=null;
        switch(informationType){
            case IH_FACT:
                if(type!=null){
                    orientTypeClass= CimDataEngineConstant.CLASSPERFIX_IHFACT+type;
                }else{
                    orientTypeClass= CimDataEngineConstant.IHFACT_ROOTCLASSNAME;
                }
                break;
            case FACT:
                if(type!=null){
                    orientTypeClass= CimDataEngineConstant.CLASSPERFIX_FACT+type;
                }else{
                    orientTypeClass= CimDataEngineConstant.FACT_ROOTCLASSNAME;
                }
                break;
            case DIMENSION:
                if(type!=null){
                    orientTypeClass= CimDataEngineConstant.CLASSPERFIX_DIMENSION+type;
                }else{
                    orientTypeClass= CimDataEngineConstant.DIMENSION_ROOTCLASSNAME;
                }
                break;
            case RELATION:
                if(type!=null){
                    orientTypeClass= CimDataEngineConstant.CLASSPERFIX_RELATION+type;
                }else{
                    orientTypeClass= CimDataEngineConstant.RELATION_ROOTCLASSNAME;
                }
                break;
        }
        StringBuffer querySQLStringBuffer=new StringBuffer();
        switch(sqlType){
            case FETCH:
                querySQLStringBuffer.append("SELECT FROM ");
                break;
            case COUNT:
                querySQLStringBuffer.append("SELECT COUNT(*) FROM ");
                 break;
            case STAT:
                querySQLStringBuffer.append("SELECT COUNT(*) FROM ");
                break;
        }
        querySQLStringBuffer.append(orientTypeClass);

        if(exploreParameters.getDefaultFilteringItem()!=null){
            querySQLStringBuffer.append(" WHERE ");
            querySQLStringBuffer.append(exploreParameters.getDefaultFilteringItem().getFilteringLogic());

            List<FilteringItem> andFilteringItemsList= exploreParameters.getAndFilteringItemsList();
            for(FilteringItem andFilteringItem:andFilteringItemsList){
                querySQLStringBuffer.append(" AND ");
                querySQLStringBuffer.append(andFilteringItem.getFilteringLogic());
            }
            List<FilteringItem> orFilteringItemsList= exploreParameters.getOrFilteringItemsList();
            for(FilteringItem orFilteringItem:orFilteringItemsList){
                querySQLStringBuffer.append(" OR ");
                querySQLStringBuffer.append(orFilteringItem.getFilteringLogic());
            }
        }
        if(sqlType.equals(SQLType.FETCH) || sqlType.equals(sqlType.STAT)){
            setCommonPagingAndSortingParameters(querySQLStringBuffer, exploreParameters);
        }
        logger.info(querySQLStringBuffer.toString());
        return querySQLStringBuffer.toString();
    }

    private static void setCommonPagingAndSortingParameters(StringBuffer querySQLStringBuffer, ExploreParameters exploreParameters) throws CimDataEngineInfoExploreException {
        if(exploreParameters.getSortAttributes() !=null && exploreParameters.getSortAttributes().size() > 0){
            querySQLStringBuffer.append(" ");
            querySQLStringBuffer.append("ORDER BY ");
            querySQLStringBuffer.append(formatSortingAttributesValue(exploreParameters.getSortAttributes()));
            querySQLStringBuffer.append(" "+exploreParameters.getSortingLogic());
        }

        if(exploreParameters.getStartPage()!=0){
            if(exploreParameters.getStartPage()<0){
                String exceptionMessage = "start page must great then zero";
                throw CimDataEngineException.getInfoExploreException(exceptionMessage);
            }
            if(exploreParameters.getPageSize()<0){
                String exceptionMessage = "page size must great then zero";
                throw CimDataEngineException.getInfoExploreException(exceptionMessage);
            }

            int runtimePageSize=exploreParameters.getPageSize()!=0?exploreParameters.getPageSize():50;
            int runtimeStartPage=exploreParameters.getStartPage()-1;
            if(exploreParameters.getEndPage()!=0){
                //get data from start page to end page, each page has runtimePageSize number of record
                if(exploreParameters.getEndPage()<0||exploreParameters.getEndPage()<=exploreParameters.getStartPage()){
                    String exceptionMessage = "end page must great than start page";
                    throw CimDataEngineException.getInfoExploreException(exceptionMessage);
                }
                int runtimeEndPage=exploreParameters.getEndPage()-1;
                querySQLStringBuffer.append(" ");
                querySQLStringBuffer.append("SKIP ");
                querySQLStringBuffer.append(runtimePageSize*runtimeStartPage);
                querySQLStringBuffer.append(" ");
                querySQLStringBuffer.append("LIMIT ");
                querySQLStringBuffer.append((runtimeEndPage-runtimeStartPage)*runtimePageSize);
            }else{
                //filter the data before the start page
                querySQLStringBuffer.append(" ");
                querySQLStringBuffer.append("SKIP ");
                querySQLStringBuffer.append(runtimePageSize*runtimeStartPage);
            }
        }else{
            //if there is no page parameters,use resultNumber to control result information number
            if(exploreParameters.getResultNumber()!=0){
                if(exploreParameters.getResultNumber()<0){
                    String exceptionMessage = "result number must great then zero";
                    throw CimDataEngineException.getInfoExploreException(exceptionMessage);
                }
                querySQLStringBuffer.append(" ");
                querySQLStringBuffer.append("LIMIT ");
                querySQLStringBuffer.append(exploreParameters.getResultNumber());
            }
        }
    }
    private static String formatSortingAttributesValue(List<String> attributesList){
        StringBuffer valueStringBuffer =new StringBuffer();
        for(int i=0;i<attributesList.size();i++){
            valueStringBuffer.append(attributesList.get(i));
            if(i<attributesList.size()-1){
                valueStringBuffer.append(",");
            }
        }
        return valueStringBuffer.toString();
    }

    public static String formatFilteringValue(Object filteringValue){
        String formattedValue;
        if(filteringValue instanceof Boolean){
            formattedValue=""+((Boolean) filteringValue).booleanValue();
        }else if(filteringValue instanceof Integer){
            formattedValue=""+((Integer) filteringValue).intValue();
        }else if(filteringValue instanceof Short){
            formattedValue=""+((Short) filteringValue).shortValue();
        }else if(filteringValue instanceof Long){
            formattedValue=""+((Long) filteringValue).longValue();
        }else if(filteringValue instanceof Float){
            formattedValue=""+((Float) filteringValue).floatValue();
        }else if(filteringValue instanceof Double){
            formattedValue=""+((Double) filteringValue).doubleValue();
        }else if(filteringValue instanceof Date){
            Date filterDateValue = (Date) filteringValue;
            formattedValue=""+filterDateValue.getTime();
        }else if(filteringValue instanceof String){
            formattedValue="'"+filteringValue+"'";
        }else if(filteringValue instanceof Byte){
            formattedValue=""+((Byte) filteringValue).byteValue();
        }else{
            formattedValue=filteringValue.toString();
        }
        return formattedValue;
    }
}
