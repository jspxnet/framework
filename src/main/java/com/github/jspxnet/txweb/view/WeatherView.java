/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;


import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.network.http.HttpClientFactory;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.WeatherLog;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-9-27
 * Time: 上午9:58
 */
@Slf4j
@HttpMethod(caption = "天气预报")
public class WeatherView extends ActionSupport {


    @Operate(caption = "天气预报",method = "netweather")
    public WeatherLog getNetWeatherInfo(@Param(caption = "地区id",required = true) String id)
    {
        String url = "http://www.weather.com.cn/data/cityinfo/" + id +".html";
        HttpClient httpClient = HttpClientFactory.createHttpClient(url);
        Map<String,String> param = new HashMap<>();
        param.put("Content-Type", "application/json;charset=UTF-8");
        httpClient.setHeaders(param);
        try {
            String out = new String(httpClient.getBytes(url,null), StandardCharsets.UTF_8.name());
            JSONObject json = new JSONObject(out);
            JSONObject weatherJson = json.getJSONObject("weatherinfo");
            //{"weatherinfo":{"city":"嫩江","cityid":"101050602","temp1":"12℃","temp2":"24℃","weather":"雷阵雨转多云","img1":"n4.gif","img2":"d1.gif","ptime":"18:00"}}
            WeatherLog weatherLog = new WeatherLog();
            weatherLog.setCityId(weatherJson.getString("cityid"));
            weatherLog.setCity(weatherJson.getString("city"));
            weatherLog.setLowTemp(ObjectUtil.toInt(StringUtil.getNumber(weatherJson.getString("temp1"))));
            weatherLog.setHeightTemp(ObjectUtil.toInt(StringUtil.getNumber(weatherJson.getString("temp2"))));
            weatherLog.setWeather(weatherJson.getString("weather"));
            return weatherLog;
        } catch (Exception e) {
            log.error("调用天气接口失败");
            e.printStackTrace();
        }
        return null;
    }
}