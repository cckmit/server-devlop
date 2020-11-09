package com.glodon.pcop.weasvc.model;

import java.util.Date;

//空气指数标准
public class Aqi {
    private String cityName; //城市名称
    private String cityId; //城市名称
    private String co; //co一氧化碳指数
    private String coC;//co一氧化碳浓度
    private String no2;//no2二氧化氮指数
    private String no2C;//no2二氧化氮指数
    private String so2;  //二氧化硫
    private String so2C; //二氧化硫浓度
    private String o3; //臭氧指数
    private String o3C;//臭氧浓度
    private String pm10;//pm1.0指数
    private String pm10C;//pm1.0浓度
    private String pm25;//pm2.5指数
    private String pm25C; //pm2.5浓度
    private String value; //空气指数值
    private Date pubtime; //发布时间

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getCoC() {
        return coC;
    }

    public void setCoC(String coC) {
        this.coC = coC;
    }

    public String getNo2() {
        return no2;
    }

    public void setNo2(String no2) {
        this.no2 = no2;
    }

    public String getNo2C() {
        return no2C;
    }

    public void setNo2C(String no2C) {
        this.no2C = no2C;
    }

    public String getO3() {
        return o3;
    }

    public void setO3(String o3) {
        this.o3 = o3;
    }

    public String getO3C() {
        return o3C;
    }

    public void setO3C(String o3C) {
        this.o3C = o3C;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getPm10C() {
        return pm10C;
    }

    public void setPm10C(String pm10C) {
        this.pm10C = pm10C;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getPm25C() {
        return pm25C;
    }

    public void setPm25C(String pm25C) {
        this.pm25C = pm25C;
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }

    public String getSo2C() {
        return so2C;
    }

    public void setSo2C(String so2C) {
        this.so2C = so2C;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getPubtime() {
        return pubtime;
    }

    public void setPubtime(Date pubtime) {
        this.pubtime = pubtime;
    }

}
