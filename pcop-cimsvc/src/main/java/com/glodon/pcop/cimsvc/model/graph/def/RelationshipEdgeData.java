package com.glodon.pcop.cimsvc.model.graph.def;

/**
 * @author tangd-a
 * @date 2020/6/16 17:42
 */
public class RelationshipEdgeData {

	private String name;
	private String desc;
	private String id;
	private String source;
	private String target;

	public RelationshipEdgeData() {
	}

	public RelationshipEdgeData(String name, String desc, String id, String source, String target) {
		setName(name);
		setDesc(desc);
		setId(id);
		setSource(source);
		setTarget(target);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
