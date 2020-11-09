package com.glodon.pcop.weasvc.weather;


import com.glodon.pcop.weasvc.model.Aqi;
import com.glodon.pcop.weasvc.model.WeatherInfo;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.glodon.pcop.weasvc.util.WeatherUtil.getWindForce;

@Component
public class XiaoMiWeather {

    @Autowired
    public  RestTemplate restTemplate;

    //获取天气
    public  WeatherInfo getWeatherByCityId(String cityID, String cityName){
        String latitude = "100.00";
        String longitude = "100.00";
        String code =  cityID;

        String weatherUrl = "https://weatherapi.market.xiaomi.com/wtr-v3/weather/all?latitude=:LATITUDE&longitude=:LONGITUDE&locationKey=weathercn::CODE&days=1&appKey=weather20151024&sign=zUFJoAR2ZVrDy1vF3D07&isGlobal=false&locale=zh_cn";
        weatherUrl = weatherUrl.replace(":LATITUDE",latitude).replace(":LONGITUDE",longitude).replace(":CODE",code);
        HttpHeaders requestHeaders = new HttpHeaders();

        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
//        RestTemplate restTemplate = new  RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(weatherUrl, HttpMethod.GET, requestEntity, String.class);
        String content = response.getBody();

        if(response.getStatusCode() != HttpStatus.OK){
            return  null;
        }
        JSONObject root = new JSONObject().fromObject(content);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");


        System.out.println(root.getJSONObject("aqi"));
        //解析空气质量相关的
        Aqi aqi = new Aqi();
        JSONObject aqiInfo = root.getJSONObject("aqi");
        aqi.setCityName(cityName);
        aqi.setCityId(cityID);
        aqi.setCo(aqiInfo.getString("co"));
        aqi.setNo2(aqiInfo.getString("no2"));
        aqi.setO3(aqiInfo.getString("o3"));
        aqi.setPm10(aqiInfo.getString("pm10"));
        aqi.setPm25(aqiInfo.getString("pm25"));

        aqi.setSo2(aqiInfo.getString("so2"));

//        aqi.setCoC(aqiInfo.getString("coDesc"));
//        aqi.setNo2C(aqiInfo.getString("noDesc"));
//        aqi.setO3C(aqiInfo.getString("o3Desc"));
//        aqi.setPm10C(aqiInfo.getString("pm10Desc"));
//        aqi.setPm25C(aqiInfo.getString("pm25Desc"));
//        aqi.setSo2C(aqiInfo.getString("so2Desc"));

        aqi.setValue(aqiInfo.getString("aqi"));

        //设置发布时间
        String pubtime = aqiInfo.get("pubTime").toString();
        try {
            date = sdf.parse(pubtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        aqi.setPubtime(date);


        // 解析天气相关的数据

        //设置天气
        System.out.println(root.getJSONObject("current"));
        JSONObject current = root.getJSONObject("current");

        //设置天气相关参数
        WeatherInfo condition = new WeatherInfo();
        condition.setCityName(cityName);
        condition.setCityId(cityID);
        condition.setRealFeel(current.getJSONObject("feelsLike").getString("value"));
        condition.setPressure(current.getJSONObject("pressure").getString("value"));
        condition.setConditionId(current.getString("weather"));
        condition.setHumidity(current.getJSONObject("humidity").getString("value"));
        condition.setTemp(current.getJSONObject("temperature").getString("value"));
        condition.setUvi(current.getString("uvIndex"));
        condition.setVisibility(current.getJSONObject("visibility").getString("value"));
        //风速 风力 风向
        condition.setWindSpeed(current.getJSONObject("wind").getJSONObject("speed").getString("value"));
        condition.setWindDirection(current.getJSONObject("wind").getJSONObject("direction").getString("value"));

        double speed = Double.parseDouble(condition.getWindSpeed());
        condition.setWindForce(""+getWindForce(speed));

        String reviewLimitTime = current.get("pubTime").toString();
        try {
            date = sdf.parse(reviewLimitTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        condition.setAqi(root.getJSONObject("aqi").getString("aqi"));
        condition.setAqiDetail((new JSONObject().fromObject(aqi).toString()));
        condition.setPubTime(date);


        //后边看具体对接哪个系统
        condition.setPm25(aqiInfo.getString("pm25"));
        condition.setNoise("65");
        condition.setCongestionIndex("3.67");

        return condition;
    }

}
