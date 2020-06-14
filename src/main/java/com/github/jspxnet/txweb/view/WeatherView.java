/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;

import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.network.http.HttpClientFactory;
import com.github.jspxnet.txweb.annotation.HttpMethod;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-9-27
 * Time: 上午9:58
 */
@HttpMethod(caption = "天气预报")
public class WeatherView extends ActionSupport {
    private String url = "http://www.weather.com.cn/data/cityinfo/101260101.html";
    private String html = StringUtil.empty;
    private long lastTime = 0;

    public String getUrl() {
        return url;
    }

    @Param(caption = "天气URL")
    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml() throws JSONException {
        getJson();
        return html;
    }

    public JSONObject getJson() throws JSONException {
        if (StringUtil.isNull(html) || System.currentTimeMillis() - lastTime > DateUtil.HOUR) {
            html = getValue();
            if (!StringUtil.isNull(html) && html.contains("{")) {
                lastTime = System.currentTimeMillis();
            }
        }
        return new JSONObject(html);
    }

    public void setHtml(String html) {
        this.html = html;
    }

    private String getValue() {
        try {
            HttpClient httpClient = HttpClientFactory.createHttpClient(url);
            return httpClient.getString(url);
        } catch (Exception e) {
            return StringUtil.empty;
        }
    }
}