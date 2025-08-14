/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-10-14
 * Time: 下午3:53
 */
public interface ActionConfig extends Serializable {

    /**
     *
     * @return 描述
     */
    String getCaption();

    /**
     *
     * @return action名称
     */
    String getActionName();

    /**
     *
     * @return bean
     */
    String getIocBean();

    /**
     *
     * @return  类名
     */
    String getClassName();

    /**
     *
     * @return  要执行的方法
     */
    String getMethod();

    /**
     *
     * @return  是否手机自动切换模版
     */
    boolean isMobile();

    /**
     *
     * @return  强制要求加密传输
     */
    //boolean isSecret();

    /**
     *
     * @return 默认返回类支持
     */
    //String getResultClass();

    Map<String, Object> getParam();

    List<String> getInterceptors();

    void setCache(boolean cache);

    boolean isCache();


    String getCacheName();

    void setCacheName(String cacheName);

    List<ResultConfigBean> getResultConfigs();

    String[] getPassInterceptor();

    //Map<String, Operate> getOperateMap();

    ResultConfigBean getResultConfig(String name);

    String getNamespace();
}