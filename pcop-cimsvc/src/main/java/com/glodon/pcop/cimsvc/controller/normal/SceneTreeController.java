package com.glodon.pcop.cimsvc.controller.normal;

import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.model.tree.NodeInfoBean;
import com.glodon.pcop.cimsvc.service.tree.SceneTreeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "树")
@RestController
@RequestMapping(value = "/sceneTrees")
public class SceneTreeController {
    private static Logger log = LoggerFactory.getLogger(SceneTreeController.class);

    @Autowired
    private SceneTreeService sceneTreeService;

    @ApiOperation(value = "场景树列表", notes = "场景树列表", response = NodeInfoBean.class, responseContainer = "List")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ReturnInfo sceneTreeList(@RequestParam(defaultValue = "true") Boolean filterByPermission,
                                    @RequestHeader(name = "PCOP-USERID") String userId,
                                    @RequestHeader(name = "PCOP-TENANTID") String tenantId) {
        log.info("sceneTreeList(filterByPermission={})", filterByPermission);
        List<NodeInfoBean> childNodes = sceneTreeService.listSceneTreeNodes(tenantId, userId, filterByPermission);
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, childNodes);
        return ri;
    }

}
