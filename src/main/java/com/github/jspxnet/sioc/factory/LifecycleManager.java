/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.factory;

import com.github.jspxnet.sioc.util.AnnotationUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-12
 * Time: 12:12:38
 */
@Slf4j
public class LifecycleManager implements Lifecycle {

    final private static Map<String,LifecycleObject> CACHE = new HashMap<>();

    public LifecycleManager() {

    }

    private static String getCacheKey(String beanName, final String namespace) {
        return beanName + "_" + namespace;
    }

    /**
     * 放入管理周期生命对象
     *
     * @param beanName        bean名称
     * @param lifecycleObject 管理周期生命对象
     */
    @Override
    public void put(String beanName, final LifecycleObject lifecycleObject)  {
        if (lifecycleObject == null) {
            return;
        }
        CACHE.put(getCacheKey(beanName, lifecycleObject.getNamespace()),lifecycleObject);
        if (!lifecycleObject.getNamespace().equalsIgnoreCase(lifecycleObject.getRefNamespace()) && CACHE.get(getCacheKey(beanName, lifecycleObject.getRefNamespace())) == null) {  //如果两个相同的，命名空间不同
            CACHE.put(getCacheKey(beanName, lifecycleObject.getRefNamespace()),lifecycleObject);
        }

    }

    @Override
     public LifecycleObject get(String beanName, final String namespace) {
        return CACHE.get(getCacheKey(beanName, namespace));
    }

    /**
     * ioc 卸载操作，执行配置或者注释需要卸载的动作
     */
    @Override
    public void shutdown() {
        for (String key : CACHE.keySet()) {
            LifecycleObject lifecycleObject = CACHE.get(key);
            if (lifecycleObject != null && lifecycleObject.getObject() != null) {
                try {
                    Object bean = lifecycleObject.getObject();
                    AnnotationUtil.invokeDestroy(bean);
                } catch (Exception e) {
                    log.error(key, e);
                }
            }
        }
        CACHE.clear();
    }

}