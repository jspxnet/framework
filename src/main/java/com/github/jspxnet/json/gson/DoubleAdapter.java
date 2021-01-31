package com.github.jspxnet.json.gson;

import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.StringUtil;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/5/6 15:41
 * description: gson适配器
 */
public class DoubleAdapter implements JsonSerializer<Double>, JsonDeserializer<Double> {

    @Override
    public Double deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context) throws JsonParseException {
        return StringUtil.toDouble(json.getAsString());
    }

    @Override
    public JsonElement serialize(Double src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        String value = null;
        if(src != null){
            value = NumberUtil.getNumberStdFormat(src.toString());
        }
        return new JsonPrimitive(value);
    }

}