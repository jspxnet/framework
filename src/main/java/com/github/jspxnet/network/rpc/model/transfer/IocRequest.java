package com.github.jspxnet.network.rpc.model.transfer;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.URLUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/10 21:26
 * description: jspbox
 **/
@Data
public class IocRequest implements Serializable {

    //ioc 名称,类名
    private String className;
    //调用的命名空间
    private String namespace;

    //识别用户信息
    private String token;

    //调用的具体方法 begin
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    //调用的具体方法 end

    //set方式参数 很难用代理实现, 用 request 来代替set方法
    //private Map<String,Object> params;

    //http方式调用,是用,就是action方式
    private RequestTo request;
    private ResponseTo response;


}
