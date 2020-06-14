package com.github.jspxnet.txweb.result;


import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ChenYuan on 2016/10/24.
 */
public class ForwardResult extends ResultSupport {

    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        Action action = actionInvocation.getActionProxy().getAction();
        HttpServletResponse response = action.getResponse();
        HttpServletRequest request = action.getRequest();
        response.setContentType("text/html; charset=" + Dispatcher.getEncode());
        String url = getConfigLocationUrl(actionInvocation);
        if (!StringUtil.isNull(url)) {
            request.getRequestDispatcher(url).forward(request, response);
            action.setActionResult(ActionSupport.NONE);
        }
    }
}