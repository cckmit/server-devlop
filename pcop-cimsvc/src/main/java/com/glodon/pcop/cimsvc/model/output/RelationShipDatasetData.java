package com.glodon.pcop.cimsvc.model.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cimsvc.model.graph.def.RelationshipEdgeData;
import com.glodon.pcop.cimsvc.model.graph.def.RelationshipNodeData;

import java.util.List;

/**
 * @author tangd-a
 */
public class RelationShipDatasetData {
	@JsonProperty("dataset_nodes")
	private List<RelationshipNodeData> relationshipNodeDataList;
	@JsonProperty("dataset_edges")
	private List<RelationshipEdgeData> relationshipEdgeDataList;

	public RelationShipDatasetData(){};


	public RelationShipDatasetData(List<RelationshipNodeData> relationshipNodeDataList, List<RelationshipEdgeData> relationshipEdgeDataList) {
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
