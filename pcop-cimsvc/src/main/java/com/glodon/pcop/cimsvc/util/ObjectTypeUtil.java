package com.glodon.pcop.cimsvc.util;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.FilteringItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ObjectTypeUtil {

    private static Logger log = LoggerFactory.getLogger(ObjectTypeUtil.class);

    /**
     * 根据ID查询实例
     *
     * @param cds
     * @param objectType
     * @param id
     * @return
     */
    public static Fact queryFactById(CimDataSpace cds, String objectType, String id) {
        if (StringUtils.isBlank(objectType) || StringUtils.isBlank(id)) {
            log.error("error input: objectType=[{}], id=[{}]", objectType, id);
            return null;
        }
        FilteringItem filteringItem = new EqualFilteringItem(CimConstants.ID_PROPERTY_TYPE_NAME, id.trim());
        ExploreParameters ep = new ExploreParameters();
        ep.setDefaultFilteringItem(filteringItem);
        ep.setType(objectType.trim());

        InformationExplorer ie = cds.getInformationExplorer();
        try {
            List<Fact> factList = ie.discoverInheritFacts(ep);
            if (factList != null && factList.size() > 0) {
                Fact firstFact = factList.get(0);
                return firstFact;
            }
        } catch (Exception e) {
            log.error("fact query error", e);
        }
        return null;
    }


}
