/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc;

import com.github.jspxnet.sioc.factory.LifecycleObject;
import com.github.jspxnet.sioc.tag.BeanModel;

import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-12
 * Time: 16:57:15
 */

public interface BeanFactory {

    IocContext getIocContext();

    void setIocContext(IocContext iocContext);

    <T> T getBean(Class<T> classes);

    Object getBean(String beanName);

    Object getBean(String beanName, String namespace);

    <T> T getBean(Class<T> classes, final String namespace);

    boolean containsBean(String beanName);

    boolean containsBean(String beanName, String namespace) throws Exception;

    boolean isSingleton(String beanName, String namespace);

    void shutdown();

    Object createEntry(LifecycleObject lifecycleObject) throws Exception;

    LifecycleObject getLifecycleObject(String beanName, String namespace) throws Exception;

    String[] setRef(Class<?> cla,Object o, final String namespace) throws Exception;

    Map<String, String> getApplicationMap();

    boolean registerBean(BeanModel beanElement);

}