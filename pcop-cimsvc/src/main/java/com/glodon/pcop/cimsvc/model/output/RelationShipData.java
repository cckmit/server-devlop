package com.glodon.pcop.cimsvc.model.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cimsvc.model.graph.def.RelationshipEdgeData;
import com.glodon.pcop.cimsvc.model.graph.def.RelationshipNodeData;

import java.util.List;

/**
 * @author tangd-a
 * @date 2020/6/16 19:26
 */
public class RelationShipData {
	@JsonProperty("nodes")
	private List<RelationshipNodeData> relationshipNodeDataList;
	@JsonProperty("edges")
	private List<RelationshipEdgeData> relationshipEdgeDataList;

	public RelationShipData(){};


	public RelationShipData(List<RelationshipNodeData> relationshipNodeDataList, List<RelationshipEdgeData> relationshipEdgeDataList) {
		this.relationshipNodeDataList = relationshipNodeDataList;
		this.relationshipEdgeDataList = relationshipEdgeDataList;
	}

	public List<RelationshipNodeData> getRelationshipNodeDataList() {
		return relationshipNodeDataList;
	}

	public void setRelationshipNodeDataList(List<RelationshipNodeData> relationshipNodeDataList) {
		this.relationshipNodeDataList = relationshipNodeDataList;
	}

	public List<RelationshipEdgeData> getRelationshipEdgeDataList() {
		return relationshipEdgeDataList;
	}

	public void setRelationshipEdgeDataList(List<RelationshipEdgeData> relationshipEdgeDataList) {
		this.relationshipEdgeDataList = relationshipEdgeDataList;
	}
}
