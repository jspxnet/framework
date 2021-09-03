package com.github.jspxnet.json.gson;

import com.github.jspxnet.utils.IpUtil;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;

public class InetSocketAddressAdapter implements JsonSerializer<InetSocketAddress>, JsonDeserializer<InetSocketAddress> {

    @Override
    public InetSocketAddress deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {
        return IpUtil.getSocketAddress(json.getAsString());
    }


    @Override
    public JsonElement serialize(InetSocketAddress socketAddress, Type type, JsonSerializationContext jsonSerializationContext) {
        String value = null;
        if(socketAddress != null){
            value = IpUtil.getIp(socketAddress);
        }
        return new JsonPrimitive(value);
    }


}