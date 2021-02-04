package com.github.jspxnet.network.rpc.model.transfer;

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
    private String url;

    //识别用户信息
    private String token;

    //调用的具体方法 begin
    private String methodName;
    //放入json 格式参数
    private String parameters;
    //调用的具体方法 end
    //http方式调用,是用,就是action方式
    private RequestTo request;
    private ResponseTo response;


}
