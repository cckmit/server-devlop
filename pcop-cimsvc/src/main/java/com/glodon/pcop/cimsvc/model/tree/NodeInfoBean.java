package com.glodon.pcop.cimsvc.model.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CommonTags;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.*;

@ApiModel(value = "单个节点的信息")
public class NodeInfoBean {
	private String ID;
	private String NAME;
	private String treeDefId;
	@ApiModelProperty(value = "节点类型")
	private NodeType nodeType;
	@ApiModelProperty(value = "tree的父id")
	private String parentId;
	private String relationType;
	@ApiModelProperty(value = "如果节点是对象类型，该字段赋值")
	private String refObjType;
	@ApiModelProperty(value = "逻辑id")
	private String refCimId;
	@ApiModelProperty(value = "移动树的时候之前的原始节点id")
	private String originalNodeId;
	private String filter;
	@ApiModelProperty(value = "子节点数量")
	private Integer childCount;
	@ApiModelProperty(value = "相关实例物理id")
	private String refInstanceRid;
	@ApiModelProperty(value = "相关的行业分类物理id")
	private String refIndustryRid;
	@ApiModelProperty(value = "相关的对象类型物理id")
	private String refObjectTypeRid;
	@ApiModelProperty(value = "相关的属性集物理id")
	private String refDatasetRid;
	@ApiModelProperty(value = "相关的关联关系物理id")
	private String refRelationShipRid;
	@ApiModelProperty(value = "树的层级，根为-1")
	private Integer level;
	@ApiModelProperty(value = "移动排序值")
	private Double idx;
	@ApiModelProperty(value = "附加信息")
	private Map<String, Object> attachedMap = new HashMap<>();
	@ApiModelProperty(value = "树的数据库的物理id")
	private String rid;
	@JsonIgnore
	private Date createTime;
	@JsonIgnore
	private Date updateTime;

	private String creator;
	private String updator;

	private List<NodeInfoBean> childNodes = new ArrayList<>();

	public NodeInfoBean(String treeDefId) {
		this.treeDefId = treeDefId;
	}

	public NodeInfoBean() {
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String NAME) {
		this.NAME = NAME;
	}

	public String getTreeDefId() {
		return treeDefId;
	}

	public void setTreeDefId(String treeDefId) {
		this.treeDefId = treeDefId;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public String getRefObjType() {
		return refObjType;
	}

	public void setRefObjType(String refObjType) {
		this.refObjType = refObjType;
	}

	public String getRefCimId() {
		return refCimId;
	}

	public void setRefCimId(String refCimId) {
		this.refCimId = refCimId;
	}

	public String getOriginalNodeId() {
		return originalNodeId;
	}

	public void setOriginalNodeId(String originalNodeId) {
		this.originalNodeId = originalNodeId;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public Integer getChildCount() {
		return childCount;
	}

	public void setChildCount(Integer childCount) {
		this.childCount = childCount;
	}

	public String getRefInstanceRid() {
		return refInstanceRid;
	}

	public void setRefInstanceRid(String refInstanceRid) {
		this.refInstanceRid = refInstanceRid;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public List<NodeInfoBean> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(List<NodeInfoBean> childNodes) {
		this.childNodes = childNodes;
	}

	public Double getIdx() {
		return idx;
	}

	public void setIdx(Double idx) {
		this.idx = idx;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getUpdator() {
		return updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}

	public Map<String, Object> getAttachedMap() {
		return attachedMap;
	}

	public void setAttachedMap(Map<String, Object> attachedMap) {
		this.attachedMap = attachedMap;
	}

	public List<NodeInfoBean> addChildNode(NodeInfoBean nodeInfoBean) {
		this.childNodes.add(nodeInfoBean);
		return this.childNodes;
	}

	public String getRefIndustryRid() {
		return refIndustryRid;
	}

	public void setRefIndustryRid(String refIndustryRid) {
		this.refIndustryRid = refIndustryRid;
	}

	public String getRefObjectTypeRid() {
		return refObjectTypeRid;
	}

	public void setRefObjectTypeRid(String refObjectTypeRid) {
		this.refObjectTypeRid = refObjectTypeRid;
	}

	public String getRefDatasetRid() {
		return refDatasetRid;
	}

	public void setRefDatasetRid(String refDatasetRid) {
		this.refDatasetRid = refDatasetRid;
	}

	public String getRefRelationShipRid() {
		return refRelationShipRid;
	}

	public void setRefRelationShipRid(String refRelationShipRid) {
		this.refRelationShipRid = refRelationShipRid;
	}

	public enum NodeType {
		INDUSTRY, OBJECT, DATASET, RELATIONSHIP, FILE, INSTANCE, VIRTUNALNODE;
	}

	@Override
	public String toString() {
		return "NodeInfoBean{" +
				"ID='" + ID + '\'' +
				", NAME='" + NAME + '\'' +
				", treeDefId='" + treeDefId + '\'' +
				", nodeType=" + nodeType +
				", parentId='" + parentId + '\'' +
				", relationType='" + relationType + '\'' +
				", refObjType='" + refObjType + '\'' +
				", refCimId='" + refCimId + '\'' +
				", originalNodeId='" + originalNodeId + '\'' +
				", filter='" + filter + '\'' +
				", childCount=" + childCount +
				", refInstanceRid='" + refInstanceRid + '\'' +
				", refIndustryRid='" + refIndustryRid + '\'' +
				", level=" + level +
				", idx=" + idx +
				", attachedMap=" + attachedMap +
				", rid='" + rid + '\'' +
				", createTime=" + createTime +
				", updateTime=" + updateTime +
				", creator='" + creator + '\'' +
				", updator='" + updator + '\'' +
				", childNodes=" + childNodes +
				'}';
	}
}

