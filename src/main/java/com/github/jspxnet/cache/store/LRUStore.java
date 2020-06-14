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

import com.github.jspxnet.cache.CacheException;
import com.github.jspxnet.cache.IStore;
import com.github.jspxnet.cache.container.CacheEntry;
import com.github.jspxnet.util.LRUHashMap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-10-19
 * Time: 15:13:02
 * 标准的LRU本地内存缓存,默认限制长度250
 * 数据一直保持，过期周期设置长一点比较合适
 */
public class LRUStore extends Store implements IStore {
    private final LRUHashMap<String, CacheEntry> cacheLists = new LRUHashMap<String, CacheEntry>(4000);

    public LRUStore() {

    }

    @Override
    public void setMaxElements(int maxElements) {
        cacheLists.setMaxCapacity(maxElements);
        super.setMaxElements(maxElements);
    }

    @Override
    public void put(CacheEntry entry) {
        cacheLists.put(entry.getKey(), entry);
    }


    @Override
    public long size() {
        return cacheLists.size();
    }

    /**
     * 得到
     *
     * @param key key
     * @return 返回数据
     */
    @Override
    public CacheEntry get(String key) {
        return cacheLists.get(key);
    }

    /**
     * 将被逐出的对象
     *
     * @return 得到第一个对象
     */
    @Override
    public CacheEntry getEvictedCacheEntry() {
        Iterator<CacheEntry> iterator = cacheLists.values().iterator();
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
        return cacheLists.remove(key);
    }

    /**
     * 删除所有
     */

    @Override
    public void removeAll() {
        cacheLists.clear();
    }

    @Override
    public long getSizeInBytes() throws CacheException {
        long sizeInBytes = 0;
        for (CacheEntry entry : cacheLists.values()) {
            sizeInBytes += entry.getSerializedSize();
        }
        return sizeInBytes;
    }

    @Override
    public Set<String> getKeys() {
        return new HashSet<String>(cacheLists.keySet());
    }

    @Override
    public boolean containsKey(String key) {
        return cacheLists.containsKey(key);
    }

    @Override
    public boolean isUseTimer() {
        return true;
    }

    @Override
    public void dispose() {
        cacheLists.clear();
    }


}