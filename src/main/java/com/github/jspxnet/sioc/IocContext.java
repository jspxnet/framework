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

import com.github.jspxnet.sioc.tag.BeanElement;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-12
 * Time: 15:56:31
 */
public interface IocContext {

    BeanElement getBeanElement(String beanName, String namespace);

    boolean containsBean(String beanName, String namespace) throws Exception;

    List<BeanElement> getElementList();

    void reload() throws Exception;

    void registryRpcClientBean(Class<?> cla);

    void setConfigFile(String file);

    void setConfigFile(String[] configFile);

    Map<String, String> getExtendMap() throws Exception;

    BeanElement getBeanElementForNamespace(String beanName, String namespace);

    Map<String, String> getApplicationMap();

    void registerBean(BeanElement beanElement);

    void sanIocBean(String className) throws IOException;

    void registryIocBean(Class<?> cla);

    Map<String, String> getSchedulerMap();

    List<BeanElement> getInjectionBeanElements();
}