package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.common.model.entity.AddDataSetInputBean;
import com.glodon.pcop.cim.common.model.entity.DataSetEntity;
import com.glodon.pcop.cim.common.model.entity.UpdateDataSetInputBean;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimsvc.model.transverter.DataSetTsr;
import com.glodon.pcop.cimsvc.service.v2.engine.DataSetDefService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSetsService {
    private static Logger log = LoggerFactory.getLogger(DataSetsService.class);

    @Autowired
    private DataSetDefService dataSetDefService;

    /**
     * 新增属性集定义，包含属性定义，属性的限制条件定义
     *
     * @param tenantId
     * @param objectTypeId
     * @param dataSetEntity
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws CimDataEngineRuntimeException
     */
    public DataSetEntity addDataSetAndPropertyDef(String tenantId, String objectTypeId,
                                                  AddDataSetInputBean dataSetEntity) throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        DatasetVO datasetVO = DataSetTsr.addInputToVoV2(dataSetEntity, true);
        DatasetDef datasetDef = dataSetDefService.addDataSetAndPropertyDef(tenantId, objectTypeId, datasetVO);
        if (datasetDef == null) {
            log.error("data set add failed");
            return null;
        } else {
            return DataSetTsr.voToEntityV2(dataSetDefService.getDataSetAndPropertyDefWithoutObjectType(tenantId, objectTypeId,
                    datasetDef.getDatasetRID()), true);
        }
    }




    /**
     * 新增属性集定义，包含属性定义，属性的限制条件定义(不包含对象类型)
     *
     * @param userId
     * @param tenantId
     * @param dataSetEntity
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws CimDataEngineRuntimeException
     */
    public DataSetEntity addDataSetAndPropertyDefWithoutObjectType(String userId,String tenantId, AddDataSetInputBean dataSetEntity) throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        DatasetVO datasetVO = DataSetTsr.addInputToVoV2(dataSetEntity, true);
        DatasetDef datasetDef = dataSetDefService.addDataSetAndPropertyDefWithoutObjectType(userId,tenantId, datasetVO);
        if (datasetDef == null) {
            log.error("data set add failed");
            return null;
        } else {
            return DataSetTsr.voToEntityV2(dataSetDefService.getDataSetAndPropertyDefWithoutObjectType(userId,tenantId,
                    datasetDef.getDatasetRID()), true);
        }
    }


    /**
     * 更新属性集，及其关联的属性
     *
     * @param tenantId
     * @param objectTypeId
     * @param dataSetId
     * @param dataSetEntity
     * @return
     * @throws DataServiceModelRuntimeException
     * @throws CimDataEngineRuntimeException
     */
    public DataSetEntity updateDataSetAndPropertyDef(String tenantId, String objectTypeId, String dataSetId,
                                                     UpdateDataSetInputBean dataSetEntity) throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
        DatasetVO datasetVO = DataSetTsr.updateInputToVoV2(dataSetEntity, true);
        DatasetDef datasetDef = dataSetDefService.updateDataSetAndPropertyDef(tenantId, objectTypeId, dataSetId,
                datasetVO);
        if (datasetDef == null) {
            log.error("update data set failed");
            return null;
        } else {
            return DataSetTsr.voToEntityV2(dataSetDefService.getDataSetAndPropertyDef(tenantId, objectTypeId,
                    datasetDef.getDatasetRID()), true);
        }
    }

    /**
     * 删除属性集定义，属性集与对象模型的关系，不删除关联的属性
     *
     * @param tenantId
     * @param objectTypeId
     * @param dataSetId
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public boolean deleteDataSetAndPropertyDef(String tenantId, String objectTypeId, String dataSetId) throws DataServiceModelRuntimeException {
        return dataSetDefService.deleteDataSetAndPropertyDef(tenantId, objectTypeId, dataSetId);
    }

    /**
     * 删除属性集定义，属性集与对象模型的关系，不删除关联的属性
     *
     * @param userId
     * @param tenantId
     * @param dataSetId
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public boolean deleteDataSetAndPropertyDefWithoutObjectType(String userId, String tenantId, String dataSetId) throws DataServiceModelRuntimeException {
        return dataSetDefService.deleteDataSetAndPropertyDefWithoutObjectType(userId, tenantId, dataSetId);
    }

    /**
     * 查询属性集及其关联的属性的定义
     *
     * @param userId
     * @param tenantId
     * @param dataSetId
     * @return
     */
    public DataSetEntity getDataSetAndPropertyDef(String userId, String tenantId, String dataSetId) {
        return DataSetTsr.voToEntityV2(dataSetDefService.getDataSetAndPropertyDefWithoutObjectType(userId, tenantId, dataSetId),
                true);
    }

    public DataSetEntity updateDataSetAndPropertyDefWithoutObjectType(String userId, String tenantId, String dataSetRid,
                                                                      UpdateDataSetInputBean dataSetEntity) throws DataServiceModelRuntimeException, CimDataEngineRuntimeException {
            DatasetVO datasetVO = DataSetTsr.updateInputToVoV2(dataSetEntity, true);
            DatasetDef datasetDef = dataSetDefService.updateDataSetAndPropertyDefWithoutObjectType(userId, tenantId, dataSetRid,
                    datasetVO);
            if (datasetDef == null) {
                log.error("update data set failed");
                return null;
            } else {
                return DataSetTsr.voToEntityV2(dataSetDefService.getDataSetAndPropertyDefWithoutObjectType(userId, tenantId,
                        datasetDef.getDatasetRID()), true);
            }


    }
}
