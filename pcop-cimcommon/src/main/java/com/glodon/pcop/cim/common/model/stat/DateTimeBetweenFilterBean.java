package com.glodon.pcop.cim.common.model.stat;

public class DateTimeBetweenFilterBean {
    private String property;
    // private Long startTime;
    // private Long endTime;
    private String startTime;
    private String endTime;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "DateTimeBetweenFilterBean{" +
                "property='" + property + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
