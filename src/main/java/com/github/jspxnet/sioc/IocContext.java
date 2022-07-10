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
    /**
     * @param beanName  得到 bean名称
     * @param namespace 命名空间
     * @return 返回配置
     */
    BeanElement getBeanElement(String beanName, String namespace);
    /**
     * @param beanName  名称
     * @param namespace 命名空间
     * @return 判断是否存在
     */
    boolean containsBean(String beanName, String namespace);
    /**
     *
     * @return 得到元素列表
     */
    List<BeanElement> getElementList();

    /**
     *
     * @return 扫描包列表
     */
    List<String> getScanPackageList();

    /**
     * {@code
     * Map<String,Map<String,BeanElement>>  Map<命名空间,Map<bean name,BeanElement>>
     * }
     * 得到bean 命名空间
     *
     * @throws Exception 异常
     */
    void reload() throws Exception;

    void setConfigFile(String file);

    void setConfigFile(String[] configFile);

    Map<String, String> getExtendMap() throws Exception;

    BeanElement getBeanElementForNamespace(String beanName, String namespace);

    Map<String, String> getApplicationMap();
    /**
     * 注册在registerBeanMap 中的bean对象不会清除，将一直保留在系统中
     *
     * @param beanElement bean
     */
    void registerBean(BeanElement beanElement);
    /**
     * 扫描载入
     *
     * @param className 类名称
     */
    void sanIocBean(String className);
    /**
     * 注册class
     *
     * @param cla 类对象
     */
    void registryIocBean(Class<?> cla);
    /**
     *
     * @return 得到定时器map
     */
    Map<String, String> getSchedulerMap();

    List<BeanElement> getInjectionBeanElements();


    void shutdown();
}