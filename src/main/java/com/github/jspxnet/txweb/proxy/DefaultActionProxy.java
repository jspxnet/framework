/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.proxy;

import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.dispatcher.handle.ActionHandle;
import com.github.jspxnet.txweb.dispatcher.handle.HessianHandle;
import com.github.jspxnet.txweb.dispatcher.handle.RocHandle;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ValidUtil;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-26
 * Time: 23:59:57
 * action 代理执行容器
 */
public class DefaultActionProxy implements ActionProxy {
    private Action action;

    @Override
    public void destroy() {
        action.destroy();
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public void setMethod(String method) {
        Class<?> cls = ClassUtil.getClass(action.getClass());
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (StringUtil.hasLength(method) && !HessianHandle.NAME.equalsIgnoreCase(actionContext.getExeType())) {
            Method methodTmp = TXWebUtil.getExeMethod(this,cls,actionContext.getCallJson(),method,actionContext.getNamespace());
            if (methodTmp!=null)
            {
                actionContext.setMethod(methodTmp);
            }
        }
        //添加模版为空的时候能执行方法
        if (actionContext.getMethod() ==null)
        {
            String actionName = actionContext.getActionName();
            HttpMethod httpMethod = cls.getAnnotation(HttpMethod.class);
            if (httpMethod!=null&&StringUtil.ASTERISK.equals(httpMethod.actionName())&&ValidUtil.isGoodName(actionName,1,30))
            {
                Method methodTmp = TXWebUtil.getExeMethod(this,cls, actionContext.getCallJson(),actionName,actionContext.getNamespace());
                if (methodTmp!=null)
                {
                    actionContext.setMethod(methodTmp);
                }
            }
        }

        if (actionContext.getMethod() == null) {
            try {
                actionContext.setMethod(ClassUtil.getClass(action.getClass()).getMethod(ActionEnv.DEFAULT_EXECUTE));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    private String caption = "none";


    /**
     * @return 类描述
     */
    @Override
    public String getCaption() {

        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * @return 得到方法描述
     */
    @Override
    public String getMethodCaption() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        try {
            if (actionContext.getMethod() == null) {
                return StringUtil.empty;
            }
            Operate operate = actionContext.getMethod().getAnnotation(Operate.class);
            if (operate != null) {
                return operate.caption();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtil.empty;
    }

    /**
     * 执行action方法
     *
     * @return 返回
     * @throws Exception 异常
     */
    @Override
    public String execute(ActionContext actionContext) throws Exception {
        action.initialize();
        if (HessianHandle.NAME.equals(actionContext.getExeType())) {
            //远程调用
            HessianHandle.execute(this);
            //执行最后的清理动作
            action.execute();
            return ActionSupport.NONE;
        }
        String result;
        try {
            //下边是roc，和传统方式调用
            if (ActionHandle.NAME.equalsIgnoreCase(actionContext.getExeType()))
            {
                TXWebUtil.setTurnPage(action);
            }
            if (!ActionEnv.DEFAULT_EXECUTE.equals(actionContext.getMethod().getName())&&TXWebUtil.checkOperate(action, actionContext.getMethod())) {

                if (RocHandle.NAME.equalsIgnoreCase(actionContext.getExeType())) {
                    //ROC 普通调用
                    RocHandle.execute(action,actionContext);
                }
                else
                {
                    //默认模版方式调用
                    ActionHandle.execute(action,actionContext);
                }
            }
        } finally {
            //这个方法什么时候都会执行
            result =  action.execute();
            //做状态标识
            if (actionContext.getMethod()!=null&&ActionEnv.DEFAULT_EXECUTE.equals(actionContext.getMethod().getName()))
            {
                actionContext.setExecuted(true);
            }
        }
        return result;
    }

    /**
     * 只为了兼容
     * @return 返回需要执行的方法
     */
    @Deprecated
    @Override
    public Method getMethod() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return actionContext.getMethod();
    }

}