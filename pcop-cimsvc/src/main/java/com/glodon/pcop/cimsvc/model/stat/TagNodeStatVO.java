package com.glodon.pcop.cimsvc.model.stat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class TagNodeStatVO {
    private String showName;
    private String name;
    private Boolean status;
    private Map<String, Object> stat = new HashMap<>();
    private List<TagNodeStatVO> child;
    private Set<String> relationObjectTypeIds = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<TagNodeStatVO> getChild() {
        return child;
    }


    public void setStatData(Map<String, Map<String, Object>> statData) {
        Map<String, Object> map = new HashMap<>();
        for (String key : statData.keySet()) {
            if (relationObjectTypeIds.contains(key)) {
                Map<String, Object> map2 = statData.get(key);
                for (String key1 : map2.keySet()) {
                    if (map.get(key1) == null) {
                        map.put(key1, map2.get(key1));
                    } else {
                        map.put(key1, Long.parseLong(map.get(key1).toString()) + Long.parseLong(map2.get(key1).toString()));
                    }
                }
            }
        }
        setStat(map);
        return;
    }

    @JsonIgnore
    public Set<String> getRelationObjectTypeIds() {
        return relationObjectTypeIds;
    }

    public void setRelationObjectTypeIds(Set<String> relationObjectTypeIds) {
        this.relationObjectTypeIds = relationObjectTypeIds;
    }

    public Map<String, Object> getStat() {
        return stat;
    }

    public void setStat(Map<String, Object> stat) {
        this.stat = stat;
    }

    public void setChild(List<TagNodeStatVO> child) {
        this.child = child;
        if(child != null) {
            for (TagNodeStatVO tagNodeStatVO : child) {
                relationObjectTypeIds.addAll(tagNodeStatVO.getRelationObjectTypeIds());
            }
        }
    }
}
