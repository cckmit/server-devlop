package com.glodon.pcop.cimsvc.controller.deprecate;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Dimension;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.feature.IndustryTypeFeatures;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.IndustryTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.CommonOperationUtil;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cimapi.common.ReturnInfo;
import com.glodon.pcop.cimsvc.entity.IndustryTypeEntityV1;
import com.glodon.pcop.cimsvc.exception.EntityNotFoundException;
import com.glodon.pcop.cimsvc.model.IndustryTypeBean;
import com.glodon.pcop.cimsvc.model.IndustryTypeTreeBean;
import com.glodon.pcop.cimsvc.model.adapter.IndustryTypeAdapter;
import com.glodon.pcop.cimsvc.service.IndustryTypeService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author yuanjk(yuanjk @ glodon.com), 2018-07-23 14:00:24
 */
@SuppressWarnings("Duplicates")
// @Api(tags = "v1--行业分类")
// @RestController
@RequestMapping("/abort")
public class IndustryTypeDefController {
    static Logger log = LoggerFactory.getLogger(IndustryTypeDefController.class);

    private static final String INDUSTRY_NOT_EXISTS_OR_NOT_BELONG_TO_THIS_TENANT = "industry not exists or not belong to this tenant";

    @Autowired
    private IndustryTypeService industryTypeService;

    @ApiOperation(value = "新增行业分类", notes = "根据输入的对象新增行业分类模型", response = IndustryTypeEntityV1.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "industryType", value = "行业分类实体", required = true, dataType = "IndustryTypeBean", paramType = "body"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header")})
    @RequestMapping(value = "/industryTypes", method = RequestMethod.POST)
    public ReturnInfo addIndustryClass(@RequestBody IndustryTypeBean industryType,
                                       @RequestHeader(name = "PCOP-USERID", required = true) String userId,
                                       @RequestHeader(name = "PCOP-TENANTID", required = true) String tenantId) {
        log.info("addIndustryClass(typeName={}, parentTypeId={})", industryType.getTypeName(), industryType.getParentTypeId());

        ReturnInfo ri;
        if (StringUtils.isBlank(industryType.getTypeName())) {
            String msg = "industry name should not be balnk";
            log.error(msg);
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05040006, msg, null);
            return ri;
        }

        if (StringUtils.isBlank(tenantId)) {
            String msg = "tenant should not be balnk";
            log.error(msg);
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05040006, msg, null);
            return ri;
        }

        IndustryTypeVO itvo = IndustryTypeAdapter.industryAdapter(industryType, userId);
        IndustryTypeVO nItvo = null;
        if (StringUtils.isBlank(industryType.getParentTypeId())) {
            nItvo = IndustryTypeFeatures.addRootIndustryType(CimConstants.defauleSpaceName, itvo);
        } else {
            nItvo = IndustryTypeFeatures.addChildIndustryType(CimConstants.defauleSpaceName, itvo, industryType.getParentTypeId());
        }

        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            Dimension dimension = cds.getDimensionById(nItvo.getIndustryTypeId());
            CommonOperationUtil.addToBelongingTenant(cds, tenantId, dimension);
        } catch (CimDataEngineRuntimeException e) {
            log.error("add industru type to tennat failed", e);
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            log.error("add industru type to tennat failed", e);
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), IndustryTypeAdapter.industryAdapter(nItvo, industryType.getParentTypeId()));

        return ri;
    }

    @ApiOperation(value = "删除行业分类", notes = "根据输入行业分类typeId删除行业分类", response = Boolean.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "typeId", value = "行业分类typeId", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header")})
    @RequestMapping(value = "/industryTypes/{typeId}", method = RequestMethod.DELETE)
    public ReturnInfo deleteIndustryClass(@PathVariable String typeId,
                                          @RequestHeader(name = "PCOP-USERID") String creator,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws EntityNotFoundException {
        log.info("deleteIndustryClass(typeid={})", typeId);
        CimDataSpace cds = null;
        boolean flag = false;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            Dimension dimension = cds.getDimensionById(typeId);
            if (dimension != null && CommonOperationUtil.isTenantContainsData(tenantId, dimension)) {
                flag = IndustryTypeFeatures.removeIndustryType(CimConstants.defauleSpaceName, typeId);
            } else {
                String msg = String.format(INDUSTRY_NOT_EXISTS_OR_NOT_BELONG_TO_THIS_TENANT);
                log.error(msg);
                throw new EntityNotFoundException(EnumWrapper.CodeAndMsg.E05040003, msg);
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), flag);
        return ri;
    }

    @ApiOperation(value = "更新行业分类", notes = "根据输入的typeId和对象更新行业分类", response = Boolean.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "typeId", value = "行业分类typeId", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "industryType", value = "行业分类实体", required = true, dataType = "IndustryTypeBean", paramType = "body"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header")})
    @RequestMapping(value = "/industryTypes/{typeId}", method = RequestMethod.PUT)
    public ReturnInfo updateIndustryClass(@PathVariable String typeId, @RequestBody IndustryTypeBean industryType,
                                          @RequestHeader(name = "PCOP-USERID") String creator,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws EntityNotFoundException, CimDataEngineRuntimeException {
        log.info("updateIndustryClass(typeId={}, typeName={}, parentTypeId={})", typeId, industryType.getTypeName(), industryType.getParentTypeId());
        ReturnInfo ri;
        if (StringUtils.isBlank(industryType.getTypeName())) {
            String msg = "industry name should not be balnk";
            log.error(msg);
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05040006, msg, null);
        } else {
            CimDataSpace cds = null;
            try {
                cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
                Dimension dimension = cds.getDimensionById(typeId);
                if (dimension != null && CommonOperationUtil.isTenantContainsData(tenantId, dimension)) {
                    IndustryTypeVO industryTypeVO = IndustryTypeAdapter.industryAdapter(industryType, "");
                    boolean flag = IndustryTypeFeatures.updateIndustryType(cds, typeId, industryTypeVO);
                    if (flag) {
                        // itvo = IndustryTypeFeatures.getIndustryTypeVOById(CimConstants.defauleSpaceName, typeId);
                        // cds.flushUncommitedData();
                        industryTypeVO = IndustryTypeFeatures.getIndustryTypeVOById(cds, typeId);
                        ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), IndustryTypeAdapter.industryAdapter(industryTypeVO, industryType.getParentTypeId()));
                    } else {
                        log.error("Update Industry Type, Industry type is not defined: {}", typeId);
                        throw new EntityNotFoundException(EnumWrapper.CodeAndMsg.E05040003, "industry type is not defined: " + typeId);
                    }
                } else {
                    String msg = String.format(INDUSTRY_NOT_EXISTS_OR_NOT_BELONG_TO_THIS_TENANT);
                    log.error(msg);
                    throw new EntityNotFoundException(EnumWrapper.CodeAndMsg.E05040003, msg);
                }
            } finally {
                if (cds != null) {
                    cds.closeSpace();
                }
            }
        }
        return ri;
    }

    @ApiOperation(value = "行业分类详情", notes = "根据输入行业分类typeId获取行业分类详情", response = IndustryTypeEntityV1.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "typeId", value = "行业分类typeId", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header")})
    @RequestMapping(value = "/industryTypes/{typeId}", method = RequestMethod.GET)
    public ReturnInfo getIndustryClass(@PathVariable String typeId, @RequestHeader(name = "PCOP-USERID") String creator,
                                       @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws EntityNotFoundException, CimDataEngineRuntimeException {
        log.info("getIndustryClass(typeId={})", typeId);
        IndustryTypeBean itb;
        CimDataSpace cds = null;
        try {
            cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
            Dimension dimension = cds.getDimensionById(typeId);
            if (StringUtils.isNotBlank(tenantId) && dimension != null && CommonOperationUtil.isTenantContainsData(tenantId, dimension)) {
                itb = IndustryTypeAdapter.industryAdapter(IndustryTypeFeatures.getIndustryTypeVOById(cds, typeId), "");
            } else {
                String msg = String.format(INDUSTRY_NOT_EXISTS_OR_NOT_BELONG_TO_THIS_TENANT);
                log.error(msg);
                throw new EntityNotFoundException(EnumWrapper.CodeAndMsg.E05040003, msg);
            }
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), itb);
        return ri;
    }

    @ApiOperation(value = "行业分类树", notes = "根据输入的parentTypeId获取指定层级level（可选，默认全部子类）且typeName包含指定关键字filter（可选，默认不过滤）的行业分类树", response = IndustryTypeTreeBean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentTypeId", value = "行业分类parentTypeId", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "level", value = "行业分类树层级数", required = false, dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "filter", value = "typeName过滤关键词", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "isIncludeObj", value = "是否包含对象及对象的属性集", required = false, dataType = "boolean", paramType = "query"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header")})
    @RequestMapping(value = "/industryTypesTree", method = RequestMethod.GET)
    @Deprecated
    public ReturnInfo getIndustryClassTree(@RequestParam(required = false) String parentTypeId,
                                           @RequestParam(required = false, defaultValue = "2") Integer level,
                                           @RequestParam(required = false) String filter,
                                           @RequestParam(required = false, defaultValue = "true") Boolean isIncludeObj,
                                           @RequestHeader(name = "PCOP-USERID") String creator, @RequestHeader(name = "PCOP-TENANTID") String tenantId)
            throws CimDataEngineRuntimeException, CimDataEngineInfoExploreException {
        log.info("getIndustryClassTree(parentTypeId={}, level={}, filter={}, isIncludeObj={})", parentTypeId, level, filter, isIncludeObj);
        IndustryTypeTreeBean industryTree = new IndustryTypeTreeBean();
        if (StringUtils.isBlank(parentTypeId)) {
            IndustryTypeBean itb = new IndustryTypeBean();
            itb.setTypeId("0");
            industryTree.setParentIndustryType(itb);
            List<IndustryTypeVO> itvos = IndustryTypeFeatures.listIndustryTypesInherit(CimConstants.defauleSpaceName, 0);
            for (IndustryTypeVO itvo : itvos) {
                industryTree.childIndustryList.add(IndustryTypeAdapter.industryTreeAdapter(itvo));
            }
        } else {
            CimDataSpace ids = null;
            try {
                ids = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
                IndustryTypeVO root = IndustryTypeFeatures.getIndustryTypeVOById(ids, parentTypeId);
                if (root != null) {
                    List<IndustryTypeVO> itvos = IndustryTypeFeatures
                            .getChildrenIndustryTypes(CimConstants.defauleSpaceName, parentTypeId,
                                    level - 1);
                    root.setChildrenIndustryTypes(itvos);
                    List<InfoObjectTypeVO> infoObjectTypeVOS = IndustryTypeFeatures.getLinkedInfoObjectTypes(ids, root.getIndustryTypeId());
                    root.setLinkedInfoObjectTypes(infoObjectTypeVOS);
                    industryTree = IndustryTypeAdapter.industryTreeAdapter(root);
                }
            } finally {
                if (ids != null) {
                    ids.closeSpace();
                }
            }
        }
        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), industryTree);
        return ri;
    }

    @ApiOperation(value = "行业分类树搜索", notes = "根据输入的关键词对行业分类，对象模型，属性集进行搜索", response = IndustryTypeTreeBean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyWord", value = "关键词", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "PCOP-USERID", value = "用户id", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "PCOP-TENANTID", value = "租户id", required = true, dataType = "string", paramType = "header")})
    @RequestMapping(value = "/industryTypesTreeSearch", method = RequestMethod.GET)
    public ReturnInfo getIndustryClassTree(@RequestParam(required = false) String keyWord, @RequestHeader(name = "PCOP-USERID") String creator, @RequestHeader(name = "PCOP-TENANTID") String tenantId) {

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200);
        return ri;
    }

    @ApiOperation(value = "行业分类的子类和对象类型", notes = "行业分类的子类和对象类型", response = IndustryTypeVO.class)
    @RequestMapping(value = "/industryTypes/tree", method = RequestMethod.GET)
    public ReturnInfo getIndustryTypeTree(@RequestParam(required = false) String industryTypeRid,
                                          @RequestHeader(name = "PCOP-TENANTID") String tenantId) throws DataServiceModelRuntimeException, EntityNotFoundException {
        log.info("getIndustryTypeTree(industryTypeRid={})", industryTypeRid);
        ReturnInfo ri;
        if (StringUtils.isBlank(tenantId)) {
            String msg = "tenant should not be balnk";
            log.error(msg);
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05040006, msg, null);
        } else {
            ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), industryTypeService.getAllChildIndustryTypesAndLinkedObjectTypes(tenantId, industryTypeRid));
        }
        return ri;
    }


}
