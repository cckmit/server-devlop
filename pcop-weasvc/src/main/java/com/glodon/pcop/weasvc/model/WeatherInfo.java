package com.glodon.pcop.weasvc.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.Date;
@ApiModel(value = "WeatherInfo", description = "天气情况")
public class WeatherInfo  implements Serializable {
    // 城市名字
    private String cityName;
    // 城市id
    private String cityId;
    // 天气
    private String condition;
    // 实时天气id
    private String conditionId;
    // 湿度
    private String humidity;
    // 气压
    private String pressure;
    // 体感温度
    private String realFeel;
    // 温度
    private String temp;
    // 紫外线强度
    private String uvi;
    // 能见度高低
    private String visibility;
    // 风向
    private String windDirection;
    // 风力
    private String windForce;
    // 风速
    private String windSpeed;
    // 日出时间
    private Date sunRise;
    // 日落时间
    private Date sunSet;
    // 空气质量指数
    private String aqi;
    // 空气质量详情
    private String aqiDetail;

    //pm25
    private String pm25;
    //噪音
    private String noise;
    //拥堵指数
    private String  congestionIndex;

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getNoise() {
        return noise;
    }

    public void setNoise(String noise) {
        this.noise = noise;
    }

    public String getCongestionIndex() {
        return congestionIndex;
    }

    public void setCongestionIndex(String congestionIndex) {
        this.congestionIndex = congestionIndex;
    }

    // 更新时间
    private Date pubTime;

    // 采集日期(精确到天)
    private String fetchDay;

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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getConditionId() {
        return conditionId;
    }

    public void setConditionId(String conditionId) {
        this.conditionId = conditionId;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getRealFeel() {
        return realFeel;
    }

    public void setRealFeel(String realFeel) {
        this.realFeel = realFeel;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getUvi() {
        return uvi;
    }

    public void setUvi(String uvi) {
        this.uvi = uvi;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindForce() {
        return windForce;
    }

    public void setWindForce(String windForce) {
        this.windForce = windForce;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Date getSunRise() {
        return sunRise;
    }

    public void setSunRise(Date sunRise) {
        this.sunRise = sunRise;
    }

    public Date getSunSet() {
        return sunSet;
    }

    public void setSunSet(Date sunSet) {
        this.sunSet = sunSet;
    }

    public Date getPubTime() {
        return pubTime;
    }

    public void setPubTime(Date pubTime) {
        this.pubTime = pubTime;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getAqiDetail() {
        return aqiDetail;
    }

    public void setAqiDetail(String aqiDetail) {
        this.aqiDetail = aqiDetail;
    }

    public String getFetchDay() {
        return fetchDay;
    }

    public void setFetchDay(String fetchDay) {
        this.fetchDay = fetchDay;
    }

    @JsonIgnore
    public static String getCimName() {
        return "weatherInfo";
    }
}
