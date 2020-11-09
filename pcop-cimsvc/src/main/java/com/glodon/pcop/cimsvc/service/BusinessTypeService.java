package com.glodon.pcop.cimsvc.service;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.GlobalDimensionTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.*;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tangd-a
 * @date 2019/6/12 14:35
 */
@Service
public class BusinessTypeService {
    static Logger log = LoggerFactory.getLogger(BusinessTypeService.class);

    public Map<String, Long> countByBusinessType(String tenantId, List<String> businessTypeIds) {
        Map<String, Long> objectInstanceCount = new HashMap<>(16);
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        Long count = null;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            cimModelCore.setCimDataSpace(cds);
            InfoObjectDefs infoObjectDefs = cimModelCore.getInfoObjectDefs();
            if (businessTypeIds != null && businessTypeIds.size() > 0) {
                for (String businessTypeId : businessTypeIds) {
                    objectInstanceCount.put(businessTypeId, -1L);
                    try {
                        List<Long> sumList = new ArrayList();
                        List<InfoObjectDef> infoObjectDefList = infoObjectDefs.getInfoObjectDefsInBusinessCatalog(businessTypeId);
                        for (InfoObjectDef infoObjectDef : infoObjectDefList) {
                            if (infoObjectDef != null) {
                                count = infoObjectDef.countAllInstance();
                            } else {
                                log.info("===object type not found");
                            }

                        }
                        sumList.add(count);
                        long sum = sumList.stream().mapToLong(Long::longValue).sum();

                        objectInstanceCount.put(businessTypeId, sum);sumList.clear();
                    } catch (DataServiceModelRuntimeException e) {
                        e.printStackTrace();
                    } catch (CimDataEngineInfoExploreException e) {
                        e.printStackTrace();
                    } catch (CimDataEngineRuntimeException e) {
                        e.printStackTrace();
                    }

                }
            }
        }finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return objectInstanceCount;
    }

    public Map<String, Object> countByDataType(String tenantId) {
        HashMap<String, Object> itemDataMap = new HashMap<>();
        List<String> list = new ArrayList<>();
        //城市部件
        list.add("MANG_CityComponent");
        //建筑物
        list.add("UrbanInfrastructure_Building");
        //建筑空间
        list.add("UrbanInfrastructure_BuildingSpace");
        //地下管网
        list.add("UrbanInfrastructure_UndergroundPipeNetwork");
        //交通系统
        list.add("UrbanInfrastructure_TrafficSystem");
        //交通工具
        list.add("UrbanInfrastructure_TrafficTransportation");
        //自然地理
        list.add("PhysicalGeography_GD");

        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            cimModelCore.setCimDataSpace(cds);
            InfoObjectDefs infoObjectDefs = cimModelCore.getInfoObjectDefs();
            InfoObjectDef infoObjectDefCityComponent = infoObjectDefs.getInfoObjectDef(list.get(0));
            itemDataMap.put("CityComponent", infoObjectDefCityComponent.countAllInstance());
            InfoObjectDef infoObjectDefBuilding = infoObjectDefs.getInfoObjectDef(list.get(1));
            InfoObjectDef infoObjectDefBuilding2 = infoObjectDefs.getInfoObjectDef(list.get(2));
            itemDataMap.put("Building", infoObjectDefBuilding.countAllInstance() + infoObjectDefBuilding2.countAllInstance());
            InfoObjectDef infoObjectDefCityNetwork = infoObjectDefs.getInfoObjectDef(list.get(3));
            itemDataMap.put("Network", infoObjectDefCityNetwork.countAllInstance());
            InfoObjectDef infoObjectDefTraffic = infoObjectDefs.getInfoObjectDef(list.get(4));
            itemDataMap.put("Traffic", infoObjectDefTraffic.countAllInstance());
            InfoObjectDef infoObjectDefPhysicalGeography= infoObjectDefs.getInfoObjectDef(list.get(6));
            itemDataMap.put("PhysicalGeography", infoObjectDefPhysicalGeography.countAllInstance());

        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        return itemDataMap;
    }

}
