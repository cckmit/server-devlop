package com.glodon.pcop.cimsvc.service;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.exception.InputErrorException;
import com.glodon.pcop.cimsvc.model.v2.*;
import com.glodon.pcop.cimsvc.model.v2.mapping.ObjectMappingInputBean;
import com.glodon.pcop.cimsvc.service.v2.InstancesService;
import com.glodon.pcop.cimsvc.service.v2.RelationshipsService;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters.FilteringLogic;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Jimmy.Liu(liuzm @ glodon.com), Jul/07/2018.
 */
@Service
public class BizWrapperService {

    private static Logger log = LoggerFactory.getLogger(BizWrapperService.class);
    @Autowired
    private InstancesService instancesService;
    @Autowired
    private RelationshipsService relationshipService;

    /**
     * 根据查询数据类型 bim 3D cim查询相应数据
     * @elementType "BIM"  "3D"  "CIM" 不同查询类型
     **/
    public Object  queryGisBim(String objectTypeId, String elementId, String dataSetName, String dataSetType,
                            String instanceRid,
                            String tenantId,String elementType){
      //  Object resObj = null;
       // SingleQueryOutput singleQueryOutputRes = null;
        if(elementType.equalsIgnoreCase("BIM") ) {
            //bim类型获取
            String relationTypeName = "COMMON_VisualRepresentationOf";
            Map<String,Object> objOuput = getBimData(objectTypeId, relationTypeName, elementId,
                                     tenantId);
            if(objOuput != null ){
                return objOuput;
            }
            else{
                dataSetType = "Object";
                instanceRid = "1";
                dataSetName = "1";
                 CollectionQueryOutput coll = getCimCollection(objectTypeId, dataSetName, dataSetType,
                        instanceRid, elementId, tenantId);
                if(coll != null && coll.getInstances() != null && coll.getInstances().size() > 0){
                    List<CollectionInstancesQueryOutput> listCol = coll.getInstances();
                    Map<String, Object> flatMap = new HashedMap();
                    if(listCol != null) {
                        for (int i = 0; i < listCol.size(); i++) {
                            CollectionInstancesQueryOutput colQ = listCol.get(i);
                            if(colQ != null && colQ.getInstanceData() != null) {
                                Map<String, Object> objs = colQ.getInstanceData();
                                List<Map<String, Object>> vlist = (List<Map<String, Object>>)objs.get("items");
                                for (Map<String,Object>mapObj:vlist) {
                                    if (mapObj != null){
                                        flatMap.put(mapObj.get("key").toString(),mapObj.get("value"));
                                    }
                                }
                            }
                        }
                    }
                    return flatMap ;
                }
                else {
                    dataSetName = null;
                    String idStr = objectTypeId + "_" + elementId;
                    SingleQueryOutput singleQueryOutputRes = getCimSingleData(objectTypeId, dataSetName, idStr, tenantId);
                    if (singleQueryOutputRes != null && singleQueryOutputRes.getInstances() != null && singleQueryOutputRes.getInstances().size() > 0) {
                        SingleInstancesQueryOutput singleOut = singleQueryOutputRes.getInstances().get(0);
                        Map<String, Map<String, Object>> mapCol = singleOut.getInstanceData();
                        Map<String, Object> flatMap = flatMapEentry(mapCol);
                        return flatMap;
                    }
                }
            }
        }
        else if(elementType.equalsIgnoreCase("CIM")) {
            // cim类型获取 直接获取 CIM 对象
            dataSetName = null;
            SingleQueryOutput singleQueryOutputRes = getCimSingleData(objectTypeId, dataSetName,
                                                                 elementId, tenantId);


            if (singleQueryOutputRes != null && singleQueryOutputRes.getInstances() != null && singleQueryOutputRes.getInstances().size() > 0) {
                SingleInstancesQueryOutput singleOut = singleQueryOutputRes.getInstances().get(0);
                Map<String, Map<String, Object>> mapCol = singleOut.getInstanceData();
                Map<String, Object> flatMap = flatMapEentry(mapCol);
                return flatMap;
            }

        }
        return null;
    }
    private Map<String,Object> flatMapEentry( Map<String, Map<String, Object>> mapCol) {
        Map<String, Object> flatMap = new HashedMap();
        for (Map<String, Object> value : mapCol.values()) {
            if(value != null) {
                for (Map.Entry<String, Object> entry : value.entrySet()) {
                    if(entry != null) {
                        flatMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return flatMap;
     }

    public  Map<String,Object> getBimData(String objectTypeId, String relationTypeName, String elementId,
                                                String tenantId){
        //如果bim类型  relationTypeName = 'COMMON_VisualRepresentationOf'
        String idStr = objectTypeId + "_" + elementId;
        ObjectTypeRelatedInstancesQueryInput queryConditions = new ObjectTypeRelatedInstancesQueryInput();
        queryConditions.setId(idStr);
        queryConditions.setPageIndex(0);
        queryConditions.setPageSize(200);
        queryConditions.setRelationDirection(RelationDirection.FROM);
        BasePageableQueryOutput<ObjectTypeRelatedInstancesQueryOutput> resultMapList = null;
        ObjectTypeRelatedInstancesQueryOutput Instance = null;
        try {
            resultMapList =
                    relationshipService.getRelatedInstanceByObjectType(tenantId, objectTypeId, relationTypeName,
                            queryConditions);

        } catch (DataServiceModelRuntimeException e) {
            e.printStackTrace();
        }

        if (resultMapList != null && resultMapList.getInstances() != null && resultMapList.getInstances().size() > 0) {
            List<ObjectTypeRelatedInstancesQueryOutput> list = resultMapList.getInstances();
            Instance = list.get(0);
            if(Instance.getRelatedInstances() != null && Instance.getRelatedInstances().size() == 0)
            {
             //   RelatedInstancesBean reBean = Instance.getRelatedInstances().get(0);
                return null;
            }
            Map<String,Map<String,Object>> mapCol =  Instance.getInstanceData();
            Map<String, Object> flatMap = flatMapEentry(mapCol);
            return flatMap;
        }
        return null;
    }

    public SingleQueryOutput  getCimSingleData(String objectTypeId, String dataSetName,
                                                String elementId,
                                                String tenantId){

            InstancesQueryInput queryConditions = new InstancesQueryInput();
            CommonQueryConditionsBean commonQueryConditionsBean = new CommonQueryConditionsBean();
            commonQueryConditionsBean.setFilterLogical(FilteringLogic.AND);
            commonQueryConditionsBean.setFilterType("EqualFilteringItem");
            //elementid
            commonQueryConditionsBean.setFirstParam(elementId);//"fzxm_zonghexiang_001"
            commonQueryConditionsBean.setPropertyName("ID");
            List<CommonQueryConditionsBean> listb= new ArrayList<>();
            listb.add(commonQueryConditionsBean);
            queryConditions.setConditions(listb);

            SingleQueryOutput result = null;
            try {
                result = instancesService.queryInstanceSingle(tenantId, objectTypeId.trim(),
                                                                        dataSetName, queryConditions);
            } catch (InputErrorException e) {
                e.printStackTrace();
            }

        return result;
    }

   public CollectionQueryOutput getCimCollection(String objectTypeId, String dataSetName, String dataSetType,
                                String instanceRid,String elementId,
                                String tenantId){
       InstancesQueryInput queryConditions = new InstancesQueryInput();
      // String cimid = objectTypeId + "_" + elementId;
       CommonQueryConditionsBean commonQueryConditionsBean = new CommonQueryConditionsBean();
       commonQueryConditionsBean.setFilterLogical(FilteringLogic.AND);
       commonQueryConditionsBean.setFilterType("EqualFilteringItem");
       commonQueryConditionsBean.setFirstParam(elementId);
       commonQueryConditionsBean.setPropertyName("ID");
       List<CommonQueryConditionsBean> listb= new ArrayList<>(); //commonQueryConditionsBean
       listb.add(commonQueryConditionsBean);
       queryConditions.setConditions(listb);
       CollectionQueryOutput collectionResult = null;
       try {
           if (dataSetType.trim().toUpperCase().equals("INSTANCE")) {
               collectionResult = instancesService.queryInstanceCollection(tenantId, objectTypeId, instanceRid,
                       dataSetName, queryConditions);
           } else if (dataSetType.trim().toUpperCase().equals("OBJECT")) {
               collectionResult = instancesService.queryObjectCollection(tenantId, objectTypeId, instanceRid,
                       dataSetName, queryConditions);
           } else {
                  log.error("not support data set type: [{}]", dataSetType);
           }
       } catch (DataServiceModelRuntimeException e) {
           e.printStackTrace();
       } catch (EntityNotFoundException e) {
           e.printStackTrace();
       }
       return collectionResult;
   }

}

