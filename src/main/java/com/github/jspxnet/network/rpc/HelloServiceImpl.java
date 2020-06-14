package com.github.jspxnet.network.rpc;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/10 23:05
 * description: jspbox
 **/
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String msg) {
        return msg != null ? msg + " -----> I am fine." : "I am fine.";
    }


}