package com.glodon.pcop.cimclt.client.fallback;

import com.glodon.pcop.cimapi.DataImportApi;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimapi.model.InstanceQueryInputBean;
import org.springframework.stereotype.Component;

/**
 * @Auther: shiyw-a
 * @Date: 2018/10/10 11:07
 * @Description:
 */
@Component
public class DataImportClientFallback implements DataImportApi {
    @Override
    public ReturnInfo instanceDataQuery(InstanceQueryInputBean conditions, String userId, String tenantId) {
        return null;
    }
}
