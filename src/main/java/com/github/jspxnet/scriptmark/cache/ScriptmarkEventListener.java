/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.cache;

import com.github.jspxnet.cache.event.CacheEventListener;
import com.github.jspxnet.cache.Cache;
import com.github.jspxnet.cache.container.CacheEntry;
import com.github.jspxnet.sioc.factory.LifecycleObject;
import com.github.jspxnet.sioc.util.AnnotationUtil;
import com.github.jspxnet.scriptmark.TemplateModel;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-17
 * Time: 15:33:29
 */
public class ScriptmarkEventListener implements CacheEventListener {


    //删除后
    @Override
    public void notifyElementRemoved(Cache cache, CacheEntry entry) {
        //  log.debug("notifyElementRemoved-删除后----jspx.test.sioc  cache=" + cache.getName() + "  entry=" + entry.getKey());
    }

    //放入
    @Override
    public void notifyElementPut(Cache cache, CacheEntry entry) {
        //  log.debug("notifyElementPut-放入----jspx.test.sioc  cache=" + cache.getName() + "  entry=" + entry.getKey());
    }

    //修改
    @Override
    public void notifyElementUpdated(Cache cache, CacheEntry entry) {
        //  log.debug("notifyElementUpdated-修改----jspx.test.sioc  cache=" + cache.getName() + "  entry=" + entry.getKey());
    }

    //过期的
    @Override
    public void notifyElementExpired(Cache cache, CacheEntry entry) {
        //  log.debug("notifyElementExpired-过期的----jspx.test.sioc  cache=" + cache.getName() + "  entry=" + entry.getKey());
    }

    //被逐出 超出范围的 删除的
    @Override
    public void notifyElementEvicted(Cache cache, CacheEntry entry) {
        //  log.debug("notifyElementEvicted----jspx.test.sioc cache=" + cache.getName() + "  entry=" + entry.getKey());
        TemplateModel lifecycleObject = (TemplateModel) entry.getValue();
        if (lifecycleObject != null) {
            lifecycleObject.clear();
        }
    }

    //删除所有
    @Override
    public void notifyRemoveAll(Cache cache) {

        for (String key : cache.getKeys()) {
            CacheEntry cacheEntry = cache.get(key);
            LifecycleObject lifecycleObject = (LifecycleObject) cacheEntry.getValue();
            if (lifecycleObject != null) {
                try {
                    AnnotationUtil.invokeDestroy(lifecycleObject.getObject());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

}