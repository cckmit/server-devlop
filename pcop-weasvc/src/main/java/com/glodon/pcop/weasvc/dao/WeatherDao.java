package com.glodon.pcop.weasvc.dao;




import com.glodon.pcop.weasvc.model.WeatherCity;
import com.glodon.pcop.weasvc.model.WeatherInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherDao extends BaseExDao {
    public static boolean saveWeather(WeatherInfo weather) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day = sdf.format(new Date());
        sdf = new SimpleDateFormat("HH");
        String str1 = sdf.format(new Date());
        String hour = day+"-"+str1;
        String kindName = weather.getCimName();
        Map<String, Object> queryMap = new HashMap<>();
        Map<String, Object> map = object2Map(weather);
        queryMap.put("cityName" ,weather.getCityName());
        queryMap.put("fetchDay",day);
        map.replace("fetchDay" ,day);
        map.replace("fetchHour",hour);

        updateOrInsertOneToCim(kindName,map,queryMap);
        return true;
    }
    public static List<Map<String, Object>> getFetchCitys() {
        String kindName = WeatherCity.getCimName();
        Map<String, Object> queryMap = new HashMap<>();
        return QueryToCim(kindName, queryMap);
    }

    public static boolean saveWeatherCity(WeatherCity city) {
        String kindName = WeatherCity.getCimName();
        Map<String, Object> queryMap = new HashMap<>();
        Map<String, Object> map = object2Map(city);
        queryMap.put("cityName", city.getCityName());
        updateOrInsertOneToCim(kindName, map, queryMap);
        return  true;
    }
}
