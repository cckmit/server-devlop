package com.glodon.pcop.cimsvc.model.graph.def;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.glodon.pcop.cimsvc.model.output.RelationShipDatasetData;

import java.util.Collection;
import java.util.List;

/**
 * @author tangd-a
 * @date 2020/6/16 17:40
 */
public class RelationshipNodeData {
	private String name;
	private String desc;
	private String category;
	private String id;
	@JsonProperty("datasets_data")
	private RelationShipDatasetData relationShipDatasetData;

	public RelationshipNodeData(){}

	public RelationshipNodeData(String name, String desc, String category, String id, RelationShipDatasetData relationShipDatasetData){
		setName(name);
		setDesc(desc);
		setId(id);
		setCategory(category);
		setRelationShipDatasetData(relationShipDatasetData);
	}


	public RelationShipDatasetData getRelationShipDatasetData() {
		return relationShipDatasetData;
	}

	public void setRelationShipDatasetData(RelationShipDatasetData relationShipDatasetData) {
		this.relationShipDatasetData = relationShipDatasetData;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
