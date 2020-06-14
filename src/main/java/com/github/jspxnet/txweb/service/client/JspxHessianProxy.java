/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.service.client;

import com.caucho.hessian.client.HessianProxy;
import com.caucho.hessian.client.HessianProxyFactory;
import com.github.jspxnet.utils.StringUtil;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-8-28
 * Time: 上午9:45
 * 这里主要为了不破坏Hessian 添加认证功能
 */
public class JspxHessianProxy extends HessianProxy {
    private static final String AUTHORIZATION = "Authorization";
    /**
     * Variable for saving cookie list
     */
    private List<String> cookies = null;
    private String token = StringUtil.empty;

    /**
     * @param url       url
     * @param factory   对象工厂
     * @param token 用户token
     */
    public JspxHessianProxy(URL url, HessianProxyFactory factory, String token) {

        super(url, factory);
        super._factory.getConnectionFactory().setHessianProxyFactory(factory);
        this.token = token;
    }

    @Override
    protected void parseResponseHeaders(URLConnection conn) {
        List<String> setCookies = conn.getHeaderFields().get("Set-Cookie");
        if (setCookies != null) {
            cookies = setCookies;
        }

        super.parseResponseHeaders(conn);
    }

    @Override
    protected void addRequestHeaders(com.caucho.hessian.client.HessianConnection conn) {
        if (conn != null) {
            conn.addHeader(AUTHORIZATION, "Bearer " + token);
        }
        if (cookies != null) {
            for (String cookieString : cookies) {
                conn.addHeader("Cookie", cookieString);
            }
        }
        super.addRequestHeaders(conn);
    }


}