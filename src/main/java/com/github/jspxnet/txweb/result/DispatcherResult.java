/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.result;

import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.Action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-27
 * Time: 1:52:21
 * RequestDispatcher  转发
 */
public class DispatcherResult extends RedirectResult {
    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        Action action = actionInvocation.getActionProxy().getAction();
        HttpServletResponse response = action.getResponse();
        HttpServletRequest request = action.getRequest();
        request.getRequestDispatcher(getConfigLocationUrl(actionInvocation)).forward(request, response);
    }
}