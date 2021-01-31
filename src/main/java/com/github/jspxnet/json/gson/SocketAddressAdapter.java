package com.github.jspxnet.json.gson;

import com.github.jspxnet.utils.IpUtil;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.net.SocketAddress;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/24 23:43
 * description: gson SocketAddress 解析适配器
 **/
public class SocketAddressAdapter implements JsonSerializer<SocketAddress>, JsonDeserializer<SocketAddress> {

    @Override
    public SocketAddress deserialize(JsonElement json, Type typeOfT,
                               JsonDeserializationContext context) throws JsonParseException {
        return IpUtil.getSocketAddress(json.getAsString());
    }


    @Override
    public JsonElement serialize(SocketAddress socketAddress, Type type, JsonSerializationContext jsonSerializationContext) {
        String value = null;
        if(socketAddress != null){
            value = IpUtil.getIp(socketAddress);
        }
        return new JsonPrimitive(value);
    }
}