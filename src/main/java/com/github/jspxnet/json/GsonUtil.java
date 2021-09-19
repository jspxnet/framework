package com.github.jspxnet.json;

import com.github.jspxnet.json.gson.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.awt.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/5/6 15:30
 * description: gson单元
 */
public class GsonUtil {
    private static Gson gson;
    private GsonUtil()
    {

    }
    public static Gson createGson(String dataFormat)
    {
        return new GsonBuilder()
                //序列化null
                .serializeNulls()
                // 设置日期时间格式，另有2个重载方法
                // 在序列化和反序化时均生效
                .setDateFormat(dataFormat)
                // 禁此序列化内部类
                .disableInnerClassSerialization()
                //生成不可执行的Json（多了 )]}' 这4个字符）
                .generateNonExecutableJson()
                //禁止转义html标签
                .disableHtmlEscaping()
                //格式化输出
                .setPrettyPrinting()
                .registerTypeAdapter(int.class,new IntAdapter())
                .registerTypeAdapter(Integer.class,new IntAdapter())
                .registerTypeAdapter(long.class,new LongAdapter())
                .registerTypeAdapter(Long.class,new LongAdapter())
                .registerTypeAdapter(float.class,new FloatAdapter())
                .registerTypeAdapter(Float.class,new FloatAdapter())
                .registerTypeAdapter(double.class,new DoubleAdapter())
                .registerTypeAdapter(Double.class,new DoubleAdapter())
                .registerTypeAdapter(BigDecimal.class,new BigDecimalAdapter())
                .registerTypeAdapter(Date.class,new DateAdapter())
                .registerTypeAdapter(TimeAdapter.class,new TimeAdapter())
                .registerTypeAdapter(TimestampAdapter.class,new TimestampAdapter())
                .registerTypeAdapter(SocketAddress.class,new SocketAddressAdapter())
                .registerTypeAdapter(InetSocketAddress.class,new InetSocketAddressAdapter())
                .registerTypeAdapter(Point.class,new PointAdapter())
                .create();

    }
    public static Gson createGson()
    {
        if (gson!=null)
        {
            return gson;
        }
        return gson = createGson(JSONObject.FULL_ST_FORMAT);
    }


    /**
     * 转换
     * @param json 格式
     * @param cls 类型
     * @param <T>  类型
     * @return 对象
     */

    public static <T> List<T> getList(String json, Class<T> cls) {
        Gson gson = createGson();
        List<T> list = new ArrayList<>();
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }


    /**
     * 转成list中有map的
     * @param json json
     * @param <T> 类型
     * @return 对象
     */
    public static <T> Map<String,T> getListMaps(String json) {
        Gson gson = createGson();
        return gson.fromJson(json, new TypeToken<Map<String, T>>() {}.getType());
    }

}
