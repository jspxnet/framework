package com.github.jspxnet.json.gson;

import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import com.google.gson.*;
import java.lang.reflect.Type;

public class ClassAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

    @Override
    public Class<?> deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context) throws JsonParseException {

        String src = json.getAsString();
        if (src!=null && src.contains("."))
        {
            if (src.contains("Class(") && src.contains(")"))
            {
                src = StringUtil.substringBetween(src,"Class(",")");
            }
            Class<?> cls;
            try {
                cls = ClassUtil.loadClass(src);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            return cls;
        }
        return null;
    }

    @Override
    public JsonElement serialize(Class src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        String value = null;
        //直接是日期格式
        if(src != null){
            value = src.getName();
        }
        return new JsonPrimitive(value);
    }

}