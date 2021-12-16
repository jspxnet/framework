/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.cache.store;

import com.github.jspxnet.cache.container.CacheEntry;
import com.github.jspxnet.cache.IStore;
import com.github.jspxnet.cache.CacheException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-13
 * Time: 10:55:51
 * <p>
 * 标准的本地内存缓存
 */

public class MemoryStore extends Store implements IStore {
    final private Map<String,CacheEntry> cacheList = new ConcurrentHashMap<>();

    public MemoryStore() {

    }

    @Override
    public boolean isUseTimer() {
        return true;
    }


    @Override
    public void put(CacheEntry entry) {
        if (cacheList.size() > getMaxElements()) {
            return;
        }
        cacheList.put(entry.getKey(), entry);
    }


    @Override
    public long size() {
        return cacheList.size();
    }

    /**
     * 得到
     *
     * @param key key
     * @return 返回数据
     */
    @Override
    public CacheEntry get(String key) {
        return cacheList.get(key);
    }

    /**
     * 将被逐出的对象
     *
     * @return 得到第一个对象
     */
    @Override
    public CacheEntry getEvictedCacheEntry() {
        Iterator<CacheEntry> iterator = cacheList.values().iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    /**
     * 删除
     *
     * @param key key
     * @return 删除的数据
     */
    @Override
    public CacheEntry remove(String key) {
        return cacheList.remove(key);
    }

    /**
     * 删除所有
     */
    @Override
    public void removeAll() {
        cacheList.clear();
    }

    @Override
    public long getSizeInBytes() throws CacheException {
        long sizeInBytes = 0;
        for (CacheEntry entry : cacheList.values()) {
            sizeInBytes += entry.getSerializedSize();
        }
        return sizeInBytes;
    }

    @Override
    public Set<String> getKeys() {
        return cacheList.keySet();
    }

    @Override
    public Collection<CacheEntry> getAll() {
        return cacheList.values();
    }

    @Override
    public boolean containsKey(String key) {
        return cacheList.containsKey(key);
    }

    @Override
    public void dispose() {
        cacheList.clear();
    }


}