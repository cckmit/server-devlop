package com.glodon.pcop.weasvc.controller;



import com.glodon.pcop.weasvc.model.ResultVo;
import com.glodon.pcop.weasvc.model.WeatherInfo;
import com.glodon.pcop.weasvc.service.FetchData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "/weather", tags = "天气情况")
@RestController
@Component
public class WeatherController {

    @Autowired
    public FetchData weather;

    //获取城市天气情况
    @ApiOperation(value = "增加采集城市", notes = "添加要采集的天气城市，获取天气情况")
    @GetMapping("/fetchData/weather/{cityName}")
    public ResultVo weatherInfo(@PathVariable("cityName") String cityName) {
        //采集当前城市数据到库里面
        ResultVo resultVo = new ResultVo();
        WeatherInfo w = weather.getWeatherByName(cityName,true);
        if(w == null){
            resultVo.setCode("E04000404");
            resultVo.setMessage("fail");
            resultVo.setData("没有该城市天气");
        }

        resultVo.setCode("E05000200");
        resultVo.setMessage("Success");
        resultVo.setData(w);
        return resultVo;
    }

    //获取天气种类
    @ApiOperation(value = "天气种类", notes = "获取天气分类情况")
    @GetMapping("/fetchData/weatherKind")
    public ResultVo watherKind() {
        ResultVo resultVo = new ResultVo();
        resultVo.setCode("E05000200");
        resultVo.setMessage("Success");
        resultVo.setData(weather.getWeatherKind());
        return resultVo;
    }



}


