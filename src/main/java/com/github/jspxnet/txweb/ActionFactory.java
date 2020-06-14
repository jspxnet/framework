/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb;

import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.env.TXWeb;

import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.txweb.config.TXWebConfigManager;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sober.exception.ValidException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-17
 * Time: 16:21:03
 * com.github.jspxnet.txweb.ActionFactory
 * Action 和 TXweb 整合接口
 */
public class ActionFactory {
    final private static WebConfigManager WEB_CONFIG_MANAGER = TXWebConfigManager.getInstance();

    private ActionFactory() {

    }

    final private static BeanFactory beanFactory = EnvFactory.getBeanFactory();

    public static Object getActon(Action action, String className, List arguments) throws Exception {
        if (className == null) {
            return null;
        }
        String txWeb_namespace = null;
        if (className.contains(TXWebUtil.AT)) {
            String tmpClassName = className;
            className = className.substring(0, tmpClassName.indexOf(TXWebUtil.AT));
            txWeb_namespace = StringUtil.substringAfter(tmpClassName, TXWebUtil.AT);
        } else if (action != null) {
            txWeb_namespace = action.getEnv(ActionEnv.Key_Namespace);
        }
        if (!StringUtil.hasLength(txWeb_namespace)) {
            txWeb_namespace = TXWeb.global;
        }

        ActionConfig actionConfig = WEB_CONFIG_MANAGER.getActionConfig(className, txWeb_namespace, false);
        ActionSupport result = null;
        if (actionConfig != null) {
            result = (ActionSupport) beanFactory.getBean(actionConfig.getIocBean(), txWeb_namespace);
        }
        if (result == null) {
            result = (ActionSupport) beanFactory.getBean(className, txWeb_namespace);
        }
        putArg(action, result, className, arguments);
        return result;

    }

    /**
     * action创建对象    //参数说明 1 是否接收参数， 2 是否默认执行或者方法名称
     *
     * @param action    主action
     * @param result    子action
     * @param arguments 参数列表
     * @throws Exception      异常      异常
     * @throws ValidException 验证错误
     */
    static private void putArg(Action action, Action result, String className, List arguments) throws Exception {
        //参数说明 1 isIgnoreParams， 2 Execute
        if (action != null && result != null) {
            result.setRequest(action.getRequest());
            result.setResponse(action.getResponse());
            Map<String, Object> actionMap = new HashMap<String, Object>(action.getEnv());
            action.setActionResult(null);
            action.setResult(null);
            result.put(ActionEnv.Key_ActionName, className);
            result.setEnv(actionMap);
        }
        if (arguments == null || arguments.isEmpty()) {
            return;
        }
        /////////////设置请求数据begin
        Object reParam = arguments.get(0);
        if (ObjectUtil.toBoolean(reParam)) {
            //如果两边的请求方式不同将不能够设置请求参数,所以要判断
            //第一个参数  boolean 类型, 表示接受参数，并且放入 当前系统的内置变量
            assert result != null;
            assert action != null;
            BeanUtil.copyFiledValue(action, result);
            result.put(ActionEnv.Key_ActionName, className);
        }
        /////////////设置请求数据end
        ////////////运行方法begin
        String exeMethod = TXWeb.none;
        if (arguments.size() > 1) {
            Object exec = arguments.get(1).toString();
            if ("true".equals(exec) || StringUtil.empty.equals(exec)) {
                exeMethod = TXWebUtil.defaultExecute;
            } else {
                exeMethod = exec.toString();
                if ("false".equalsIgnoreCase(exeMethod) || "0".equals(exeMethod)) {
                    exeMethod = TXWeb.none;
                }
            }
        }
        ////////////运行方法end
        String termData;
        if (arguments.size() >= 2) {
            for (int i = 2; i < arguments.size(); i++) {
                termData = arguments.get(i).toString();
                if (termData != null && termData.contains("=")) {
                    String vName = StringUtil.substringBefore(termData, "=");
                    String vValue = termData.substring(vName.length() + 1);
                    Object value = vValue;
                    if (vValue.startsWith(TXWebUtil.AT)) {
                        value = BeanUtil.getProperty(action, vValue.substring(1));
                    }
                    BeanUtil.setSimpleProperty(result, vName, value);
                }
            }
        }

        if (!TXWeb.none.equalsIgnoreCase(exeMethod) && !StringUtil.isNull(exeMethod)) {
            if (action != null && result != null) {
                Method method = ClassUtil.getDeclaredMethod(result.getClass(), exeMethod);
                if (method != null) {
                    TXWebUtil.invokeFun(result, method, null);
                }
            } else {
                BeanUtil.invoke(result, exeMethod);
            }
        }

        assert result != null;
        TXWebUtil.setTurnPage(result);
    }

}