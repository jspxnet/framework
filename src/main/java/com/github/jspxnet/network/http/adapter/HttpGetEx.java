package com.github.jspxnet.network.http.adapter;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/4/11 0:06
 * description: 扩展支持非标准二开系统
 **/
public class HttpGetEx  extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "GET";

    public HttpGetEx() {
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpGetEx(URI uri) {
        this.setURI(uri);
    }

    public HttpGetEx(String uri) {
        this.setURI(URI.create(uri));
    }

}