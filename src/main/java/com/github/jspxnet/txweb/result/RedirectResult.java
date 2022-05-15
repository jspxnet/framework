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

import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.StringUtil;
import com.thetransactioncompany.cors.CORSResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ResponseFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-27
 * Time: 15:42:39
 * 页面跳转
 */
@Slf4j
public class RedirectResult extends ResultSupport {

    private String url;


    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletResponse response = actionContext.getResponse();
        response.setContentType("text/html; charset=" + Dispatcher.getEncode());
        if (!(response instanceof ResponseFacade) && !(response instanceof CORSResponseWrapper) && response.isCommitted()) {
            log.error("redirect response.isCommitted():" + response.isCommitted());
            return;
        }

        try {
            if (StringUtil.isEmpty(url))
            {
                url = getConfigLocationUrl(actionInvocation);
            }
            if (!StringUtil.isNull(url))
            {
                response.sendRedirect(url);
                actionContext.setActionResult(ActionSupport.NONE);
            }
        } catch (Exception e) {
            log.error(actionContext.getActionName() + "检查跳转配置是否正确,check redirect config action is:" + url, e);
        }
    }
}