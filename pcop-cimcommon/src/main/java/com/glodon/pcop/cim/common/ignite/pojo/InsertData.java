package com.glodon.pcop.cim.common.ignite.pojo;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * ignite Sql 模式下的插入对象
 *
 * @author tangd-a
 * @date 2019/9/5 10:48
 */
public class InsertData {
    private Object[] args;
    private String insertFields;
    private String insertParams;

    public InsertData(String key, String primaryKey, Map<String, Object> values) {
        args = new Object[values.size() + 1];
        int idx = 0;
        args[idx++] = key;

        StringBuilder sbFields = new StringBuilder(primaryKey);
        StringBuilder sbParams = new StringBuilder("?");

        for (Map.Entry<String, Object> e : values.entrySet()) {
            args[idx++] = e.getValue();
            sbFields.append(',').append(e.getKey());
            sbParams.append(", ?");
        }
        this.insertFields = sbFields.toString();
        this.insertParams = sbParams.toString();
    }


    public InsertData(String primaryKey, Map<String, Object> values) {
        args = new Object[values.size()];
        int idx = 0;
        args[idx++] = values.get(primaryKey);

        Map<String, Object> filterMap = values.entrySet().stream().filter(map -> !primaryKey.equals(map.getKey())).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        StringBuilder sbFields = new StringBuilder(primaryKey);
        StringBuilder sbParams = new StringBuilder("?");

        for (Map.Entry<String, Object> e : filterMap.entrySet()) {
            args[idx++] = e.getValue();
            sbFields.append(',').append(e.getKey());
            sbParams.append(", ?");
        }
        this.insertFields = sbFields.toString();
        this.insertParams = sbParams.toString();
    }

    public Object[] getArgs() {
        return args;
    }

    public String getInsertFields() {
        return insertFields;
    }

    public String getInsertParams() {
        return insertParams;
    }

}
