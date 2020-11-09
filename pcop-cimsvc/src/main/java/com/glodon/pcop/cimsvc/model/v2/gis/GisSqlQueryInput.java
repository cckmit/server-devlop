package com.glodon.pcop.cimsvc.model.v2.gis;

public class GisSqlQueryInput {
    private String querySql;

    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }

    @Override
    public String toString() {
        return "GisSqlQueryInput{" +
                "querySql='" + querySql + '\'' +
                '}';
    }
}
