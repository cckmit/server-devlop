package com.glodon.pcop.spacialimportsvc.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glodon.pcop.core.models.IdResult;
import com.glodon.pcop.core.tenancy.context.TenantContext;
import com.glodon.pcop.jobapi.JobResponse;
import com.glodon.pcop.jobapi.dto.JobParmDTO;
import com.glodon.pcop.jobapi.dto.JobPropsDTO;
import com.glodon.pcop.jobclt.client.JobInfoClient;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author tangd-a
 * @date 2019/4/25 12:28
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JobClientTest {

    static Logger log = LoggerFactory.getLogger(JobClientTest.class);

    @Autowired
    private JobInfoClient jobInfoClient;


    @Autowired
    private TenantContext tenantContext;


    // @Test
    public void demo1() throws JsonProcessingException {
        /*Integer id = 6300007;
        JobParmDTO jobParmDTO = new JobParmDTO();
        jobParmDTO.setStatus("PAUSED");
        jobParmDTO.setDate(DateUtil.getCurrentDate());
        jobParmDTO.setJobType("pcopAnalyticStorage");
        jobInfoClient.updateStatus(6300007,jobParmDTO);

        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(jobParmDTO);
        log.info(json);*/
        JobPropsDTO jobPropsDTO = new JobPropsDTO();
        // shp导入消息
        jobPropsDTO.setJobName("123123");
        jobPropsDTO.setTypeCode("pcopDataImport");
        // 消息内容
        jobPropsDTO.setParam("321321");


        //log.info(json);
        JobResponse<IdResult> jobResponse = jobInfoClient.add(jobPropsDTO);
        long id = jobResponse.getData().getId();
        System.out.println(id);
    }

    @Test
    public void getJobInfo() {
        tenantContext.setTenantId(3L);

        Long jobId = 1907176301164L;
        JobResponse<JobParmDTO> jobResponse = jobInfoClient.getJobStatus(jobId);
        System.out.println("====job status=====" + new Gson().toJson(jobResponse) + "=======");
    }

}
