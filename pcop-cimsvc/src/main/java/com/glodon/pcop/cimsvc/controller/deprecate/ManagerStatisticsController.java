package com.glodon.pcop.cimsvc.controller.deprecate;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.model.ProjectStatisticsBean;
import com.glodon.pcop.cimsvc.model.SupervisingAndConstructionUnitStatisticBean;
import com.glodon.pcop.cimsvc.model.SupervisingUnitBean;
import com.glodon.pcop.cimsvc.model.WorkerBean;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


/**
 * @author yuanjk(yuanjk @ glodon.com)
 * @date 2018/8/5 15:39
 */
// @Api(tags = "v1--其他服务")
// @RestController
// @RequestMapping("/v1")
@Deprecated
public class ManagerStatisticsController {
    private static Logger log = LoggerFactory.getLogger(ManagerStatisticsController.class);

    @ApiOperation(value = "统计表", notes = "每一个项目，每一个统计周期（暂为一个月）", response = ProjectStatisticsBean.class,
            responseContainer = "list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "String", paramType =
                    "path"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType =
                    "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string",
                    paramType = "header")})
    @RequestMapping(value = "/projectStatistics/{projectId}", method = RequestMethod.GET)
    public ReturnInfo getProjectPerPeriod(
            @PathVariable String projectId,
            @RequestHeader(name = "PCOP-USERID") String creator,
            @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getProjectPerPeriod(projectId={})", projectId);
        List<ProjectStatisticsBean> beanList = new ArrayList<>();
        final String RID = "#10:10";
        ProjectStatisticsBean bean = new ProjectStatisticsBean(RID, "数字中国", "2018 - 08");
        beanList.add(bean);

        ProjectStatisticsBean bean1 = new ProjectStatisticsBean(RID, "数字中国", "2018 - 07");
        beanList.add(bean1);

        ProjectStatisticsBean bean2 = new ProjectStatisticsBean(RID, "数字中国", "2018 - 06");
        beanList.add(bean2);

        ProjectStatisticsBean bean3 = new ProjectStatisticsBean("#20:30", "广联达", "2018 - 08");
        beanList.add(bean3);

        ProjectStatisticsBean bean4 = new ProjectStatisticsBean("#30:30", "百度", "2018 - 05");
        beanList.add(bean4);

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                beanList);
        return ri;
    }

    @ApiOperation(value = "管理人员分析，查询", notes = "查询每一个单位的施工人员详情", response =
            SupervisingAndConstructionUnitStatisticBean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "String", paramType =
                    "path"),
            @ApiImplicitParam(name = "startDate", value = "开始时间", example = "4092599349000", required = true,
                    dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", example = "4092599349000", required = true, dataType
                    = "long", paramType = "query"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType =
                    "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string",
                    paramType = "header")})
    @RequestMapping(value = "/projectWorkerDetail/{projectId}", method = RequestMethod.GET)
    public ReturnInfo getProjectWorkerDetail(@PathVariable String projectId,
                                             @RequestParam(name = "startDate") Long startDate,
                                             @RequestParam(name = "endDate") Long endDate,
                                             @RequestHeader(name = "PCOP-USERID") String creator,
                                             @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getProjectWorkerDetail()");

        List<WorkerBean> list = new ArrayList<>();
        WorkerBean workerBean = new WorkerBean("#10:00", "zhangsan", "512025445225555", "专家", 10, 0.9f);
        list.add(workerBean);
        WorkerBean workerBean1 = new WorkerBean("#11:00", "lisi", "512025445225501", "高级工程师", 5, 0.95f);
        list.add(workerBean1);
        WorkerBean workerBean2 = new WorkerBean("#20:00", "wangwu", "512025445225505", "工程师", 0, 1.0f);
        list.add(workerBean2);

        SupervisingUnitBean supervisingUnitBean1 = new SupervisingUnitBean("#60:01", "监理单位1", list);
        SupervisingUnitBean supervisingUnitBean2 = new SupervisingUnitBean("#61:01", "监理单位2", list);
        List<SupervisingUnitBean> jianliList = new ArrayList<>();
        jianliList.add(supervisingUnitBean1);
        jianliList.add(supervisingUnitBean2);

        SupervisingUnitBean supervisingUnitBean3 = new SupervisingUnitBean("#70:01", "施工单位1", list);
        SupervisingUnitBean supervisingUnitBean4 = new SupervisingUnitBean("#71:01", "施工单位2", list);
        List<SupervisingUnitBean> shigongList = new ArrayList<>();
        shigongList.add(supervisingUnitBean3);
        shigongList.add(supervisingUnitBean4);

        SupervisingAndConstructionUnitStatisticBean supervisingAndConstructionUnitStatisticBean =
                new SupervisingAndConstructionUnitStatisticBean(shigongList, jianliList);

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(),
                supervisingAndConstructionUnitStatisticBean);
        return ri;
    }

}
