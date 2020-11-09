package com.glodon.pcop.cimstatsvc.sql;

import java.util.List;

public class GroupParameters {
    private List<String> attributeValue;
    public GroupParameters(List<String> attributeValue){
        this.attributeValue=attributeValue;
    }

    public String getGroupString() {
        StringBuffer inPartBuffer = new StringBuffer();
        if(attributeValue != null) {
            for (int i = 0; i < attributeValue.size(); i++) {
                String filteringValueStr = attributeValue.get(i);
                inPartBuffer.append(filteringValueStr);
                if (i < attributeValue.size() - 1) {
                    inPartBuffer.append(",");
                }
            }
        }
        return  inPartBuffer.toString();
    }

    public String getGroupExpression() {
        return " GROUP BY "+ getGroupString();
    }
}
