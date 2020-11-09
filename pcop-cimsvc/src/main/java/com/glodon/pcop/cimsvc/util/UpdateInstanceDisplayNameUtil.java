package com.glodon.pcop.cimsvc.util;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relationable;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class UpdateInstanceDisplayNameUtil {
    private static final Logger log = LoggerFactory.getLogger(UpdateInstanceDisplayNameUtil.class);
    private static final String ID = "ID";
    private static final String NAME = "NAME";

    public static boolean updateDisplayName(Fact instanceFact, String nameKey, String relationTypeName, Set<String> treeObjectNames) {
        try {
            String newName;
            if (instanceFact.hasProperty(nameKey) && instanceFact.getProperty(nameKey).getPropertyValue() != null) {
                newName = instanceFact.getProperty(nameKey).getPropertyValue().toString();
            } else {
                log.error("fact not contain property [{}], ID is used as name", nameKey);
                newName = instanceFact.getProperty(ID).getPropertyValue().toString();
            }

            List<Relation> relationList = instanceFact.getAllSpecifiedRelations(relationTypeName, RelationDirection.TWO_WAY);
            if (CollectionUtils.isNotEmpty(relationList)) {
                for (Relation relation : relationList) {
                    Relationable targetRelationable = relation.getFromRelationable();
                    if (targetRelationable.getId().equals(instanceFact.getId())) {
                        targetRelationable = relation.getToRelationable();
                    }

                    if (treeObjectNames.contains(((Fact) targetRelationable).getType())) {
                        if (targetRelationable.hasProperty(NAME)) {
                            targetRelationable.updateProperty(NAME, newName);
                        } else {
                            targetRelationable.addProperty(NAME, newName);
                        }
                    } else {
                        log.debug("not target tree, display name update omitted");
                    }
                }
                return true;
            } else {
                log.info("no tree node link with this instance: [{}]", instanceFact.getId());
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void updateDisplayNameBatch(CimDataSpace cds, String objectTypeId, Set<String> treeObjectNames) throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        ExploreParameters ep = new ExploreParameters();
        ep.setType(objectTypeId);
        ep.setResultNumber(Integer.MAX_VALUE);

        String relationTypeName = "TREE_NODE_TO_RALATIONABLE_RELATION_TYPE";
        List<Fact> factList = cds.getInformationExplorer().discoverInheritFacts(ep);
        log.info("fact size: [{}]", factList.size());
        if (CollectionUtils.isNotEmpty(factList)) {
            int processedSize = 0;
            for (Fact fact : factList) {
                updateDisplayName(fact, NAME, relationTypeName, treeObjectNames);
                processedSize++;
                if (processedSize % 100 == 0) {
                    log.info("process progress: [{}]", processedSize);
                }
            }
        }
    }


}
