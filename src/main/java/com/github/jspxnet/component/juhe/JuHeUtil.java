package com.github.jspxnet.component.juhe;

import com.github.jspxnet.component.juhe.dto.WeatherInfoDto;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.network.http.HttpClientFactory;
import com.github.jspxnet.utils.MapUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public final class JuHeUtil {
    private JuHeUtil()
    {

    }

    /**
     * 未来7天的天气预报
     * @param key key
     * @param cityname 城市名称 中文
     * @return 天气josn
     *
     *{
     *     "resultcode":"200",
     *     "reason":"查询成功",
     *     "result":{
     *         "sk":{
     *             "temp":"-7",
     *             "wind_direction":"北风",
     *             "wind_strength":"4级",
     *             "humidity":"60%",
     *             "time":"11:02"
     *         },
     *         "today":{
     *             "temperature":"-19℃~-6℃",
     *             "weather":"多云",
     *             "weather_id":{
     *                 "fa":"01",
     *                 "fb":"01"
     *             },
     *             "wind":"西北风3-5级",
     *             "week":"星期四",
     *             "city":"嫩江",
     *             "date_y":"2021年11月25日",
     *             "dressing_index":"寒冷",
     *             "dressing_advice":"天气寒冷，建议着厚羽绒服、毛皮大衣加厚毛衣等隆冬服装。年老体弱者尤其要注意保暖防冻。",
     *             "uv_index":"最弱",
     *             "comfort_index":"",
     *             "wash_index":"较适宜",
     *             "travel_index":"较不宜",
     *             "exercise_index":"较不宜",
     *             "drying_index":""
     *         },
     *         "future":[{
     *             "temperature":"-19℃~-6℃",
     *             "weather":"多云",
     *             "weather_id":{
     *                 "fa":"01",
     *                 "fb":"01"
     *             },
     *             "wind":"西北风3-5级",
     *             "week":"星期四",
     *             "date":"20211125"
     *         },{
     *             "temperature":"-23℃~-6℃",
     *             "weather":"晴",
     *             "weather_id":{
     *                 "fa":"00",
     *                 "fb":"00"
     *             },
     *             "wind":"西北风微风",
     *             "week":"星期五",
     *             "date":"20211126"
     *         },{
     *             "temperature":"-16℃~-6℃",
     *             "weather":"晴",
     *             "weather_id":{
     *                 "fa":"00",
     *                 "fb":"00"
     *             },
     *             "wind":"西北风微风",
     *             "week":"星期六",
     *             "date":"20211127"
     *         },{
     *             "temperature":"-23℃~-8℃",
     *             "weather":"多云",
     *             "weather_id":{
     *                 "fa":"01",
     *                 "fb":"01"
     *             },
     *             "wind":"西南风3-5级",
     *             "week":"星期日",
     *             "date":"20211128"
     *         },{
     *             "temperature":"-17℃~-7℃",
     *             "weather":"多云",
     *             "weather_id":{
     *                 "fa":"01",
     *                 "fb":"01"
     *             },
     *             "wind":"东北风3-5级",
     *             "week":"星期一",
     *             "date":"20211129"
     *         },{
     *             "temperature":"-23℃~-8℃",
     *             "weather":"多云",
     *             "weather_id":{
     *                 "fa":"01",
     *                 "fb":"01"
     *             },
     *             "wind":"西南风3-5级",
     *             "week":"星期二",
     *             "date":"20211130"
     *         },{
     *             "temperature":"-23℃~-8℃",
     *             "weather":"多云",
     *             "weather_id":{
     *                 "fa":"01",
     *                 "fb":"01"
     *             },
     *             "wind":"西南风3-5级",
     *             "week":"星期三",
     *             "date":"20211201"
     *         }]
     *     },
     *     "error_code":0
     * }
     *
     */
    static public JSONObject getWeather7(String key,String cityname)
    {
        String url = "http://v.juhe.cn/weather/index";
        Map<String,Object> valueMap = new HashMap<>();
        valueMap.put("cityname", cityname);
        valueMap.put("json", "json");
        valueMap.put("format", "2");
        valueMap.put("key", key);
        HttpClient httpClient = HttpClientFactory.createRocHttpClient(url+"?" + MapUtil.toQueryString(valueMap));
        try {
            String out = httpClient.getString();
            if (!StringUtil.isJsonObject(out))
            {
                return null;
            }
            return new JSONObject(out);
        } catch (Exception e) {
            log.error("天气接口调用错误");
            e.printStackTrace();
        } finally {
            httpClient.close();
        }
        return null;
    }


    static public List<WeatherInfoDto> getWeather7List(String key, String cityname)
    {
        JSONObject jsonObject = getWeather7(key, cityname);
        if (jsonObject == null) {
            return new ArrayList<>(0);
        }
        if (200 != jsonObject.getInt("resultcode")) {
            log.error("天气接口调用错误:{}", jsonObject);
            return new ArrayList<>(0);
        }

        JSONObject result = jsonObject.getJSONObject("result");
        if (result == null) {
            log.error("天气接口调用错误:{}", jsonObject);
            return new ArrayList<>(0);
        }
        JSONArray future = result.getJSONArray("future");
        return future.parseObject(WeatherInfoDto.class);
    }
}
