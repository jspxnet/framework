/*
 * Copyright (c) 2013. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.result;

import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;

import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 13-4-2
 * Time: 下午10:53
 * 直接打印后输出
 */
public class PrintResult extends ResultSupport {
    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        Action action = actionInvocation.getActionProxy().getAction();
        String contentType = "text/html; charset=" + Dispatcher.getEncode();
        HttpServletResponse response = action.getResponse();
        response.setCharacterEncoding(Dispatcher.getEncode());
        response.setContentType(contentType);
        TXWebUtil.print(action.getResult() == null ? StringUtil.empty : (String) action.getResult(), WebOutEnumType.HTML.getValue(), response);

    }
}