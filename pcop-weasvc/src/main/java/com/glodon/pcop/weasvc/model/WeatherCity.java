package com.glodon.pcop.weasvc.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class WeatherCity {
    private String cityName;
    private String cityId;

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

    @JsonIgnore
    public static String getCimName() {
        return "weatherCity";
    }
}
