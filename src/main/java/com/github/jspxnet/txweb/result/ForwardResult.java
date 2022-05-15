package com.github.jspxnet.txweb.result;


import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
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
        ActionContext actionContext = ThreadContextHolder.getContext();
        actionContext.setActionResult(ActionSupport.NONE);
        HttpServletRequest request = actionContext.getRequest();
        HttpServletResponse response = actionContext.getResponse();
        response.setContentType("text/html; charset=" + Dispatcher.getEncode());
        String url = getConfigLocationUrl(actionInvocation);
        if (!StringUtil.isNull(url)) {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }
}