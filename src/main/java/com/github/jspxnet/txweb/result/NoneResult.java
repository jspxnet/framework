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
import com.github.jspxnet.txweb.support.ActionSupport;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-12-2
 * Time: 17:01:37
 * 不输出任何数据
 */
public class NoneResult extends ResultSupport {
    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        ActionSupport action = actionInvocation.getActionProxy().getAction();
        HttpServletResponse response = action.getResponse();
        if (response != null && response.isCommitted()) {
            response.getOutputStream().close();
        }
    }
}