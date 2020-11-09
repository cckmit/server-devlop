package com.glodon.pcop.cimsvc.service.stat;

import com.glodon.pcop.cim.common.model.stat.DateTimeBetweenFilterBean;
import com.glodon.pcop.cim.common.model.stat.MultiplePropertiesStatInputBean;
import com.glodon.pcop.cim.common.model.stat.MultiplePropertiesStatOutputBean;
import com.glodon.pcop.cim.common.model.stat.MultiplePropertiesStatPropertyFilterBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureauImpl.OrientDBCimDataSpaceImpl;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.CimDataEngineConstant;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class MultiplePropertiesStatService {
    private static Logger log = LoggerFactory.getLogger(MultiplePropertiesStatService.class);

    private static final String CNT = "countResult";

    public List<MultiplePropertiesStatOutputBean> multiplePropertyStat(String tenantId,
                                                                       MultiplePropertiesStatInputBean statInputBean) {
        List<MultiplePropertiesStatOutputBean> allRecords = new ArrayList<>();
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            OrientGraph graph = ((OrientDBCimDataSpaceImpl) cds).getGraph();
            List<String> sqlArray = sqlBuilder(tenantId, statInputBean);
            String firstKey = sqlArray.get(0);
            boolean flag = true;

            MultiplePropertiesStatOutputBean tmpOutputBean = new MultiplePropertiesStatOutputBean();
            allRecords.add(tmpOutputBean);
            Map<String, Object> tmpSummaryCount = new HashMap<>();
            tmpOutputBean.setSummaryCount(tmpSummaryCount);
            List<Map<String, Object>> tmpDetailCount = new ArrayList<>();
            tmpOutputBean.setDetailCount(tmpDetailCount);
            String tmpValue = "";
            long tmpCount = 0;
            for (Vertex v : (Iterable<Vertex>) graph.command(
                    new OCommandSQL(sqlArray.get(1))).execute()) {
                Map<String, Object> oneRecord = new HashMap<>();
                for (String key : v.getPropertyKeys()) {
                    oneRecord.put(key, v.getProperty(key));
                }
                // tmpDetailCount.add(oneRecord);
                String currentFirstVal = v.getProperty(firstKey);
                long currentCnt = (long) v.getProperty(CNT);

                if (flag) {
                    tmpValue = currentFirstVal;
                    tmpCount = currentCnt;
                    flag = false;
                    tmpDetailCount.add(oneRecord);
                    continue;
                }

                if (currentFirstVal.equals(tmpValue)) {
                    tmpCount += currentCnt;
                    tmpDetailCount.add(oneRecord);
                } else {
                    tmpSummaryCount.put(firstKey, tmpValue);
                    tmpSummaryCount.put(CNT, tmpCount);

                    tmpOutputBean = new MultiplePropertiesStatOutputBean();
                    allRecords.add(tmpOutputBean);
                    tmpSummaryCount = new HashMap<>();
                    tmpOutputBean.setSummaryCount(tmpSummaryCount);
                    tmpDetailCount = new ArrayList<>();
                    tmpOutputBean.setDetailCount(tmpDetailCount);

                    tmpValue = currentFirstVal;
                    tmpCount = currentCnt;
                    tmpDetailCount.add(oneRecord);
                }
            }
            //last summary record
            tmpSummaryCount.put(firstKey, tmpValue);
            tmpSummaryCount.put(CNT, tmpCount);
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        return allRecords;
    }

    public List<String> sqlBuilder(String tenantId, MultiplePropertiesStatInputBean statInputBean) {
        StringBuilder whereCondBuilder = new StringBuilder();
        whereCondBuilder.append(" WHERE ")
                .append("\"" + tenantId + "\"")
                .append(" IN ")
                .append("both(\'GLD_RELATION_CIM_BUILDIN_RELATIONTYPE_BELONGSTOTENANT\').CIM_BUILDIN_TENANT_ID ");

        List<MultiplePropertiesStatPropertyFilterBean> properties = statInputBean.getProperties();
        List<String> groupAndSortProperties = new ArrayList<>();
        String firstKey = properties.get(0).getProperty();

        for (MultiplePropertiesStatPropertyFilterBean property : properties) {
            groupAndSortProperties.add(property.getProperty());
            if (property.getPropertyValues() != null && CollectionUtils.isNotEmpty(property.getPropertyValues())) {
                whereCondBuilder.append(" AND ").append(property.getProperty()).append(" IN ").append(joinListStr(property.getPropertyValues()));
            }
        }
        //between condistion
        DateTimeBetweenFilterBean betweenFilter = statInputBean.getCondition();
        if (betweenFilter == null ||
                StringUtils.isBlank(betweenFilter.getProperty()) ||
                StringUtils.isBlank(betweenFilter.getStartTime()) ||
                StringUtils.isBlank(betweenFilter.getEndTime())) {
            log.info("no between query condition or error input");
        } else {
            whereCondBuilder.append(" AND ").append(betweenFilter.getProperty()).
                    append(" BETWEEN ").append(betweenFilter.getStartTime()).append(" and ").append(betweenFilter.getEndTime());
        }

        //equal conditions
        if (statInputBean.getEqualConditions() != null && statInputBean.getEqualConditions().size() > 0) {
            for (Map.Entry<String, Object> entry : statInputBean.getEqualConditions().entrySet()) {
                whereCondBuilder.append(" AND ").append(entry.getKey()).
                        append(" = ").append("\"").append(entry.getValue()).append("\"");
            }
        } else {
            log.info("no equal conditions");
        }

        StringBuilder props = new StringBuilder();
        boolean flag = true;
        for (String st : groupAndSortProperties) {
            if (flag) {
                props.append(st);
                flag = false;
            } else {
                props.append(", ").append(st);
            }
        }

        whereCondBuilder.append(" GROUP BY ").append(props.toString());
        whereCondBuilder.append(" ORDER BY ").append(props.toString());

        StringBuilder sqlBuilter = new StringBuilder();
        // sqlBuilter.append("SELECT ").append(props.toString()).append(", COUNT(*) AS CNT_ ").
        sqlBuilter.append("SELECT ").append(props.toString()).append(", COUNT(*) AS ").append(CNT).
                append(" FROM ").append(CimDataEngineConstant.CLASSPERFIX_IHFACT).append(statInputBean.getObjectTypeId()).
                append(whereCondBuilder);

        log.info("multiple properties count: [{}]", sqlBuilter.toString());
        return Arrays.asList(firstKey, sqlBuilter.toString());
    }

    private String joinListStr(List<String> stringList) {
        StringBuilder props = new StringBuilder();
        props.append(" [ ");
        boolean flag = true;
        for (String st : stringList) {
            if (flag) {
                props.append("\"").append(st).append("\"");
                flag = false;
            } else {
                props.append(", ").append("\"").append(st).append("\"");
            }
        }
        props.append(" ] ");
        return props.toString();
    }

}