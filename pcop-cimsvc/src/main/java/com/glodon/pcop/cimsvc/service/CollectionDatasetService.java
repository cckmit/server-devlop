package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import com.glodon.pcop.cimsvc.exception.QueryConditionException;
import com.glodon.pcop.cimsvc.model.QueryConditionsBean;
import com.glodon.pcop.cimsvc.util.QueryConditionsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CollectionDatasetService {
    private static Logger log = LoggerFactory.getLogger(CollectionDatasetService.class);


    /**
     * 解析实例查询参数
     *
     * @param objectTypeId
     * @param instanceId
     * @param conditions
     * @return
     * @throws QueryConditionException
     */
    public ExploreParameters queryParameters(String objectTypeId, String instanceId, List<QueryConditionsBean> conditions, boolean isIncludeCollectionDataSet) throws QueryConditionException {
        ExploreParameters ep = new ExploreParameters();
        ep.setType(objectTypeId);
        ep.setDefaultFilteringItem(new EqualFilteringItem(CimConstants.ID_PROPERTY_TYPE_NAME, instanceId));
        for (QueryConditionsBean bean : conditions) {
            FilteringItem item = QueryConditionsUtil.parseQueryCondition(objectTypeId, bean, isIncludeCollectionDataSet);
            if (item != null) {
                ep.addFilteringItem(item, bean.getFilterLogical());
            } else {
                log.error("Query condition parser failed");
                throw new QueryConditionException(EnumWrapper.CodeAndMsg.E05040001);
            }
        }
        return ep;
    }


}
