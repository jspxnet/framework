package com.github.jspxnet.json.gson;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/5/6 14:36
 * description: gson适配器
 */
public class DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement json, Type typeOfT,
                                 JsonDeserializationContext context) throws JsonParseException {
        return StringUtil.getDate(json.getAsString());
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        String value = null;
        if(src != null){
            value = DateUtil.toString(src, JSONObject.FULL_ST_FORMAT);
        }
        return new JsonPrimitive(value);
    }

}