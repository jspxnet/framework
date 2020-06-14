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

import com.github.jspxnet.cache.event.CacheEventListener;
import com.github.jspxnet.cache.Cache;
import com.github.jspxnet.cache.container.CacheEntry;
import com.github.jspxnet.sioc.util.AnnotationUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-23
 * Time: 0:33:17
 */
@Slf4j
public class LifecycEventListeners implements CacheEventListener {
    //private static final Logger log = LoggerFactory.getLogger(LifecycEventListeners.class);

    /**
     * 删除后
     * @param cache 缓存
     * @param entry 实体
     */
    @Override
    public void notifyElementRemoved(final Cache cache, final CacheEntry entry) {
        //log.debug("notifyElementRemoved-删除后----jspx.test.sioc  cache=" + cache.getName() + "  entry=" + entry.getKey());
    }

    /**
     * 放入
     * @param cache 缓存
     * @param entry 实体
     */
    @Override
    public void notifyElementPut(final Cache cache, final CacheEntry entry) {
        //  log.debug("notifyElementPut-放入----jspx.test.sioc  cache=" + cache.getName() + "  entry=" + entry.getKey());
    }

    /**
     * 修改
     * @param cache 缓存
     * @param entry 实体
     */
    @Override
    public void notifyElementUpdated(final Cache cache, final CacheEntry entry) {
        //  log.debug("notifyElementUpdated-修改----jspx.test.sioc  cache=" + cache.getName() + "  entry=" + entry.getKey());
    }

    /**
     * 过期的
     * @param cache 缓存
     * @param entry 实体
     */
     @Override
    public void notifyElementExpired(final Cache cache, final CacheEntry entry) {
        //  log.debug("notifyElementExpired-过期的----jspx.test.sioc  cache=" + cache.getName() + "  entry=" + entry.getKey());
    }


    /**
     * 被逐出 超出范围的 删除的
     * @param cache 缓存
     * @param entry 实体
     */
    @Override
    public void notifyElementEvicted(final Cache cache, final CacheEntry entry) {
        //  log.debug("notifyElementEvicted----jspx.test.sioc cache=" + cache.getName() + "  entry=" + entry.getKey());
        LifecycleObject lifecycleObject = (LifecycleObject) entry.getValue();
        if (lifecycleObject != null && lifecycleObject.getObject() != null && !lifecycleObject.isSingleton()) {
            try {
                Object bean = lifecycleObject.getObject();
                AnnotationUtil.invokeDestroy(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除所有
     * @param cache 缓存
     */
    @Override
    public void notifyRemoveAll(final Cache cache) {
        for (String key : cache.getKeys()) {
            CacheEntry cacheEntry = cache.get(key);
            LifecycleObject lifecycleObject = (LifecycleObject) cacheEntry.getValue();
            if (lifecycleObject != null) {
                Object bean = lifecycleObject.getObject();
                try {
                    AnnotationUtil.invokeDestroy(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

}