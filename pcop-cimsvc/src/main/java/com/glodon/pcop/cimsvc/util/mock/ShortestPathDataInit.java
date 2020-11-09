package com.glodon.pcop.cimsvc.util.mock;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineDataMartException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShortestPathDataInit {
    private static final Logger log = LoggerFactory.getLogger(ShortestPathDataInit.class);

    public String objectTypeName = "short_path_vertex_test";
    public String edgeTypeName = "short_path_edge_test";

    public void typeInit(CimDataSpace cds) throws CimDataEngineDataMartException {
        if (cds.hasInheritFactType(objectTypeName)) {
            log.info("Object type [{}] is already exists", objectTypeName);
        } else {
            cds.addInheritFactType("short_path_vertex_test");
        }

        if (cds.hasRelationType(edgeTypeName)) {
            log.info("relation type [{}] is already exists", edgeTypeName);
        } else {
            cds.addRelationType("short_path_edge_test");
        }
    }

    public void addVertexAndEdge(CimDataSpace cds) {
        String id = "ID";
        String idPrefix = "ID-";
        String name = "NAME";
        String namePrefix = "NM-";
        Map<String, Fact> factMap = new HashMap<>();
        int vertexNum = 13;
        for (int i = 0; i < vertexNum; i++) {
            Fact fact = CimDataEngineComponentFactory.createFact(objectTypeName);
            String idValue = idPrefix + i;
            fact.setInitProperty(id, idValue);
            String nameValue = namePrefix + i;
            fact.setInitProperty(name, nameValue);
            try {
                log.info("add fact id [{}], name [{}]", idValue, nameValue);
                fact = cds.addFact(fact);
                factMap.put(idValue, fact);
            } catch (CimDataEngineRuntimeException e) {
                log.error("ad fact failed [{}]", idValue);
            }
        }

        List<String[]> relationMap = new ArrayList<>();
        String[] r0 = {"ID-1", "ID-2"};
        relationMap.add(r0);

        String[] r1 = {"ID-1", "ID-3"};
        relationMap.add(r1);

        String[] r2 = {"ID-1", "ID-4"};
        relationMap.add(r2);

        String[] r3 = {"ID-2", "ID-5"};
        relationMap.add(r3);

        String[] r4 = {"ID-2", "ID-6"};
        relationMap.add(r4);

        String[] r5 = {"ID-4", "ID-7"};
        relationMap.add(r5);

        String[] r6 = {"ID-4", "ID-8"};
        relationMap.add(r6);

        String[] r7 = {"ID-5", "ID-9"};
        relationMap.add(r7);

        String[] r8 = {"ID-5", "ID-10"};
        relationMap.add(r8);

        String[] r9 = {"ID-7", "ID-11"};
        relationMap.add(r9);

        String[] r10 = {"ID-7", "ID-12"};
        relationMap.add(r10);

        for (String[] rr : relationMap) {
            String fromId = rr[0];
            String toId = rr[1];
            try {
                Fact fromFact = factMap.get(fromId);
                Fact toFact = factMap.get(toId);
                Relation relation = fromFact.addToRelation(toFact, edgeTypeName);
                log.info("add relation [{}], fromFact [{}], toFact [{}]", relation.getId(), fromId, toId);
            } catch (Exception e) {
                log.error("add relation fromFact [{}] toFact [{}] failed", fromId, toId);
            }
        }
    }

}
