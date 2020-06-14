/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.cache;

import com.github.jspxnet.cache.core.JSCache;

import java.util.Collection;
import java.util.Map;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-14
 * Time: 15:28:38
 */
public interface CacheManager extends Serializable {
    Cache createCache(IStore store, Map<String, String> configMap);

    Cache createCache(IStore store, Class<?> name, int keepTime, int maxElements, boolean eternal, String diskStorePath) throws Exception;

    void registeredCache(JSCache cache);

    Cache getCache(String name);

    Collection<Cache> getCaches();

    boolean containsKey(String key);

    Cache getCache(Class<?> cls);

}