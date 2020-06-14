package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.support.ActionSupport;

/**
 * 禁止访问拦截器
 */
public class ProhibitInterceptor extends InterceptorSupport {
    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String intercept(ActionInvocation actionInvocation) {
        //这里是不需要验证的action
        ActionSupport action = actionInvocation.getActionProxy().getAction();
        action.addFieldInfo(Environment.warningInfo, "此页面禁止访问");
        return ActionSupport.UNTITLED;
        //也可以 return Action.ERROR; 终止action的运行
    }
}