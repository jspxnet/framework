/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.network.http.HttpClientFactory;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-9-2
 * Time: 上午11:49
 * 跨站登录拦截器,拦截 如果登录成功，就发送登录成功信息给其他的服务器
 * com.github.jspxnet.txweb.interceptor.PassportInterceptor
 */
@Slf4j
public class PassportInterceptor extends InterceptorSupport {

    private String[] urlList;

    @Override
    public void init() {

    }

    @Ref(namespace = Sioc.global)
    private OnlineManager onlineManager;

    public void setUrlList(String[] url) {
        this.urlList = url;

    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        //这里是不需要验证的action
        String result = actionInvocation.invoke();
        Action action = actionInvocation.getActionProxy().getAction();
        IUserSession userSession = onlineManager.getUserSession(action);
        long tmc = action.getRequest().getSession(true).getMaxInactiveInterval();
        if (userSession != null && !userSession.isGuest() && urlList != null) {
            for (String url : urlList) {
                HttpClient httpClient = HttpClientFactory.createHttpClient(url);
                Map<String,Object> param = new HashMap<>();
                param.put("ticket", CookieUtil.getCookieString(action.getRequest(), TXWeb.COOKIE_TICKET, null));
                param.put("tmc", tmc + "");
                try {
                    httpClient.post(url, param);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getLocalizedMessage());
                }
            }
        }
        return result;
        //也可以 return Action.ERROR; 终止action的运行
    }

    @Override
    public void destroy() {

    }
}