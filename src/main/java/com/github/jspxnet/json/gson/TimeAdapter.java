package com.github.jspxnet.json.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Time;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/9/4 1:20
 * description: jspx-framework
 **/
public class TimeAdapter implements JsonSerializer<Time>, JsonDeserializer<Time> {

    @Override
    public Time deserialize(JsonElement json, Type typeOfT,
                                 JsonDeserializationContext context) throws JsonParseException {
        if(json == null){
            return null;
        } else {
            try {
                return Time.valueOf (json.getAsString());
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Override
    public JsonElement serialize(Time src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        String value = null;
        if(src != null){

            value =  src.toString();
        }
        return new JsonPrimitive(value);
    }

}