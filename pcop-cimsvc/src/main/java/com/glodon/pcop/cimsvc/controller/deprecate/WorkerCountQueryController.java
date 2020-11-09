package com.glodon.pcop.cimsvc.controller.deprecate;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.model.worker.WorkerPerProjectBean;
import com.glodon.pcop.cimsvc.model.worker.WorkerPerTeamBean;
import com.glodon.pcop.cimsvc.model.worker.WorkerPerTypeBean;
import com.glodon.pcop.cimsvc.service.WorkerCountService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * @author yuanjk(yuanjk @ glodon.com)
 * @date 2018/8/5 15:39
 */
// @Api(value = "/", tags = "施工人数")
// @RestController
// @RequestMapping("/v1")
// @Deprecated
public class WorkerCountQueryController {
    private static Logger log = LoggerFactory.getLogger(WorkerCountQueryController.class);

    @Autowired
    private WorkerCountService wcs;


    @ApiOperation(value = "项目人数", notes = "每个项目实时在场人数和在场总数 (随机项目，需要15个)", response = WorkerPerProjectBean.class, responseContainer = "list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header") })
    @RequestMapping(value = "/projectWorkers/{projectId}", method = RequestMethod.GET)
    public ReturnInfo getWorkerCountPerProject(
            @PathVariable(name = "projectId") String projectId,
            @RequestHeader(name = "PCOP-USERID") String creator,
            @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getWorkerCountPerProject(projectId={})", projectId);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), wcs.getWorkerCountPerProject());
        return ri;
    }

    @ApiOperation(value = "队伍人数", notes = "每一个队伍的人数", response = WorkerPerTeamBean.class, responseContainer = "list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header") })
    @RequestMapping(value = "/teamWorkers/{projectId}", method = RequestMethod.GET)
    public ReturnInfo getWorkerCountPerTeam(@PathVariable String projectId,
                                            @RequestHeader(name = "PCOP-USERID") String creator,
                                            @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getWorkerCountPerTeam(projectId={})", projectId);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), wcs.getWorkerCountPerTeam());
        return ri;
    }

    @ApiOperation(value = "工种人数", notes = "每一个工种的人数", response = WorkerPerTypeBean.class, responseContainer = "list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header") })
    @RequestMapping(value = "/workTypeWorkers/{projectId}", method = RequestMethod.GET)
    public ReturnInfo getWorkerCountPerType(@PathVariable String projectId,
                                            @RequestHeader(name = "PCOP-USERID") String creator,
                                            @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("getWorkerCountPerType(projectId={})", projectId);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), wcs.getWorkerCountPerWorkType());
        return ri;
    }

}
