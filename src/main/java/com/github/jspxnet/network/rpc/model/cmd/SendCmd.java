package com.github.jspxnet.network.rpc.model.cmd;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/13 1:06
 * description: jspx-framework
 **/
@Data
public class SendCmd implements Serializable {
    //请求id,应答的时候用这个ID匹配返回,确保请求对应
    private String id;
    // register:注册集群服务器,route:请求路由表;offline:请求下线;ping:心跳, http调用,ioc:ioc对象接口调用
    private String action;
    //传输的data数据类型,默认为json,特殊情况byte为base64编码
    private String type = "json";

    private Map<String,Object> request;

    private String data;

}
