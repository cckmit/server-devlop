package com.glodon.pcop.cimsvc.service.v2;

import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Dimension;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Relation;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.RelationDirection;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationFiltering.EqualFilteringItem;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cimsvc.model.input.TreeNodeInputBean;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContentInstanceService {
    private static Logger log = LoggerFactory.getLogger(ContentInstanceService.class);

    /**
     * 获取指定实例关联的所有分类的名称和rid
     *
     * @param parentNode
     * @param cds
     * @return
     */
    public static Map<String, String> getAllChildIndustryNamesByType(TreeNodeInputBean parentNode, CimDataSpace cds) {//NOSONAR
        Map<String, String> industryNames = new HashMap<>();
        Fact fact = null;
        try {
            fact = cds.getFactById(parentNode.getInstanceRid());
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }
        if (fact != null) {
            List<Relation> industryRelations = null;
            try {
                ExploreParameters ep = new ExploreParameters();
                ep.addRelatedRelationType(BusinessLogicConstant.INDUSTRYTYPE_INFOOBJECT_LINK_RELATION_TYPE_NAME);
                EqualFilteringItem filteringItem = new EqualFilteringItem(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_INDUSTRY_RID, parentNode.getParentIndustryRid());
                ep.setDefaultFilteringItem(filteringItem);
                industryRelations = fact.getSpecifiedRelations(ep, RelationDirection.TO);
            } catch (CimDataEngineRuntimeException e) {
                e.printStackTrace();
            }
            if (industryRelations != null && industryRelations.size() > 0) {
                for (Relation relation : industryRelations) {
                    Dimension dimension = (Dimension) relation.getToRelationable();
                    if (dimension.getType().equals(BusinessLogicConstant.INDUSTRY_TYPE_DIMENSION_TYPE_NAME)) {
                        if (dimension.hasProperty(CimConstants.IndustryTypeNodeKeys.INDUSTRY_NAME)) {
                            industryNames.put(dimension.getProperty(CimConstants.IndustryTypeNodeKeys.INDUSTRY_NAME).getPropertyValue().toString(), dimension.getId());
                        } else {
                            log.error("industry type name of {} is null", dimension.getId());
                        }
                    }
                }
            }
        } else {
            log.error("instance not found");
        }
        return industryNames;
    }


    /**
     * 获取指定实例关联的所有文件的名称和rid
     *
     * @param parentNode
     * @param cds
     * @return
     */
    public static Map<String, String> getAllChildFilesNamesByType(TreeNodeInputBean parentNode, CimDataSpace cds) {
        Map<String, String> fileNames = new HashMap<>();
        Fact fact = null;
        try {
            fact = cds.getFactById(parentNode.getInstanceRid());
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }
        ExploreParameters ep = new ExploreParameters();
        ep.addRelatedRelationType(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_TYPE_NAME);
        EqualFilteringItem filteringItem = new EqualFilteringItem(BusinessLogicConstant.INSTANCE_RELATED_FILES_RELATION_INDUSTRY_RID, parentNode.getParentIndustryRid());
        ep.setDefaultFilteringItem(filteringItem);
        List<Relation> filesRelations = null;
        try {
            filesRelations = fact.getSpecifiedRelations(ep, RelationDirection.TO);
            if (filesRelations != null && filesRelations.size() > 0) {
                for (Relation relation : filesRelations) {
                    Fact fileFact = (Fact) relation.getFromRelationable();
                    if (fileFact.getType().equals(CimConstants.BaseFileInfoKeys.BaseFileObjectTypeName)) {
                        if (fact.hasProperty(CimConstants.BaseFileInfoKeys.FILE_DATA_NAME)) {
                            fileNames.put(fact.getProperty(CimConstants.BaseFileInfoKeys.FILE_DATA_NAME).getPropertyValue().toString(), fact.getId());
                        } else {
                            log.error("file name of file type is null");
                        }
                    }
                }
            }
        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        }
        return fileNames;
    }


    /**
     * 查询实例的子名称
     *
     * @param parentNode
     * @param cds
     * @param nodeType
     * @return
     */
    public static Map<String, String> getAllChildNamesByType(TreeNodeInputBean parentNode, CimDataSpace cds, TreeNodeInputBean.TreeNodeTypeEnum nodeType) {
        Map<String, String> names = new HashMap<>();
        switch (nodeType) {
            case FILE:
                names = getAllChildFilesNamesByType(parentNode, cds);
                break;
            case INDUSTRY:
                names = getAllChildIndustryNamesByType(parentNode, cds);
                break;
            default:
                log.error("not support node type");
                break;
        }
        return names;
    }

}
