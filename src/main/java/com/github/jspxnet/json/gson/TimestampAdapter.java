package com.github.jspxnet.json.gson;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * date: 2020/5/6 14:34
 * description: jspxpro
 */
import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Timestamp;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/5/6 15:27
 * description: gson适配器
 */
public class TimestampAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

    @Override
    public Timestamp deserialize(JsonElement json, Type typeOfT,
                                 JsonDeserializationContext context) throws JsonParseException {
        if(json == null){
            return null;
        } else {
            try {
                return new Timestamp(json.getAsLong());
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Override
    public JsonElement serialize(Timestamp src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        String value = "";
        if(src != null){
            value =  String.valueOf(src.getTime());
        }
        return new JsonPrimitive(value);
    }

}