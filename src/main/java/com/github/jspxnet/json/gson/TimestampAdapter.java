package com.github.jspxnet.json.gson;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * date: 2020/5/6 14:34
 * description: jspxpro
 */
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;

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
                Date data = StringUtil.getDate(json.getAsString());
                return new Timestamp(data.getTime());
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Override
    public JsonElement serialize(Timestamp src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        String value = null;
        if(src != null){
            value = DateUtil.toString(src, DateUtil.FULL_ST_FORMAT);
        }
        return new JsonPrimitive(value);
    }

}