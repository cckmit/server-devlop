package com.glodon.pcop.spacialimportsvc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.glodon.pcop.cim.common.constant.JobStatusConst;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JobClientUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void init() {
    }

    @Test
    public void sendTaskStatusMq() {
    }

    @Test
    public void sendTaskStatusMq1() {
    }

    @Test
    public void addTask() {
        String typeCode = JobStatusConst.BIM_MODEL_TRANSLATE;
        String jobNamePrefix = JobStatusConst.PCOP_ANALYTIC_STORAGE_JOBNAME_PREFIX;
        Map<String, String> jobBody = new HashMap<>();
        jobBody.put(JobStatusConst.PCOP_ANALYTIC_STORAGE_ID, "410569ea-98c7-49c7-ac0e-7c32756c7699__YF-06-0310-八标段轻量化" +
                ".rvt");
        jobBody.put(JobStatusConst.PCOP_ANALYTIC_STORAGE_FILENAME, "YF-06-0310-八标段轻量化.rvt");
        jobBody.put(JobStatusConst.PCOP_ANALYTIC_STORAGE_JOBTYPE,
                JobStatusConst.PcopAnalyticStorageJobTypeEnum.MODEL_TRANSLATE.toString());

        String tenantId = "3";
        try {
            JobClientUtil.addTask(typeCode, jobNamePrefix, tenantId, jobBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getJobStatus() {
        JobClientUtil.getJobStatus("");
    }
}