/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.util;

import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.utils.*;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-7-17
 * Time: 22:06:19
 * request 的一个代理对象,简化模板操作,并保留到其他参数的能力
 * containsKey() 判断是否有这个参数
 * 不推荐使用本功能，尽量使用 action.getString("xxx") 这样的方式更好
 */
public class RequestMap extends HashMap<String,Object> implements Map<String,Object> {
    private HttpServletRequest request;
    private Method method = null;

    public RequestMap(HttpServletRequest request) {
        this.request = request;
        try {
            method = RequestUtil.class.getMethod("getString", HttpServletRequest.class, String.class, boolean.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object get(Object key) {
        if (key == null) {
            return StringUtil.empty;
        }
        Object o = super.get(key);
        if (o != null) {
            return o;
        }
        //解决 msoa 在firefox 中得到路径错误问题,firefox得到路径认为是更目录下开始
        if ("requestURIPath".equals(key)) {
            return URLUtil.getURLPath(request.getRequestURI());
        }
        if ("hostURL".equals(key)) {
            return URLUtil.getHostURL(request.getRequestURL().toString());
        }
        if ("URLPath".equals(key)) {
            return FileUtil.mendPath(URLUtil.getURLPath(request.getRequestURL().toString()));
        }
        if ("realPath".equals(key)) {
            return Dispatcher.getRealPath();
        }
        if ("isPirated".equals(key)) {
            return RequestUtil.isPirated(request);
        }
        if ("locationURL".equals(key)) {
            if (StringUtil.isNull(request.getQueryString())) {
                return request.getRequestURL().toString();
            }
            return request.getRequestURL().toString() + "?" + request.getQueryString();
        }
        if (RequestUtil.requestUserAgent.equals(key)) {
            return RequestUtil.getUserAgent(request);
        }
        if (RequestUtil.requestReferer.equals(key)) {
            return request.getHeader(RequestUtil.requestReferer);
        }
        if (request != null && ClassUtil.isDeclaredMethod(request.getClass(), ClassUtil.METHOD_NAME_GET + StringUtil.capitalize((String) key))) {
            o = BeanUtil.getProperty(request, (String) key);
        } else {
            if (((String) key).endsWith("[]")) {
                return BeanUtil.getProperty(request, "getParameterValues", new Object[]{StringUtil.substringBefore(((String) key), "[")}, false);
            }
            try {
                o = method.invoke(method, request, key, false);
            } catch (Exception e) {
                return BeanUtil.getProperty(request, "getParameter", new Object[]{key}, false);
            }
        }
        return o;
    }

    public Object get(Object key, String def) {
        if (key == null) {
            return StringUtil.empty;
        }
        Object o = this.request.getParameter(key.toString());
        if (o == null) {
            return def;
        }
        return o;
    }

    public boolean getBoolean(Object key) {
        return ObjectUtil.toBoolean(get(key));
    }

    public int getInt(Object key) {
        return ObjectUtil.toInt(get(key));
    }

    public long getLong(Object key) {
        return ObjectUtil.toLong(get(key));
    }

    public float getFloat(Object key) {
        return ObjectUtil.toFloat(get(key));
    }

    @Override
    public String toString() {
        JSONArray jsonArray = new JSONArray();
        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            jsonArray.put(enumeration.nextElement());
        }
        return jsonArray.toString();
    }
}