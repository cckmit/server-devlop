package com.glodon.pcop.spacialimportsvc.service;

import com.glodon.pcop.jobapi.type.JobStatusEnum;
import com.glodon.pcop.spacialimportsvc.util.JobClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BimfaceCallbackService {
    private static final Logger log = LoggerFactory.getLogger(BimfaceCallbackService.class);

    public Boolean bimfaceCallback(String tenantId, String objectType, String taskId, Long fileId) {
        boolean flag = false;
        try {
            JobClientUtil.sendTaskStatusMq(Long.valueOf(taskId), JobStatusEnum.ENDED.getCode(), tenantId);
            // JobClientUtil.sendTaskStatusMq(taskId, objectType, Long.valueOf(tenantId));
            flag = true;
        } catch (Exception e) {
            log.error("get file fact failed", e);
            JobClientUtil.sendTaskStatusMq(Long.valueOf(taskId), JobStatusEnum.FAIL.getCode(), tenantId);
        }
        return flag;
    }


}
