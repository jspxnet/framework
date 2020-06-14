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
import com.github.jspxnet.txweb.dispatcher.Dispatcher;

import javax.servlet.ServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-10-28
 * Time: 15:41:16
 * 采用xml方式返回TXWeb信息
 */
public class MessageResult extends ResultSupport {
    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        Action action = actionInvocation.getActionProxy().getAction();
        StringBuilder sb = new StringBuilder();
        Map<String, String> fieldInfo = action.getFieldInfo();
        if (fieldInfo != null && !fieldInfo.isEmpty()) {
            sb.append("<ul id=\"fieldInfo\">\r\n");
            for (String key : fieldInfo.keySet()) {
                sb.append("<li>").append(key).append(":").append(fieldInfo.get(key)).append("</li>\r\n");
            }
            sb.append("</ul>\r\n");
        }
        List<String> messages = actionInvocation.getActionProxy().getAction().getActionMessage();
        if (messages != null && !messages.isEmpty()) {
            sb.append("<ul id=\"message\">\r\n");
            for (String msg : messages) {
                sb.append("<li>").append(msg).append("</li>\r\n");
            }
            sb.append("</ul>\r\n");
        }

        String contentType = "text/html; charset=" + Dispatcher.getEncode();
        ServletResponse response = action.getResponse();
        response.setCharacterEncoding(Dispatcher.getEncode());
        response.setContentType(contentType);
        PrintWriter pw = response.getWriter();
        pw.print(sb.toString());
        pw.flush();
        pw.close();
    }
}