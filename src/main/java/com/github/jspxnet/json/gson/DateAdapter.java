package com.github.jspxnet.json.gson;

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
   // Class<?>
    @Override
    public Date deserialize(JsonElement json, Type typeOfT,
                                 JsonDeserializationContext context) throws JsonParseException {

        String src = json.getAsString();
        //  "\/Date(1666865230259+0800)\/"
        if (src!=null && src.contains("Date(") && src.contains(")"))
        {
            String dateStr = StringUtil.substringBetween(src,"Date(",")");
            if (dateStr!=null&&dateStr.contains("+"))
            {
                dateStr = StringUtil.substringBefore(dateStr,"+");
            }
            if (dateStr!=null&&dateStr.contains("-"))
            {
                dateStr = StringUtil.substringBefore(dateStr,"-");
            }
            if (!StringUtil.isNull(dateStr))
            {
                long time = StringUtil.toLong(dateStr);
                if (time>0)
                {
                    return new Date(time);
                }
            }
        }
        return StringUtil.getDate(json.getAsString());
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        String value = null;
        //直接是日期格式
        if(src != null){
            value = DateUtil.toString(src, DateUtil.FULL_ST_FORMAT);
        }
        return new JsonPrimitive(value);
    }
/*
    public static void main(String[] args) {
        //JspxNetApplication.restart();
      //  org.apache.logging.log4j.util.ServiceLoaderUtil
        String str = "{\"createDate\":\"\\/Date(1666865230259+0800)\\/\",\"ip\":\"2022-10-27 18:07:10\"}";
        //org.apache.logging.log4j.util.ServiceLoaderUtil
        JSONObject json = new JSONObject(str);
        System.out.println("-----------" + json.toString(4));
        BaseOperateVo vo = json.parseObject(BaseOperateVo.class);
        System.out.println("-----------" + ObjectUtil.toString(vo));

    }*/
}