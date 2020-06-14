/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-9-2
 * Time: 11:36:28
 * 页面拦截
 */
public interface InterceptorUrl {
    /**
     * 发生在转换之前
     *
     * @param request  请求
     * @param response 应答
     * @return true 表示继续执行后边动作,false 就不执行后边的动作了
     */
    boolean dispatcherBefore(HttpServletRequest request, HttpServletResponse response);

    /**
     * 发生在action执行玩的最后
     *
     * @param request  请求
     * @param response 应答
     */
    void dispatcherAfter(HttpServletRequest request, HttpServletResponse response);
}