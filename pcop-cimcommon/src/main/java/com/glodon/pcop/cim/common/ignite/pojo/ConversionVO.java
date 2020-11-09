package com.glodon.pcop.cim.common.ignite.pojo;

/**
 * @author tangd-a
 * @date 2019/9/6 14:49
 */
public class ConversionVO {

    private String schemaName;
    private String tableName;
    private String primaryKey;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
}
