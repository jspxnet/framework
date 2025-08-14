/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.module;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.JspxConfiguration;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.context.DefultContextHolderStrategy;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.dispatcher.handle.ActionHandle;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.proxy.DefaultActionInvocation;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.sober.exception.ValidException;
import com.github.jspxnet.utils.URLUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-12-20
 * Time: 15:40:12
 */
public class JspxModule {
    public JspxModule() {

    }

    public static JspxConfiguration getBaseConfiguration() {
        return EnvFactory.getBaseConfiguration();
    }

    public static BeanFactory getBeanFactory() {
        return EnvFactory.getBeanFactory();
    }

    public static EnvironmentTemplate getEnvironmentTemplate() {
        return EnvFactory.getEnvironmentTemplate();
    }

    public static Object getBean(String classname) {
        String webNamespace = null;
        if (classname == null) {
            return null;
        }
        if (classname.contains(TXWebUtil.AT)) {
            webNamespace = StringUtil.substringAfter(classname, TXWebUtil.AT);
            classname = StringUtil.substringBefore(classname, TXWebUtil.AT);
        }
        BeanFactory beanFactory = getBeanFactory();
        if (webNamespace == null) {
            return beanFactory.getBean(classname);
        }
        return beanFactory.getBean(classname, webNamespace);
    }

    public static Object getBean(String name, String namespace) {
        BeanFactory beanFactory = getBeanFactory();
        return beanFactory.getBean(name, namespace);
    }

    public static Action getAction(HttpServletRequest request, HttpServletResponse response, String namePart) throws ValidException {
        String namespace = URLUtil.getNamespace(request.getRequestURI());
        return getAction(request, response, namePart, namespace);
    }

    public static Action getAction(HttpServletRequest request, HttpServletResponse response, String namePart, String namespace) throws ValidException {
        if (StringUtil.isNull(namespace)) {
            namespace = StringUtil.empty;
        }
        ///////////////////////////////////环境参数 begin


        Map<String, Object> envParams = TXWebUtil.createEnvironment();
        envParams.put(ActionEnv.Key_ActionName, namePart);
        envParams.put(ActionEnv.Key_Namespace, namespace);
        envParams.put(ActionEnv.Key_RealPath, Dispatcher.getRealPath());
        DefultContextHolderStrategy.createContext(request,response,envParams);

        envParams.put(ActionEnv.CONTENT_TYPE, "text/html; charset=" + request.getCharacterEncoding());
        ///////////////////////////////////环境参数 end
        ////////////////////ajax begin
        WebConfigManager webConfigManager = TxWebConfigManager.getInstance();
        try {
            ActionConfig actionConfig = webConfigManager.getActionConfig(namePart, namespace, true);
            if (actionConfig == null) {
                envParams.clear();
                return null;
            }
            ActionInvocation actionInvocation = new DefaultActionInvocation(actionConfig, envParams, ActionHandle.NAME, null, request, response,false);
            actionInvocation.initAction();
            actionInvocation.invoke();
            return actionInvocation.getActionProxy().getAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ////////////////////ajax end
        return null;
    }

    public static Object getAction(String name, String namespace) {
        BeanFactory beanFactory = getBeanFactory();
        return beanFactory.getBean(name, namespace);
    }

}