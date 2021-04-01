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

import com.caucho.quercus.env.Env;
import com.caucho.quercus.module.AbstractQuercusModule;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.JspxConfiguration;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.dispatcher.handle.ActionHandle;
import com.github.jspxnet.txweb.env.ActionEnv;
import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.proxy.DefaultActionInvocation;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.sober.exception.ValidException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2007-12-20
 * Time: 14:59:14
 * com.github.jspxnet.txweb.module.JspxPhpModule
 */
@Slf4j
public class JspxPhpModule extends AbstractQuercusModule {

    public JspxPhpModule() {
        JspxNetApplication.autoRun();
    }

    public JspxConfiguration getBaseConfiguration() {
        return EnvFactory.getBaseConfiguration();
    }

    public BeanFactory getBeanFactory() {
        return EnvFactory.getBeanFactory();
    }

    public EnvironmentTemplate getEnvironmentTemplate() {
        return EnvFactory.getEnvironmentTemplate();
    }

    public Object getBean(String classname) {
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
        try {
            return beanFactory.getBean(classname, webNamespace);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getBean(String name, String namespace) {
        BeanFactory beanFactory = getBeanFactory();
        try {
            return beanFactory.getBean(name, namespace);
        } catch (Exception e) {
            log.error("getBean name:" + name + " namespace:" + namespace, e);
        }
        return null;
    }


    public Action getAction(Env env, String classname) throws ValidException {
        HttpServletRequest request = (HttpServletRequest) env.getRequest();
        String namespace = TXWebUtil.getNamespace(request.getServletPath());
        if (classname == null) {
            return null;
        }
        if (classname.contains(TXWebUtil.AT)) {
            namespace = StringUtil.substringAfter(classname, TXWebUtil.AT);
            classname = StringUtil.substringBefore(classname, TXWebUtil.AT);
        }
        return getAction(env, classname, namespace);

    }


    public Action getAction(Env env, String namePart, String namespace) throws ValidException {

        HttpServletRequest request = (HttpServletRequest) env.getRequest();
        HttpServletResponse response = (HttpServletResponse) env.getResponse();
        if (StringUtil.isNull(namespace)) {
            namespace = StringUtil.empty;
        }
        response.setCharacterEncoding(Environment.defaultEncode);

        ///////////////////////////////////环境参数 begin
        Map<String, Object> envParams = TXWebUtil.createEnvironment();
        envParams.put(ActionEnv.Key_ActionName, namePart);
        envParams.put(ActionEnv.Key_Namespace, namespace);
        envParams.put(ActionEnv.Key_RealPath, request.getServletPath());
        envParams.put(ActionEnv.ContentType, "text/html; charset=" + request.getCharacterEncoding());
        ///////////////////////////////////环境参数 end

        ////////////////////ajax begin
        WebConfigManager webConfigManager = TxWebConfigManager.getInstance();
        try {
            ActionConfig actionConfig = webConfigManager.getActionConfig(namePart, namespace, true);
            if (actionConfig == null) {
                envParams.clear();
                return null;
            }
            ActionInvocation actionInvocation = new DefaultActionInvocation(actionConfig, envParams, ActionHandle.NAME, null, request, response);
            actionInvocation.initAction();
            actionInvocation.invoke();
            return actionInvocation.getActionProxy().getAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ////////////////////ajax end

        return null;

    }

    public Object getAction(String name, String namespace) {
        BeanFactory beanFactory = getBeanFactory();
        try {
            return beanFactory.getBean(name, namespace);
        } catch (Exception e) {
            log.error("getBean name:" + name + " namespace:" + namespace, e);
        }
        return null;
    }
}