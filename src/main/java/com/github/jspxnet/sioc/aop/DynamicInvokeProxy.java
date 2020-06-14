/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.aop;

import com.github.jspxnet.sioc.AopBean;
import com.github.jspxnet.sioc.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-13
 * Time: 21:14:19
 * <pre>
 * {@code
 *
 * <bean name="realSubject" class="testaio.jspx.test.sioc.RealSubject" singleton="true">
 * </bean>
 *
 * <bean name="testAopBean" class="testaio.jspx.test.sioc.TestAopBean" singleton="false">
 * </bean>
 *
 * <bean name="subject" class="com.github.jspxnet.sioc.aop.DynamicInvokeProxy" create="proxy" singleton="false">
 * <property name="target" ref="true">realSubject</property>
 * <property name="aopBean" ref="true">testAopBean</property>
 * </bean>
 * }
 * </pre>
 */
public class DynamicInvokeProxy implements InvocationHandler {

    private Object target;

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public DynamicInvokeProxy() {

    }

    private AopBean aopBean;

    public AopBean getAopBean() {
        return aopBean;
    }

    public void setAopBean(AopBean aopBean) {
        this.aopBean = aopBean;
    }

    private MethodInterceptor methodInterceptor;

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if (aopBean != null) {
            aopBean.before(proxy, method, args);
        }
        if (methodInterceptor != null) {
            result = methodInterceptor.invoke(target, method, args);
        } else {
            result = method.invoke(target, args);
        }
        if (aopBean != null) {
            aopBean.after(proxy, method, args);
        }
        return result;
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class[]{target.getClass().getInterfaces()[0]}, this);
    }

}