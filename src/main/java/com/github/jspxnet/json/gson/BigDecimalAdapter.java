package com.github.jspxnet.json.gson;

import com.github.jspxnet.utils.NumberUtil;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/5/6 15:27
 * description: gson适配器
 */
public class BigDecimalAdapter implements JsonSerializer<BigDecimal>, JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context) throws JsonParseException {

        return new BigDecimal(json.getAsString());
    }

    @Override
    public JsonElement serialize(BigDecimal src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        String value = "";
        if(src != null){
            value = NumberUtil.getNumberStdFormat(src.toString());
        }
        return new JsonPrimitive(value);
    }

}