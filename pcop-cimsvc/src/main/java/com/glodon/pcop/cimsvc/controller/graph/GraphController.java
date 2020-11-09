package com.glodon.pcop.cimsvc.controller.graph;

import com.glodon.pcop.cim.common.model.graph.TubulationAnalysisInputBean;
import com.glodon.pcop.cim.common.model.graph.TubulationAnalysisOutputBean;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.input.ConnectedInputBean;
import com.glodon.pcop.cimsvc.model.output.ConnectedOutputBean;
import com.glodon.pcop.cimsvc.service.graph.GraphAnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "关联关系分析")
@RestController
@RequestMapping(path = "/graph")
public class GraphController {
    private static final Logger log = LoggerFactory.getLogger(GraphController.class);

    @Autowired
    private GraphAnalysisService graphAnalysisService;

    @ApiOperation(value = "实例是否连通", notes = "实例是否连通", response = ConnectedOutputBean.class)
    @RequestMapping(path = "/connected", method = RequestMethod.POST)
    public ReturnInfo shortestPath(@RequestBody ConnectedInputBean conditions,
                                   @RequestHeader(name = "PCOP-USERID", required = false) String creator,
                                   @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId) {
        log.info("shortestPath(conditions={})", conditions);
        ConnectedOutputBean data = graphAnalysisService.instanceConnected(conditions);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, data);
        return ri;
    }

    @ApiOperation(value = "爆管分析", notes = "爆管分析-二次关阀", response = TubulationAnalysisOutputBean.class)
    @RequestMapping(path = "/tubulationAnalysis", method = RequestMethod.POST)
    public ReturnInfo tubulationAnalysis(@Validated @RequestBody TubulationAnalysisInputBean conditions,
                                         @RequestHeader(name = "PCOP-USERID", required = false) String creator,
                                         @RequestHeader(name = "PCOP-TENANTID", required = false) String tenantId)
            throws EntityNotFoundException {
        log.info("tubulationAnalysis(conditions={})", conditions);
        TubulationAnalysisOutputBean data = graphAnalysisService.tubulationAnalysis(tenantId, conditions);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, data);
        return ri;
    }

}
