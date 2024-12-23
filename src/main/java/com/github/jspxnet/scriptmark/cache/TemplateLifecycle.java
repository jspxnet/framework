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

import com.github.jspxnet.scriptmark.TemplateLoader;
import com.github.jspxnet.scriptmark.TemplateModel;
import com.github.jspxnet.cache.Cache;
import com.github.jspxnet.cache.CacheManager;
import com.github.jspxnet.cache.store.MemoryStore;
import com.github.jspxnet.cache.JSCacheManager;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-17
 * Time: 15:28:31
 */
public class TemplateLifecycle implements TemplateLoader {
    private static boolean useCache = false;

    public TemplateLifecycle(int second, int size) {
        CacheManager cacheManager = JSCacheManager.getCacheManager();
        try {
            Cache cache = cacheManager.createCache(new MemoryStore(), TemplateLifecycle.class, second, size, false, null);
            cache.registeredEventListeners(new ScriptmarkEventListener());
            useCache = true;
        } catch (Exception e) {
            useCache = false;
        }
    }

    @Override
    public boolean isUseCache() {
        return useCache;
    }

    @Override
    public void put(String beanName, TemplateModel lifecycleObject) {
        if (lifecycleObject == null) {
            return;
        }
        JSCacheManager.put(TemplateLifecycle.class, beanName, lifecycleObject);
    }

    @Override
    public TemplateModel get(String beanName) {
        return (TemplateModel) JSCacheManager.get(TemplateLifecycle.class, beanName);
    }

    @Override
    public void clear() {
        JSCacheManager.removeAll(TemplateLifecycle.class);
    }
}