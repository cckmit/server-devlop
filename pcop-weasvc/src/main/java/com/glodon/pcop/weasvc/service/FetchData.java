package com.glodon.pcop.weasvc.service;

import com.glodon.pcop.cim.engine.dataServiceCache.CacheUtil;
import com.glodon.pcop.cim.engine.dataServiceCache.CimCacheManager;
import com.glodon.pcop.weasvc.dao.WeatherDao;
import com.glodon.pcop.weasvc.model.WeatherCity;
import com.glodon.pcop.weasvc.model.WeatherCode;
import com.glodon.pcop.weasvc.model.WeatherInfo;
import com.glodon.pcop.weasvc.weather.DefaultWeather;
import com.glodon.pcop.weasvc.weather.XiaoMiWeather;
import org.apache.commons.io.FileUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//采集天气信息

@Component
public class FetchData {
    @Autowired
    public XiaoMiWeather xiaoMiWeather;

    private String cacheName = "CIM_WEA_SVC_CACHE";

    private static final Logger mLog = LoggerFactory.getLogger(FetchData.class);


    private static HashMap<String, String> cityIdMap = new HashMap<>();
    private static List<WeatherCode> weatherList = new ArrayList();

    static {
        initWeatherKind();
        initCityIdMap();
    }

    private String getWeatherDesc(String index) {
        for (int i = 0; i < weatherList.size(); i++) {
            if ((weatherList.get(i).getCode() + "").equals(index)) {
                return weatherList.get(i).getWeather();
            }
        }
        return "未知";
    }



    public static void initWeatherKind() {
        String content = "{\"weatherinfo\":[{\"code\":0,\"wea\":\"晴\"},{\"code\":1,\"wea\":\"多云\"},{\"code\":2,\"wea\":\"阴\"},{\"code\":3,\"wea\":\"阵雨\"},{\"code\":4,\"wea\":\"雷阵雨\"},{\"code\":5,\"wea\":\"雷阵雨并伴有冰雹\"},{\"code\":6,\"wea\":\"雨夹雪\"},{\"code\":7,\"wea\":\"小雨\"},{\"code\":8,\"wea\":\"中雨\"},{\"code\":9,\"wea\":\"大雨\"},{\"code\":10,\"wea\":\"暴雨\"},{\"code\":11,\"wea\":\"大暴雨\"},{\"code\":12,\"wea\":\"特大暴雨\"},{\"code\":13,\"wea\":\"阵雪\"},{\"code\":14,\"wea\":\"小雪\"},{\"code\":15,\"wea\":\"中雪\"},{\"code\":16,\"wea\":\"大雪\"},{\"code\":17,\"wea\":\"暴雪\"},{\"code\":18,\"wea\":\"雾\"},{\"code\":19,\"wea\":\"冻雨\"},{\"code\":20,\"wea\":\"沙尘暴\"},{\"code\":21,\"wea\":\"小雨-中雨\"},{\"code\":22,\"wea\":\"中雨-大雨\"},{\"code\":23,\"wea\":\"大雨-暴雨\"},{\"code\":24,\"wea\":\"暴雨-大暴雨\"},{\"code\":25,\"wea\":\"大暴雨-特大暴雨\"},{\"code\":26,\"wea\":\"小雪-中雪\"},{\"code\":27,\"wea\":\"中雪-大雪\"},{\"code\":28,\"wea\":\"大雪-暴雪\"},{\"code\":29,\"wea\":\"浮沉\"},{\"code\":30,\"wea\":\"扬沙\"},{\"code\":31,\"wea\":\"强沙尘暴\"},{\"code\":32,\"wea\":\"飑\"},{\"code\":33,\"wea\":\"龙卷风\"},{\"code\":34,\"wea\":\"若高吹雪\"},{\"code\":35,\"wea\":\"轻雾\"},{\"code\":53,\"wea\":\"霾\"},{\"code\":99,\"wea\":\"未知\"}]}";
        JSONObject root = new JSONObject().fromObject(content);
        JSONArray list = root.getJSONArray("weatherinfo");
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.getJSONObject(i);
            WeatherCode weatherCode = new WeatherCode();
            weatherCode.setCode(obj.getInt("code"));
            weatherCode.setWeather(obj.getString("wea"));
            weatherList.add(weatherCode);
        }
    }

    //读取地区表 确认要抓取那些数据
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void getWeathers() {
        List<Map<String, Object>> list = WeatherDao.getFetchCitys();
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            getWeatherByName((String) list.get(i).get("cityName"), false);
        }
    }

    public static void initCityIdMap() {
        System.out.println("读取城市配置文件");
        ClassPathResource classPathResource = new ClassPathResource("cityId.json");
        InputStream stream = null;
        try {
            stream = classPathResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            br.close();
        } catch (FileNotFoundException e) {
            mLog.info("FileNotFoundException:" + e);
        } catch (IOException e) {
            mLog.info("IOException:" + e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    mLog.info("close br error:" + e);
                }
            }
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    mLog.info("close strem error:" + e);
                }
            }
        }

        String content = sb.toString();
        JSONObject root = new JSONObject().fromObject(content);
        JSONArray list = root.getJSONArray("data");
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.getJSONObject(i);
            cityIdMap.put(obj.getString("city"), obj.getString("id"));
        }
    }

    //获取天气种类
    public List<WeatherCode> getWeatherKind() {
        return weatherList;
    }

    //获取天气  //目前只采用了小米天气
    public WeatherInfo getWeatherByName(String name, boolean fetch) {
        String  cacheKey = name;
//        Cache<String,WeatherInfo> cache = CacheUtil.getOrCreateCache(cacheName, String.class,WeatherInfo.class,180l);
        Cache<String,WeatherInfo> cache = CimCacheManager.getOrCreateCache(cacheName, String.class,WeatherInfo.class);
        if (cache != null) {
            if (cache.containsKey(cacheKey)) {
                return  cache.get(cacheKey);
            }
        }

        String cityId = cityIdMap.get(name);
        WeatherInfo weather;
        if (cityId != null) {
            weather = xiaoMiWeather.getWeatherByCityId(cityId, name);
            cache.put(cacheKey,weather);

            weather.setCondition(getWeatherDesc(weather.getConditionId()));
            if (weather != null) {
               WeatherDao.saveWeather(weather);

                if(fetch){
                    WeatherCity city = new WeatherCity();
                    city.setCityId(cityId);
                    city.setCityName(name);
                    WeatherDao.saveWeatherCity(city);
                }
                return weather;
            }
        }

        WeatherDao.saveWeather(DefaultWeather.getWeatherByCityId(cityId, name));

        return null;
    }
}
