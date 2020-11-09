package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.common.model.entity.ActionEntity;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.ActionInfoVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.Actionset;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObject;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.modelImpl.ActionsetDSImpl;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.ActionExecutionResult;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.action.ActionExecution;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.transverter.ActionTsr;
import com.glodon.pcop.cimsvc.service.OrientDBCommonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ActionService {
    private static Logger log = LoggerFactory.getLogger(ActionService.class);

    /**
     * 在指定的实例上采取行动
     *
     * @param tenantId
     * @param objectTypeId
     * @param instanceRid
     * @param actionName
     * @param actionPayload
     * @return
     * @throws DataServiceModelRuntimeException
     */
    public ActionExecutionResult takeAction(String tenantId, String objectTypeId, String instanceRid, String actionName, Map<String, Object> actionPayload) throws DataServiceModelRuntimeException {
        CIMModelCore cimModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            cimModelCore.setCimDataSpace(cds);
            ActionExecutionResult result = null;
            InfoObjectDef infoObjectDef = cimModelCore.getInfoObjectDef(objectTypeId);
            if (StringUtils.isNotBlank(instanceRid)) {
                InfoObject infoObject = infoObjectDef.getObject(instanceRid);
                result = infoObject.executeAction(actionName, actionPayload);
            } else {
                Actionset actionset = infoObjectDef.getActionset();
                ActionInfoVO targetInfoVO = actionset.getActionInfo(actionName);
                if (targetInfoVO == null) {
                    DataServiceModelRuntimeException dataServiceModelRuntimeException = new DataServiceModelRuntimeException();
                    dataServiceModelRuntimeException.setCauseMessage("Action name " + actionName + " not registered for InfoObjectType " + objectTypeId);
                    throw dataServiceModelRuntimeException;
                } else if (targetInfoVO.getActionExecutionClass() == null) {
                    DataServiceModelRuntimeException dataServiceModelRuntimeException = new DataServiceModelRuntimeException();
                    dataServiceModelRuntimeException.setCauseMessage("Action execution class not registered for action name " + actionName);
                    throw dataServiceModelRuntimeException;
                } else {
                    try {
                        String executionClassFullName = targetInfoVO.getActionExecutionClass();
                        Class actionExecutionClass = Class.forName(executionClassFullName);//NOSONAR
                        Object actionExecutionObject = actionExecutionClass.newInstance();
                        if (actionExecutionObject != null && actionExecutionObject instanceof ActionExecution) {
                            ActionExecution actionExecution = (ActionExecution) actionExecutionObject;
                            result = actionExecution.executeAction(cds, objectTypeId, instanceRid, actionPayload);
                        }
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                        DataServiceModelRuntimeException dataServiceModelRuntimeException = new DataServiceModelRuntimeException();
                        dataServiceModelRuntimeException.setCauseMessage("Action execution class " + targetInfoVO.getActionExecutionClass() + " of Action name " + actionName + " init error");
                        throw dataServiceModelRuntimeException;
                    }
                }
            }
            return result;
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
    }

    /**
     * 获取在指定对象类型上已经定义的Actions
     *
     * @param tenantId
     * @param objectTypeId
     * @return
     */
    public List<ActionEntity> getActions(String tenantId, String objectTypeId) {
        Actionset actionset = new ActionsetDSImpl(CimConstants.defauleSpaceName, tenantId, objectTypeId);

        List<ActionInfoVO> infoVOList = actionset.getActionInfos();
        if (infoVOList == null) {
            return null;
        }

        List<ActionEntity> actionEntities = new ArrayList<>();
        for (ActionInfoVO infoVO : infoVOList) {
            actionEntities.add(ActionTsr.voToEntity(infoVO));
        }
        return actionEntities;
    }


}
