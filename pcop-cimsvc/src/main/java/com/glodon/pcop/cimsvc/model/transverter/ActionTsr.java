package com.glodon.pcop.cimsvc.model.transverter;

import com.glodon.pcop.cim.common.model.entity.ActionEntity;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.ActionInfoVO;

public class ActionTsr {

    public static ActionEntity voToEntity(ActionInfoVO infoVO) {
        ActionEntity entity = new ActionEntity();

        if (infoVO == null) {
            return null;
        }

        entity.setName(infoVO.getActionName());
        entity.setDesc(infoVO.getActionDesc());
        entity.setExecClassName(infoVO.getActionExecutionClass());

        return entity;
    }

    public static ActionInfoVO entityToVo(ActionEntity entity) {
        ActionInfoVO infoVO = new ActionInfoVO();

        if (entity == null) {
            return null;
        }

        infoVO.setActionName(entity.getName());
        infoVO.setActionDesc(entity.getDesc());
        infoVO.setActionExecutionClass(entity.getExecClassName());

        return infoVO;
    }


}
