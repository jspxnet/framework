package com.github.jspxnet.json.gson;

import com.github.jspxnet.utils.StringUtil;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.Date;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/9/4 1:20
 * description:
 **/
public class TimeAdapter implements JsonSerializer<Time>, JsonDeserializer<Time> {

    @Override
    public Time deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context) throws JsonParseException {
        if (json == null) {
            return null;
        }
        String src = json.getAsString();
        if (src != null && src.contains("Date(") && src.contains(")")) {
            String dateStr = StringUtil.substringBetween(src, "Date(", ")");
            if (dateStr != null && dateStr.contains("+")) {
                dateStr = StringUtil.substringBefore(dateStr, "+");
            }
            if (dateStr != null && dateStr.contains("-")) {
                dateStr = StringUtil.substringBefore(dateStr, "-");
            }
            if (!StringUtil.isNull(dateStr)) {
                long time = StringUtil.toLong(dateStr);
                if (time > 0) {
                    return new Time(time);
                }
            }
        }
        if (src != null && src.contains("Time(") && src.contains(")")) {
            String dateStr = StringUtil.substringBetween(src, "Time(", ")");
            if (dateStr != null && dateStr.contains("+")) {
                dateStr = StringUtil.substringBefore(dateStr, "+");
            }
            if (dateStr != null && dateStr.contains("-")) {
                dateStr = StringUtil.substringBefore(dateStr, "-");
            }
            if (!StringUtil.isNull(dateStr)) {
                long time = StringUtil.toLong(dateStr);
                if (time > 0) {
                    return new Time(time);
                }
            }
        }
        try {
            return Time.valueOf(json.getAsString());
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public JsonElement serialize(Time src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        String value = null;
        if (src != null) {

            value = src.toString();
        }
        return new JsonPrimitive(value);
    }

}