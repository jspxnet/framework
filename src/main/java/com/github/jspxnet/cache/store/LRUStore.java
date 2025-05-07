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
import com.github.jspxnet.utils.ObjectUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-10-19
 * Time: 15:13:02
 * 使用apahce的LURMap
 * 数据一直保持，过期周期设置长一点比较合适
 */
public class LRUStore extends Store implements IStore {
    private LRUHashMap<String, CacheEntry> cacheList = new LRUHashMap<>(500);

    public LRUStore() {

    }

    @Override
    public void setMaxElements(int maxElements) {
        cacheList.clear();
        cacheList = null;
        cacheList = new LRUHashMap<>(maxElements);
        super.setMaxElements(maxElements);
    }

    @Override
    public boolean lock(String key) {
        return lock( key, 10);
    }

    @Override
    public boolean lock(String key, int timeToLive) {
        CacheEntry cacheEntry = new CacheEntry();
        try {
            cacheEntry.setKey(key);
            cacheEntry.setValue(1);
            cacheEntry.setLive(timeToLive);
        } catch (Exception e) {
          //...
        }
        put(cacheEntry);
        return true;
    }

    @Override
    public boolean isLock(String key) {
        CacheEntry cacheEntry = get(key);
        if (cacheEntry==null)
        {
            return false;
        }
        return 1 == ObjectUtil.toInt(cacheEntry.getValue());
    }

    @Override
    public boolean unLock(String key) {
        cacheList.remove(key);
        return true;
    }

    @Override
    public void put(CacheEntry entry) {
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
        return new HashSet<String>(cacheList.keySet());
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
    public boolean isUseTimer() {
        return true;
    }

    @Override
    public void dispose() {
        cacheList.clear();
    }


}