package com.glodon.pcop.cimsvc.controller.stat;

import com.glodon.pcop.cim.common.common.ReturnInfo;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.EnumWrapper;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureauImpl.OrientDBCimDataSpaceImpl;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CommonTag;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CommonTags;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.model.stat.PropertyStatVO;
import com.glodon.pcop.cimsvc.model.stat.TagStatInput;
import com.glodon.pcop.cimsvc.model.stat.TagTreeStatInput;
import com.glodon.pcop.cimsvc.model.stat.TagNodeStatVO;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "统计功能")
@RestController
@RequestMapping(value = "/stat")
public class TagStatController {


    public static List<Map<String, Object>> executeQuery(CimDataSpace cimDataSpace, String sql) {
        System.out.println(sql);
        List<Map<String, Object>> list = new ArrayList();
        OrientDBCimDataSpaceImpl orientDBCimDataSpaceImpl = (OrientDBCimDataSpaceImpl) cimDataSpace;
        OResultSet resultSet = orientDBCimDataSpaceImpl.getGraph().getRawGraph().query(sql);
        while (resultSet.hasNext()) {
            Map<String, Object> map = new HashMap<>();
            OResult r = resultSet.next();
            for (String key : r.getPropertyNames()) {
                map.put(key, r.getProperty(key));
            }
            list.add(map);
        }
        resultSet.close();
        return list;
    }

    public Map<String, Map<String, Object>> getData(CimDataSpace cds, String rid, TagStatInput relationDean) {
        String relationType = relationDean.getRelationShipType();
        List<PropertyStatVO> list2 = relationDean.getPropertyList();
        Map<String, Map<String, Object>> resMap = new HashMap<>();

        for (PropertyStatVO propertyStatVO : list2) {
            String property = propertyStatVO.getPropertyName();
            String logic = propertyStatVO.getLogic();
            String propertySource = propertyStatVO.getPropertySource();
            String aliasName = propertyStatVO.getAliasName();

            String sql = "select  in.@class.replace(\"GLD_IH_FACT_\",\"\") as  objectTypeId, count(in." + property + ") as  " + aliasName + " ,in." + property + " as name from `GLD_RELATION_" + relationType + "`  WHERE out = " + rid + "" +
                    " group by in.@class";
            if (logic.toString().toUpperCase().equals("SUM")) {
                sql = "select  in.@class.replace(\"GLD_IH_FACT_\",\"\") as  objectTypeId, sum(in." + property + ") as  " + aliasName + "  ,in." + "@class.replace(\"GLD_IH_FACT_\",\"\")" + " as name from `GLD_RELATION_" + relationType + "`  WHERE out = " + rid + "" +
                        " group by in.@class";
            }
            List<Map<String, Object>> list = new ArrayList<>();
            list = executeQuery(cds, sql);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).get("objectTypeId") != null) {
                    String objectTypeId = list.get(i).get("objectTypeId").toString();
                    if (resMap.get(objectTypeId) == null) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(aliasName, list.get(i).get(aliasName));
                        resMap.put(objectTypeId, map);
                    } else {
                        if (list.get(i).get(aliasName) == null) {
                            resMap.get(objectTypeId).put(aliasName, 0);
                        } else {
                            resMap.get(objectTypeId).put(aliasName, list.get(i).get(aliasName));
                        }
                    }
                }
            }
        }

        return resMap;


    }

    //获取根目录
    @ApiOperation(value = "标签树统计", notes = "标签树，从一个节点找标签中关联的所有实例")
    @PostMapping("/commonTag/treeStat")
    public ReturnInfo tagTreeStat(
            @RequestHeader(name = "PCOP-USERID") String userId,
            @RequestHeader(name = "PCOP-TENANTID") String tenantId,
            @RequestBody List<TagTreeStatInput> tagBeanList
    ) {
        System.out.println(tagBeanList.toString());
        List<Object> res = new ArrayList<>();

        //找到所有标签的树

        CimDataSpace cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
        try {
            CIMModelCore targetCIMModelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName, tenantId);
            targetCIMModelCore.setCimDataSpace(cds);
            CommonTags commonTags = targetCIMModelCore.getCommonTags();
            HashMap<String, TagNodeStatVO> tagNodeTreeCache = new HashMap<>();
            for (int i = 0; i < tagBeanList.size(); i++) {
                TagTreeStatInput tagBean = tagBeanList.get(i);
                String rid = tagBean.getInstanceRid();
                List<Object> res3 = new ArrayList<>();
                HashMap<String, Object> res1 = new HashMap<>();
                for (int j = 0; j < tagBean.getTagList().size(); j++) {
                    TagStatInput relationDean = tagBean.getTagList().get(j);
                    String tagName = relationDean.getTagName();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("tag_name", tagName);
                    Map<String, Map<String, Object>> statData = new HashMap<>();
                    statData = getData(cds, rid, relationDean);
                    TagNodeStatVO tagNodeStatVO = tagNodeTreeCache.get(tagName);
                    if (tagNodeStatVO == null) {
                        CommonTag commonTag = commonTags.getTag(tagName);
                        tagNodeStatVO = commonTagTreeCreate(commonTag,tagNodeTreeCache);
                    }
                    res3.add(commonTagTreeStat(tagNodeStatVO, statData));
                }
                res1.put("tag_data", res3);
                res1.put("instance_rid", rid);
                res.add(res1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cds != null) {
                cds.closeSpace();
            }
        }

        ReturnInfo ri = new ReturnInfo(EnumWrapper.CodeAndMsg.E05000200, EnumWrapper.CodeAndMsg.E05000200.getMsg(), res);
        return ri;
    }
    private TagNodeStatVO commonTagTreeStat(TagNodeStatVO tagNodeStatVO, Map<String, Map<String, Object>> statData) {
        TagNodeStatVO tagNodeStatVO2 = new TagNodeStatVO();
        tagNodeStatVO2.setName(tagNodeStatVO.getName());
        tagNodeStatVO2.setShowName(tagNodeStatVO.getShowName());
        tagNodeStatVO2.setStatus(tagNodeStatVO.getStatus());
        List<TagNodeStatVO> tagNodeStatVOS = new ArrayList<>();
        if (tagNodeStatVO.getChild() != null) {
            for (TagNodeStatVO child : tagNodeStatVO.getChild()) {
                tagNodeStatVOS.add(commonTagTreeStat(child, statData));
            }
        }
        tagNodeStatVO2.setChild(tagNodeStatVOS);
        tagNodeStatVO2.setRelationObjectTypeIds(tagNodeStatVO.getRelationObjectTypeIds());
        //执行计算逻辑
        tagNodeStatVO2.setStatData(statData);
        return tagNodeStatVO2;
    }



    //标签
    private TagNodeStatVO commonTagTreeCreate(CommonTag commonTag ,HashMap<String, TagNodeStatVO> tagNodeTreeCache) {
        TagNodeStatVO tagNodeStatVO = new TagNodeStatVO();
        tagNodeStatVO.setName(commonTag.getTagName());
        tagNodeStatVO.setShowName(commonTag.getTagDesc());
        tagNodeStatVO.setStatus(true);

        //找到当前标签的子标签
        List<TagNodeStatVO> childTagNodeStatVOList = new ArrayList<>();
        List<CommonTag> commonTags = commonTag.getChildTags();
        for (CommonTag child : commonTags) {
            if(tagNodeTreeCache.get(child.getTagName()) == null){
                childTagNodeStatVOList.add(commonTagTreeCreate(child,tagNodeTreeCache));
            }else{
                childTagNodeStatVOList.add(tagNodeTreeCache.get(child.getTagName()));
            }
        }

        //找到当前标签的子类型
        List<InfoObjectDef> infoObjectDefs = commonTag.getAttachedInfoObjectDefs("C_LogicContain", RelationDirection.TWO_WAY);
        for (InfoObjectDef infoObjectDef : infoObjectDefs) {
            TagNodeStatVO infoObjectTypeStatVO = infoObjectTypeTree(infoObjectDef);
            childTagNodeStatVOList.add(infoObjectTypeStatVO);
        }
        tagNodeStatVO.setChild(childTagNodeStatVOList);
        tagNodeTreeCache.put(commonTag.getTagName(),tagNodeStatVO);
        return tagNodeStatVO;
    }

    private TagNodeStatVO infoObjectTypeTree(InfoObjectDef infoObjectDef) {
        String objectTypeId = infoObjectDef.getObjectTypeName();
        TagNodeStatVO tagNodeStatVO = new TagNodeStatVO();
        tagNodeStatVO.setName(objectTypeId);
        tagNodeStatVO.setShowName(infoObjectDef.getObjectTypeDesc());
        tagNodeStatVO.setStatus(false);
        Set<String> set = new HashSet<String>();
        set.add(objectTypeId);
        tagNodeStatVO.setRelationObjectTypeIds(set);
        return tagNodeStatVO;
    }


}
