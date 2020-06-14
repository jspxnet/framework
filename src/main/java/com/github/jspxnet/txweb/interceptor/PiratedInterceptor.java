package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yuan on 2015/7/18 0018.
 * 盗链拦截器 防止拦截
 */
@Slf4j
public class PiratedInterceptor extends InterceptorSupport {

    @Override
    public void init() {

    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        //这里是不需要验证的action
        Action action = actionInvocation.getActionProxy().getAction();
        HttpServletRequest request = action.getRequest();
        if (language == null) {
            return ActionSupport.ERROR;
        }
        if (RequestUtil.isPirated(request)) {
            action.addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.notAllowedPiratedLink));
            return ActionSupport.UNTITLED;
        }
        return actionInvocation.invoke();
        //也可以 return Action.ERROR; 终止action的运行
    }

    @Override
    public void destroy() {

    }
}