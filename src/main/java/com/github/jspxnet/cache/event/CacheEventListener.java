/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.cache.event;

import com.github.jspxnet.cache.container.CacheEntry;
import com.github.jspxnet.cache.Cache;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-12
 * Time: 18:09:40
 */

public interface CacheEventListener extends Cloneable {
    /**
     * 删除后
     * @param cache 缓存
     * @param entry 实体
     */
    void notifyElementRemoved(Cache cache, com.github.jspxnet.cache.container.CacheEntry entry);

    //放入
    void notifyElementPut(Cache cache, com.github.jspxnet.cache.container.CacheEntry entry);

    //修改
    void notifyElementUpdated(Cache cache, com.github.jspxnet.cache.container.CacheEntry entry);

    //过期的
    void notifyElementExpired(Cache cache, com.github.jspxnet.cache.container.CacheEntry entry);

    //被逐出 超出范围的 删除的
    void notifyElementEvicted(Cache cache, CacheEntry entry);

    //删除所有
    void notifyRemoveAll(Cache cache);

}