package com.github.jspxnet.json.gson;

import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.StringUtil;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/5/6 14:40
 * description: gson适配器
 */
public class LongAdapter implements JsonSerializer<Long>, JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context) throws JsonParseException {
        return StringUtil.toLong(json.getAsString());
    }

    @Override
    public JsonElement serialize(Long src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        String value = "";
        if(src != null){
            value = NumberUtil.getNumberStdFormat(src.toString());
        }

        return new JsonPrimitive(value);
    }

}