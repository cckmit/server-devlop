package com.glodon.pcop.cimstatsvc.sql;

public class Sql {
    private String select;
    private String from;
    private String where;
    private String group;
    private String order;
    private String limit;

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = "SELECT " + select;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = "FROM " + from;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = "WHERE " + where;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = "group by "+ group;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order =  "ORDER BY "+ order;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = "limit BY "+ limit;
    }


    public String getSql() {
        String sql = " ";
        if(this.select != null){
            sql+= ("    " + this.select+ "   ");;
        }
        if(from!= null){
            sql+= from;
        }
        if(where!= null){
            sql+= ("    " + where + "   ");;
        }
        if(group!= null){
            sql+=("    " +group + "   ");;
        }
        if(order!= null){
            sql+=("    " +order+ "   ");;
        }
        if(limit!= null){
            sql+=("    " +limit+ "   ");;
        }

        return sql;
    }

}
