/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.service.client;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.HessianRemoteObject;
import com.github.jspxnet.txweb.service.HessianClient;
import com.github.jspxnet.utils.ClassUtil;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-27
 * Time: 下午10:40
 * jspx.net 提供的Hessian 调用接口
 * 当然你也可以使用原生接口调用方式
 * <p>
 * 简单例子
 * <pre>{@code
HessianClient hessianClient = HessianClientFactory.getInstance();
hessianClient.setToken("12345679xxxxxx99999999999999"); //认证token  Auth 2.0
try {
SpringPersionInterface springPersionInterface = hessianClient.getInterface(SpringPersionInterface.class, url);
int response = springPersionInterface.validUpdate(8, 10);
System.out.println(new JSONObject(response).toString(4));
} catch (Exception e) {
e.printStackTrace();
}
 * }</pre>
 */


public class HessianClientFactory extends HessianProxyFactory implements HessianClient {


    public HessianClientFactory() {
        super.setOverloadEnabled(true);
        super.setHessian2Reply(true);
        super.setHessian2Request(true);
        super.getSerializerFactory().setSendCollectionType(true);
        super.setConnectTimeout(10000);
        super.setReadTimeout(10000);
    }

    private static final HessianClient INSTANCE = new HessianClientFactory();

    public static HessianClient getInstance() {
        return INSTANCE;
    }


    /**
     *  Hessian 方法创建
     * @param api api接口
     * @param urlName url
     * @param <T> 返回的类型
     * @return  接口实例
     * @throws MalformedURLException 异常
     */
    @Override
    public  <T> T create(Class<T> api, String urlName, String token) throws MalformedURLException {
        if (api == null) {
            throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
        }
        Class<?> cls = ClassUtil.getClass(api);
        if (cls!=null)
        {
            cls = ClassUtil.findRemoteAPI(cls);
        }
        if (cls==null)
        {
            cls = api;
        }
        return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{cls,HessianRemoteObject.class}, new JspxHessianProxy(new URL(urlName), this, token));
    }

}

