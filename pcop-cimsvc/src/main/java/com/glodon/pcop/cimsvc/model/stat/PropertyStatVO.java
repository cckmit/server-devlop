package com.glodon.pcop.cimsvc.model.stat;

public class PropertyStatVO {
    private  String propertyName;
    private  String propertySource;
    private  String logic;
    private  String aliasName;


    public String getPropertySource() {
        return propertySource;
    }

    public void setPropertySource(String propertySource) {
        this.propertySource = propertySource;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
}
