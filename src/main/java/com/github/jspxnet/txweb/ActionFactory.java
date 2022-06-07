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

import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.network.rpc.model.transfer.ResponseTo;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.DefultContextHolderStrategy;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.sober.exception.ValidException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-17
 * Time: 16:21:03
 * com.github.jspxnet.txweb.ActionFactory
 * Action 和 TXweb 整合接口
 */
public class ActionFactory {
    final private static WebConfigManager WEB_CONFIG_MANAGER = TxWebConfigManager.getInstance();

    private ActionFactory() {

    }

    final private static BeanFactory BEAN_FACTORY = EnvFactory.getBeanFactory();

    public static Object getActon(Action action, String className, List<?> arguments) throws Exception {
        if (className == null) {
            return null;
        }
        String webNamespace = null;
        if (className.contains(TXWebUtil.AT)) {
            String tmpClassName = className;
            className = className.substring(0, tmpClassName.indexOf(TXWebUtil.AT));
            webNamespace = StringUtil.substringAfter(tmpClassName, TXWebUtil.AT);
        } else if (action != null) {
            webNamespace = action.getEnv(ActionEnv.Key_Namespace);
        }
        if (StringUtil.isEmpty(webNamespace)) {
            webNamespace = TXWeb.global;
        }

        ActionConfig actionConfig = WEB_CONFIG_MANAGER.getActionConfig(className, webNamespace, false);
        ActionSupport result = null;
        if (actionConfig != null) {
            result = (ActionSupport) BEAN_FACTORY.getBean(actionConfig.getIocBean(), webNamespace);
        }
        if (result == null) {
            result = (ActionSupport) BEAN_FACTORY.getBean(className, webNamespace);
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
    static private void putArg(Action action, Action result, String className, List<?> arguments) throws Exception {
        ActionContext actionContext = ThreadContextHolder.getContext();
        //参数说明 1 isIgnoreParams， 2 Execute
        if (action != null && result != null) {
            if (actionContext!=null)
            {
                action.initEnv(actionContext.getEnvironment(),actionContext.getExeType());
            } else
            {
                DefultContextHolderStrategy.createContext(new RequestTo(new HashMap<>()),new ResponseTo(new HashMap<>()),new HashMap<>());
            }
            action.setActionResult(null);
            action.setResult(null);
            result.put(ActionEnv.Key_ActionName, className);
        }
        if (arguments == null || arguments.isEmpty()) {
            return;
        }
        /////////////设置请求数据begin
        Object reParam = arguments.get(0);
        if (ObjectUtil.toBoolean(reParam)) {
            //如果两边的请求方式不同将不能够设置请求参数,所以要判断
            //第一个参数  boolean 类型, 表示接受参数，并且放入 当前系统的内置变量
            TXWebUtil.copyRequestProperty(result);
        }
        /////////////设置请求数据end
        ////////////运行方法begin
        String exeMethod = TXWeb.none;
        if (arguments.size() > 1) {
            Object exec = arguments.get(1).toString();
            if ("true".equals(exec) || StringUtil.empty.equals(exec)) {
                exeMethod = ActionEnv.DEFAULT_EXECUTE;
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
                if (termData != null && termData.contains(StringUtil.EQUAL)) {
                    String vName = StringUtil.substringBefore(termData, StringUtil.EQUAL);
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
                    TXWebUtil.invokeFun(result, actionContext, null);
                }
            } else {
                BeanUtil.invoke(result, exeMethod);
            }
        }

        assert result != null;
        TXWebUtil.setTurnPage(result);
    }

}