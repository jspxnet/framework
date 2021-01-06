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

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
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

    private Method method = null;

    private String exeType;

    private String namespace;

    @Override
    public String getExeType() {
        return exeType;
    }

    @Override
    public void setExeType(String exeType) {
        this.exeType = exeType;
    }

    private ActionSupport action;

    @Override
    public void setCallJson(JSONObject callJson) {
        action.put(ActionEnv.Key_CallRocJsonData, callJson);
    }

    @Override
    public JSONObject getCallJson() {
        return (JSONObject) action.getEnv().get(ActionEnv.Key_CallRocJsonData);
    }


    @Override
    public void destroy() {
        action.destroy();
    }

    @Override
    public ActionSupport getAction() {
        return action;
    }

    @Override
    public void setAction(ActionSupport action) {
        this.action = action;
    }

    @Override
    public Method getMethod() {
        if (method == null) {
            try {
                return ClassUtil.getClass(action.getClass()).getMethod(TXWebUtil.defaultExecute);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        }
        return method;
    }

    @Override
    public void setMethod(String method) {
        Class<?> cls = ClassUtil.getClass(action.getClass());
        if (StringUtil.hasLength(method) && !HessianHandle.NAME.equalsIgnoreCase(exeType)) {
            this.method = TXWebUtil.getExeMethod(this,cls, method);
        }
        //添加模版为空的时候能执行方法
        if (this.method ==null)
        {
            String actionName = getActionName();
            HttpMethod httpMethod = cls.getAnnotation(HttpMethod.class);
            if (httpMethod!=null&&StringUtil.ASTERISK.equals(httpMethod.actionName())&&ActionHandle.NAME.equalsIgnoreCase(exeType)&&ValidUtil.isGoodName(actionName,1,30))
            {
                Method methodTmp = TXWebUtil.getExeMethod(this,cls, actionName);
                if (methodTmp!=null&&methodTmp.getGenericReturnType().equals(Void.TYPE))
                {
                    this.method  = methodTmp;
                }
            }
        }
    }

    @Override
    public String getActionName() {
        return action.getEnv(ActionEnv.Key_ActionName);
    }

    @Override
    public String getNamespace() {
        return namespace;
    }
    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
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
        try {
            if (method == null) {
                return StringUtil.empty;
            }
            Operate operate = method.getAnnotation(Operate.class);
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
    public String execute() throws Exception {
        action.initialize();

        if (HessianHandle.NAME.equals(exeType)) {
            //远程调用
            HessianHandle.execute(this);
            //执行最后的清理动作
            action.execute();
            return ActionSupport.NONE;
        }

        //下边是roc，和传统方式调用
        if (method == null) {
            method = ClassUtil.getClass(action.getClass()).getMethod(TXWebUtil.defaultExecute);
        }

        TXWebUtil.setTurnPage(action);
        if (!TXWebUtil.defaultExecute.equals(method.getName())&&TXWebUtil.checkOperate(action, method)) {
            if (exeType.equalsIgnoreCase(RocHandle.NAME)) {
                //ROC 普通调用
                RocHandle.execute(this);
            } else {
                //默认模版方式调用
                ActionHandle.execute(this);
            }
            if (ActionSupport.NONE.equals(action.getActionResult())) {
                return ActionSupport.NONE;
            }
        }

        //这个方法什么时候都会执行
        return action.execute();
    }


}