package com.glodon.pcop.spacialimportsvc.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glodon.pcop.core.models.IdResult;
import com.glodon.pcop.core.tenancy.context.TenantContext;
import com.glodon.pcop.jobapi.JobResponse;
import com.glodon.pcop.jobapi.dto.JobParmDTO;
import com.glodon.pcop.jobapi.dto.JobPropsDTO;
import com.glodon.pcop.jobclt.client.JobInfoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class JobClientUtil {
    private static final Logger log = LoggerFactory.getLogger(JobClientUtil.class);

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JobInfoClient jobInfoClient;

    @Autowired
    private TenantContext tenantContext;

    @Autowired
    private ObjectMapper objectMapper;

    private static JobInfoClient innerJobInfoClient;
    private static TenantContext innerTenantContext;
    private static ObjectMapper innerObjectMapper;

    @PostConstruct
    public void init() {
        innerJobInfoClient = this.jobInfoClient;
        innerTenantContext = this.tenantContext;
        innerObjectMapper = this.objectMapper;
    }

    /**
     * 发送更改任务消息到MQ--更改文件任务状态
     *
     * @return
     */
    public static void sendTaskStatusMq(Long taskId, String status, String tenantId) {
        innerTenantContext.setTenantId(Long.valueOf(tenantId));

        JobParmDTO jobParmDTO = new JobParmDTO();
        jobParmDTO.setStatus(status);
        jobParmDTO.setDate(DateUtil.getCurrentDate());
        log.info("更改文件任务状态结束{}", DateUtil.getCurrentDate());
        innerJobInfoClient.updateStatus(taskId, jobParmDTO);
    }

    public static void sendTaskStatusMq(String taskId, String status, Long tenantId) {
        innerTenantContext.setTenantId(tenantId);

        JobParmDTO jobParmDTO = new JobParmDTO();
        jobParmDTO.setStatus(status);
        jobParmDTO.setDate(DateUtil.getCurrentDate());
        log.info("更改文件任务状态结束{}", DateUtil.getCurrentDate());
        innerJobInfoClient.updateStatus(Long.valueOf(taskId), jobParmDTO);
    }

    public static Long addTask(String typeCode, String jobNamePrefix, String tenantId, Object obj) throws JsonProcessingException {
        innerTenantContext.setTenantId(Long.valueOf(tenantId));

        JobPropsDTO jobPropsDTO = new JobPropsDTO();
        log.debug("typeCode={}", typeCode);
        jobPropsDTO.setJobName(jobNamePrefix + "-" + DateUtil.getCurrentDateReadable());
        jobPropsDTO.setTypeCode(typeCode);
        // 消息内容
        jobPropsDTO.setParam(innerObjectMapper.writeValueAsString(obj));
        JobResponse<IdResult> jobResponse = innerJobInfoClient.add(jobPropsDTO);
        log.info("Start job response: status code [{}], content [{}]", jobResponse.getCode(), jobResponse.getData());
        return jobResponse.getData().getId();
    }

    public static void getJobStatus(String tenantId) {
        innerTenantContext.setTenantId(Long.valueOf(tenantId));
        Long jobId = 1907176301164L;
        JobResponse<JobParmDTO> jobResponse = innerJobInfoClient.getJobStatus(jobId);
        System.out.println("====job status=====" + JSON.toJSONString(jobResponse) + "=======");
    }

    public static void setInnerJobInfoClient(JobInfoClient innerJobInfoClient) {
        JobClientUtil.innerJobInfoClient = innerJobInfoClient;
    }
}
