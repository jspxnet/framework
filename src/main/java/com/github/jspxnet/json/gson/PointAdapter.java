package com.github.jspxnet.json.gson;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.StringUtil;
import com.google.gson.*;
import java.awt.*;
import java.lang.reflect.Type;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/9/4 12:12
 * description: x,y 坐标类型,格式  "[x=" + x + ",y=" + y + "]",兼容两种类型,json 和 系统默认格式
 **/
public class PointAdapter implements JsonSerializer<Point>, JsonDeserializer<Point> {

    @Override
    public Point deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context) throws JsonParseException {
        if(json == null){
            return null;
        } else {
            try {
                String v = StringUtil.trim(json.getAsString());
                if (v.startsWith("{")&&v.endsWith("}"))
                {
                    //json
                    JSONObject poJson = new JSONObject(v);
                    int x = poJson.getInt("x");
                    int y = poJson.getInt("y");
                    poJson.clear();
                    return  new Point(x,y);
                }
                if (v.startsWith("[")&&v.endsWith("]"))
                {
                    //json
                    v = StringUtil.substringBetween(v,"[","]");
                    StringMap<String,String> map = new StringMap<>();
                    map.setLineSplit(",");
                    map.setKeySplit("=");
                    int x = map.getInt("x");
                    int y = map.getInt("y");
                    return  new Point(x,y);
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Override
    public JsonElement serialize(Point src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        String value = null;
        if(src != null){

            JSONObject json = new JSONObject();
            json.put("x",src.x);
            json.put("y",src.y);
            value =  json.toString();
        }
        return new JsonPrimitive(value);
    }

}