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

import com.github.jspxnet.cache.container.CacheEntry;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-13
 * Time: 16:46:56
 */
public interface IStore extends Serializable {

    void setName(String name);

    String getName();

    int getMaxElements();

    void setMaxElements(int maxElements);

    /**
     * @param entry Puts an item into the cache.
     * @throws CacheException 异常
     */
    void put(com.github.jspxnet.cache.container.CacheEntry entry) throws CacheException;


    /**
     * @param key Gets an item from the cache.
     * @return 缓存实体
     */
    com.github.jspxnet.cache.container.CacheEntry get(String key);


    /**
     * Removes an item from the cache.
     *
     * @param key key
     * @return 缓存实体
     */
    com.github.jspxnet.cache.container.CacheEntry remove(String key);


    /**
     * Remove all of the elements from the store.
     *
     * @throws CacheException 异常
     */
    void removeAll() throws CacheException;


    /**
     * Prepares for shutdown.
     */
    void dispose();


    /**
     * @return the current store size.
     */
    long size();

    Set<String> getKeys();

    Collection<CacheEntry> getAll();

    long getSizeInBytes();


    /**
     * @param key The Element key
     * @return true if found. No check is made transfer see if the Element is expired.
     */
    boolean containsKey(String key);

    CacheEntry getEvictedCacheEntry();

    boolean isUseTimer();

    void setSecond(int second);

    int getSecond();
}